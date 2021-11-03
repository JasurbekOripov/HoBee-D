package com.glight.hobeedistribuition.utils

object ArrayListUtils {
    fun <T> merge(first: List<T>, second: List<T>): List<T> {
        val list: MutableList<T> = ArrayList()
        list.addAll(first)
        list.addAll(second)
        return list
    }
}