@file:Suppress("UNCHECKED_CAST")

package uz.glight.hobee.distribuition.utils

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import kotlin.IllegalArgumentException

//class HobeeViewModelFactory(
//    private val repository: RemoteRepository,
//    var owner: ViewModelStoreOwner,
//    var lOwner: LifecycleOwner
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
//            return HomeViewModel(owner, lOwner) as T
//        } else {
//            throw IllegalArgumentException()
//        }
//    }
//}