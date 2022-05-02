package com.example.xianhang.product

import android.view.View
import androidx.lifecycle.*
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.adapter.USER_PRODUCT
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.ProductsResponse
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductsViewModel(
    private val method: Int,
    private val id: Int?,
    private val token: String?,
): ViewModel() {
    private val _products = MutableLiveData<List<ProductItem>>()
    val products: LiveData<List<ProductItem>> = _products

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _image = MutableLiveData<Int>()
    val image: LiveData<Int> = _image

    init {
        getProducts()
    }

    fun getImageUrl(ids: List<Int>): String {
        if (ids.isNotEmpty())
            return "${IMAGE_URL}${ids[0]}"
        return ""
    }

    class Factory(
        private val method: Int,
        private val id: Int?,
        private val token: String?
    ): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductsViewModel(method, id, token) as T
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp: ProductsResponse = when (method) {
                    BUYER -> Api.retrofitService.getAllProducts(token!!)
                    // USER_PRODUCT and SELLER request same
                    else -> Api.retrofitService.getUserProduct(id!!)
                }
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
