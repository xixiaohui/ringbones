package com.xxh.ringbones.utils

import android.app.Activity
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.rewarded.RewardedAd
import com.xxh.ringbones.*
import com.xxh.ringbones.data.Ringtone

/**
 * 响应激励广告
 */
class MyRewardedAdHandler(val activity: AppCompatActivity, val rewardedAd: RewardedAd) : Handler() {

    var mRewardedAd: RewardedAd? = null

    private fun setRewardedAd() {
        this.mRewardedAd = rewardedAd
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
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
                        mRewardedAd = RewardedAdUtils.createAndLoadRewardedAd(activity, this,SHOW_REWARDED_AD)
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
                        MainActivity.url,ringtone = null) { url,ringtone->
                        RewardedAdUtils.startDownloadRingtone(activity,
                            url,ringtone)
                    }
                }
                LOAD_REWARDED_AD_SETRINGTONE -> {
                    setRewardedAd()

                    val ringtone: Ringtone? = msg.obj as Ringtone
                    MainActivity.url = ringtone!!.url!!

                    Log.i("MyRewardedAdHandler", MainActivity.url)

                    if (mRewardedAd!!.isLoaded) {
                        mRewardedAd = RewardedAdUtils.createAndLoadRewardedAd(activity, this,SHOW_REWARDED_AD_GETRINGTONE,ringtone)
                    } else {
                        val message: Message = Message.obtain()
                        message.what = SHOW_REWARDED_AD_GETRINGTONE
                        message.obj = ringtone
                        this.sendMessage(message)
                    }

                }
                SHOW_REWARDED_AD_GETRINGTONE -> {
                    val ringtone: Ringtone? = msg.obj as Ringtone
                    RewardedAdUtils.showRewardedAd(activity,
                        mRewardedAd!!,
                        url = "",
                        ringtone!!) { url,ringtone ->
                        RewardedAdUtils.startDownloadAndSetRingtone(activity,url,ringtone)
                    }
                }
            }
        }
    }
}