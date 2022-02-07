package com.example.customviewsudacityproject.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customviewsudacityproject.R
import com.example.customviewsudacityproject.Utils
import com.example.customviewsudacityproject.customViews.LoadingButtonUtils
import timber.log.Timber
import java.lang.Exception

class MainActivityViewModel: ViewModel() {

    private val utils = Utils()

    private val _downloadId = MutableLiveData<Long>()
    val downloadId: LiveData<Long> get() = _downloadId

    fun initiateDownload(URL:String, context: Context){
        utils.createNotificationChannel(context)
        try {
            _downloadId.value = utils.getDownloadRequest(URL,context)

        }catch (ex: Exception){
            Timber.e(ex.localizedMessage)
        }

    }
}