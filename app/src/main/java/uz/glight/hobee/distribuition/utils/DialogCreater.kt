package com.glight.hobeedistribuition.utils

import android.R
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog


class DialogCreater(private val ctx: Context) {
    private val inflater: LayoutInflater = LayoutInflater.from(this.ctx)
    fun create(@LayoutRes layoutId: Int, root: ViewGroup?, @StyleRes styleId: Int?): Dialog {
        val dialog = if (styleId == null){
            BottomSheetDialog(ctx)
        } else {
            BottomSheetDialog(ctx, styleId)
        }
        dialog.dismissWithAnimation = true
        val v = inflater.inflate(layoutId, root)
        dialog.setContentView(v)
        return dialog
    }
}