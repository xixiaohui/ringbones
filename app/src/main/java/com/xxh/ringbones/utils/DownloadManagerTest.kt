package com.xxh.ringbones.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

class DownloadManagerTest {


    companion object {
        private val TAG = "DownloadManagerTest"

        fun download(context: Context, downloadUrl: String, fileName: String) {

            //创建下载任务，downloadUrl就是下载链接
            var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(downloadUrl))

            request.apply {
                setTitle(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            }

            //指定下载路径和下载文件名
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_RINGTONES, "$fileName.mp3"
            )

            //获取下载管理器
            var downloadManager: DownloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            //将下载任务加入下载队列，否则不会进行下载
            downloadManager.enqueue(request)
        }
    }
}