package com.psu.accessapplication.items

import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.psu.accessapplication.R
import com.psu.accessapplication.databinding.ItemSpaceBinding
import com.psu.accessapplication.tools.BaseBindingItem
import com.psu.accessapplication.tools.Dimension

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
