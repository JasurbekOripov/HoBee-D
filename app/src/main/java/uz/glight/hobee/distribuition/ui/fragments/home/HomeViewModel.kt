package uz.glight.hobee.distribuition.ui.fragments.home

import androidx.lifecycle.*
import uz.glight.hobee.distribuition.network.models.ClinicModel
import com.glight.hobeedistribuition.network.model.ErrorModel
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.parseError

class HomeViewModel : ViewModel() {

    private var _viewmodel = MutableLiveData<ViewState>()
    val datamodel: LiveData<ViewState> = _viewmodel

    private var _viewmodelClinic = MutableLiveData<ViewState>()
    val datamodelClinic: LiveData<ViewState> = _viewmodelClinic

    fun getPharmacy(query: String){
        viewModelScope.launch {
            _viewmodel.postValue(ViewState.Loading)
            val res = RemoteRepository.getPharmacy(query)
            if (res.isSuccessful){
                _viewmodel.postValue(ViewState.Success<List<ClinicModel>>(data = res.body()!!))
            } else {
                _viewmodel.postValue(ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }
    fun getClinic(query: String){
        viewModelScope.launch {
            _viewmodelClinic.postValue(ViewState.Loading)
            val res = RemoteRepository.getClinic(query)
            if (res.isSuccessful){
                _viewmodelClinic.postValue(ViewState.Success<List<ClinicModel>>(data = res.body()!!))
            } else {
                _viewmodelClinic.postValue(ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }

    fun getPharmacy(){
        viewModelScope.launch {
            _viewmodel.postValue(ViewState.Loading)
            val res = RemoteRepository.getPharmacy()
            if (res.isSuccessful){
                _viewmodel.postValue(ViewState.Success<List<ClinicModel>>(data = res.body()!!))
            } else {
                _viewmodel.postValue(ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }
    fun getClinic(){
        viewModelScope.launch {
            _viewmodelClinic.postValue(ViewState.Loading)
            val res = RemoteRepository.getClinic()
            if (res.isSuccessful){
                _viewmodelClinic.postValue(ViewState.Success<List<ClinicModel>>(data = res.body()!!))
            } else {
                _viewmodelClinic.postValue(ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }

}