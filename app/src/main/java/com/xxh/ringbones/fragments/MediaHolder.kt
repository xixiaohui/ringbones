package com.xxh.ringbones.fragments

import android.content.Context
import android.media.MediaPlayer
import android.widget.ProgressBar

class MediaHolder {


    private var context: Context? = null

    private lateinit var mediaPlayer: MediaPlayer


    constructor(context: Context) {
        this.context = context
        mediaPlayer = MediaPlayer()
    }

    private fun play() {
        mediaPlayer.start()
    }

    fun start(){
        mediaPlayer.start()
    }

    fun stop(action: MediaAction) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.reset()
            mediaPlayer.stop()
            action.doAction()
        }

    }

    fun setDataSource(path: String, action: MediaAction) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            action.doAction()
            //播放
            this.play()
            //设置循环播放
            this.mediaPlayer.isLooping = true
        }
    }

    interface MediaAction {
        fun doAction()
    }

    fun release() {
        mediaPlayer.release()
    }

    fun isPlaying():Boolean{
        return mediaPlayer.isPlaying
    }

    fun getDuration(): Int{
        return mediaPlayer.duration
    }

    fun pause(){
        mediaPlayer.pause()
    }


}