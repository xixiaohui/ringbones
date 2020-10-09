package com.xxh.ringbones.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xxh.ringbones.data.NewRingstone

@Dao
interface RingtoneDao{

    @Query("SELECT * FROM ringtone")
    fun getAll(): LiveData<List<NewRingstone>>

    @Query("SELECT * FROM ringtone Where  title = :title")
    fun loadRingtoneByTitle(title: String): NewRingstone

    @Query("SELECT * FROM ringtone where ringtoneId IN (:ringtoneIds)")
    fun loadRingtoneByIds(ringtoneIds: List<String>): List<NewRingstone>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg newRingstone: NewRingstone)

    @Delete
    fun delete(newRingstone: NewRingstone)

    @Query("DELETE FROM ringtone")
    suspend fun deleteAll()
}