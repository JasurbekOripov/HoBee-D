package uz.glight.hobee.distribuition.ui.fragments.home

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import uz.glight.hobee.distribuition.network.models.ClinicModel
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.paging.ClinicsSource
import uz.glight.hobee.distribuition.network.paging.PharmacySource

class HomeViewModel() : ViewModel() {

    private var _viewmodel = MutableLiveData<PagingData<ClinicModel>>()
    val datamodel: LiveData<PagingData<ClinicModel>> = _viewmodel

    private var _viewmodelClinic = MutableLiveData<PagingData<ClinicModel>>()
    val datamodelClinic: LiveData<PagingData<ClinicModel>> = _viewmodelClinic

    fun getPharmacy(query: String, owner: LifecycleOwner) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 30)) {
                PharmacySource(query)
            }.flow.cachedIn(viewModelScope).asLiveData().observe(owner, {
                _viewmodel.postValue(it)
            })
        }
    }


    fun getClinic(query: String, owner: LifecycleOwner) {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 30)) {
                ClinicsSource(query)
            }.flow.cachedIn(viewModelScope).asLiveData().observe(owner, {
                _viewmodelClinic.postValue(it)
            })
        }
    }

}