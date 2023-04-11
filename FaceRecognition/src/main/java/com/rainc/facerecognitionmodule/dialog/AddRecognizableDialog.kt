package com.rainc.facerecognitionmodule.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.extentions.mirrored

/**
 * Dialog to confirm that user want to use this [previewImage]
 */
class AddRecognizableDialog(context: Context, previewImage: Bitmap, onClickListener: () -> Unit, onCancelListener: DialogInterface.OnCancelListener) : MaterialAlertDialogBuilder(context) {


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_recognizable, null)

        val imageView: ImageView = view.findViewById(R.id.person_image_preview)
        imageView.setImageBitmap(previewImage.mirrored())
        setView(view)

        setPositiveButton(context.getString(android.R.string.ok)) { dialog, which ->
            onClickListener.invoke()
        }

        setNegativeButton(context.getString(android.R.string.cancel)){ dialog, which ->
            onCancelListener.onCancel(dialog)
        }

        setOnCancelListener(onCancelListener)
    }
}