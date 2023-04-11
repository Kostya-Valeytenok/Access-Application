package com.rainc.coroutinecore.tools

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LoadingStateObject<T:Any>(
    defaultState: LoadingState<T> = LoadingState.NotInit(),
    private val stateMigrationRute: StateMigrationRule = StateMigrationRule()
) {

    private val mutableStateFlow = MutableStateFlow(defaultState)
    val state:StateFlow<LoadingState<T>>
        get() = mutableStateFlow
    var onStateMigrationError: ((Throwable) -> Unit)? = null
        private set

    private val stateMigrationBlocked = Mutex()

    suspend fun migrate(newState: LoadingState<T>) = withContext(Dispatchers.Default) {
        stateMigrationBlocked.withLock {
            val currentState = mutableStateFlow.value
            if (stateMigrationRute.isValidMigration(currentState, newState) ) mutableStateFlow.emit(newState)
            else onStateMigrationError?.invoke(Throwable("imposable migrate from $currentState to $newState "))
        }
    }

    fun migrateBlocked(newState: LoadingState<T>) {
        runBlocking { migrate(newState = newState) }
    }

    fun tryMigrate(newState: LoadingState<T>): Boolean {
        val isLocked = stateMigrationBlocked.tryLock()
        if (isLocked.not()) return false
        val currentState = mutableStateFlow.value
        val isMigrated = if (stateMigrationRute.isValidMigration(
                currentState,
                newState
            )
        ) mutableStateFlow.tryEmit(newState)
        else {
            onStateMigrationError?.invoke(Throwable("imposable migrate from $currentState to $newState "))
            false
        }
        stateMigrationBlocked.unlock()
        return isMigrated
    }

    suspend fun collect(scope: suspend LoadingStateCallback<T>.() -> Unit) {
        state.collectState(scope)
    }
}

suspend fun <T : Any> StateFlow<LoadingState<T>>.collectState(scope: suspend LoadingStateCallback<T>.() -> Unit) {
    val mutableLoadingStateCallback = MutableLoadingStateCallback<T>()
    val stateScope = object : StateScope {}
    scope.invoke(mutableLoadingStateCallback)
    collect{
        when(it){
            is LoadingState.Error -> mutableLoadingStateCallback.onError?.invoke(stateScope, it.error)
            is LoadingState.InProgress -> mutableLoadingStateCallback.onProgress?.invoke(stateScope, it.progress)
            is LoadingState.OnSuccessful -> mutableLoadingStateCallback.OnSuccessful?.invoke(stateScope, it.result)
            is LoadingState.NeedUpdate -> mutableLoadingStateCallback.onNeedUpdate?.invoke(stateScope)
            is LoadingState.NotInit -> mutableLoadingStateCallback.onNotInit?.invoke(stateScope)
        }
        mutableLoadingStateCallback.onStateUpdate?.invoke(stateScope,it)
    }
}

open class StateMigrationRule() {
    open fun <T : Any> isValidMigration(
        oldState: LoadingState<T>,
        newState: LoadingState<T>
    ): Boolean = true
}

sealed class LoadingState<T> {

    data class NotInit<T>(private val id: Int = 0) : LoadingState<T>()
    data class NeedUpdate<T>(private val id: Int = 1) : LoadingState<T>()
    data class InProgress<T>(val progress: Double = 0.0) : LoadingState<T>()
    data class Error<T>(val error: Throwable) : LoadingState<T>()
    data class OnSuccessful<T : Any>(val result: T) : LoadingState<T>()

}

suspend fun <T : Any> Flow<LoadingState<T>>.collectAsLoadingState(scope: suspend LoadingStateCallback<T>.() -> Unit) {
    val stateScope = object : StateScope {}
    val mutableLoadingStateCallback = MutableLoadingStateCallback<T>()
    scope.invoke(mutableLoadingStateCallback)
    collect{
        when(it){
            is LoadingState.Error -> mutableLoadingStateCallback.onError?.invoke(stateScope, it.error)
            is LoadingState.InProgress -> mutableLoadingStateCallback.onProgress?.invoke(stateScope, it.progress)
            is LoadingState.OnSuccessful -> mutableLoadingStateCallback.OnSuccessful?.invoke(stateScope, it.result)
            is LoadingState.NeedUpdate -> mutableLoadingStateCallback.onNeedUpdate?.invoke(stateScope)
            is LoadingState.NotInit -> mutableLoadingStateCallback.onNotInit?.invoke(stateScope)
        }
        mutableLoadingStateCallback.onStateUpdate?.invoke(stateScope, it)
    }
}

@CallbackDLS
private class MutableLoadingStateCallback<T : Any>() : LoadingStateCallback<T>() {

    var onNotInit: (suspend StateScope.() -> Unit)? = null
        private set
    var onProgress: (suspend StateScope.(Double) -> Unit)? = null
        private set
    var OnSuccessful: (suspend StateScope.(T) -> Unit)? = null
        private set
    var onError: (suspend StateScope.(Throwable) -> Unit)? = null
        private set
    var onNeedUpdate: (suspend StateScope.() -> Unit)? = null
        private set

    var onStateUpdate: (suspend StateScope.(LoadingState<T>) -> Unit)? = null

    override suspend fun OnNotInit(action: suspend StateScope.() -> Unit) {
        onNotInit = action
    }

    override suspend fun OnProgress(action: suspend StateScope.(Double) -> Unit) {
        onProgress = action
    }

    override suspend fun OnSuccessful(action: suspend StateScope.(T) -> Unit) {
        OnSuccessful = action
    }

    override suspend fun OnError(action: suspend StateScope.(Throwable) -> Unit) {
        onError = action
    }

    override suspend fun OnNeedUpdate(action: suspend StateScope.() -> Unit) {
        onNeedUpdate = action
    }

    override suspend fun OnStateUpdate(action: suspend StateScope.(LoadingState<T>) -> Unit) {
        onStateUpdate = action
    }

}

@CallbackDLS
abstract class LoadingStateCallback<T>() {
    abstract suspend fun OnStateUpdate(action: suspend StateScope.(LoadingState<T>) -> Unit)
    abstract suspend fun OnNotInit(action: suspend StateScope.() -> Unit)
    abstract suspend fun OnProgress(action: suspend StateScope.(Double) -> Unit)
    abstract suspend fun OnSuccessful(action: suspend StateScope.(T) -> Unit)
    abstract suspend fun OnError(action: suspend StateScope.(Throwable) -> Unit)
    abstract suspend fun OnNeedUpdate(action: suspend StateScope.() -> Unit)

}

@CallbackDLS
interface StateScope

suspend fun <T> LoadingStateCallback<T>.RunIfUpdateRequed(action: suspend () -> Unit) {
    OnStateUpdate { loadingState ->
        when (loadingState) {
            is LoadingState.Error, is LoadingState.NeedUpdate, is LoadingState.NotInit -> action.invoke()
            else -> {}
        }
    }
}

fun StateFlow<LoadingState<*>>.isLoaded(): Boolean {
    return if (value is LoadingState.OnSuccessful) return true else false
}