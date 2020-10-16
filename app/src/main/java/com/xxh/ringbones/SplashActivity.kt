package com.xxh.ringbones

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.xxh.ringbones.databases.RingtoneRoomDatabase
import com.xxh.ringbones.databases.RingtoneRoomDatabase.Companion.databaseHolderName
import com.xxh.ringbones.utils.RingtoneActionUtils
import java.io.File

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        RingtoneActionUtils.check(this)


        val button = findViewById<Button>(R.id.welcome)
        button.setOnClickListener{

            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
            if (RingtoneActionUtils.checkPermission(this)){
                RingtoneActionUtils.createDir(RingtoneRoomDatabase.databaseHolderName)
            }
            startActivity(intent)
        }

    }
}