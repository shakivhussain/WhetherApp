package com.shakib_mansoori.util

import android.content.Context
import android.net.ConnectivityManager

class CheckInternet {

    fun isConected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return wifiCon != null && wifiCon.isConnected || mobiCon != null && mobiCon.isConnected
    }

}