package com.xxh.ringbones.utils

import android.media.MediaPlayer
import android.view.View
import java.lang.Exception

class PlayMusic {


    companion object {
        val testUrl =
            "http://tyst.migu.cn/public/600902-2008430/tone/2008/09/10/2008年9月/4月环球106首歌曲/彩铃/7_mp3-128kbps/等你等到我心痛-张学友.mp3"

        val malaya = "https://2u039f-a.akamaihd.net/downloads/ringtones/files/mp3/helalu-51148.mp3"

        var mediaPlayer: MediaPlayer = MediaPlayer()

        fun click(v: View) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else {
                mediaPlayer.reset()
            }

            try {
                mediaPlayer.setDataSource(testUrl)
                mediaPlayer.prepare()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}