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
import androidx.fragment.app.FragmentTransaction
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
import com.xxh.ringbones.fragments.WHICHACTIVITY
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.KotlinUtils
import androidx.activity.viewModels as viewModels

class FavActivity : AppCompatActivity() {
    private val TAG = "FavActivity"

    private lateinit var binding: ActivityFavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = this.supportFragmentManager
        val fa: FragmentTransaction = fm.beginTransaction()
        val superAwesomeCardFragment = SuperAwesomeCardFragment.newInstance(0, WHICHACTIVITY.FAV_ACTVITY.ordinal)
        fa.replace(R.id.container_port_fav,superAwesomeCardFragment)
        fa.commit()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)



    }




}