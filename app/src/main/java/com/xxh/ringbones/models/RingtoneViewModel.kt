package com.xxh.ringbones.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.databases.RingtoneRepository

class RingtoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RingtoneRepository

    private val allRingtones: LiveData<List<Ringtone>>

    init {
        repository = RingtoneRepository(application)
        allRingtones = repository.getAllRingtones()
    }


    fun insert(ringtone: Ringtone) {
        repository.insert(ringtone)
    }

    fun insertRingtoneList(ringtones: List<Ringtone>){
        repository.insertRingtoneList(ringtones)
    }

    fun delete(ringtone: Ringtone){
        repository.delete(ringtone)
    }

    fun getAllRingtones():LiveData<List<Ringtone>>{
        return allRingtones
    }

    fun deleteAll(){
        repository
    }


    fun update(ringtone: Ringtone){
        repository.update(ringtone)
    }

    fun updateByTitle(filename: String,isSelect: Boolean){
        repository.updateByTitle(filename,isSelect)
    }


}