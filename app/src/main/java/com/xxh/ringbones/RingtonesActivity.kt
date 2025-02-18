package com.xxh.ringbones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databinding.ActivityRingtonesBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.fragments.WHICHACTIVITY
import com.xxh.ringbones.utils.MyRewardedAdHandler
import com.xxh.ringbones.utils.RewardedAdUtils

class RingtonesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingtonesBinding

    var handler: Handler? = null
    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRingtonesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = this.supportFragmentManager
        val fa: FragmentTransaction = fm.beginTransaction()
        val superAwesomeCardFragment = SuperAwesomeCardFragment.newInstance(0, WHICHACTIVITY.RINGTONE_ACTIVITY.ordinal)
        fa.replace(R.id.container_port_myringtones,superAwesomeCardFragment)
        fa.commit()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

        mRewardedAd = RewardedAdUtils.initRewardedAd(this)
        handler = MyRewardedAdHandler(this, mRewardedAd!!)
    }
}