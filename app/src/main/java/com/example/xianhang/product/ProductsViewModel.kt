package com.example.xianhang.product

import android.view.View
import androidx.lifecycle.*
import com.example.xianhang.model.Product
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductsViewModel(private val id: Int): ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    init {
        getProducts(id)
    }

    class Factory(private val id: Int): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductsViewModel(id) as T
        }
    }

    private fun getProducts(id: Int) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = Api.retrofitService.getUserProduct(id)
                if (resOk(resp)) {
                    _status.value = View.GONE
                    _products.value = resp.products
                } else {
                    _status.value = View.GONE
                    _products.value = listOf()
                }
            } catch (e: HttpException) {
                _status.value = View.GONE
                _products.value = listOf()
            } catch (e: Exception) {
                _status.value = View.GONE
                _products.value = listOf()
            }
        }
    }
}
