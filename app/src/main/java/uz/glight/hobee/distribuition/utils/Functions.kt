package uz.glight.hobee.distribuition.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

   fun View.internetError() {
        Snackbar.make(this, "Нет подключения к Интернету", Snackbar.LENGTH_SHORT).show()
    }

    fun View.simpleError(str: String="ошибка") {
        Snackbar.make(this, str, Snackbar.LENGTH_SHORT).show()
    }
