package uz.glight.hobee.distribuition.ui.fragments.drugstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glight.hobeedistribuition.network.model.CreateOrderModel
import com.glight.hobeedistribuition.network.model.DrugModel
import com.glight.hobeedistribuition.network.model.ErrorModel
import com.ulugbek.ibragimovhelpers.helpers.commons.append
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.ibrogimov.commons.ViewState
import uz.glight.hobee.ibrogimov.commons.parseError

class DrugStoreViewModel : ViewModel() {

    private var _viewmodelBasket = MutableLiveData<List<CreateOrderModel.DrugsListItem>>()
    val datamodelBasket: LiveData<List<CreateOrderModel.DrugsListItem>> = _viewmodelBasket

    val generalPrice = MutableLiveData<Double>(0.00)

    private var _viewmodelWarehouse = MutableStateFlow<ViewState>(ViewState.Success(data = emptyList<DrugModel>()))
    val datamodelWarehouse: StateFlow<ViewState> = _viewmodelWarehouse

    fun getWarehouse(){
        viewModelScope.launch {
            _viewmodelWarehouse.value = (ViewState.Loading)
            val res = RemoteRepository.getWarehouse()
            if (res.isSuccessful){
                _viewmodelWarehouse.value = (ViewState.Success<List<DrugModel>>(data = res.body()!!))
            } else {
                _viewmodelWarehouse.value = (ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }
    fun getWarehouse(query: String){
        viewModelScope.launch {
            _viewmodelWarehouse.value = (ViewState.Loading)
            val res = RemoteRepository.getWarehouse(query = query)
            if (res.isSuccessful){
                _viewmodelWarehouse.value = (ViewState.Success<List<DrugModel>>(data = res.body()!!))
            } else {
                _viewmodelWarehouse.value = (ViewState.Error<ErrorModel>(error = parseError(res)))
            }
        }
    }


    fun clearBasket() {
        _viewmodelBasket.postValue(emptyList())
    }

    fun removeFromBasket(id: Int) {

    }

    fun addDrugToBasket(item: CreateOrderModel.DrugsListItem) {
        val newArr = append(_viewmodelBasket.value ?: emptyList(), item)
        var genPrice = 0.00
        newArr.forEach {
            genPrice += it.allPrice
        }
        generalPrice.value = genPrice
        _viewmodelBasket.postValue(newArr);
    }
}