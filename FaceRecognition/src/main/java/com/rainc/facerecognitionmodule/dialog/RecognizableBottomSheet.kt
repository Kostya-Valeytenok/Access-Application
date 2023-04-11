package com.rainc.facerecognitionmodule.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rainc.facerecognitionmodule.R
import com.rainc.recognitionsource.model.Recognizable

class RecognizableBottomSheet(val recognizable: List<Recognizable>, val onClickListener: (item: Recognizable) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recognizable_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RecognizableAdapter(recognizable) {
                dismissAllowingStateLoss()
                onClickListener.invoke(it)
            }
        }
    }
}