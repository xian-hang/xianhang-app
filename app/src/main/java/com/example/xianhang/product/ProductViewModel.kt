package com.example.xianhang.product

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.xianhang.model.Product
import com.example.xianhang.model.ProductId
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.Api
import com.example.xianhang.network.BASE_URL
import com.example.xianhang.network.response.DefaultResponse
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductViewModel: ViewModel() {
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _tradingMethod = MutableLiveData<String>()
    val tradingMethod: LiveData<String> = _tradingMethod

    private val _visibility = MutableLiveData<Int>()
    val visibility: LiveData<Int> = _visibility

    private val _collected = MutableLiveData<Int>()
    val collected: LiveData<Int> = _collected

    private val _uncollect = MutableLiveData<Int>()
    val uncollect: LiveData<Int> = _uncollect

    private val _imageSrcUrl = MutableLiveData<String>()
    val imageSrcUrl: LiveData<String> = _imageSrcUrl

    var collectId: Int? = null

    fun setProduct(productItem: ProductItem) {
        _status.value = View.GONE

        collectId = productItem.collectId
        println("collectId = ${productItem.collectId}")
        _collected.value = if (collectId == null) View.GONE else View.VISIBLE
        _uncollect.value = if (collectId != null) View.GONE else View.VISIBLE
        println("collected = ${collected.value}, uncollect = ${uncollect.value}")

        if (productItem.imagesId.isNotEmpty()) {
            _imageSrcUrl.value = "${IMAGE_URL}${productItem.imagesId[0]}"
        } else {
            _imageSrcUrl.value = ""
        }
        _product.value = productItem.product
        _tradingMethod.value = when(productItem.product.tradingMethod) {
            0 -> "寄送"
            1 -> "自取"
            2 -> "自取，寄送"
            else -> ""
        }
        _visibility.value = if (productItem.product.tradingMethod > 0) View.VISIBLE else View.GONE
    }

    fun collect(context: Context?, token: String, productId: Int?) {
        _collected.value = if (collectId != null) View.GONE else View.VISIBLE
        _uncollect.value = if (collectId == null) View.GONE else View.VISIBLE
        viewModelScope.launch {
            try {
                if (collectId != null) {
                    println("uncollect")
                    val resp = Api.retrofitService.uncollect(token, collectId!!)
                    if (resOk(context, resp)) {
                        collectId = null
                        Toast.makeText(context, "uncollected", Toast.LENGTH_LONG).show()
                    }
                } else {
                    println("collect")
                    val resp = Api.retrofitService.collect(token, ProductId(productId!!))
                    if (resOk(context, resp)) {
                        collectId = resp.collectionId
                        Toast.makeText(context, "collected", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val IMAGE_URL = "${BASE_URL}product/image/"
    }
}