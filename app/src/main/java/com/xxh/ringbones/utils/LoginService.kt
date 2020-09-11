package com.xxh.ringbones.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.*
import java.util.*


object LoginService {
    /**
     * 保存用户名和方法的业务方法
     * @param context 上下文
     * @param username 用户名
     * @param password 方法
     * @return
     */
    fun saveUserInfo(context: Context?, username: String, password: String): Boolean {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(context, "sd卡不可用", Toast.LENGTH_SHORT).show()
            return false
        }
        val file = File(Environment.getExternalStorageDirectory(), "/info1.txt")
        return try {
            val fos = FileOutputStream(file)
            val info = "$username##$password"
            fos.write(info.toByteArray())
            fos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 读取
     * @return
     */
    fun getSavedUserInfo(context: Context?): Map<String, String>? {
        val file = File(Environment.getExternalStorageDirectory(), "/info1.txt")
        return try {
            val fis = FileInputStream(file)
            val br = BufferedReader(InputStreamReader(fis))
            val res = br.readLine().split("##".toRegex()).toTypedArray()
            val map: MutableMap<String, String> = HashMap()
            map["username"] = res[0]
            map["password"] = res[1]
            map
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}