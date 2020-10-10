package com.xxh.ringbones.databases

import androidx.lifecycle.LiveData
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.NewRingstone

class RingtoneRepository(private val ringtoneDao: RingtoneDao) {

    val allRingtones: LiveData<List<NewRingstone>> = ringtoneDao.getAll()

    suspend fun insert(newRingstone: NewRingstone){
        ringtoneDao.insertAll(newRingstone)
    }

    fun getRingtoneByTitle(title: String): NewRingstone{
        val ringtone = ringtoneDao.loadRingtoneByTitle(title)
        return ringtone
    }

}