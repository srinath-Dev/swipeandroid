package com.example.swipeandroid.app.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swipeandroid.app.adapter.ProductAdapter
import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.networkCalls.RetrofitService
import com.example.swipeandroid.app.repo.ProductRepository
import com.example.swipeandroid.app.viewModel.MainViewModel
import com.example.swipeandroid.app.viewModel.MyViewModelFactory
import com.example.swipeandroid.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    private val adapter = ProductAdapter()
    lateinit var binding: ActivityMainBinding
    lateinit var productLists: ArrayList<Products>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val i = Intent(this@MainActivity, AddProductActivity::class.java)
            startActivity(i)
        }

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = ProductRepository(retrofitService)
        binding.recyclerview.adapter = adapter
        //binding.search.onActionViewExpanded();
        binding.search.setIconifiedByDefault(false);
        binding.search.setQueryHint("Search Here (Product Name)");
        val editText = binding.search.findViewById<EditText>(R.id.search_src_text)
        editText.setTextColor(getResources().getColor(com.example.swipeandroid.R.color.black));
        editText.setHintTextColor(getResources().getColor(com.example.swipeandroid.R.color.black));
        if(!binding.search.isFocused()) {
            binding.search.clearFocus();
        }

        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository)).get(MainViewModel::class.java)


        viewModel.productList.observe(this) {
            adapter.setProducts(it)
            productLists = it as ArrayList<Products>
            binding.recyclerview.visibility = View.VISIBLE
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        })



        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg)
                return false
            }
        })


    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<Products> = ArrayList()

        for (item in productLists) {
            if (item.productName!!.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
          //  Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            adapter.filterList(filteredlist)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllProducts()
    }
}