package com.xxh.ringbones.utils

import android.app.Activity
import android.content.*
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.loader.content.CursorLoader
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import java.io.*
import java.nio.channels.FileChannel
import java.nio.file.Files


class RingtoneAction {

    companion object {
        val TAG = "RingtoneAction"



        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setMyRingtoneWithFileName(activity: Activity, filename: String){
            val path = combine(filename)
            setMyRingtone(activity,path)
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

            Snackbar.make(SuperAwesomeCardFragment.rootView,"The ringtone is set successfully!",Snackbar.LENGTH_LONG).show()
        }

        fun setRingtongByID(context: Context, id: Long, internal: Boolean){
            val external_content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val internal_content_uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

            var  current_uri = external_content_uri
            if(internal){
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
//        fun getExternalHolder(context: Context) {
//
//            val path = Environment.getExternalStorageDirectory().absolutePath
//            val path2 = Environment.getDataDirectory().absolutePath
//            val path3 = context.getExternalFilesDir("")!!.absolutePath
//            val path4 = context.externalCacheDir!!.absolutePath
//            val path5 = Environment.getExternalStoragePublicDirectory("").absolutePath
//
//            val path6 = context.filesDir.absolutePath
//            val path7 = context.getExternalFilesDir("")!!.absolutePath
//            Log.e(TAG, path)
//            Log.e(TAG, path2)
//            Log.e(TAG, path3)
//            Log.e(TAG, path4)
//            Log.e(TAG, path5)
//            Log.e(TAG, path6)
//            Log.e(TAG, path7)
//
//        }
//
//        fun getFilePath(context: Context, dir: String): String? {
//            var directoryPath: String? = ""
//            directoryPath =
//                if (MEDIA_MOUNTED == Environment.getExternalStorageState()) { //判断外部存储是否可用
//                    context.getExternalFilesDir(dir)!!.absolutePath
//                } else { //没外部存储就使用内部存储
//                    context.filesDir.toString() + File.separator.toString() + dir
//                }
//
//            val file = File(directoryPath)
//            if (!file.exists()) { //判断文件目录是否存在
//                file.mkdirs()
//            }
//            return directoryPath
//        }
//
//
//        fun getFileList() {
//
//
//            var downloadDir: File? = null
//            var ringtoneDir: File? = null
//
//            val sdCardExist =
//                Environment.getExternalStorageState() == MEDIA_MOUNTED // 判断sd卡是否存在
//
//            if (sdCardExist) {
////                downloadDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
////                ringtoneDir =
//
////                copyTest()
//                test(DIRECTORY_RINGTONES)
//
//
//            }
//
//        }
//
//        fun getUri(activity: Activity, title: String?): Uri? {
//            var ringtoneTitle = title
//            val parcialUri = Uri.parse("content://media/external/audio/media")
//            var finalSuccessfulUri: Uri? = null
//            val rm = RingtoneManager(activity.applicationContext)
//            val cursor = rm.cursor
//            cursor.moveToFirst()
//
//            while (!cursor.isAfterLast) {
//                if (ringtoneTitle!!.compareTo(
//                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)),
//                        ignoreCase = true
//                    ) == 0
//                ) {
//                    val ringtoneID =
//                        cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
//                    finalSuccessfulUri = Uri.withAppendedPath(parcialUri, "" + ringtoneID)
//
//                    return finalSuccessfulUri
//                }
//                cursor.moveToNext()
//            }
//            return finalSuccessfulUri
//
//        }
//
//        fun setRawFileToRingtone(context: Context) {
//            val name = "LaAfareyeFi"
//            val file = File(getExternalStorageDirectory(), "/Ringtones/")
//            if (!file.exists()) {
//                file.mkdirs()
//            }
//
//            val path = getExternalStorageDirectory()
//                .absolutePath + "/Ringtones/"
//            val f = File("$path/", "$name.mp3")
//            val mUri = Uri.parse(
//                ("android.resource://"
//                        + context.packageName) + "/raw/" + name
//            )
//            val mCr: ContentResolver = context.contentResolver
//            val soundFile: AssetFileDescriptor?
//            soundFile = try {
//                mCr.openAssetFileDescriptor(mUri, "r")
//            } catch (e: FileNotFoundException) {
//                null
//            }
//
//            try {
//                val readData = ByteArray(1024)
//                val fis = soundFile!!.createInputStream()
//                val fos = FileOutputStream(f)
//                var i = fis.read(readData)
//                while (i != -1) {
//                    fos.write(readData, 0, i)
//                    i = fis.read(readData)
//                }
//                fos.close()
//            } catch (io: IOException) {
//            }
//
//            val values = ContentValues()
//            values.put(MediaStore.MediaColumns.DATA, f.absolutePath)
//            values.put(MediaStore.MediaColumns.TITLE, name)
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
//            values.put(MediaStore.MediaColumns.SIZE, f.length())
//            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name)
//            values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
//            values.put(MediaStore.Audio.Media.IS_ALARM, true)
//            values.put(MediaStore.Audio.Media.IS_MUSIC, true)
//
//            val uri = MediaStore.Audio.Media.getContentUriForPath(f.absolutePath)
//            val newUri = mCr.insert(uri, values)
//
//            try {
//                RingtoneManager.setActualDefaultRingtoneUri(
//                    context,
//                    RingtoneManager.TYPE_RINGTONE, newUri
//                )
//                Settings.System.putString(mCr, Settings.System.RINGTONE, newUri.toString())
//            } catch (t: Throwable) {
//            }
//        }
//
//        fun sendBroadcastTest(context: Context, path: String?, filename: String) {
//            context.sendBroadcast(
//                Intent(
//                    Intent.ACTION_MEDIA_MOUNTED, Uri.parse(
//                        "file://" + path + filename.toString() + ".mp3"
//                                + getExternalStorageDirectory()
//                    )
//                )
//            )
//        }
//
//        fun getLocalRingtone(context: Context) {
//            var localringtonguri = RingtoneManager.getActualDefaultRingtoneUri(
//                context,
//                RingtoneManager.TYPE_RINGTONE
//            )
//
//            Log.i(TAG, localringtonguri.toString())
//            Log.i(TAG, localringtonguri.path)
//        }


//        fun getFilesAllName(path: String?): List<String>? {
//            val file = File(path)
//            val files = file.listFiles()
//            if (files == null) {
//                Log.e("error", "空目录")
//                return null
//            }
//            val s: MutableList<String> = ArrayList()
//            for (i in files.indices) {
//                s.add(files[i].absolutePath)
//            }
//            return s
//        }
//
//        fun test(type: String) {
//
//            var sdDir: File? = Environment.getExternalStoragePublicDirectory(type)
//            Log.e(TAG, sdDir.toString())
//
////            FileUtils.createDir(sdDir!!.path + File.separator + "first")
//
//            val s = getFilesAllName(sdDir!!.path)
//            if (s == null) {
//                Log.e("error", "空目录")
//            } else {
//                s!!.forEach {
//                    Log.i(TAG, it)
//                }
//            }
//        }
//
//        fun copyTest() {
//            var downloadDir: File? = null
//            var ringtoneDir: File? = null
//
//            downloadDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
//            ringtoneDir =
//                Environment.getExternalStoragePublicDirectory(DIRECTORY_RINGTONES) // 获取根目录
//            var srcPath = downloadDir.path + File.separator + "test.mp3"
//            var destPath = ringtoneDir.path + File.separator + "test.mp3"
//            copyFile(srcPath, destPath)
//            Log.i(TAG, "拷贝结束")
//        }

        /**
         * srcPath : 当前文件夹
         * destPath : 目标文件夹
         */
//        fun copyFile(srcPath: String, destPath: String) {
//            val fileSrc: File = File(srcPath)
//            val fileDest: File = File(destPath)
//            fileSrc.copyTo(fileDest)
//        }

//        @Throws(IOException::class)
//        fun copy(src: File?, dst: File?) {
//            FileInputStream(src).use { `in` ->
//                FileOutputStream(dst).use { out ->
//                    // Transfer bytes from in to out
//                    val buf = ByteArray(1024)
//                    var len: Int
//                    while (`in`.read(buf).also { len = it } > 0) {
//                        out.write(buf, 0, len)
//                    }
//                }
//            }
//        }
//
//        @Throws(IOException::class)
//        fun copySecond(src: File?, dst: File?) {
//            val inStream = FileInputStream(src)
//            val outStream = FileOutputStream(dst)
//            val inChannel: FileChannel = inStream.channel
//            val outChannel: FileChannel = outStream.channel
//            inChannel.transferTo(0, inChannel.size(), outChannel)
//            inStream.close()
//            outStream.close()
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        @Throws(IOException::class)
//        fun copyThird(origin: File, dest: File) {
//            Files.copy(origin.toPath(), dest.toPath())
//        }


        /**
         * 跳转
         */
//        fun jumpToRingtonePicker() {
//            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
////            intent.putExtra(
////                RingtoneManager.EXTRA_RINGTONE_TYPE,
////                RingtoneManager.TYPE_NOTIFICATION
////            )
////
////            val uri: String = myPrefs.beepUri().get()
////
////            if (uri !== "") {
////                Log.i("Log", "uri is $uri")
////                RingtoneManager.setActualDefaultRingtoneUri(
////                    getActivity(),
////                    RingtoneManager.TYPE_NOTIFICATION,
////                    Uri.parse(uri)
////                )
////            }
//
//            startActivityForResult(intent, 1)
//        }



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

        fun getAllRingtoneList(context: Context): Array<Uri?>?{
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
                val notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
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

//        fun changeMod(destFile: File){
//            try {
//                val command = "chmod 777 " + destFile.absolutePath
//                Log.i("zyl", "command = $command")
//                val runtime = Runtime.getRuntime()
//                val proc = runtime.exec(command)
//            } catch (e: IOException) {
//                Log.i("zyl", "chmod fail!!!!")
//                e.printStackTrace()
//            }
//        }

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
        fun getCurrentRingtoneFilePathFromUri(context: Context): String?{
            var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            var path = getMediaFilePathFromUri(context,uri)
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
        fun playDefaultRingtone(activity: Activity){
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

        fun fileIsExistsInRingtonesHolder(fileName: String): Boolean{
            val file = File(getExternalStorageDirectory(), File.separator+Environment.DIRECTORY_RINGTONES+File.separator)
            if (!file.exists()) {
                file.mkdirs()
            }
            val root = getExternalStorageDirectory()
            val path = "$root" +  File.separator+Environment.DIRECTORY_RINGTONES+File.separator + fileName
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

    }

}