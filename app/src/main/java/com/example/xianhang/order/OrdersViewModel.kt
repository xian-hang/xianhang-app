package com.example.xianhang.order

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.xianhang.adapter.BUYER
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.OrdersResponse
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class OrdersViewModel: ViewModel() {
    private val _orders = MutableLiveData<List<OrderItem>>()
    val orders: LiveData<List<OrderItem>> = _orders

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    fun setOrders(context: Context?, token: String, method: Int, getStatus: Int) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = if (method == BUYER) {
                    when (getStatus) {
                        UNPAID -> Api.retrofitService.getStatusOrders(token, StatusId(UNPAID))
                        PAID -> Api.retrofitService.getStatusOrders(token, StatusId(PAID))
                        SHIPPED -> Api.retrofitService.getStatusOrders(token, StatusId(SHIPPED))
                        else -> Api.retrofitService.getBoughtOrders(token)
                    }
                }
                else Api.retrofitService.getSoldOrders(token)

                println("resp = $resp")
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _orders.value = resp.orders
                } else {
                    setError()
                }
            } catch (e: HttpException) {
                println("get orders http error")
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
                setError()
            } catch (e: Exception) {
                println("get orders other error")
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                setError()
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _orders.value = listOf()
    }
}