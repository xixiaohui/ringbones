package com.xxh.ringbones.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databases.RingtoneRepository
import com.xxh.ringbones.databases.RingtoneRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RingtoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RingtoneRepository

    private val allRingtones: LiveData<List<NewRingstone>>

    init {

        repository = RingtoneRepository(application)
        allRingtones = repository.getAllRingtones()
    }


    fun insert(newRingstone: NewRingstone) {
        repository.insert(newRingstone)
    }

    fun delete(newRingstone: NewRingstone){
        repository.delete(newRingstone)
    }

    fun getAllRingtones():LiveData<List<NewRingstone>>{
        return allRingtones
    }

    fun deleteAll(){
        repository
    }



}