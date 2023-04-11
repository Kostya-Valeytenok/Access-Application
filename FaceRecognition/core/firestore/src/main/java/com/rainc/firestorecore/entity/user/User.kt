package com.rainc.firestorecore.entity.user

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.rainc.firebaseglobal.tools.EMPTY_STRING
import com.rainc.firestorecore.entity.BaseManagedEntity
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class User internal constructor (
    override var documentId:String = "",
    @get:PropertyName(KEY_IV)
    @set:PropertyName(KEY_IV)
    var IV: String = EMPTY_STRING,
    @get:PropertyName(KEY_ADD)
    @set:PropertyName(KEY_ADD)
    var ADD:String = EMPTY_STRING,
    @get:PropertyName(KEY_MODEL)
    @set:PropertyName(KEY_MODEL)
    var model: String = EMPTY_STRING,
    var creationDate: Double = 0.0,
) : BaseManagedEntity(documentId), Parcelable {

    companion object {

        const val KEY_MODEL = "model"
        const val KEY_IV = "IV"
        const val KEY_ADD = "ADD"
        fun newUser(
            id: String,
            IV: String,
            ADD: String,
            model:String
           ): User {
            val user = User()
            user.creationDate = Instant.now().epochSecond.toDouble()
            user.documentId = id
            user.IV = IV
            user.ADD = ADD
            user.model = model

            return user
        }
    }

    override fun mergeFields(): List<String> = listOf(
        KEY_IV,
        KEY_ADD,
        KEY_MODEL
    )
}
