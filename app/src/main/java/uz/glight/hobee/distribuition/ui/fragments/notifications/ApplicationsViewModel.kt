package uz.glight.hobee.distribuition.ui.fragments.notifications

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glight.hobeedistribuition.network.model.ErrorModel
import com.glight.hobeedistribuition.network.model.OrderModel
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.parseError

class ApplicationsViewModel : ViewModel() {

    private val _applicationsState = MutableLiveData<ViewState>(
        ViewState.Success<List<OrderModel>>(
            emptyList()
        )
    )
    val applicationsState: LiveData<ViewState> = _applicationsState

    fun getApplications(page: Int){
        viewModelScope.launch {
            _applicationsState.postValue(ViewState.Loading)
            val response = RemoteRepository.getApplicationsList(page)
            if (response.isSuccessful){
                _applicationsState.postValue(ViewState.Success<List<OrderModel>>(response.body()!!))
            } else {
                _applicationsState.postValue(ViewState.Error<ErrorModel>(parseError(response)))
            }
        }
    }
}