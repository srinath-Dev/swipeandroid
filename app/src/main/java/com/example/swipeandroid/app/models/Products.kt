package com.example.swipeandroid.app.models

import androidx.databinding.BaseObservable
import com.example.swipeandroid.api.Response
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Products (

    @SerializedName("image"        ) var image       : String? = null,
    @SerializedName("price"        ) var price       : Double?    = null,
    @SerializedName("product_name" ) var productName : String? = null,
    @SerializedName("product_type" ) var productType : String? = null,
    @SerializedName("tax"          ) var tax         : Double?    = null

)