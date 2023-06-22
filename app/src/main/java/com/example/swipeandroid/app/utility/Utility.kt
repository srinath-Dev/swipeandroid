package com.example.swipeandroid.app.utility

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast

object Utility {
    @SuppressLint("MissingPermission")
    fun isInternetAvailable(context: Context?): Boolean {
        try {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }

    fun getMimeType(url: String?): String {
        var type = ""
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        }
        return type
    }

    fun getRealPathFromURI(contentURI: Uri, context: Context): String? {
        val result: String
        val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun checkInternet(context: Context): Boolean {
        return if (isInternetAvailable(context)) true
        else {
            Toast.makeText(context,"Please check internet connection",Toast.LENGTH_SHORT).show()
            false
        }
    }

}