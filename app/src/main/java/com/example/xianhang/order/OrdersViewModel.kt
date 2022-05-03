package com.example.xianhang.order

import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.adapter.IMAGE_URL
import com.example.xianhang.model.OrderItem
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.OrdersResponse
import com.example.xianhang.product.ProductsViewModel
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class OrdersViewModel(
    private val method: Int,
    private val token: String
): ViewModel() {
    private val _orders = MutableLiveData<List<OrderItem>>()
    val orders: LiveData<List<OrderItem>> = _orders

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    init {
        getOrders()
    }

    class Factory(
        private val method: Int,
        private val token: String
    ): ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OrdersViewModel(method, token) as T
        }
    }

    private fun getOrders() {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                var resp: OrdersResponse? = null
                resp = if (method == BUYER) Api.retrofitService.getBoughtOrders(token)
                else Api.retrofitService.getSoldOrders(token)
                if (resOk(resp)) {
                    _status.value = View.GONE
                    _orders.value = resp.orders
                } else {
                    println("get orders failed")
                    setError()
                }
            } catch (e: HttpException) {
                println(e.message())
                setError()
            } catch (e: Exception) {
                println(e.message)
                setError()
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _orders.value = listOf()
    }
}