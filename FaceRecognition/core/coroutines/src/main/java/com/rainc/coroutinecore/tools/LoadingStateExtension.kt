package com.rainc.utm.tool

import com.rainc.coroutinecore.tools.LoadingState
import com.rainc.coroutinecore.tools.LoadingStateObject

fun <T:Any> LoadingStateObject<T>.errorBlocked(message: String){
    migrateBlocked(newState = LoadingState.Error(Throwable(message)))
}

fun <T:Any> LoadingStateObject<T>.errorBlocked(error: Throwable){
    migrateBlocked(newState = LoadingState.Error(error))
}

fun <T:Any> LoadingStateObject<T>.progressBlocked(progress: Double = 0.0){
    migrateBlocked(newState = LoadingState.InProgress(progress = progress))
}

fun <T:Any> LoadingStateObject<T>.successfulBlocked(result: T){
    migrateBlocked(newState = LoadingState.OnSuccessful(result))
}

fun <T:Any> LoadingStateObject<T>.needUpdateBlocked(){
    migrateBlocked(newState = LoadingState.NeedUpdate())
}
suspend fun <T:Any> LoadingStateObject<T>.error(message: String){
    migrate(newState = LoadingState.Error(Throwable(message)))
}

suspend fun <T:Any> LoadingStateObject<T>.error(error: Throwable){
    migrate(newState = LoadingState.Error(error))
}

suspend fun <T:Any> LoadingStateObject<T>.progress(progress: Double = 0.0){
    migrate(newState = LoadingState.InProgress(progress = progress))
}

suspend fun <T:Any> LoadingStateObject<T>.successful(result: T){
    migrate(newState = LoadingState.OnSuccessful(result))
}

suspend fun <T:Any> LoadingStateObject<T>.needUpdate(){
    migrate(newState = LoadingState.NeedUpdate())
}

fun LoadingStateObject<*>.isLoaded(): Boolean {
   return state.value is LoadingState.OnSuccessful
}

fun LoadingState<*>.isLoaded(): Boolean {
    return this is LoadingState.OnSuccessful
}

fun LoadingState<*>.isNotLoaded(): Boolean {
    return this is LoadingState.InProgress || this is LoadingState.NotInit
}

fun LoadingState<*>.isLoading(): Boolean {
    return this is LoadingState.InProgress
}
