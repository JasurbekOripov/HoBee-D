package com.glight.hobeedistribuition.utils

import uz.glight.hobee.distribuition.R

object Constants {
    val TAG = "DEV_TAG"
    val MAIN_TABS = arrayOf<String>("Аптеки", "Поликлиники")
    val DRUGSTORE_TABS = arrayOf<String>("Препараты", "Корзина")
    val HOSPITAL_TABS = arrayOf<String>("Врачи", "История")

    val HOSPITAL_TAB_ICONS = arrayOf<Int>(R.drawable.ic_stethoscope, R.drawable.ic_story)
    val DRUGSTORE_TAB_ICONS = arrayOf<Int>(R.drawable.ic_pill, R.drawable.ic_shopping_cart)

    var AUDIO_FOLDER: String? = null
}