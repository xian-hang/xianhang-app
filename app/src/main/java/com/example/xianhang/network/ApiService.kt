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

const val BASE_URL = "https://xianhang.ga/api/"
const val SCHEME = "https"

const val AUTH = "Authorization"

val moshi = Moshi.Builder()
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

    @POST("user/forgot/password/")
    suspend fun forgotPassword(@Body studentId: StudentId): DefaultResponse

    @POST("user/search/")
    suspend fun searchUser(@Header(AUTH) authToken: String, @Body data: SearchRequest): UsersResponse

    @POST("user/resent/")
    suspend fun resend(@Body studentId: StudentId): DefaultResponse

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
    suspend fun getUserProduct(@Header(AUTH) authToken: String?, @Path("id") userId: Int): ProductsResponse

    @POST("product/search/")
    suspend fun searchProduct(@Header(AUTH) authToken: String, @Body data: SearchRequest): ProductsResponse

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
    ): ImageResponse

    @DELETE("product/image/{id}/delete/")
    suspend fun deleteProductImage(
        @Header(AUTH) authToken: String,
        @Path("id") id: Int
    ): DefaultResponse

    @GET("product/{id}/")
    suspend fun getProduct(@Header(AUTH) authToken: String, @Path("id") id: Int): GetProductResponse

    @GET("product/all/")
    suspend fun getAllProducts(@Header(AUTH) authToken: String): ProductsResponse

    @DELETE("product/{id}/delete/")
    suspend fun deleteProduct(@Path("id") id: Int, @Header(AUTH) authToken: String): DefaultResponse

    @GET("product/feed/")
    suspend fun getFeeds(@Header(AUTH) authToken: String): ProductsResponse

    @POST("user/like/create/")
    suspend fun like(@Header(AUTH) authToken: String, @Body userId: UserId): LikeResponse

    @DELETE("user/like/{id}/delete/")
    suspend fun unlike(@Header(AUTH) authToken: String, @Path("id") id: Int): DefaultResponse

    @POST("followership/create/")
    suspend fun follow(@Header(AUTH) authToken: String, @Body userId: UserId): FollowResponse

    @DELETE("followership/{id}/delete/")
    suspend fun unfollow(@Header(AUTH) authToken: String, @Path("id") id: Int): DefaultResponse

    @POST("collection/create/")
    suspend fun collect(@Header(AUTH) authToken: String, @Body productId: ProductId): CollectResponse

    @DELETE("collection/{id}/delete/")
    suspend fun uncollect(@Header(AUTH) authToken: String, @Path("id") collectionId: Int): DefaultResponse

    @POST("order/create/")
    suspend fun createOrder(@Header(AUTH) authToken: String, @Body order: OrderRequest): OrderIdResponse

    @GET("order/buying/")
    suspend fun getBoughtOrders(@Header(AUTH) authToken: String): OrdersResponse

    @POST("order/buying/status/")
    suspend fun getStatusOrders(
        @Header(AUTH) authToken: String,
        @Body status: StatusId
    ): OrdersResponse

    @GET("order/selling/")
    suspend fun getSoldOrders(@Header(AUTH) authToken: String): OrdersResponse

    @GET("order/{id}/")
    suspend fun getOrder(@Header(AUTH) authToken: String, @Path("id") id: Int): OrderResponse

    @POST("order/{id}/edit/status/")
    suspend fun editOrderStatus(
        @Header(AUTH) authToken: String,
        @Path("id") id: Int,
        @Body data: OrderStatusRequest
    ): DefaultResponse

    @POST("order/{id}/edit/postage/")
    suspend fun setPostage(
        @Header(AUTH) authToken: String,
        @Path("id") id: Int,
        @Body data: PostageReqeust
    ): DefaultResponse

    @GET("collection/list/")
    suspend fun getCollections(@Header(AUTH) authToken: String): ProductsResponse

    @POST("report/create/")
    suspend fun createReport(@Header(AUTH) authToken: String, @Body data: ReportRequest): ReportResponse

    @Multipart
    @POST("report/image/create/")
    suspend fun createReportImage(
        @Header(AUTH) authToken: String,
        @Part image: MultipartBody.Part,
        @Part("reportId") reportId: RequestBody
    ): DefaultResponse

    @GET("report/notice/")
    suspend fun getNotification(@Header(AUTH) authToken: String): NoticeResponse

    @GET("chat/list/")
    suspend fun getChatList(@Header(AUTH) authToken: String): ChatResponse
}