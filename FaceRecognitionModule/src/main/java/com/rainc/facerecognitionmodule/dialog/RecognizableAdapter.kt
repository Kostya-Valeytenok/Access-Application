package com.rainc.facerecognitionmodule.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.functions.Recognizable

class RecognizableAdapter(private val recognizable: List<Recognizable>, private val onClickListener: (item: Recognizable) -> Unit) : RecyclerView.Adapter<RecognizableAdapter.RecognizableItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecognizableItem = RecognizableItem(LayoutInflater.from(parent.context)
            .inflate(R.layout.recognizable_item, parent, false))

    override fun getItemCount(): Int = recognizable.size

    override fun onBindViewHolder(holder: RecognizableItem, position: Int) {
        val context = holder.imageView.context
        val recognizableFace = recognizable[position]

        holder.itemView.setOnClickListener {
            onClickListener.invoke(recognizableFace)
        }
        holder.imageView.setImageBitmap(recognizableFace.getPreview(context))
        holder.textView.text = recognizableFace.displayName
    }

    class RecognizableItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.text)
    }
}