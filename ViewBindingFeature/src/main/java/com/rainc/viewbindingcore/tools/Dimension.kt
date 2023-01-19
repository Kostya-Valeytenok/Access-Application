package com.rainc.viewbindingcore.tools

import android.content.Context
import androidx.annotation.DimenRes
import com.rainc.viewbindingcore.extension.convertDpToPixel
import com.rainc.viewbindingcore.extension.convertSpToPixel
import com.rainc.viewbindingcore.extension.getDimension
import kotlin.math.roundToInt

sealed class Dimension {
    class Sp(val sp: Int) : Dimension()
    class Px(val px: Int) : Dimension()
    class Dp(val dp: Int) : Dimension()
    class Resource(@DimenRes val res: Int) : Dimension()

    fun toPx(context: Context): Int {
        return when (this) {
            is Sp -> context.convertSpToPixel(sp)
            is Px -> px
            is Dp -> context.convertDpToPixel(dp)
            is Resource -> context.getDimension(res).roundToInt()
        }
    }
}