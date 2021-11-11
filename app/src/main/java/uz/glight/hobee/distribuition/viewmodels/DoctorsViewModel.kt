package uz.glight.hobee.distribuition.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.glight.hobee.distribuition.network.paging.DoctorsSource

class DoctorsViewModel : ViewModel() {
    fun getData(id: String, name: String) =
        Pager(PagingConfig(pageSize = 25)) {
            DoctorsSource(id, name)
        }.flow.cachedIn(viewModelScope).asLiveData()

}