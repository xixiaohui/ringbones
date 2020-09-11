package com.xxh.ringbones.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*


class HttpUtils {

    val client = OkHttpClient()

    val testUrl =
        "http://tyst.migu.cn/public/600902-2008430/tone/2008/09/10/2008年9月/4月环球106首歌曲/彩铃/7_mp3-128kbps/等你等到我心痛-张学友.mp3"


    fun run(url: String = testUrl): String {

        var request = Request.Builder().url(url).build();

        var musicDirFile: File = File(Environment.getExternalStorageDirectory().getAbsolutePath())

        var target: String = "$musicDirFile/test.mp3"
        target = target.replace("/0", "/1")

        var fileTaget: File = File(target)
        try {
            var response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val ps: PrintStream = PrintStream(fileTaget)
                var bytes = response.body?.bytes()
                ps.write(bytes, 0, bytes!!.size)
                ps.close()
            }
            return response.body.toString()
        } catch (e: Exception) {
            System.err.println(e.printStackTrace())
        }
        return ""
    }


    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    @Throws(IOException::class)
    fun post(url: String, json: String): String {
        val body: RequestBody = json.toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { response -> return response.body.toString() }
    }

    companion object {

        fun doCopy(context: Context, assetsPath: String, desPath: String) {

            val srcFiles = context.assets.list(assetsPath)

            srcFiles?.forEach {
                val outFileName: String = desPath + File.separator + it
                var inFileName: String = assetsPath + File.separator + it
                if (assetsPath.equals("")) {
                    inFileName = it
                }
                try {
                    val inputStream: InputStream = context.assets.open(inFileName)
                    val outputStream: FileOutputStream = FileOutputStream(outFileName)
                    copyAndClose(inputStream, outputStream)
                } catch (ex: IOException) {
                    File(desPath).mkdir()
                    doCopy(context, inFileName, outFileName)
                }
            }
        }

        private fun closeQuietly(out: OutputStream?) {
            try {
                out?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        private fun closeQuietly(`is`: InputStream?) {
            try {
                `is`?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        @Throws(IOException::class)
        private fun copyAndClose(`is`: InputStream, out: OutputStream) {
            copy(`is`, out)
            closeQuietly(`is`)
            closeQuietly(out)
        }

        @Throws(IOException::class)
        private fun copy(`is`: InputStream, out: OutputStream) {
            val buffer = ByteArray(1024)
            var n = 0
            while (-1 != `is`.read(buffer).also { n = it }) {
                out.write(buffer, 0, n)
            }
        }

        fun canUseExternalStorage(context: Context): Boolean {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "SD卡不可用", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun getExtSDCardPath(): List<String>? {
            val lResult: MutableList<String> = ArrayList()
            try {
                val rt = Runtime.getRuntime()
                val proc = rt.exec("mount")
                val `is` = proc.inputStream
                val isr = InputStreamReader(`is`)
                val br = BufferedReader(isr)
                var line: String
                while (br.readLine().also { line = it } != null) {
                    if (line.contains("extSdCard")) {
                        val arr = line.split(" ".toRegex()).toTypedArray()
                        val path = arr[1]
                        val file = File(path)
                        if (file.isDirectory) {
                            lResult.add(path)
                        }
                    }
                }
                isr.close()
            } catch (e: java.lang.Exception) {
            }
            return lResult
        }

        fun getSDPath(): String? {
            var sdDir: File? = null
            val sdCardExist = (Environment.getExternalStorageState()
                    == Environment.MEDIA_MOUNTED) //判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory() //获取跟目录
            }
            return sdDir.toString()
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun createSDCardDir(context: Context, directory: String) {
            var folderPath =
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + File.separator + directory
            val file = File(folderPath)
            if (!file.exists()) {
                val wallpaperDirectory = File(folderPath)
                wallpaperDirectory.mkdirs()
            } else {
                val results =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath).list()

            }
        }
    }

}

fun main() {

    val httpUtils = HttpUtils()
    var result =
        httpUtils.run("http://www.mediafire.com/file/5uu29cjorcb8r17/tiktok_dance_2020.mp3")
    println(result)

}