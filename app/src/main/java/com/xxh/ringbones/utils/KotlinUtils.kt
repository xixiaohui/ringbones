package com.xxh.ringbones.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        fun getRingtoneLocalPath(ringtone_url: String): String{

            val name = ringtone_url.split("/").last()
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).absolutePath
            return "$path${File.separator}$name"
        }

        //包含后缀.mp3
        fun getFileNameFromUrl(url: String): String? {
//        var filename = url.substring(url.lastIndexOf('/')+1);
            return url.substring(url.lastIndexOf("/") + 1)
        }
    }
}