package com.xxh.ringbones


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        topAppBar.setOnClickListener{

        }

        topAppBar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.top_favorite ->{
                    Snackbar.make(topAppBar,"点我喜欢",Snackbar.LENGTH_LONG).show()

                    true
                }
                R.id.top_download ->{
                    Snackbar.make(topAppBar,"点我下载",Snackbar.LENGTH_LONG).show()
                    true
                }
                R.id.top_search ->{
                    Snackbar.make(topAppBar,"点我搜索",Snackbar.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()

    }

}