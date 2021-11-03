package com.ulugbek.ibragimovhelpers.helpers.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import uz.glight.hobee.ibrogimov.R
import uz.glight.hobee.ibrogimov.databinding.DialogDefaultBinding


data class DialogProperties(
    val context: Context,
    @LayoutRes
    var layoutId: Int = 0,
    var layoutView: View?,
    val listener: OnClickDialogButton?,
    val theme: Int?
)

interface OnClickDialogButton{
    fun setPositive()
    fun setNegative()
}

class DialogMaker {

    class Builder(context: Context) {

        private lateinit var dialog: DialogFragment

        fun setPositive(): Builder{

            return this
        }

        fun setNegative(): Builder{

            return this
        }

        fun setView(view: View): Builder{
            dialog
            return this
        }

        fun setView(@LayoutRes view: Int): Builder{

            return this
        }

        fun create(): DialogFragment{

            return dialog

        }
    }
}