package com.example.xianhang.home

import android.view.View
import androidx.lifecycle.*
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class HomeViewModel(private val token: String): ViewModel() {
    private val _products = MutableLiveData<List<ProductItem>>()
    val products : LiveData<List<ProductItem>> = _products

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    init {
        getProducts(token)
    }

    class Factory(private val token: String): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(token) as T
        }
    }

    private fun getProducts(token: String) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = Api.retrofitService.getAllProducts(token)
                if (resOk(resp)) {
                    println("res ok")
                    _status.value = View.GONE
                    _products.value = resp.products
                } else {
                    println("res not ok")
                    setError()
                }
            } catch (e: HttpException) {
                setError()
                println(e.message())
            } catch (e: Exception) {
                setError()
                println(e.message)
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _products.value = listOf()
    }

}