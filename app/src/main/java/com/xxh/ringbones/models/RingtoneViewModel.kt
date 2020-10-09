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

class RingtoneViewModel(application: Application) : AndroidViewModel(application){

    private val repository: RingtoneRepository

    val allRingtones: LiveData<List<NewRingstone>>

    init {
        val ringtoneDao = RingtoneRoomDatabase.getDatabase(application,viewModelScope).ringtoneDao()
        repository = RingtoneRepository(ringtoneDao)
        allRingtones = repository.allRingtones
    }

    fun insert(newRingstone: NewRingstone) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(newRingstone)
    }

}