package com.xxh.ringbones.utils

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.xxh.ringbones.MainActivity
import com.xxh.ringbones.SHOW_REWARDED_AD

class RewardedAdUtils {

    companion object{

        fun initRewardedAd(activity: Activity): RewardedAd{
            val rewardedAd = RewardedAd(activity, "ca-app-pub-3940256099942544/5224354917")
            val adLoadCallback = object: RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                }
                override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                    // Ad failed to load.
                }
            }
            rewardedAd!!.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }

        /**
         * 初始化奖励广告
         */

         fun createAndLoadRewardedAd(activity: Activity,handler: Handler): RewardedAd {
            val rewardedAd = RewardedAd(activity, "ca-app-pub-3940256099942544/5224354917")
            val adLoadCallback = object : RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    // Ad successfully loaded.
                    Log.d("TAG", "on Rewarded Ad Opened.")

                    val message: Message = Message.obtain()
                    message.what = SHOW_REWARDED_AD
                    val bundle = Bundle()
                    bundle.putString("url", MainActivity.url)
                    message.data = bundle
                    handler.sendMessage(message)
                }

                override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                    // Ad failed to load.
                    Log.d("TAG", "on Rewarded Ad Failed To Load.")
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
            return rewardedAd
        }

        /**
         * 实际开始下载的奖励
         */
        fun startDownloadRingtone(activity: Activity,url: String) {
            var title = RingtoneActionUtils.getFileNameFromUrl(url)
            DownloadManagerTest.download(activity, url, title!!)
        }

       fun showRewardedAd(activity: Activity,rewardedAd: RewardedAd, url: String, doAction: (String) -> Unit) {
            if (rewardedAd.isLoaded) {
                val activityContext: Activity = activity
                val adCallback = object : RewardedAdCallback() {
                    override fun onUserEarnedReward(p0: RewardItem) {
                        doAction(url)
                    }

                    override fun onRewardedAdOpened() {
                        super.onRewardedAdOpened()
                        Log.d("TAG", "on Rewarded Ad Opened.")
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
            }
        }
    }
}