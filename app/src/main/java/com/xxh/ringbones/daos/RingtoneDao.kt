package com.xxh.ringbones.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xxh.ringbones.data.Ringtone

@Dao
interface RingtoneDao{

    @Query("SELECT * FROM ringtone ORDER BY id ASC")
    fun getAll(): LiveData<List<Ringtone>>

    @Query("SELECT * FROM ringtone Where  title = :title")
    fun loadRingtoneByTitle(title: String): Ringtone

//    @Query("SELECT * FROM ringtone where id IN (:ringtoneIds)")
//    fun loadRingtoneByIds(ringtoneIds: List<String>): List<Ringtone>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg ringtone: Ringtone)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(ringtone: Ringtone)

    @Delete
    fun delete(ringtone: Ringtone)

    @Update
    fun update(ringtone: Ringtone)

    @Query("DELETE FROM ringtone")
    fun deleteAll()
}