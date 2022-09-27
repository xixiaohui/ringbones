package com.xxh.ringbones.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.MainActivity
import com.xxh.ringbones.SHOW_REWARDED_AD
import com.xxh.ringbones.SHOW_REWARDED_AD_GETRINGTONE
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment

class RewardedAdUtils {

    companion object {

        //发布用
        const val REWARADED_ADS_ID_RELEASE = "ca-app-pub-9929154001666083/2178031347"

        //测试用
        const val REWARADED_ADS_ID_TEST = "ca-app-pub-3940256099942544/5224354917"

        const val REWARADED_ADS_ID_USE = REWARADED_ADS_ID_RELEASE

        fun initRewardedAd(activity: Activity): RewardedAd {
            val rewardedAd = RewardedAd(activity, REWARADED_ADS_ID_USE)
            val adLoadCallback = object : RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                    Toast.makeText(activity.baseContext,"Rewarded Ad Loaded.",Toast.LENGTH_SHORT).show()
                }

                override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                    // Ad failed to load.
                    Toast.makeText(activity.baseContext,"Rewarded Ad FailedTo Load",Toast.LENGTH_SHORT).show()
                }
            }
            rewardedAd!!.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }

        /**
         * 初始化奖励广告
         */

        fun createAndLoadRewardedAd(activity: AppCompatActivity, handler: Handler,messageWhat: Int,ringtone: Ringtone? = null): RewardedAd {
            val rewardedAd = RewardedAd(activity, REWARADED_ADS_ID_USE)
            val adLoadCallback = object : RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                    Log.d("TAG", "on Rewarded Ad Loaded.")

                    val message: Message = Message.obtain()
                    message.what = messageWhat
                    message.obj = ringtone

                    if (ringtone !=null){
                        val bundle = Bundle()
                        bundle.putString("url", MainActivity.url)
                        message.data = bundle
                    }
                    handler.sendMessage(message)
                }

                override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                    // Ad failed to load.
                    Log.d("TAG", "on Rewarded Ad Failed To Load.")
                    hideLoading(activity)
                    Toast.makeText(activity.baseContext,"Rewarded Ad Failed To Load.",Toast.LENGTH_LONG).show()
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }

        fun hideLoading(activity: AppCompatActivity){
            val fragments = activity.supportFragmentManager.fragments
            fragments.forEach{
                if (it is SuperAwesomeCardFragment){
                    it.setLoadingInVisible()
                }
            }
        }
        /**
         * 实际开始下载的奖励
         */
        fun startDownloadRingtone(activity: AppCompatActivity, url: String,ringtone: Ringtone?) {
            var title = RingtoneActionUtils.getFileNameFromUrl(url)
            DownloadManagerTest.download(activity, url, title!!)
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun startDownloadAndSetRingtone(activity: AppCompatActivity, url: String,ringtone: Ringtone?){

//            Log.d("TAG", "startDownloadAndSetRingtone")
//            Log.d("TAG", ringtone!!.url)

            startDownloadService(activity, ringtone!!)
        }

        private fun startDownloadService(activity: Activity, ringtone: Ringtone) {

//            Log.i("RewardedAdUtils","startDownloadService")
            var intent = Intent(activity, MyIntentService(ringtone.title)::class.java)

            val filename = RingtoneActionUtils.getFileNameFromUrl(ringtone.url)
            intent.putExtra(MyIntentService.URL, ringtone.url)
            intent.putExtra(MyIntentService.FILENAME, filename)
            intent.putExtra(MyIntentService.TITLE, ringtone.title)

            activity.baseContext.startService(intent)
        }

        fun showRewardedAd(
            activity: AppCompatActivity,
            rewardedAd: RewardedAd,
            url: String,
            ringtone: Ringtone? = null,
            doAction: (String,Ringtone?) -> Unit,
        ) {
            if (rewardedAd.isLoaded) {
                val activityContext: AppCompatActivity = activity
                val adCallback = object : RewardedAdCallback() {
                    override fun onUserEarnedReward(p0: RewardItem) {

                        Log.d("TAG", "on User Earned Reward.")
                        doAction(url,ringtone)
                    }

                    override fun onRewardedAdOpened() {
                        super.onRewardedAdOpened()
                        Log.d("TAG", "on Rewarded Ad Opened.")

                        hideLoading(activity)
                    }

                    override fun onRewardedAdClosed() {
                        super.onRewardedAdClosed()
                        Log.d("TAG", "on Rewarded Ad Closed.")
                    }

                    override fun onRewardedAdFailedToShow(p0: AdError?) {
                        super.onRewardedAdFailedToShow(p0)
                        Log.d("TAG", "onRewardedAd Failed To Show.")
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            } else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                hideLoading(activity)
                Toast.makeText(activity.baseContext,"The rewarded ad wasn't loaded yet.",Toast.LENGTH_LONG).show()

//                doAction(url,ringtone)
//                Snackbar.make(SuperAwesomeCardFragment.rootView,"The rewarded ad wasn't loaded yet.",Snackbar.LENGTH_LONG).show()
            }
        }


    }
}