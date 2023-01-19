package com.rainc.viewbindingcore.item

import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.rainc.viewbindingcore.R
import com.rainc.viewbindingcore.databinding.ItemSpaceBinding
import com.rainc.viewbindingcore.tools.Dimension

class SpaceItem(val space: Dimension) : BaseBindingItem<ItemSpaceBinding>(ItemSpaceBinding::class) {
    override val type: Int = R.id.is_space
    override var identifier: Long = R.id.is_space.hashCode().toLong()

    override fun ItemSpaceBinding.bind() {
        root.updateLayoutParams<RecyclerView.LayoutParams> {
            this.height = space.toPx(context)
        }
    }

    override fun ItemSpaceBinding.unbind() {
    }
}
