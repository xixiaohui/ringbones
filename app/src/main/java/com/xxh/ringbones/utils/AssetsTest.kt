package com.xxh.ringbones.utils

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer


class AssetsTest(private val activity: Activity,private val tempPath: String) {
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var afd: AssetFileDescriptor = activity.assets.openFd(tempPath)

    init {
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mediaPlayer.prepare()
    }
    companion object {
        fun getList(activity: Activity): Array<out String>? {
            return activity.assets.list("ring")
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun release(){
        if (mediaPlayer != null){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

}
