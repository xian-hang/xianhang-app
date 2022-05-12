package com.example.xianhang.product

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.xianhang.adapter.*
import com.example.xianhang.model.ProductItem
import com.example.xianhang.model.SearchRequest
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.ProductsResponse
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductsViewModel: ViewModel() {
    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _image = MutableLiveData<Int>()
    val image: LiveData<Int> = _image

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> = _query

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

    fun setProducts(context: Context?, method: Int, token: String, id: Int?) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = when (method) {
                    BUYER -> Api.retrofitService.getAllProducts(token)
                    COLLECTION -> Api.retrofitService.getCollections(token)
                    FEEDS -> Api.retrofitService.getFeeds(token)
                    else -> Api.retrofitService.getUserProduct(token, id!!)
                }
                println(resp)
                if (resOk(context, resp)) {
                    println("setProducts done")
                    _status.value = View.GONE
                    _products.value = resp.products!!
                } else {
                    println("get Products failed")
                    setError()
                }
            } catch (e: HttpException) {
                println("setProducts http error")
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
                setError()
            } catch (e: CancellationException) {
                println("cancel error")
                e.printStackTrace() // Must let the CancellationException propagate
            } catch (e: Exception) {
                println("setProducts other error")
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                setError()
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _products.value = listOf()
    }
}
