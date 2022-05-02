package com.example.xianhang.order

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.xianhang.model.DELIVERY
import com.example.xianhang.model.PICKUP
import com.example.xianhang.model.Product

class OrderViewModel: ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    private val _price = MutableLiveData<Double>()
    val price: LiveData<Double> = _price

    private val _postage = MutableLiveData<Double>()
    val postage: LiveData<Double> = _postage

    private val _amount = MutableLiveData<Int>()
    val amount: LiveData<Int> = _amount

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _action = MutableLiveData<String>()
    val action: LiveData<String> = _action

    private val _tradingMethod = MutableLiveData<String>()
    val tradingMethod: LiveData<String> = _tradingMethod

    private val _visible = MutableLiveData<Int>()
    val visible: LiveData<Int> = _visible

    fun setOrder(p: Product, a: Int, t: Int) {
        _product.value = p
        _amount.value = a
        _tradingMethod.value = when (t) {
            DELIVERY -> "寄送"
            PICKUP -> "自取"
            else -> ""
        }
        _visible.value = if (t == DELIVERY) View.VISIBLE
        else View.GONE
        updatePrice()
    }

    fun setPostage(p: Double) {
        _postage.value = p
        updatePrice()
    }

    fun setStatus(state: Int) {

    }

    private fun updatePrice() {
        _price.value = (amount.value?.toDouble() ?: 0.0) *
                (product.value?.price ?: 0.0) +
                (postage.value ?: 0.0)
    }
}
