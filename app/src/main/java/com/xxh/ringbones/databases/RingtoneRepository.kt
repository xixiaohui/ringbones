package com.xxh.ringbones.databases

import android.app.Application
import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.NewRingstone

class RingtoneRepository(application: Application) {

    var mAllRingtones: LiveData<List<NewRingstone>>
    private lateinit var ringtoneDao: RingtoneDao
//    val anotherRingtones: List<NewRingstone> = ringtoneDao.getSecondAll()


    init {
        val db = RingtoneRoomDatabase.getDatabase(application)
        ringtoneDao = db.ringtoneDao()
        mAllRingtones = ringtoneDao.getAll()
    }

    fun getAllRingtones():LiveData<List<NewRingstone>>{
        return mAllRingtones
    }

    fun insert(newRingstone: NewRingstone) {
        insertAsyncTask(ringtoneDao).execute(newRingstone)
    }

    fun getRingtoneByTitle(title: String): NewRingstone {
        val ringtone = ringtoneDao.loadRingtoneByTitle(title)
        return ringtone
    }

    fun delete(newRingstone: NewRingstone){
        deleteAsyncTask(ringtoneDao).execute(newRingstone)
    }

    private class insertAsyncTask(val dao: RingtoneDao) : AsyncTask<NewRingstone, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: NewRingstone): Void? {
            mAsyncTaskDao!!.insert(params[0])
            return null
        }
    }

    private class deleteAsyncTask(val dao: RingtoneDao) : AsyncTask<NewRingstone, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: NewRingstone): Void? {
            mAsyncTaskDao!!.delete(params[0])
            return null
        }
    }

}