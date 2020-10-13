package com.xxh.ringbones

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.xxh.ringbones.adapter.RingstoneHolder
import com.xxh.ringbones.adapter.RingtoneListAdapter
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.ActivityFavBinding
import com.xxh.ringbones.fragments.MainFragment
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.KotlinUtils
import androidx.activity.viewModels as viewModels

class FavActivity : AppCompatActivity() {
    private val TAG = "FavActivity"

    private lateinit var binding: ActivityFavBinding
    lateinit var ringtoneViewModel: RingtoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

        val recyclerView = binding.recyclerview


        val adapter = RingtoneListAdapter(null,
            { ringstone, holder, position ->
                ringstoneItemClicked(ringstone, holder, position)
            },
            { ringstone, url -> setRingtone(ringstone, url) },
            { ringtone, select -> clickFavButton(ringtone, select) })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setItemViewCacheSize(500)

        val permission: Boolean = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (permission) {
            Log.i(TAG, "有写入权限")
            ringtoneViewModel = ViewModelProvider(this).get(RingtoneViewModel::class.java)

            ringtoneViewModel.getAllRingtones().observe(this, Observer { ringtones ->
                ringtones?.let {rings ->
                    adapter.setRingtones(rings.filter { it.isFav })
                }
            })
        }

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//            var newRingstone = NewRingstone("Senotttttttttttttt","2020 ringtone",
//                "https://www.tonesmp3.com/ringtones/senorita-saxophone.mp3")
//            this.ringtoneViewModel.insert(newRingstone)
//        }

    }

    private fun clickFavButton(ringtone: NewRingstone, select: Boolean) {
        if (!select) {
//            ringtoneViewModel.delete(ringtone)
            ringtone.isFav = select
            ringtoneViewModel.update(ringtone)
        }
    }

    private fun setRingtone(ringstone: NewRingstone, url: String) {

    }

    private fun ringstoneItemClicked(
        ringstone: NewRingstone,
        holder: RingstoneHolder,
        position: Int,
    ) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode === RESULT_OK) {
            val jsonData = data!!.getStringExtra(SuperAwesomeCardFragment.EXTRA_REPLY)
            val ringtone = Gson().fromJson(jsonData, NewRingstone::class.java)
            ringtoneViewModel.insert(ringtone)

        } else {

        }
    }


    companion object {
        val NEW_WORD_ACTIVITY_REQUEST_CODE = 1


    }


}