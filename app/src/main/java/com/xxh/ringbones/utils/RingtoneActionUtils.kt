package com.xxh.ringbones.utils

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import java.io.File


class RingtoneActionUtils {

    companion object {
        val TAG = "RingtoneAction"
        val SELECT: String = "Select"
        val UNSELECT: String = "unSelect"


        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setMyRingtoneWithFileName(activity: Activity, filename: String) {
            val path = combine(filename)
            setMyRingtone(activity, path)
        }

        /**
         * 读取到的是音频文件的路径，
         * 需要先将音乐文件插入到多媒体库
         */
        //设置--铃声的具体方法
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setMyRingtone(activity: Activity, path: String) {
            val sdfile: File = File(path)

            val external_content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val internal_content_uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

            val current_uri = external_content_uri

            val projection = arrayOf(MediaStore.Audio.Media._ID)
            val selectionClause = MediaStore.Audio.Media.DATA + " = ? "
            val selectionArgs = arrayOf<String>(sdfile.absolutePath)
            val cursor: Cursor? = activity.baseContext.contentResolver.query(
                current_uri,
                projection,
                selectionClause,
                selectionArgs,
                null
            )
            val insertedUri: Uri
            insertedUri = if (cursor == null || cursor.count < 1) {
                // not exist, insert into MediaStore
                val cv = ContentValues()
                cv.put(MediaStore.Audio.Media.DATA, sdfile.absolutePath)
                cv.put(MediaStore.MediaColumns.TITLE, sdfile.name)
                cv.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                cv.put(MediaStore.Audio.Media.SIZE, sdfile.length())
                cv.put(MediaStore.Audio.Media.ARTIST, R.string.app_name)
                cv.put(MediaStore.Audio.Media.IS_RINGTONE, true)
                cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                cv.put(MediaStore.Audio.Media.IS_ALARM, false)
                cv.put(MediaStore.Audio.Media.IS_MUSIC, false)

                activity.baseContext.contentResolver.insert(current_uri, cv)!!
            } else {
                // already exist
                cursor.moveToNext()
                val id = cursor.getLong(0)
                ContentUris.withAppendedId(current_uri, id)
            }
//            RingtoneUtils.addCustomExternalRingtone(
//                activity.baseContext,
//                insertedUri,
//                RingtoneManager.TYPE_RINGTONE
//            )
            RingtoneManager.setActualDefaultRingtoneUri(
                activity.baseContext,
                RingtoneManager.TYPE_RINGTONE,
                insertedUri
            )

            Snackbar.make(SuperAwesomeCardFragment.rootView,
                "The ringtone is set successfully!",
                Snackbar.LENGTH_LONG).show()
        }

        fun setRingtongByID(context: Context, id: Long, internal: Boolean) {
            val external_content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val internal_content_uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

            var current_uri = external_content_uri
            if (internal) {
                current_uri = internal_content_uri
            }
            val insertedUri = ContentUris.withAppendedId(current_uri, id)

            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                insertedUri
            )
        }

