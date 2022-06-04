package com.example.xianhang.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.xianhang.R
import com.example.xianhang.adapter.PRODUCT
import com.example.xianhang.databinding.FragmentReportBinding
import com.example.xianhang.login.LoginActivity
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.*
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.DefaultResponse
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.lang.Exception

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private var imagePath: String = ""
    private var upload = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            submit()
        }

        binding.image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun submit() {
        val sharedPreferences = activity?.getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences?.getString(TOKEN, null)

        if (token == null) {
            Toast.makeText(context, "Please login", Toast.LENGTH_LONG).show()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            return
        }
        if (!checkData()) return

        val content = binding.content.text.toString()
        val reportUserId = arguments?.getInt(ID)
        println("report")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val resp = Api.retrofitService.createReport(token, ReportRequest(content, reportUserId!!))
                println("resp.message = ${resp.message}")
                if (resOk(context, resp)) {
                    val res  = uploadImage(token, File(imagePath), resp.reportId!!)
                    if (resOk(context, res)) {
                        Toast.makeText(context, "Report success", Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                binding.progressBar.visibility = View.GONE
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun checkData(): Boolean {
        val text = binding.content.text
        if (text.isEmpty()) {
            Toast.makeText(context, "Please fill in the blank", Toast.LENGTH_LONG).show()
            return false
        }
        if (!upload) {
            Toast.makeText(context, "Please upload image", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private suspend fun uploadImage(token: String, image: File, id: Int): DefaultResponse {
        val reqImage = image.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "image",
            "upload.jpg",
            reqImage
        )

        val reqId = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return Api.retrofitService.createReportImage(token, filePart, reqId)
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
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
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