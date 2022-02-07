package com.example.customviewsudacityproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.customviewsudacityproject.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail)
        setSupportActionBar(detailBinding.toolbar)

        detailBinding.contentDetail.fileName.text = intent.getStringExtra(getString(R.string.file_name_key))

        val status = intent.getStringExtra(getString(R.string.status_key))
        if (status.equals("Fail")) {
            detailBinding.contentDetail.status.text = status
            detailBinding.contentDetail.status.setTextColor(Color.RED)
        }else{
            detailBinding.contentDetail.status.text = status
        }

        detailBinding.contentDetail.okButton.setOnClickListener {
            finish()
        }
    }
}