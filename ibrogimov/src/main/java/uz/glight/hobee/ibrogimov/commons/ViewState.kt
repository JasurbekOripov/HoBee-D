package uz.glight.hobee.ibrogimov.commons

sealed class ViewState  {
    class Success<out T>(val data: T) : ViewState()
    class Error<D>(val error: D) : ViewState()
    object Loading : ViewState()
}