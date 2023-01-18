package com.rainc.facerecognitionmodule.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.extentions.displayMetrics
import net.glxn.qrgen.android.QRCode

class QRCodeDialog : AppCompatDialogFragment() {

    companion object {

        private const val KEY_QR_CODE_VALUE = "QR_CODE_VALUE"

        fun newInstance(value: String?): QRCodeDialog {
            val args = Bundle()
            args.putString(KEY_QR_CODE_VALUE, value)
            val fragment = QRCodeDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.qr_code_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val qrCodeView: ImageView = view.findViewById(R.id.qr_code_view)
        qrCodeView.setOnClickListener { dismiss() }

        val qrCodeData = arguments?.getString(KEY_QR_CODE_VALUE)?:run{
            dismiss()
            return
        }
        runCatching {
            val size =(requireContext().displayMetrics.widthPixels * 0.70).toInt()
            val qrCode = QRCode.from(qrCodeData).withSize(size,size).bitmap()
            qrCodeView.setImageBitmap(qrCode)
        }.onFailure {
            dismiss()
        }
    }

    private fun String?.validateQRCodeValue() : Result<String>{
       return  runCatching {
            if(isNullOrBlank()) throw NullPointerException()
            this
        }
    }

}