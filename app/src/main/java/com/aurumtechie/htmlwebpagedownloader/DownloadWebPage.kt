@file:Suppress("BlockingMethodInNonBlockingContext")

package com.aurumtechie.htmlwebpagedownloader

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL

/** Attempts downloading the web page located at the given web address. Prompts the user with the result.
 * @author Neeyat Lotlikar
 * @param urlString String web address of the web page to be downloaded
 * @param progressTextView TextView displaying text status of the download
 * @param circularProgress ProgressBar displayed while the download is in progress
 * @param fileName String name of the file with a default value */
@JvmOverloads // Optimizes Kotlin functions having default parameter values which are called in Java
fun downloadWebPage(
        urlString: String, progressTextView: TextView, circularProgress: ProgressBar,
        fileName: String = "/storage/emulated/0/Download/download_${System.currentTimeMillis()}.html"
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Create a URL object
            val url = URL(urlString)
            // Create a buffered reader object using the url object
            val reader = url.openStream().bufferedReader()

            // Enter filename in which you want to download
            val downloadFile = File(fileName).also { it.createNewFile() }
            // Create a buffered writer object for the file
            val writer = FileWriter(downloadFile).buffered()

            // read and write each line from the stream till the end
            var line: String
            while (reader.readLine().also { line = it?.toString() ?: "" } != null)
                writer.write(line)

            // Close all open streams
            reader.close()
            writer.close()

            // Update UI for download is successful
            withContext(Dispatchers.Main) {
                circularProgress.visibility = View.GONE
                progressTextView.apply {
                    text = context.getString(R.string.download_successful)
                    setTextColor(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                context.getColor(android.R.color.holo_green_light)
                            else // For APIs below 23
                                context.resources.getColor(android.R.color.holo_green_light)
                    )
                }

                // Show a message for when the app is successfully downloaded
                // Also provide an action to view the downloaded file
                Snackbar.make(
                        progressTextView,
                        R.string.file_downloaded_successfully,
                        Snackbar.LENGTH_LONG
                ).setAction(R.string.view) {
                    it.context.startActivity(Intent.createChooser(
                            Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(downloadFile.toUri(),
                                        "text/plain")
                            },
                            progressTextView.context
                                    .getString(R.string.open_using)
                    ))
                }.show()
            }

        } catch (e: Exception) {
            // Update UI for download has failed
            withContext(Dispatchers.Main) {
                when (e) {
                    is MalformedURLException ->
                        Snackbar.make(progressTextView, R.string.malformed_url, Snackbar.LENGTH_SHORT).show()
                    else ->
                        Snackbar.make(progressTextView, R.string.download_failed, Snackbar.LENGTH_SHORT).show()
                }

                // File is downloaded incompletely in case of a download fail. Delete this file.
                val incompleteFile = File(fileName)
                if (incompleteFile.exists()) incompleteFile.delete()

                circularProgress.visibility = View.GONE
                progressTextView.apply {
                    text = context.getString(R.string.download_failed)
                    setTextColor(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                context.getColor(android.R.color.holo_red_light)
                            else context.resources.getColor(android.R.color.holo_red_light)
                    )
                }

                e.printStackTrace()
            }
        }
    }
}