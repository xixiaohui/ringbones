package com.xxh.ringbones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databinding.ActivityFavBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.fragments.WHICHACTIVITY

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