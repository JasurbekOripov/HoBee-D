@file:Suppress("UNCHECKED_CAST")

package uz.glight.hobee.distribuition.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.fragments.home.HomeViewModel
import kotlin.IllegalArgumentException

class HobeeViewModelFactory(private val repository: RemoteRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel() as T
        } else {
            throw IllegalArgumentException()
        }
    }
}