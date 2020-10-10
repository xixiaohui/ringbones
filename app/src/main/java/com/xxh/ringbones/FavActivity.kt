package com.xxh.ringbones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databinding.ActivityFavBinding
import com.xxh.ringbones.models.RingtoneViewModel

class FavActivity : AppCompatActivity() {
    private val TAG = "FavActivity"

    private lateinit var binding: ActivityFavBinding
    private lateinit var ringtoneViewModel: RingtoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this,topAppBar)

//        this.ringtoneViewModel = ViewModelProvider(this).get(RingtoneViewModel::class.java)
//
//        val ringtones = ringtoneViewModel.getRingtoneByTitle("Astronomia Mp3 Tone")
//        if (ringtones != null){
//            Log.i(TAG,ringtones.title)
//        }
//        ringtones!!.forEach { it ->
//            Log.i(TAG,it.title)
//            Log.i(TAG,it.des)
//            Log.i(TAG,it.url)
//        }

    }



}