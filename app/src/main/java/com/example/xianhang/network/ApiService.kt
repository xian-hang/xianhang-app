package com.example.xianhang.network

import com.example.xianhang.model.*
import com.example.xianhang.network.response.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

//private const val BASE_URL = "http://192.168.38.131:8000/"
//private const val BASE_URL = "http://192.168.0.117:8000/"
const val BASE_URL = "https://xianhang.herokuapp.com/"
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

    @DELETE("user/delete/")
    suspend fun deleteUser(@Header(AUTH) authToken: String): DefaultResponse

    @GET("user/{id}/product/")
    suspend fun getUserProduct(@Path("id") userId: Int): UserProductsResponse

    @POST("product/create/")
    suspend fun sellProduct(@Header(AUTH) authToken: String, @Body data: Product): SellProductResponse

    @Multipart
    @POST("product/image/create/")
    suspend fun createProductImage(
        @Header(AUTH) authToken: String,
        @Part image: MultipartBody.Part,
        @Part("productId") productId: RequestBody
    ): DefaultResponse

    @GET("product/{id}/")
    suspend fun getProduct(@Path("id") id: Int): GetProductResponse
}
