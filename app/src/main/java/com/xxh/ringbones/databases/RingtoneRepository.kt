package com.xxh.ringbones.databases

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.Ringtone

class RingtoneRepository(application: Application) {

    var mAllRingtones: LiveData<List<Ringtone>>
    private lateinit var ringtoneDao: RingtoneDao
//    val anotherRingtones: List<NewRingstone> = ringtoneDao.getSecondAll()


    init {
        val db = RingtoneRoomDatabase.getDatabase(application)
        ringtoneDao = db.ringtoneDao()
        mAllRingtones = ringtoneDao.getAll()
    }

    fun getAllRingtones():LiveData<List<Ringtone>>{
        return mAllRingtones
    }

    fun insert(ringtone: Ringtone) {
        insertAsyncTask(ringtoneDao).execute(ringtone)
    }

    fun insertRingtoneList(ringtones: List<Ringtone>) {
        insertRingtoneListAsyncTask(ringtoneDao).execute(ringtones)
    }


    fun getRingtoneByTitle(title: String): Ringtone {
        val ringtone = ringtoneDao.loadRingtoneByTitle(title)
        return ringtone
    }

    fun delete(ringtone: Ringtone){
        deleteAsyncTask(ringtoneDao).execute(ringtone)
    }

    fun update(ringtone: Ringtone){
        updateAsyncTask(ringtoneDao).execute(ringtone)
    }

    fun updateByTitle(title: String,isSelect: Boolean) {
        updateByFilenameAsyncTask(ringtoneDao,isSelect).execute(title)
    }


    private class updateByFilenameAsyncTask(val dao: RingtoneDao,val isSelect: Boolean) : AsyncTask<String, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: String): Void? {

            val title = params[0]
            val ringtone = mAsyncTaskDao!!.loadRingtoneByTitle(title)
            ringtone.isRingtone = isSelect
            mAsyncTaskDao!!.update(ringtone)
            return null
        }
    }

    private class updateAsyncTask(val dao: RingtoneDao) : AsyncTask<Ringtone, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: Ringtone): Void? {
            mAsyncTaskDao!!.update(params[0])
            return null
        }
    }


    private class insertRingtoneListAsyncTask(val dao: RingtoneDao) : AsyncTask<List<Ringtone>, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: List<Ringtone>): Void? {

            val ringtones = params[0]

            ringtones.forEach{
                mAsyncTaskDao!!.insert(it)
            }

            return null
        }
    }


    private class insertAsyncTask(val dao: RingtoneDao) : AsyncTask<Ringtone, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: Ringtone): Void? {
            mAsyncTaskDao!!.insert(params[0])
            return null
        }
    }

    private class deleteAsyncTask(val dao: RingtoneDao) : AsyncTask<Ringtone, Void, Void>() {
        private var mAsyncTaskDao: RingtoneDao? = null

        init {
            mAsyncTaskDao = dao
        }

        override fun doInBackground(vararg params: Ringtone): Void? {
            mAsyncTaskDao!!.delete(params[0])
            return null
        }
    }

}