        //设置--提示音的具体实现方法
        fun setMyNotification(activity: Activity, path: String?) {
            val sdfile = File(path)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, sdfile.absolutePath)
            values.put(MediaStore.MediaColumns.TITLE, sdfile.name)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
            values.put(MediaStore.Audio.Media.IS_ALARM, false)
            values.put(MediaStore.Audio.Media.IS_MUSIC, false)
            val uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.absolutePath)
            val newUri: Uri? = activity.contentResolver.insert(uri, values)
            RingtoneManager.setActualDefaultRingtoneUri(
                activity,
                RingtoneManager.TYPE_NOTIFICATION,
                newUri
            )
            Toast.makeText(activity, "设置提示音成功！", Toast.LENGTH_SHORT).show()
        }

        //设置--闹铃音的具体实现方法
        fun setMyAlarm(activity: Activity, path: String?) {
            val sdfile = File(path)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, sdfile.absolutePath)
            values.put(MediaStore.MediaColumns.TITLE, sdfile.name)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            values.put(MediaStore.Audio.Media.IS_ALARM, true)
            values.put(MediaStore.Audio.Media.IS_MUSIC, false)
            val uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.absolutePath)
            val newUri: Uri? = activity.contentResolver.insert(uri, values)
            RingtoneManager.setActualDefaultRingtoneUri(
                activity,
                RingtoneManager.TYPE_ALARM,
                newUri
            )
            Toast.makeText(activity, "设置闹钟铃声成功！", Toast.LENGTH_SHORT).show()
        }


        /**
         * 1、Environment.getDataDirectory() = /data
        这个方法是获取内部存储的根路径
        2、getFilesDir().getAbsolutePath() = /data/user/0/packname/files
        这个方法是获取某个应用在内部存储中的files路径
        3、getCacheDir().getAbsolutePath() = /data/user/0/packname/cache
        这个方法是获取某个应用在内部存储中的cache路径
        4、getDir(“myFile”, MODE_PRIVATE).getAbsolutePath() = /data/user/0/packname/app_myFile
        这个方法是获取某个应用在内部存储中的自定义路径
        方法2,3,4的路径中都带有包名，说明他们是属于某个应用
        …………………………………………………………………………………………
        5、Environment.getExternalStorageDirectory().getAbsolutePath() = /storage/emulated/0
        这个方法是获取外部存储的根路径
        6、Environment.getExternalStoragePublicDirectory(“”).getAbsolutePath() = /storage/emulated/0
        这个方法是获取外部存储的根路径
        7、getExternalFilesDir(“”).getAbsolutePath() = /storage/emulated/0/Android/data/packname/files
        这个方法是获取某个应用在外部存储中的files路径
        8、getExternalCacheDir().getAbsolutePath() = /storage/emulated/0/Android/data/packname/cache
        这个方法是获取某个应用在外部存储中的cache路径
         */


        fun getAllRingtonesAvailable(activity: Activity) {
            var cursor = RingtoneManager(activity.applicationContext).cursor
            while (cursor.moveToNext()) {

//                var urls = cursor.getString(URI_COLUMN_INDEX)
//                Log.i(TAG, urls)

//                var ids = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
//                Log.i(TAG, ids)

                var titles = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                Log.i(TAG, titles)

//                val uri = getUri(activity,titles)
//                Log.i(TAG,uri!!.path)

            }
        }

        fun getAllRingtoneList(context: Context): Array<Uri?>? {
            val ringtoneMgr = RingtoneManager(context)
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE)
            val alarmsCursor = ringtoneMgr.cursor
            val alarmsCount = alarmsCursor.count
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                return null
            }
            val alarms = arrayOfNulls<Uri>(alarmsCount)
            while (!alarmsCursor.isAfterLast && alarmsCursor.moveToNext()) {
                val currentPosition = alarmsCursor.position
                alarms[currentPosition] = ringtoneMgr.getRingtoneUri(currentPosition)
            }
            alarmsCursor.close()
            return alarms
        }

        fun getAllRingtoneMap(context: Context): Map<String, String>? {
            val manager = RingtoneManager(context)
            manager.setType(RingtoneManager.TYPE_RINGTONE)
            val cursor = manager.cursor
            val list: MutableMap<String, String> = HashMap()
            while (cursor.moveToNext()) {
                val notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val notificationUri =
                    cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
                        RingtoneManager.ID_COLUMN_INDEX
                    )
                list[notificationTitle] = notificationUri
            }
            return list
        }


        private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(context, contentUri, proj, null, null, null)
            val cursor: Cursor? = loader.loadInBackground()
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result = cursor.getString(column_index)
            cursor.close()
            return result
        }


        fun getImageFilePathFromUri(context: Context, uri: Uri?): String? {
            if (null == uri) return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
                data = uri.path
            } else if (ContentResolver.SCHEME_CONTENT == scheme) {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.Images.ImageColumns.DATA),
                    null,
                    null,
                    null
                )
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }

        /**
         * 获取当前铃声的文件地址
         */
        fun getCurrentRingtoneFilePathFromUri(context: Context): String? {
            var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            var path = getMediaFilePathFromUri(context, uri)
            return path
        }

        fun getMediaFilePathFromUri(context: Context, uri: Uri?): String? {
            if (null == uri) return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
                data = uri.path
            } else if (ContentResolver.SCHEME_CONTENT == scheme) {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.Audio.Media.DATA),
                    null,
                    null,
                    null
                )
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }

        /**
         * 播放当前音乐
         */
        fun playDefaultRingtone(activity: Activity) {
            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            val r = RingtoneManager.getRingtone(activity.baseContext, ringtone)
            r.play()
        }

        /**
         * 判断文件是否存在
         */
        fun fileIsExists(strFile: String?): Boolean {
            try {
                val f = File(strFile)
                if (!f.exists()) {
                    return false
                }
            } catch (e: java.lang.Exception) {
                return false
            }
            return true
        }

        fun fileIsExistsInRingtonesHolder(fileName: String): Boolean {
            val file = File(getExternalStorageDirectory(),
                File.separator + Environment.DIRECTORY_RINGTONES + File.separator)
            if (!file.exists()) {
                file.mkdirs()
            }
            val root = getExternalStorageDirectory()
            val path =
                "$root" + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + fileName
            return fileIsExists(path)
        }

        /**
         * 获取音频文件路径
         */
        fun combine(filename: String = "first"): String {
//        return "storage/emulated/0/MIUI/.ringtone/$filename.mp3"
            var path = ""
            var sdDir: File? = null
            val sdCardExist =
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED // 判断sd卡是否存在

            if (sdCardExist) {
                sdDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) // 获取根目录
                path = sdDir.path + File.separator + filename
            }

            return path
        }

        const val FLAG_SUCCESS = 1 //创建成功
        const val FLAG_EXISTS = 2 //已存在
        const val FLAG_FAILED = 3 //创建失败

        /**
         * 创建 文件夹
         * @param dirPath 文件夹路径
         * @return 结果码
         */
        fun createDir(dirPath: String) {
            val file = File(dirPath)
            if (!file.exists()){
                file.mkdirs()
            }
        }

        fun checkPermission(activity: Activity): Boolean {
            val permission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            return permission
        }

        val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }
        }

        fun getDownloadRingtoneFileList(context: Context): Array<File> {
            val ringtoneHolder: File? =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
            val ringtontlist: Array<File> = ringtoneHolder!!.listFiles()
            return ringtontlist
        }

        fun getDownloadRingtoneList(context: Context): MutableList<String> {
            val ringtontList: Array<File> = getDownloadRingtoneFileList(context)
            val names = mutableListOf<String>()
            ringtontList.forEach {
                val value = "https://www.tonesmp3.com/ringtones/"
                val name = it.name
                names.add("$value$name")
            }
            return names
        }

        fun getRingtoneLocalPath(ringtone_url: String): String {

            val name = ringtone_url.split("/").last()
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).absolutePath
            return "$path${File.separator}$name"
        }

        //包含后缀.mp3
        fun getFileNameFromUrl(url: String): String? {
//        var filename = url.substring(url.lastIndexOf('/')+1);
            return url.substring(url.lastIndexOf("/") + 1)
        }

        fun isRingtoneInSdcard(context: Context, ringtone: Ringtone): Boolean {
            val names = getDownloadRingtoneList(context)
            val url = ringtone.url
            return url in names
        }

    }

//    fun onSuccess(i: Int, json: String?) {
//        Log.i("Channel", "onSuccess")
//        val message: Message = Message.obtain()
//        message.what = 0
//        val bundle = Bundle()
//        bundle.putString("json", json)
//        message.setData(bundle)
//        myHandler.sendMessage(message)
//    }
//
//    //这里处理传过来的数据
//    private val myHandler: Handler = object : Handler() {
//        fun handleMessage(msg: Message) {
//            when (msg.what) {
//                0 -> {
//                    val bundle: Bundle = msg.getData()
//                    System.out.println(bundle.getString("json", ""))
//                }
//                else -> {
//                }
//            }
//        }
//    }

}