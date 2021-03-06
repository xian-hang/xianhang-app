package com.example.xianhang.order

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class OrderViewModel: ViewModel() {
    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> = _order

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _price = MutableLiveData<Double>()
    val price: LiveData<Double> = _price

    private val _postage = MutableLiveData<Double>()
    val postage: LiveData<Double> = _postage

    private val _postageShow = MutableLiveData<String>()
    val postageShow: LiveData<String> = _postageShow

    private val _amount = MutableLiveData<Int>()
    val amount: LiveData<Int> = _amount

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _visCancel = MutableLiveData<Int>()
    val visCancel: LiveData<Int> = _visCancel

    private val _action = MutableLiveData<String>()
    val action: LiveData<String> = _action

    private val _visAction = MutableLiveData<Int>()
    val visAction: LiveData<Int> = _visAction

    private val _disAction = MutableLiveData<Boolean>()
    val disAction: LiveData<Boolean> = _disAction

    private val _tradingMethod = MutableLiveData<String>()
    val tradingMethod: LiveData<String> = _tradingMethod

    private val _visAddr = MutableLiveData<Int>()
    val visAddr: LiveData<Int> = _visAddr

    // for buy fragment
    fun previewOrder(p: Product, a: Int, t: Int) {
        _product.value = p
        _name.value = p.name
        _amount.value = a
        _tradingMethod.value = when (t) {
            DELIVERY -> "??????"
            PICKUP -> "??????"
            else -> ""
        }
        _visAddr.value = if (t == DELIVERY) View.VISIBLE
        else View.GONE
        _visCancel.value = View.GONE
        _price.value = p.price * a
    }

    // for view exists order
    fun setOrder(o: Order, isBuyer: Boolean) {
        _order.value = o
        _name.value = o.productName
        _product.value = o.product
        _amount.value = o.amount
        _tradingMethod.value = when (o.tradingMethod) {
            DELIVERY -> "??????"
            PICKUP -> "??????"
            else -> ""
        }
        println("postage is null = ${o.postage == null}")
        _postage.value = o.postage ?: 0.0
        _postageShow.value = if (o.postage == null) "?????????" else String.format("$%.2f", o.postage)
        _status.value = when (o.status) {
            UNPAID -> "?????????"
            PAID -> "?????????"
            SHIPPED -> "?????????"
            COMPLETE -> "?????????"
            CANCEL -> "?????????"
            else -> ""
        }
        println("isBuyer = $isBuyer")
        println("status = ${o.status}")
        _disAction.value = if (o.status == UNPAID && isBuyer && o.postage == null) false
        else if (o.status == UNPAID && !isBuyer && o.postage != null) false
        else true
        _visAction.value = when (o.status) {
            UNPAID -> if (isBuyer) View.VISIBLE else if (o.tradingMethod == DELIVERY) View.VISIBLE else View.GONE
            PAID -> if (!isBuyer) View.VISIBLE else View.GONE
            SHIPPED -> if (isBuyer) View.VISIBLE else View.GONE
            else -> View.GONE
        }
        _action.value = when (o.status) {
            UNPAID -> if (isBuyer) "??????" else "????????????"
            PAID -> "??????"
            SHIPPED -> "??????"
            else -> ""
        }
        _visCancel.value = when (o.status) {
            UNPAID -> if (isBuyer) View.VISIBLE else View.GONE
            else -> View.GONE
        }
        _visAddr.value = when (o.tradingMethod) {
            DELIVERY -> View.VISIBLE
            else -> View.GONE
        }
        _price.value = o.price
    }

    fun setPostage(p: Double) {
        _postage.value = p
        _postageShow.value = String.format("$%.2f", p)
    }

    fun setStatus(context: Context?, token: String, state: Int, isBuyer: Boolean) {
        viewModelScope.launch {
            try {
                val resp = Api.retrofitService.editOrderStatus(
                    token,
                    order.value!!.id!!,
                    OrderStatusRequest(state)
                )
                if (resOk(context, resp)) {
                    Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
                    when (state) {
                        PAID -> println("why u in here???")
                        SHIPPED -> updateStatus(SHIPPED, isBuyer)
                        COMPLETE -> updateStatus(COMPLETE, isBuyer)
                        CANCEL -> updateStatus(CANCEL, isBuyer)
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    fun updateStatus(state: Int, isBuyer: Boolean) {
        val tradingMethod = order.value!!.tradingMethod
        _status.value = when (state) {
            UNPAID -> "?????????"
            PAID -> "?????????"
            SHIPPED -> "?????????"
            COMPLETE -> "?????????"
            CANCEL -> "?????????"
            else -> ""
        }
        _action.value = when (state) {
            UNPAID -> "??????"
            PAID -> "??????"
            SHIPPED -> "??????"
            else -> ""
        }
        _visAction.value = when (state) {
            UNPAID -> if (isBuyer) View.VISIBLE else if (tradingMethod == DELIVERY) View.VISIBLE else View.GONE
            PAID -> if (!isBuyer) View.VISIBLE else View.GONE
            SHIPPED -> if (isBuyer) View.VISIBLE else View.GONE
            else -> View.GONE
        }
        _visCancel.value = when (state) {
            UNPAID -> if (isBuyer) View.VISIBLE else View.GONE
            PAID -> View.VISIBLE
            else -> View.GONE
        }
    }
}
