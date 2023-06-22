package com.example.swipeandroid.app.utility

import com.example.swipeandroid.app.models.Products

object ValidationUtil {

    fun validateMovie(product:Products) : Boolean {
        if (product.image!!.isNotEmpty()) {
            return true
        }
        return false
    }
}