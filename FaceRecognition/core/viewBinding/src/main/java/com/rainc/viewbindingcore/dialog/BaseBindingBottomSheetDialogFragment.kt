package com.rainc.viewbindingcore.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rainc.viewbindingcore.extension.inflate
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class BaseBindingBottomSheetDialogFragment<T : ViewBinding>(private val bindingKClass: KClass<T>) : BottomSheetDialogFragment() {
    private var _binding: T? = null
    val binding
        get() = _binding!!

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            bottomSheetDialog = this as BottomSheetDialog
        }
    }
    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingKClass.inflate(inflater, container, false)
        return binding.root
    }

    final override fun onDestroyView() {
        binding.onPreDestroyView()
        super.onDestroyView()
        _binding = null
        bottomSheetDialog = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss(dialog)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.onViewCreated(view, savedInstanceState)
        }
    }

    protected fun makeExpanded(){
        bottomSheetDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED;
    }

    protected fun makeCollapsed(){
        bottomSheetDialog?.behavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
    }

    abstract suspend fun T.onViewCreated(view: View, savedInstanceState: Bundle?)
    open fun T.onPreDestroyView() {}

    protected fun View.makeDialogExpandedIfFocused() {
        setOnFocusChangeListener { v, hasFocus -> if (hasFocus) makeExpanded() }
    }
}
