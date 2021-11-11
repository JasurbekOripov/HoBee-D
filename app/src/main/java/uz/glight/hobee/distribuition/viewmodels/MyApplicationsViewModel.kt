package uz.glight.hobee.distribuition.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import uz.glight.hobee.distribuition.network.paging.ApplicationsSource

class MyApplicationsViewModel : ViewModel() {
    fun getData() =
        Pager(PagingConfig(pageSize = 30)) {
            ApplicationsSource()
        }.flow.cachedIn(viewModelScope).asLiveData()

}