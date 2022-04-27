package com.example.xianhang.network

import com.example.xianhang.model.*
import com.example.xianhang.network.response.DefaultResponse
import com.example.xianhang.network.response.LoginResponse
import com.example.xianhang.network.response.ProfileResponse
import com.example.xianhang.network.response.RegisterResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

//private const val BASE_URL = "http://192.168.38.131:8000/"
private const val BASE_URL = "http://192.168.0.117:8000/"
private const val AUTH = "Authorization"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

interface ApiService {

    @POST("user/login/")
    suspend fun login(@Body user: LoginUser): LoginResponse

    @POST("user/create/")
    suspend fun register(@Body user: CreateUser): RegisterResponse

    @POST("user/logout/")
    suspend fun logout(@Header(AUTH) authToken: String): DefaultResponse

    @GET("user/profile/")
    suspend fun getProfile(@Header(AUTH) authToken: String): ProfileResponse

    @POST("user/edit/password/")
    suspend fun editPassword(@Header(AUTH) authToken: String, @Body data: EditPassword): DefaultResponse

    @POST("user/edit/")
    suspend fun editProfile(@Header(AUTH) authToken: String, @Body data: EditUser): DefaultResponse

    @POST("product/create/")
    suspend fun sellProduct(@Header(AUTH) authToken: String, @Body data: Product): DefaultResponse
}
