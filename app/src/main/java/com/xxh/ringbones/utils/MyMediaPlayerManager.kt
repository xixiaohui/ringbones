package com.xxh.ringbones.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.xxh.ringbones.R
import com.xxh.ringbones.adapter.RingstoneHolder
import com.xxh.ringbones.fragments.MainFragment
import com.xxh.ringbones.fragments.PlayFragment



class MyMediaPlayerManager() {


    companion object {
        private var mediaPlayer: MediaPlayer = MediaPlayer()
//            .apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                setAudioAttributes(
//                    AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
//                        .build()
//                )
//            }
//        }

        fun stop() {
            mediaPlayer.stop()
        }

        fun start() {
            mediaPlayer.start()
        }

        fun isPlaying(): Boolean {
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
//                mediaPlayer.prepareAsync()
//                mediaPlayer.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
//                    override fun onPrepared(mp: MediaPlayer?) {
//                        mp?.start()
//                    }
//
//                })
            } catch (ex: Exception) {
                ex.printStackTrace()

            }
        }

        fun release() {
            mediaPlayer.release()
        }

    }

    class MediaTask(private val holder: PlayFragment.NewRingstoneHolder) :
        AsyncTask<String, Integer, MediaPlayer>() {

        var mProgressBar: ProgressBar? = null
        var mPlay: ImageView? = null

        init {
            mProgressBar = holder.getProgressBar()
            mPlay = holder.getPlay()
        }

        /**
         * 子线程运行
         * 可以把耗时的逻辑操作交给这个方法来处理
         * 当方法结束后，能够把处理完的结果返回到主线程中。
         * 这时，主线程中的界面元素就能够使用该返回结果来更新数据了
         *
         */
        override fun doInBackground(vararg url: String): MediaPlayer {
            prepare(url[0])

            return mediaPlayer
        }

        /**
         * 最先运行
         * 做一些UI的初始化操作，入进度条的显示
         */
        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar!!.visibility = View.VISIBLE
        }

        /**
         * 主线程执行
         * 这个方法用于UI组件的更新，
         *  doInBackground返回的结果会被传到这个方法中作为更新UI的数据
         */
        override fun onPostExecute(result: MediaPlayer) {
            super.onPostExecute(result)

            result.start()

            mProgressBar!!.visibility = View.INVISIBLE
            mPlay!!.setImageResource(R.drawable.ic_pause)
            mPlay!!.tag = "select"
        }
    }

    class MediaTaskSecond(private val holder: RingstoneHolder) :
        AsyncTask<String, Integer, MediaPlayer>() {

        var mProgressBar: ProgressBar? = null
        var mPlay: ImageView? = null

        init {
            mProgressBar = holder.getProgressBar()
            mPlay = holder.getPlay()
        }

        /**
         * 子线程运行
         * 可以把耗时的逻辑操作交给这个方法来处理
         * 当方法结束后，能够把处理完的结果返回到主线程中。
         * 这时，主线程中的界面元素就能够使用该返回结果来更新数据了
         *
         */
        override fun doInBackground(vararg url: String): MediaPlayer {
            prepare(url[0])

            return mediaPlayer
        }

        /**
         * 最先运行
         * 做一些UI的初始化操作，入进度条的显示
         */
        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar!!.visibility = View.VISIBLE
        }

        /**
         * 主线程执行
         * 这个方法用于UI组件的更新，
         *  doInBackground返回的结果会被传到这个方法中作为更新UI的数据
         */
        override fun onPostExecute(result: MediaPlayer) {
            super.onPostExecute(result)

            result.start()

            mProgressBar!!.visibility = View.INVISIBLE
            mPlay!!.setImageResource(R.drawable.ic_pause)
            mPlay!!.tag = "select"
        }
    }
}