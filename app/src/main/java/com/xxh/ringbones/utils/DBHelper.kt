package com.xxh.ringbones.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileNotFoundException

class DBHelper (){


    companion object{

        private var db: SQLiteDatabase? = null
        private var db_file: File?  = null

        fun exist(context: Context,dbName: String): Boolean{
            var flag: Boolean = false
            try {
                db_file = context.getDatabasePath(dbName)
                flag = db_file!!.exists()
            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }
            return flag
        }
    }
}