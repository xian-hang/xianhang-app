package com.example.xianhang.product

import androidx.lifecycle.*
import com.example.xianhang.adapter.ProductAdapter
import com.example.xianhang.model.Product
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

enum class ProductStatus { LOADING, FAIL, SUCCESS }

class ProductViewModel(private val id: Int): ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _status = MutableLiveData<ProductStatus>()
    val status = _status

    init {
        getProducts(id)
    }

    class Factory(private val id: Int): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductViewModel(id) as T
        }
    }

    private fun getProducts(id: Int) {
        viewModelScope.launch {
            _status.value = ProductStatus.LOADING
            val resp = Api.retrofitService.getUserProduct(id)
            try {
                if (resOk(resp)) {
                    _status.value = ProductStatus.SUCCESS
                    _products.value = resp.products
                } else {
                    _status.value = ProductStatus.FAIL
                    _products.value = listOf()
                }
            } catch (e: HttpException) {
                _status.value = ProductStatus.FAIL
                _products.value = listOf()
            } catch (e: Exception) {
                _status.value = ProductStatus.FAIL
                _products.value = listOf()
            }
        }
    }
}
