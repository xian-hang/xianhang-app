package com.example.xianhang.product

import android.content.Context
import android.view.View
import androidx.lifecycle.*
import com.example.xianhang.adapter.*
import com.example.xianhang.model.ProductItem
import com.example.xianhang.model.SearchRequest
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.ProductsResponse
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductsViewModel(
    private val method: Int,
    private val id: Int?,
    private val token: String?,
    private val keyword: String?,
    private val context: Context?
): ViewModel() {
    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _image = MutableLiveData<Int>()
    val image: LiveData<Int> = _image

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> = _query

    init {
        getProducts()
    }

    class Factory(
        private val method: Int,
        private val id: Int?,
        private val token: String?,
        private val keyword: String?,
        private val context: Context?
    ): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductsViewModel(method, id, token, keyword, context) as T
        }
    }

    fun search(context: Context?, token: String, keyword: String) {
        viewModelScope.launch {
            try {
                val resp = Api.retrofitService.searchProduct(token, SearchRequest(keyword))
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _products.value = resp.products
                } else {
                    println("get products failed")
                    _status.value = View.GONE
                    _products.value = listOf()
                }
            } catch (e: HttpException) {
                println("get products http failed")
                _status.value = View.GONE
                _products.value = listOf()
            } catch (e: Exception) {
                println("get products other failed")
                _status.value = View.GONE
                _products.value = listOf()
            }
        }
    }

    private fun getProducts() {
        if (method == SEARCH) _query.value = keyword!!
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                println("method = $method")
                println("token = $token")
                val resp: ProductsResponse = when (method) {
                    BUYER -> Api.retrofitService.getAllProducts(token!!)
                    COLLECTION -> Api.retrofitService.getCollections(token!!)
                    FEEDS -> Api.retrofitService.getFeeds(token!!)
                    SEARCH -> Api.retrofitService.searchProduct(token!!, SearchRequest(keyword!!))
                    // USER_PRODUCT and SELLER request same
                    else -> Api.retrofitService.getUserProduct(token, id!!)
                }
                if (resOk(context, resp)) {
                    println("get products")
                    _status.value = View.GONE
                    _products.value = resp.products
                    println("resp.products = ${resp.products}")
                } else {
                    println("get products failed")
                    _status.value = View.GONE
                    _products.value = listOf()
                }
            } catch (e: HttpException) {
                println("get products http failed")
                println(e.message())
                _status.value = View.GONE
                _products.value = listOf()
            } catch (e: Exception) {
                println("get products other failed")
                println(e.message)
                _status.value = View.GONE
                _products.value = listOf()
            }
        }
    }
}
