package com.xxh.ringbones


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.databases.RingtoneRoomDatabase
import com.xxh.ringbones.databinding.ActivityMainBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.DownloadManagerTest
import com.xxh.ringbones.utils.MyRewardedAdHandler
import com.xxh.ringbones.utils.RewardedAdUtils
import com.xxh.ringbones.utils.RingtoneActionUtils
import kotlinx.android.synthetic.main.activity_main.*

const val LOAD_REWARDED_AD = 100
const val SHOW_REWARDED_AD = 101

const val LOAD_REWARDED_AD_SETRINGTONE = 102
const val SHOW_REWARDED_AD_GETRINGTONE = 103

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var ad_view: AdView
    var handler: Handler? = null
    var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBannerAds()
        mRewardedAd = RewardedAdUtils.initRewardedAd(this)
        handler = MyRewardedAdHandler(this, mRewardedAd!!)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

        RingtoneActionUtils.check(this)
    }

    private fun initBannerAds() {
        MobileAds.initialize(this) {}

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("EC1CBD9E13BA2947C85666D17681B601","F2943A5D253923044C5806BE9E755070"))
                .build()
        )
        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        ad_view = findViewById<AdView>(R.id.ad_view)
        ad_view.loadAd(adRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RingtoneActionUtils.WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (RingtoneActionUtils.checkPermission(this)) {
                        RingtoneActionUtils.createDir(RingtoneRoomDatabase.databaseHolderName)
                        Log.i("", "create success")
                    }

                    val fragment = this.supportFragmentManager.fragments

                    fragment.forEach {
                        if (it is SuperAwesomeCardFragment) {
                            it.setDatabase()
                        }
                    }
                } else {
                    //申请失败
                    Snackbar.make(binding.root,"Failed to request permission.",Snackbar.LENGTH_LONG).show()
                    onRequestPermissionsResult(requestCode,permissions,grantResults)
                }
            }
        }
    }

    // Called when leaving the activity
    public override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        ad_view.destroy()
        super.onDestroy()
    }

    companion object {

        lateinit var url: String

        fun jumpToOtherActivity(activity: AppCompatActivity, topAppBar: MaterialToolbar) {
            topAppBar.setOnClickListener {
                activity.finish()
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.top_favorite -> {
                        val intent = Intent(activity, FavActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_download -> {
                        val intent = Intent(activity, DownloadActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_search -> {
                        val intent = Intent(activity, SearchActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_myringtones -> {
                        val intent = Intent(activity, RingtonesActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
//                    R.id.top_setting -> {
//                        val intent = Intent(activity, SettingsActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
//                        activity.startActivity(intent)
//                        true
//                    }
                    R.id.top_about_us -> {
                        val intent = Intent(activity, AboutActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }
    }


}