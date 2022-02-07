package com.example.customviewsudacityproject

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import timber.log.Timber

class Utils {

    private var fileDownloadStatus: String = "Fail"
    private var fileName: String = ""


    fun setDownloadStatus(status: String){ fileDownloadStatus = status }

     fun setDownloadFileName(fileName: String){this.fileName = fileName}

     fun getDownloadRequest(URL: String, context: Context): Long {
         val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DownloadManager.Request(Uri.parse(URL))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(context.getString(R.string.app_name))
                    .setDescription(context.getString(R.string.app_description))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setRequiresCharging(false)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,getDirectoryNameByURL(context, URL))
            } else {
                DownloadManager.Request(Uri.parse(URL))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(context.getString(R.string.app_name))
                    .setDescription(context.getString(R.string.app_description))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,getDirectoryNameByURL(context, URL))
            }
         val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
         return downloadManager.enqueue(request) // enqueue puts the download request in queue
     }

    fun getDirectoryNameByURL(context: Context,URL: String): String {
        var dirName = ""
        when(URL){
            context.getString(R.string.glide_link) -> dirName = context.getString(R.string.glide_file_name)
            context.getString(R.string.project_link) -> dirName = context.getString(R.string.project_file_name)
            context.getString(R.string.retrofit_link) -> dirName = context.getString(R.string.retrofit_file_name)
        }

        return dirName
    }

    fun createNotificationChannel(context: Context){
        val channelName = context.getString(R.string.channel_name)
        val channelId = context.getString(R.string.channel_id)
        val notificationDescription = context.getString(R.string.notification_description)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance =  NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId,channelName,importance).apply {
                description = notificationDescription
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


   private fun getPendingIntent(context: Context): PendingIntent{
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(context.getString(R.string.file_name_key),fileName)
            putExtra(context.getString(R.string.status_key),fileDownloadStatus)
        }
        return PendingIntent.getActivity(context, context.getString(R.string.channel_id).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun getNotificationBuilder(context: Context): Builder{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Builder(context,context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_baseline_assistant_24)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_description))
                .setPriority(PRIORITY_DEFAULT)
                .setCategory(CATEGORY_EVENT)
                .setVisibility(VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_baseline_assistant_24,context.getString(R.string.check_status),getPendingIntent(context))
                .setAutoCancel(true)
        } else {
            Builder(context,context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_baseline_assistant_24)
                .setContentTitle(R.string.notification_title.toString())
                .setContentText(R.string.notification_description.toString())
                .setCategory(CATEGORY_EVENT)
                .setVisibility(VISIBILITY_PUBLIC)
                .setContentIntent(getPendingIntent(context))
                .setAutoCancel(true)

        }

    }

    fun showNotification(context: Context){
        NotificationManagerCompat.from(context).notify(context.getString(R.string.channel_id).toInt(),getNotificationBuilder(context).build())
    }

}