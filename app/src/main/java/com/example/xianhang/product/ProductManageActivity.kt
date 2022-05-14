package com.example.xianhang.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.adapter.METHOD
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.adapter.SELLER
import com.example.xianhang.databinding.ActivityProductManageBinding
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.ProductItem
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class ProductManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductManageBinding
    private val viewModel: ProductViewModel by viewModels()

    private var productItem: ProductItem? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductManageBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        productItem = intent?.extras?.getParcelable(PRODUCT_ITEM)
        viewModel.setProduct(productItem!!)

        val sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        token = sharedPreferences.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        setContentView(binding.root)

        binding.edit.setOnClickListener {
            navigateEdit()
        }

        binding.delete.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun navigateEdit() {
        val intent = Intent(this, SellActivity::class.java)
        intent.putExtra(PRODUCT_ITEM, productItem)
        intent.putExtra(ACTION, "edit")
        startActivity(intent)
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage("确定删除商品吗？")
            .setPositiveButton("确认") { _, _ ->
                requestDeleteProduct()
            }
            .setNegativeButton("取消") { _, _ ->

            }
            .show()
    }

    private fun requestDeleteProduct() {
        val id = viewModel.product.value!!.id

        if (token == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_LONG).show()
            return
        }

        val context = this
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.deleteProduct(id!!, token!!)
                if (resOk(context, resp)) {
                    val intent = Intent(context, ProductActivity::class.java)
                    intent.putExtra(METHOD, SELLER)
                    startActivity(intent)
                    finish()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection wrong
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra(METHOD, SELLER)
        startActivity(intent)
    }
}