package uz.glight.hobee.distribuition.ui.fragments.hospital

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glight.hobeedistribuition.network.model.DoctorModel
import com.glight.hobeedistribuition.network.model.ErrorModel
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.parseError

class HospitalViewModel : ViewModel() {
    private val _viewState = MutableLiveData<ViewState>(ViewState.Success(data = emptyList<DoctorModel>()))
    val viewState: LiveData<ViewState> = _viewState

    fun getDoctors(id: String, name: String = ""){
        viewModelScope.launch {
            _viewState.postValue(ViewState.Loading)
            val res = RemoteRepository.getDoctors(id, name)
            if (res.isSuccessful){
                _viewState.postValue(ViewState.Success<List<DoctorModel>>(data = res.body()!!))
            } else {
                _viewState.postValue(ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }
}