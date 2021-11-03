package uz.glight.hobee.distribuition.utils

interface OnItemClickListener<T> {
    fun onClickItem(position: Int, data: T)
}