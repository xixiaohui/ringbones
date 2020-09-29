package com.xxh.ringbones.utils

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log


class DownloadServise(val context: Context, val downloadUrl: String, val filename: String) :
    Service() {

    private var downloadId: Long? = 0

    private var binder: DownloadBinder? = null
    private var downLoadBroadcast: BroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        downloadId = DownloadManagerTest.download(context, downloadUrl, filename, true)
        registerBroadcast()
        return binder
    }


    override fun onCreate() {
        super.onCreate()
        binder = DownloadBinder()
    }

    private fun registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击 */
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)

        downLoadBroadcast = DownLoadBroadcast()
        registerReceiver(downLoadBroadcast, intentFilter)
    }

    private fun unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast)
            downLoadBroadcast = null
        }
    }

    class DownLoadBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    Log.i("DownLoadBroadcast", "DownloadManager.ACTION_DOWNLOAD_COMPLETE")
                }
                DownloadManager.ACTION_NOTIFICATION_CLICKED -> {

                }
            }
        }

    }

    interface OnProgressListener {
        /**
         * 下载进度
         * @param fraction 已下载/总大小
         */
        fun onProgress(fraction: Float)
    }

    class DownloadBinder : Binder() {
        /**
         * 返回当前服务的实例
         * @return
         */
        fun getService(context: Context, downloadUrl: String, filename: String): DownloadServise {
            return DownloadServise(context, downloadUrl, filename)
        }

    }
}