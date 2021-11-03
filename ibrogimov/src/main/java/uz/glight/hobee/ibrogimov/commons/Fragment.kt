package uz.glight.hobee.ibrogimov.commons

import androidx.fragment.app.Fragment

// TODO: Rename function
fun Fragment.getFragmentTag(): String {
    return this::class.java.simpleName
}