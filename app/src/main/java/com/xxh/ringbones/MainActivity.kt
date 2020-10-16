package com.xxh.ringbones


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databases.RingtoneRoomDatabase
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.RingtoneActionUtils

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

        RingtoneActionUtils.check(this)

    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet,
    ): View? {
        return super.onCreateView(parent, name, context, attrs)
    }

    fun setHandler(handler: Handler) {

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
                        Log.i("", "create创建成功")
                    }

                    val fragment = this.supportFragmentManager.fragments

                    fragment.forEach {
                        if (it is SuperAwesomeCardFragment) {
                            it.setDatabase()
                        }
                    }
                } else {
                    //申请失败

                }
            }
        }
    }


    companion object {

        fun jumpToOtherActivity(activity: AppCompatActivity, topAppBar: MaterialToolbar) {
            topAppBar.setOnClickListener {
                activity.finish()
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.top_favorite -> {
//                    Snackbar.make(topAppBar, "点我喜欢", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(activity, FavActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_download -> {
//                    Snackbar.make(topAppBar, "点我下载", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(activity, DownloadActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_search -> {
//                    Snackbar.make(topAppBar, "点我搜索", Snackbar.LENGTH_LONG).show()
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