package com.xxh.ringbones.utils

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class DownloadManagerTest {


    companion object {
        private val TAG = "DownloadManagerTest"

        private var downloadId: Long? = 0
        private var downloadUrl: String? = ""
        fun download(
            context: Context,
            downloadUrl: String,
            fileName: String
        ): Long? {

            //创建下载任务，downloadUrl就是下载链接
            var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(downloadUrl))

            request.apply {
                setTitle(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            }

            //指定下载路径和下载文件名
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_RINGTONES, fileName
            )

            //获取下载管理器
            var downloadManager: DownloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            //将下载任务加入下载队列，否则不会进行下载
            downloadId = downloadManager.enqueue(request)
            return downloadId
        }


        fun doInBackground(context: Context,
                           downloadUrl: String,
                           fileName: String,
                           title: String,
                           sendBroadcast: (Int,String,String) -> Unit): Boolean? {
            var flag = true
            var downloading = true
            return try {
                val mManager: DownloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val idDownLoad:Long? = download(context,downloadUrl, fileName)
                var query: DownloadManager.Query? = null
                query = DownloadManager.Query()
                var c: Cursor? = null
                if (query != null) {
                    query.setFilterByStatus(DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_SUCCESSFUL or DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PENDING)
                } else {
                    return flag
                }
                while (downloading) {
                    c = mManager.query(query)
                    if (c.moveToFirst()) {
                        Log.i("FLAG", "Downloading")
                        val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            Log.i("FLAG", "done")
                            downloading = false
                            flag = true
                            sendBroadcast(DownloadManager.STATUS_SUCCESSFUL,fileName,title)
                            break
                        }
                        if (status == DownloadManager.STATUS_FAILED) {
                            Log.i("FLAG", "Fail")
                            downloading = false
                            sendBroadcast(DownloadManager.STATUS_FAILED,fileName,title)
                            flag = false
                            break
                        }
                    }
                }
                flag
            } catch (e: java.lang.Exception) {
                flag = false
                flag
            }
        }
    }

}