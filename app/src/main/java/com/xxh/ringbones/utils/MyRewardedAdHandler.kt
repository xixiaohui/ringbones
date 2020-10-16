package com.xxh.ringbones.utils

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.rewarded.RewardedAd
import com.xxh.ringbones.LOAD_REWARDED_AD
import com.xxh.ringbones.MainActivity
import com.xxh.ringbones.SHOW_REWARDED_AD

/**
 * 响应激励广告
 */
class MyRewardedAdHandler(val activity: AppCompatActivity, val rewardedAd: RewardedAd) : Handler() {

    var mRewardedAd: RewardedAd? = null

    private fun setRewardedAd() {
        this.mRewardedAd = rewardedAd
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (msg != null) {
            when (msg.what) {
                LOAD_REWARDED_AD -> {
                    setRewardedAd()

                    val bundle: Bundle = msg.data
                    val url = bundle.getString("url")
                    MainActivity.url = url!!

                    if (mRewardedAd!!.isLoaded) {
                        mRewardedAd = RewardedAdUtils.createAndLoadRewardedAd(activity, this)
                    } else {
                        val message: Message = Message.obtain()
                        message.what = SHOW_REWARDED_AD
                        val bundle2 = Bundle()
                        bundle.putString("url", MainActivity.url)
                        message.data = bundle2
                        this.sendMessage(message)
                    }

                }
                SHOW_REWARDED_AD -> {
                    val bundle: Bundle = msg.data
                    val url = bundle.getString("url")
                    RewardedAdUtils.showRewardedAd(activity,
                        mRewardedAd!!,
                        MainActivity.url) { url ->
                        RewardedAdUtils.startDownloadRingtone(activity,
                            url)
                    }
                }
            }
        }
    }
}