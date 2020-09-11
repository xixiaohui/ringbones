package com.xxh.ringbones.utils

import android.media.MediaPlayer
import com.xxh.ringbones.fragments.PlayFragment
import java.lang.Exception

class MyMediaPlayerManager {


    companion object {
        var mediaPlayer: MediaPlayer = MediaPlayer()

        var old_url: String = ""
        var new_url: String = ""

        fun stop() {
            mediaPlayer.stop()
        }

        fun start(url: String){
            prepare(url)
            mediaPlayer.start()
        }

        fun isPlaying(): Boolean{
            return mediaPlayer.isPlaying
        }

        fun reset() {
            mediaPlayer.reset()
        }

        fun prepare(url: String) {
            reset()
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepare()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun release() {
            mediaPlayer.release()
        }
    }
}