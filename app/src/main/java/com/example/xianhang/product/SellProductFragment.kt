package com.example.xianhang.product

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import android.Manifest
import androidx.core.net.toUri
import coil.load
import com.example.xianhang.adapter.ACTION
import com.example.xianhang.adapter.PRODUCT_ITEM
import com.example.xianhang.databinding.FragmentSellProductBinding
import com.example.xianhang.network.SCHEME
import com.example.xianhang.network.response.ImageResponse
import com.example.xianhang.product.ProductViewModel.Companion.IMAGE_URL
import com.example.xianhang.model.Product as Product

class SellProductFragment : Fragment() {

    private lateinit var binding: FragmentSellProductBinding
    private var imagePath: String = ""
    private var upload = false
    private var imageId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSellProductBinding.inflate(inflater)
        val type = arguments?.getString(ACTION)

        binding.image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                requestStoragePermission()
            }
        }

        if (type == "edit") {
            prefillProduct()
            binding.sell.setOnClickListener {
                requestEditProduct()
            }
        } else {
            binding.sell.setOnClickListener {
                requestSellProduct()
            }
        }

        return binding.root
    }

    private fun prefillProduct() {
        val productItem = arguments?.getParcelable<ProductItem>(PRODUCT_ITEM)
        val product = productItem!!.product
        var imageUrl = ""
        if (productItem.imagesId.isNotEmpty()) {
            imageId = productItem.imagesId[0]
            imageUrl = "$IMAGE_URL${imageId}"
        }
        println("prefill product")
        println(imageUrl)
        val imgUrl = imageUrl.toUri().buildUpon().scheme(SCHEME).build()
        println(imgUrl)
        binding.image.load(imgUrl) {
            placeholder(R.mipmap.ic_loading)
            error(R.mipmap.ic_image_placeholder)
        }

        binding.productName.setText(product.name)
        binding.productPrice.setText(product.price.toString().format("%2f"))
        binding.productStock.setText(product.stock.toString())
        binding.productDescription.setText(product.description)
        when(product.tradingMethod) {
            0 -> binding.deliver.isChecked = true
            1 -> binding.pickup.isChecked = true
            2 -> {
                binding.deliver.isChecked = true
                binding.pickup.isChecked = true
            }
        }
        if (binding.pickup.isChecked)
            binding.pickupAddress.setText(product.address)
    }

    private fun requestEditProduct() {
        val productItem = arguments?.getParcelable<ProductItem>(PRODUCT_ITEM)
        val product = productItem!!.product

        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        if (!checkData()) return

        val newProduct = getData()
        newProduct.id = product.id
        newProduct.userId = product.userId
        newProduct.username = product.username
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("edit product")
                val resp = Api.retrofitService.editProduct(token, newProduct, product.id!!)
                if (resOk(context, resp)) {
                    println("edit product success")
                    if (upload) {
                        if (imageId != null) {
                            println("run delete image")
                            try {
                                // TODO: change product.id to image.id
                                val res1 = Api.retrofitService.deleteProductImage(token, imageId!!)
                                if (!resOk(context, res1)) {
                                    println("image delete failed")
                                }
                            } catch (e: HttpException) {
                                println("delete image failed")
                                if (e.code() == 404) {
                                    println("no need to delete image")
                                }
                            }
                        }
                        println("run upload image")
                        try {
                            val res2 = uploadImage(token, File(imagePath), product.id!!)
                            imageId = res2.imageId
                            if (!resOk(context, res2)) {
                                println("upload image failed")
                            }
                        } catch (e: HttpException) {
                            println("upload image failed")
                        }
                    }
                    val images = if (imageId == null) listOf() else listOf(imageId!!)
                    val bundle = bundleOf(PRODUCT_ITEM to ProductItem(newProduct, images, null))
                    findNavController().navigate(R.id.action_sellProductFragment_to_viewProductFragment, bundle)
                }
            } catch (e: HttpException) {
                println("http exception")
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection wrong
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun requestSellProduct() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            return
        }
        if (!checkData()) return

        val product = getData()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.createProduct(token, product)
                var imageId: Int? = null
                if (resOk(context, resp)) {
                    if (upload) {
                        val res = uploadImage(token, File(imagePath), resp.product!!.id!!)
                        imageId = res.imageId
                        if (!resOk(context, res)) {
                            println("image upload failed",)
                        }
                    }
                    val images = if (imageId == null) listOf() else listOf(imageId)
                    val productItem = ProductItem(product, images, null)
                    val bundle = bundleOf(PRODUCT_ITEM to productItem)
                    findNavController().navigate(R.id.action_sellProductFragment_to_viewProductFragment, bundle)
                } else {
                    println("something wrong")
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

    private fun checkData(): Boolean {
        val name = binding.productName.text.toString()
        val description = binding.productDescription.text.toString()

        if (binding.productPrice.text!!.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the product price", Toast.LENGTH_LONG).show()
            return false
        }

        if (binding.productStock.text!!.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the product stock", Toast.LENGTH_LONG).show()
            return false
        }

        val price = binding.productPrice.text.toString().toDouble()
        val stock = binding.productStock.text.toString().toInt()
        val deliver = binding.deliver.isChecked
        val pickup = binding.pickup.isChecked
        val address = binding.pickupAddress.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the product name", Toast.LENGTH_LONG).show()
            return false
        }

        if (description.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in the description", Toast.LENGTH_LONG).show()
            return false
        }

        if (price <= 0) {
            Toast.makeText(requireActivity(), "Please fill in the product price", Toast.LENGTH_LONG).show()
            return false
        }

        if (stock <= 0) {
            Toast.makeText(requireActivity(), "Please fill in the product stock", Toast.LENGTH_LONG).show()
            return false
        }

        if (!deliver && !pickup) {
            Toast.makeText(requireActivity(), "Please choose trading method", Toast.LENGTH_LONG).show()
            return false
        }

        if (pickup && address.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in pickup address", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun getData(): Product {
        val name = binding.productName.text.toString()
        val description = binding.productDescription.text.toString()
        val price = binding.productPrice.text.toString().toDouble()
        val stock = binding.productStock.text.toString().toInt()
        val deliver = binding.deliver.isChecked
        val pickup = binding.pickup.isChecked
        var address: String? = binding.pickupAddress.text.toString()
        val tradingMethod = if (deliver && pickup) BOTH
        else if (deliver) DELIVERY
        else PICKUP
        if (!pickup) address = null

        return Product(null, name, description, price, stock, tradingMethod, address, null, null)
    }

    private suspend fun uploadImage(token: String, image: File, id: Int): ImageResponse {
        val reqImage = image.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "image",
            "upload.jpg",
            reqImage
        )

        val reqId = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return Api.retrofitService.createProductImage(token, filePart, reqId)
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Permission needed")
                .setMessage("This permission is needed")
                .setPositiveButton("ok") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE
                    )
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE
            )
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(requireActivity(), "Permission DENIED", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun selectImage() {
        println("select image")
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            startActivityForResult(it, REQUEST_CODE_IMAGE)
        }
    }

    @SuppressLint("Range")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            binding.image.setImageURI(data?.data)
            data?.data?.let { uri ->
                activity?.contentResolver
                    ?.query(uri, null, null, null, null)
                    ?.use {
                    if (it.moveToFirst())
                        imagePath = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DATA))
                }
            }
            println("upload is true now")
            upload = true
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 100
        private const val REQUEST_CODE_STORAGE = 101
    }
}
