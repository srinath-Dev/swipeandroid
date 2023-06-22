package com.example.swipeandroid.app.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.utility.ValidationUtil
import com.example.swipeandroid.databinding.AdapterProductBinding

class ProductAdapter : RecyclerView.Adapter<MainViewHolder>() {

    var productList = mutableListOf<Products>()

    fun setProducts(products: List<Products>) {
        this.productList = products.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterProductBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        val product = productList[position]
        if (ValidationUtil.validateMovie(product)) {
            holder.binding.imageview.visibility = View.VISIBLE
            holder.binding.swipeimageview.visibility = View.GONE
            Glide.with(holder.itemView.context).load(product.image).into(holder.binding.imageview)
        }else{
            holder.binding.imageview.visibility = View.GONE
            holder.binding.swipeimageview.visibility = View.VISIBLE
        }
        holder.binding.name.text = product.productName
        holder.binding.category.text = product.productType
        holder.binding.tax.text = "Sales tax - ₹ ${Math.round(product.tax!!)}"
        holder.binding.price.text = "Price - ₹ ${Math.round(product.tax!!+product.price!!)} (incl tax)"


    }

    fun filterList(filterlist: ArrayList<Products>) {
        // below line is to add our filtered
        // list in our course array list.
        this.productList = filterlist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

class MainViewHolder(val binding: AdapterProductBinding) : RecyclerView.ViewHolder(binding.root) {

}