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

    @GET("user/{id}/")
    suspend fun getUser(@Header(AUTH) authToken: String, @Path("id") id: Int): ProfileResponse

    @GET("user/{id}/product/")
    suspend fun getUserProduct(@Path("id") userId: Int): ProductsResponse

    @POST("product/create/")
    suspend fun createProduct(@Header(AUTH) authToken: String, @Body data: Product): SellProductResponse

    @POST("product/{id}/edit/")
    suspend fun editProduct(
        @Header(AUTH) authToken: String,
        @Body data: Product,
        @Path("id") id: Int
    ): DefaultResponse

    @Multipart
    @POST("product/image/create/")
    suspend fun createProductImage(
        @Header(AUTH) authToken: String,
        @Part image: MultipartBody.Part,
        @Part("productId") productId: RequestBody
    ): DefaultResponse

    @DELETE("product/image/{id}/delete/")
    suspend fun deleteProductImage(
        @Header(AUTH) authToken: String,
        @Path("id") id: Int
    ): DefaultResponse

    @GET("product/{id}/")
    suspend fun getProduct(@Header(AUTH) authToken: String, @Path("id") id: Int): GetProductResponse

    @GET("product/all/")
    suspend fun getAllProducts(@Header(AUTH) authToken: String): ProductsResponse

    @DELETE("product/image/{id}/delete/")
    suspend fun deleteProduct(@Path("id") id: Int, @Header(AUTH) authToken: String): DefaultResponse

    @POST("collection/create/")
    suspend fun collect(@Header(AUTH) authToken: String, @Body productId: Int): DefaultResponse

    @DELETE("collection/{id}/delete/")
    suspend fun uncollect(@Header(AUTH) authToken: String, @Path("id") collectionId: Int): DefaultResponse

    @POST("order/create/")
    suspend fun createOrder(@Header(AUTH) authToken: String, @Body order: OrderRequest): DefaultResponse

    @GET("order/buying/")
    suspend fun getBoughtOrders(@Header(AUTH) authToken: String): OrdersResponse

    @GET("order/selling/")
    suspend fun getSoldOrders(@Header(AUTH) authToken: String): OrdersResponse
}
