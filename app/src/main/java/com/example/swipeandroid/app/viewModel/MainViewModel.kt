package com.example.swipeandroid.app.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.networkCalls.NetworkState
import com.example.swipeandroid.app.repo.ProductRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel constructor(private val productRepo: ProductRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val productList = MutableLiveData<List<Products>>()

    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getAllProducts() {
        Log.d("Thread Outside", Thread.currentThread().name)

        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = productRepo.getAllProducts()) {
                is NetworkState.Success -> {
                    productList.postValue(response.data!!)
                    Log.d("Products from api", response.data!!.toString())
                    loading.value = false
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {

                    } else {
                    }
                }
            }
        }
    }

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}