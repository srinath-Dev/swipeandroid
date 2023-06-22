package com.example.swipeandroid.app.repo

import com.example.swipeandroid.app.networkCalls.RetrofitService
import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.networkCalls.NetworkState

class ProductRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAllProducts() : NetworkState<List<Products>> {
        val response = retrofitService.getAllProducts()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

}