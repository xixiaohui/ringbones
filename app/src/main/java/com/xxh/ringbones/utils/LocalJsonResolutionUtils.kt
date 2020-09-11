package com.xxh.ringbones.utils

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class LocalJsonResolutionUtils {


    companion object{
        /**
         * 得到json文件中的内容
         * @param context
         * @param fileName
         * @return
         */
        fun getJson(context: Context, fileName: String?): String? {
            val stringBuilder = StringBuilder()
            //获得assets资源管理器
            val assetManager: AssetManager = context.assets
            //使用IO流读取json文件内容
            try {
                val bufferedReader = BufferedReader(
                    InputStreamReader(
                        assetManager.open(fileName!!), "utf-8"
                    )
                )
                var line: String? = null
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }

        fun getJsonArray(json: String?): JSONArray{
            return JSONArray(json)
        }

        /**
         * 将字符串转换为 对象
         * @param json
         * @param type
         * @return
         */
        fun <T> jsonToObject(json: String?, type: Class<T>?): T {
            val gson = Gson()
            return gson.fromJson(json, type)
        }
    }


}