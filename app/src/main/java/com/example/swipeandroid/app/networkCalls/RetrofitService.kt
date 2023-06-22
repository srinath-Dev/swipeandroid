package com.example.swipeandroid.app.networkCalls

import com.example.swipeandroid.app.models.Products
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart

interface RetrofitService {

    @GET("get")
    suspend fun getAllProducts() : Response<List<Products>>

    @Multipart()
    suspend fun uploadProduct() : Response<Products>


    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://app.getswipe.in/api/public/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}