package com.example.customviewsudacityproject

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.customviewsudacityproject.customViews.LoadingButtonUtils
import com.example.customviewsudacityproject.customViews.LoadingIndicatorButton
import com.example.customviewsudacityproject.viewModels.MainActivityViewModel
import com.example.customviewsudacityproject.viewModels.MainActivityViewModelFactory
import com.example.customviewsudacityproject.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(),RadioGroup.OnCheckedChangeListener, View.OnClickListener {



    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var activityMainBinding: ActivityMainBinding

    private var URL: String = ""

    private val mainActivityViewModel: MainActivityViewModel by lazy{
        val mainActivityViewModelFactory = MainActivityViewModelFactory()
        ViewModelProvider(this,mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setSupportActionBar(activityMainBinding.toolbar)
        activityMainBinding.lifecycleOwner = this
        activityMainBinding.downloadBtn.setOnClickListener(this)
        activityMainBinding.contentMain.download.setOnCheckedChangeListener(this)
        activityMainBinding.viewModel = mainActivityViewModel

        registerReceiver(downloadBroadcastReceiver, IntentFilter().apply {
            addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        })
    }

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        when(p0?.checkedRadioButtonId){
            R.id.glide_radio_btn -> URL = getString(R.string.glide_link)
            R.id.project_radio_btn -> URL = getString(R.string.project_link)
            R.id.retrofit_radio_btn ->URL = getString(R.string.retrofit_link)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.download_btn -> {
                if (URL.isBlank()) {
                    Toast.makeText(this, getString(R.string.choose_lib), Toast.LENGTH_LONG).show()
                } else {
                    activityMainBinding.viewModel?.initiateDownload(URL,this)

                }
            }
        }
    }

    val downloadBroadcastReceiver = object : BroadcastReceiver() {

        private var intentId: Long = 0
        private val utils = Utils()


        override fun onReceive(context: Context?, intent: Intent?) {
            intentId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)!!

            when (intent.action) {
                "android.intent.action.DOWNLOAD_COMPLETE" -> {
                    utils.setDownloadStatus("Success")
                    utils.setDownloadFileName(utils.getDirectoryNameByURL(context!!,URL))
                    utils.showNotification(context!!)
                    activityMainBinding.downloadBtn.loadStatus = LoadingButtonUtils.ON_CLICK_DOWNLOAD
                    Timber.d("download complete")
                }

            }
        }
    }
}