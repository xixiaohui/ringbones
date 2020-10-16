package com.xxh.ringbones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databinding.ActivityFavBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.fragments.WHICHACTIVITY
import com.xxh.ringbones.utils.MyRewardedAdHandler
import com.xxh.ringbones.utils.RewardedAdUtils

class FavActivity : AppCompatActivity() {
    private val TAG = "FavActivity"

    private lateinit var binding: ActivityFavBinding

    var handler: Handler? = null
    private var mRewardedAd: RewardedAd? = null


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


        mRewardedAd = RewardedAdUtils.initRewardedAd(this)
        handler = MyRewardedAdHandler(this, mRewardedAd!!)

    }




}