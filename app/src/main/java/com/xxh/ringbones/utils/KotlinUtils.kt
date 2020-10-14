package com.xxh.ringbones.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.models.RingtoneViewModel
import java.io.File

class KotlinUtils {


    companion object {
        val SELECT: String = "Select"
        val UNSELECT: String = "unSelect"

        /**
         * 检测权限
         */
        fun check(activity: Activity) {

            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                );
            }
        }

        fun getDownloadRingtoneFileList(context: Context): Array<File> {
            val ringtoneHolder: File? =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
            val ringtontlist: Array<File> = ringtoneHolder!!.listFiles()
//            for (file in ringtontlist) {
//                val name = file.name
//                val path = file.absolutePath
//                Log.i("getDownloadRingtoneList","$name  $path")
//            }

            return ringtontlist
        }


        fun getDownloadRingtoneList(context: Context): MutableList<String>{
            val ringtontList: Array<File> = getDownloadRingtoneFileList(context)
            val names = mutableListOf<String>()
            ringtontList.forEach {
                val value = "https://www.tonesmp3.com/ringtones/"
                val name = it.name
                names.add("$value$name")
            }
            return names
        }

    }
}