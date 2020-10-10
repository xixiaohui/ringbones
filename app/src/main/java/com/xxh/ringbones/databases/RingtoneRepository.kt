package com.xxh.ringbones.databases

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.NewRingstone

class RingtoneRepository(private val ringtoneDao: RingtoneDao) {

    val allRingtones: LiveData<List<NewRingstone>> = ringtoneDao.getAll()

//    val anotherRingtones: List<NewRingstone> = ringtoneDao.getSecondAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(newRingstone: NewRingstone){
        ringtoneDao.insertAll(newRingstone)
    }

    fun getRingtoneByTitle(title: String): NewRingstone{
        val ringtone = ringtoneDao.loadRingtoneByTitle(title)
        return ringtone
    }

}