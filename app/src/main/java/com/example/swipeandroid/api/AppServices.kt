package com.example.swipeandroid.api

import android.content.Context
import android.util.Log
import com.example.swipeandroid.BuildConfig.API_URL
import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.utility.Utility
import com.google.gson.Gson
import okhttp3.CookieJar
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Url
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import com.example.swipeandroid.api.Response
import com.example.swipeandroid.app.utility.Utility.getMimeType
import okhttp3.MultipartBody
import retrofit2.http.Part
import java.io.File

class AppServices {

    object API {
        fun constructUrl(urlKey: String): String {
            return String.format("%s%s", API_URL, urlKey)
        }

        const val getProducts = "get"
        const val addProducts = "add"

    }

    private interface ApiInterface {

        @Multipart
        @POST
        fun MULTIPART_POST(
            @Url url: String,
            @Part file: MultipartBody.Part,
            @Part("product_type") produtType: String,
            @Part("product_name") productName: String,
            @Part("price") price: Int,
            @Part("tax") tax: Int,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>

        @Multipart
        @POST
        fun MULTIPART_POST_WITHOUT_IMAGE(
            @Url url: String,
            @Part("product_type") produtType: String,
            @Part("product_name") productName: String,
            @Part("price") price: Int,
            @Part("tax") tax: Int,
            @HeaderMap headerMap: Map<String, String>
        ): Call<ResponseBody>


    }

    companion object {

        private var retrofit: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null

        private fun getClient(): Retrofit {

            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                    .cookieJar(CookieJar.NO_COOKIES)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            }

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(okHttpClient!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit as Retrofit
        }


        fun addProduct(
            c: Context,
            products: Products,
            listener: ResponseListener,
            isImage:Boolean
        ) {
            try {
                val mParam = HashMap<String, RequestBody>()
                val mUrl = API.constructUrl(API.addProducts)
                val apiService = getClient().create(ApiInterface::class.java)
                val mHashCode = API.addProducts


                if (isImage){
                    val file = File(products.image!!)
                    val requestBody =
                        RequestBody.create(
                            MediaType.parse(
                                getMimeType(products.image)
                            ), file
                        )
                    val body = MultipartBody.Part.createFormData("files", file.name, requestBody)
                    val call =
                        apiService.MULTIPART_POST(
                            mUrl,
                            body,
                            products.productType!!,
                            products.productName!!,
                            products.price!!.toInt(),
                            products.tax!!.toInt(), getHeaderPart(c)
                        )

                    initService(c, call, Response::class.java, mHashCode, listener)
                }else{
                    val call =
                        apiService.MULTIPART_POST_WITHOUT_IMAGE(
                            mUrl,
                            products.productType!!,
                            products.productName!!,
                            products.price!!.toInt(),
                            products.tax!!.toInt(), getHeaderPart(c)
                        )
                    initService(c, call, Response::class.java, mHashCode, listener)
                }

                Log.d("Param --> ", mParam.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getErrorMsg(t: Throwable, hash: Int): Response {
            val r = Response()
            r.responseStatus = false
            r.responseMessage = t.message!!
            r.requestType = hash

            Log.d("failure", t.message!!)

            return r
        }

        /**
         * Initiating the api call
         */

        private fun initService(
            c: Context,
            call: Call<ResponseBody>,
            mSerializable: Type,
            mHashCode: String,
            listener: ResponseListener
        ) {
            Log.d("URL --> ", call.request().url().toString())
            Log.d("METHOD --> ", call.request().method())
            Log.d("params -->", call.request().body().toString())
            call.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    listener.onResponse(getResponse(c, response, mSerializable, mHashCode))
                    val tx: Long = response.raw().sentRequestAtMillis()
                    val rx: Long = response.raw().receivedResponseAtMillis()
                    Log.e("response time :", " $mHashCode ${rx - tx} ms")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onResponse(getErrorMsg(t, mHashCode.hashCode()))
                }
            })
        }

        private fun getResponse(
            context: Context,
            mResponse: retrofit2.Response<ResponseBody>,
            mSerializable: Type,
            mHashCode: String
        ): Response? {
            val response: Response

            if (!Utility.isInternetAvailable(context)) {
                okHttpClient?.dispatcher()?.cancelAll()
                return null
            }

            if (mResponse.isSuccessful) {
                val body = mResponse.body()?.string()!!
                Log.d("success", body)
                response = Gson().fromJson(body, mSerializable)
            } else {
                try {
                    if (mResponse.code() == 401) { // Unauthorized User / Invalid Token
                        Log.e("unauthorized", mResponse.errorBody()!!.string())
                        Log.e("unauthorized url", mResponse.raw().request().url().toString())
                        okHttpClient?.dispatcher()?.cancelAll()
                        return null
                    } else {
                        val errorBody = mResponse.errorBody()?.string()!!
                        Log.e("fail", errorBody)
                        response = Gson().fromJson(errorBody, mSerializable)
                        response?.responseStatus = false
                    }
                } catch (e: Exception) {
                    try {

                        Log.d("Server Error", mResponse.errorBody().toString())
                    } catch (_: Exception) {

                    }
                    e.localizedMessage?.let { Log.e("Server Error", it) }
                    return null
                }
            }
            response?.requestType = mHashCode.hashCode()
            return response
        }

        fun getHeaderPart(c: Context): HashMap<String, String> {
            val mHeader = HashMap<String, String>()

            Log.d("Auth Header --> ", mHeader.toString())

            return mHeader
        }
    }
}