package com.example.customviewsudacityproject.customViews

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


sealed class LoadingButtonUtils : Serializable {

    object ON_CLICK_DOWNLOAD: LoadingButtonUtils()

    object DOWNLOAD_IN_PROGRESS: LoadingButtonUtils()

    object DOWNLOAD_FINISHED: LoadingButtonUtils()

    object DOWNLOAD_ERROR: LoadingButtonUtils()
}