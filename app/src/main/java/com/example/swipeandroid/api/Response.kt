package com.example.swipeandroid.api

import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Response : Serializable, BaseObservable() {

    @SerializedName("success")
    var responseStatus:Boolean? = false

    @SerializedName("msg")
    var responseMessage = ""

    @SerializedName("is_request")
    var isRequest = false

    @SerializedName("next_link")
    var next_link: Boolean = false

//    @SerializedName("token")
//    var twilioToken: String? = null

    var requestType: Int? = null

}