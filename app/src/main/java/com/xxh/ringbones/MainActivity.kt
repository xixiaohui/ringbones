package com.xxh.ringbones


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this,topAppBar)

    }

    companion object{

        fun jumpToOtherActivity(activity: AppCompatActivity,topAppBar:MaterialToolbar){
            topAppBar.setOnClickListener {
                activity.finish()
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.top_favorite -> {
//                    Snackbar.make(topAppBar, "点我喜欢", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(activity,FavActivity::class.java)
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_download -> {
//                    Snackbar.make(topAppBar, "点我下载", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(activity,DownloadActivity::class.java)
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_search -> {
//                    Snackbar.make(topAppBar, "点我搜索", Snackbar.LENGTH_LONG).show()
                        val intent = Intent(activity,SearchActivity::class.java)
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_setting ->{
                        val intent = Intent(activity,SettingsActivity::class.java)
                        activity.startActivity(intent)
                        true
                    }
                    R.id.top_about_us ->{
                        val intent = Intent(activity,AboutActivity::class.java)
                        activity.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }
    }


}