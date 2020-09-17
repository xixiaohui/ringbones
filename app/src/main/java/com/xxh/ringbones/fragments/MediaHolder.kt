package com.xxh.ringbones.fragments

import android.content.Context
import android.media.MediaPlayer
import android.widget.ProgressBar

class MediaHolder {


    private var context: Context? = null

    private var mediaPlayer: MediaPlayer? = null



    constructor(context: Context) {
        this.context = context

        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer()
        }
    }

    private fun play(){
        mediaPlayer!!.start()
    }

    fun pause(action: MediaAction){
        if (mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
            action.doAction()
        }

    }

    fun setDataSource(path: String,action: MediaAction){
        mediaPlayer!!.setDataSource(path)
        mediaPlayer!!.prepareAsync()

        mediaPlayer!!.setOnPreparedListener{
            action.doAction()
            this.play()
        }
    }

    interface MediaAction{
        fun doAction()
    }

    fun release(){
        mediaPlayer!!.release()
    }

}