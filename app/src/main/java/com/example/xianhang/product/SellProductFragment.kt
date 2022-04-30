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
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
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
import androidx.fragment.app.viewModels
import com.example.xianhang.databinding.FragmentSellProductBinding
import com.example.xianhang.model.Product as Product

class SellProductFragment : Fragment() {

    private lateinit var binding: FragmentSellProductBinding

    private lateinit var imageView: ImageView
    private var imagePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSellProductBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString("type")
        if (type == "edit") {
            val product = arguments?.getParcelable<Product>("product")
            prefillProduct(product!!)
        }

        imageView = view.findViewById(R.id.image)
        imageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                requestStoragePermission()
            }
        }

        val sell = view.findViewById<Button>(R.id.sell)
        sell.setOnClickListener {
            requestSellProduct(view)
        }
    }

    private fun prefillProduct(product: Product) {
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

    private fun requestSellProduct(view: View) {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(requireActivity(), "Please login", Toast.LENGTH_LONG).show()
            return
        }
        if (!checkData(view)) return

        val product = getData(view)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resp = Api.retrofitService.sellProduct(token, product)
                if (resOk(resp)) {
                    uploadImage(token, File(imagePath), resp.id)
                    val bundle = bundleOf("id" to resp.id)
                    findNavController().navigate(R.id.action_sellProductFragment_to_viewProductFragment, bundle)
                } else {
                    Toast.makeText(requireActivity(), "Create Error", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(requireActivity(), e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // TODO: check connection wrong
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun checkData(view: View): Boolean {
        val name = view.findViewById<TextInputEditText>(R.id.product_name).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.product_description).text.toString()
        val price = view.findViewById<TextInputEditText>(R.id.product_price).text.toString().toDouble()
        val stock = view.findViewById<TextInputEditText>(R.id.product_stock).text.toString().toInt()
        val deliver = view.findViewById<CheckBox>(R.id.deliver).isChecked
        val pickup = view.findViewById<CheckBox>(R.id.pickup).isChecked
        val address = view.findViewById<TextInputEditText>(R.id.pickup_address).text.toString()

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

    private fun getData(view: View): Product {
        val name = view.findViewById<TextInputEditText>(R.id.product_name).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.product_description).text.toString()
        val price = view.findViewById<TextInputEditText>(R.id.product_price).text.toString().toDouble()
        val stock = view.findViewById<TextInputEditText>(R.id.product_stock).text.toString().toInt()
        val deliver = view.findViewById<CheckBox>(R.id.deliver).isChecked
        val pickup = view.findViewById<CheckBox>(R.id.pickup).isChecked
        var address: String? = view.findViewById<TextInputEditText>(R.id.pickup_address).text.toString()
        val tradingMethod = if (deliver && pickup) BOTH
        else if (deliver) DELIVERY
        else PICKUP
        if (!pickup) address = null

        return Product(null, name, description, price, stock, tradingMethod, address, null)
    }

    private suspend fun uploadImage(token: String, image: File, id: Int) {
        val reqImage = image.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "image",
            "upload.jpg",
            reqImage
        )

        val reqId = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        Api.retrofitService.createProductImage(token, filePart, reqId)
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Permission needed")
                .setMessage("This permission is needed because of this and that")
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
            imageView.setImageURI(data?.data)
            data?.data?.let { uri ->
                activity?.contentResolver
                    ?.query(uri, null, null, null, null)
                    ?.use {
                    if (it.moveToFirst())
                        imagePath = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DATA))
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 100
        private const val REQUEST_CODE_STORAGE = 101
    }
}
