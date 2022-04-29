package com.example.xianhang.product

import androidx.lifecycle.*
import com.example.xianhang.model.Product
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductViewModel(private val id: Int) : ViewModel() {
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _status = MutableLiveData<ProductStatus>()
    val status: LiveData<ProductStatus> = _status

    private val _tradingMethod = MutableLiveData<String>()
    val tradingMethod: LiveData<String> = _tradingMethod

    private val _addressTitle = MutableLiveData<String>()
    val addressTitle: LiveData<String> = _addressTitle

    // private val _images = MutableLiveData<List<>>()
    // val images: LiveData<List<>> = _images

    init {
        getProduct(id)
    }

    class Factory(private val id: Int): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductViewModel(id) as T
        }
    }

    private fun getProduct(id: Int) {
        println("============== show Product ==============")
        viewModelScope.launch {
            _status.value = ProductStatus.LOADING
            println("status Loading")
            try {
                val resp = Api.retrofitService.getProduct(id)
                if (resOk(resp)) {
                    println("resOk")
                    _status.value = ProductStatus.SUCCESS
                    _product.value = resp.product
                    _tradingMethod.value = when(resp.product.tradingMethod) {
                        0 -> "寄送"
                        1 -> "自取"
                        2 -> "自取，寄送"
                        else -> ""
                    }
                    _addressTitle.value = if (resp.product.tradingMethod > 0) "自取地址"
                    else ""
                } else {
                    println("resNotOk")
                    _status.value = ProductStatus.FAIL
                    _product.value = null
                    _tradingMethod.value = ""
                    _addressTitle.value = ""
                }
            } catch (e: HttpException) {
                println("http exception")
                _status.value = ProductStatus.FAIL
                _product.value = null
                _tradingMethod.value = ""
                _addressTitle.value = ""
            } catch (e: Exception) {
                println("exception")
                println(e.message)
                _status.value = ProductStatus.FAIL
                _product.value = null
                _tradingMethod.value = ""
                _addressTitle.value = ""
            }
        }
    }
}