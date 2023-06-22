package com.example.swipeandroid.app.activites

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.swipeandroid.R
import com.example.swipeandroid.api.AppServices
import com.example.swipeandroid.api.Response
import com.example.swipeandroid.api.ResponseListener
import com.example.swipeandroid.app.models.Products
import com.example.swipeandroid.app.utility.Utility
import com.example.swipeandroid.databinding.ActivityAddProductBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import dev.shreyaspatil.MaterialDialog.MaterialDialog


class AddProductActivity : AppCompatActivity(), ResponseListener {

    var imagePath = ""
    var productType =""
    lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val types = resources.getStringArray(R.array.Type)

        binding.imagePlaceHolder.setOnClickListener {
            onImageClick()
        }

        binding.remove.setOnClickListener {
            imagePath = ""
            binding.imagePlaceHolder.visibility = View.VISIBLE
            binding.selectedView.visibility = View.GONE
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.addBtn.setOnClickListener{

            uploadProduct()

        }


        if (binding.spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, types)
            binding.spinner.adapter = adapter

            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    (parent.getChildAt(0) as TextView).textSize = 16f
                    productType = types[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }


    }

    private fun onImageClick() {
        ImagePicker.with(this)
            .cropSquare()
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            ).start()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                val aProfilePath = data?.data!!
                imagePath = Utility.getRealPathFromURI(
                    aProfilePath,
                    this
                )!!
                Glide.with(this).load(imagePath).into(binding.imageview)
                binding.selectedView.visibility = View.VISIBLE
                binding.imagePlaceHolder.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun uploadProduct() {
        if (Utility.checkInternet(this)) {
            try {
                var  isImage = false
                isImage = !imagePath.isEmpty()

                binding.progressDialog.visibility = View.VISIBLE
                val model = Products()
                model.productName = binding.edtProductName.text.toString()
                model.image = imagePath
                model.productType = productType
                model.tax = (binding.edtProductTax.text.toString()).toDouble()
                model.price = (binding.edtPrice.text.toString()).toDouble()

                AppServices.addProduct(this, model, this,isImage)
            } catch (e: Exception) {
                binding.progressDialog.visibility = View.GONE
            }
        }
    }

    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
                when (r.requestType) {
                    AppServices.API.addProducts.hashCode() -> {
                        binding.progressDialog.visibility = View.GONE
                        if (r.responseStatus!!) {
                            //Toast.makeText(this,"Created",Toast.LENGTH_SHORT).show()
                            showMaterialDialog()
                        }

                    }
                }
            }
        }catch (exception:Exception){

        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showMaterialDialog() {
        val mDialog = MaterialDialog.Builder(this)
            .setTitle("Product Added")
            .setMessage("Product added successfully !")
            .setCancelable(false)
            .setAnimation("done.json")
            .setPositiveButton(
                "Ok"
            ) { dialogInterface, which ->
                dialogInterface.dismiss()
                onBackPressed()
            }

            .build()
        mDialog.show()
    }
}