package com.xxh.ringbones.databases

import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.NewRingstone

class RingtoneRepository(private val ringtoneDao: RingtoneDao) {

    val allRingtones: LiveData<List<NewRingstone>> = ringtoneDao.getAll()
//    val anotherRingtones: List<NewRingstone> = ringtoneDao.getSecondAll()

    fun insert(newRingstone: NewRingstone) {
        insertAsyncTask(ringtoneDao).execute(newRingstone)
    }

    fun getRingtoneByTitle(title: String): NewRingstone {
        val ringtone = ringtoneDao.loadRingtoneByTitle(title)
        return ringtone
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

}