package uz.glight.hobee.distribuition.viewmodels

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.glight.hobee.distribuition.network.paging.WareHouseSource

class WareHouseViewModel : ViewModel() {

    fun getData(str: String) =
        Pager(PagingConfig(pageSize = 25)) {
            WareHouseSource(str)
        }.flow.cachedIn(viewModelScope).asLiveData()


}