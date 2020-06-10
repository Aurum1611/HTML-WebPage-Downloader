package com.aurumtechie.htmlwebpagedownloader

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import org.intellij.lang.annotations.Language

object ConnectivityHelper {

    @Language("RegExp")
    const val WEB_URL_REGEX = "^((https?|ftp|smtp)://)?(www.)?[a-z0-9]+\\.[a-z]+(/[a-zA-Z0-9#]+/?)*$"

    @Language("RegExp") // RegEX to check whether a protocol has been mentioned in the web address or not
    const val URL_PROTOCOL_CHECK_REGEX = "^((https?|ftp|smtp)://)(www.)?[a-z0-9]+\\.[a-z]+(/[a-zA-Z0-9#]+/?)*$"

    @kotlin.jvm.JvmStatic
    fun isConnectedToNetwork(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.isConnectedOrConnecting &&
                        (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
            }
        } else {
            val network = connectivityManager.activeNetwork
            if (network != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                return capabilities?.let {
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI
                    )
                } ?: false
            }
        }
        return false
    }
}