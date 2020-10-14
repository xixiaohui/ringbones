package com.xxh.ringbones.utils

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment

class MyIntentService(name: String? = "MyIntentService") : IntentService(name) {

    private val TAG = "MyIntentService"


    companion object {
        val URL = "url"
        val FILENAME = "filename"
        val STATUS = "status"
    }

    override fun onHandleIntent(intent: Intent?) {

        Log.i(TAG, "current thread: " + Thread.currentThread().name)

        val url = intent!!.getStringExtra(MyIntentService.URL)
        val filename = intent!!.getStringExtra(MyIntentService.FILENAME)

        Log.i(TAG, "url = $url")
        Log.i(TAG, "filename = $filename")

        //下载铃声
        DownloadManagerTest.doInBackground(this.baseContext,
            url,
            filename,
            true) { status, filename -> sendThreadStatus(status, filename) }
    }

    private fun sendThreadStatus(status: Int, filename: String) {
        var intent = Intent(SuperAwesomeCardFragment.ACTION_THREAD_STATUS)
        intent.putExtra(STATUS, status)
        intent.putExtra(FILENAME, filename)
        LocalBroadcastManager.getInstance(this.baseContext).sendBroadcast(intent)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var res = super.onStartCommand(intent, flags, startId)
        Log.i(TAG, "onStartCommand, startId:$startId")
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}