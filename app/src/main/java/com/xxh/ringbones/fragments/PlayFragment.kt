package com.xxh.ringbones.fragments

import android.media.MediaPlayer
import android.media.Ringtone
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.data.Ringstone
import com.xxh.ringbones.databinding.FragmentMainBinding
import com.xxh.ringbones.databinding.FragmentPlayBinding
import com.xxh.ringbones.utils.AssetsTest
import com.xxh.ringbones.utils.LocalJsonResolutionUtils
import com.xxh.ringbones.utils.MyMediaPlayerManager
import com.xxh.ringbones.utils.PlayMusic
import org.json.JSONArray
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding


    val malaya = "https://2u039f-a.akamaihd.net/downloads/ringtones/files/mp3/helalu-51148.mp3"
    val testUrl =
        "http://tyst.migu.cn/public/600902-2008430/tone/2008/09/10/2008年9月/4月环球106首歌曲/彩铃/7_mp3-128kbps/等你等到我心痛-张学友.mp3"
    val jap = "https://audionautix.com/Music/AcousticBlues.mp3"

    private lateinit var imagePlayPause: ImageView

    private lateinit var ringtonesArray: MutableList<NewRingstone>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        prepareMediaPlay()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPlayBinding.inflate(layoutInflater)

//        imagePlayPause = binding.musicPlay

//        imagePlayPause.setOnClickListener {
//            mediaPlayer.start()
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRingtonesData()

        binding.recyclePlayContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = MusicListAdapter(ringtonesArray) { ringstone, imageView ->
                ringstoneItemClicked(ringstone, imageView)
            }
        }
    }

    private fun ringstoneItemClicked(ringstone: NewRingstone, imageView: ImageView) {
        val url = ringstone.url

//        if (MyMediaPlayerManager.isPlaying()){
//            mediaPlayer.stop()
//        }else{
//            prepareMediaPlay(url)
//            mediaPlayer.start()
//        }

        Snackbar.make(binding.root, url, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun prepareMediaPlay(url: String = testUrl) {
        MyMediaPlayerManager.prepare(url)
    }

    private fun prepareRingtonesData() {
        ringtonesArray = mutableListOf()

        val jsonString = LocalJsonResolutionUtils.getJson(this.requireContext(), "rings/2020.json")

        val jsonArray: JSONArray = LocalJsonResolutionUtils.getJsonArray(jsonString)

        val len = jsonArray.length() - 1

        for (i in 0..len) {
            var jsonObject = jsonArray.getJSONObject(i)
            var newRingstone = LocalJsonResolutionUtils.jsonToObject(
                jsonObject.toString(),
                NewRingstone::class.java
            )
            ringtonesArray.add(newRingstone)
        }

        Log.i("PlayFragment", "prepareRingtonesData...end")
    }

    class NewRingstoneHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var mTitle: TextView? = null
        private var mTag: TextView? = null

        private var mMore: ImageView? = null

        private var mHeart: ImageView? = null
        private var mPlay: ImageView? = null

        init {
            mTitle = itemView.findViewById(R.id.ringtone_share_card)
            mTag = itemView.findViewById(R.id.ringtone_share_tag)
            mMore = itemView.findViewById(R.id.ringtone_download)

            mHeart = itemView.findViewById(R.id.ringtone_fav)
            mPlay = itemView.findViewById(R.id.ringtone_card_play)

        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(ringstone: NewRingstone, clickListener: (NewRingstone, ImageView) -> Unit) {
            mTitle?.text = ringstone.title
            mTag?.text = ringstone.des

            mMore?.setOnClickListener {

            }

            mHeart?.setOnClickListener {
                val res = it.context
                val heart = res.getDrawable(R.drawable.heart)
                if (mHeart!!.drawable.current.constantState != heart!!.constantState) {
                    mHeart?.setImageDrawable(heart)
                } else {
                    mHeart?.setImageDrawable(res.getDrawable(R.drawable.emptyheart))
                }
            }

            mPlay?.setOnClickListener {
                val res = it.context
                val pause = res.getDrawable(R.drawable.pause)
                if (mPlay!!.drawable.current.constantState != pause!!.constantState) {
                    mPlay?.setImageDrawable(pause)

                    MyMediaPlayerManager.start(ringstone.url)
                } else {
                    mPlay?.setImageDrawable(res.getDrawable(R.drawable.playwhite))
                    MyMediaPlayerManager.stop()
                }

                clickListener(ringstone, mPlay!!)
            }
        }
    }

    class MusicListAdapter(
        private val data: MutableList<NewRingstone>,
        private val clickListener: (NewRingstone, ImageView) -> Unit
    ) :
        RecyclerView.Adapter<PlayFragment.NewRingstoneHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PlayFragment.NewRingstoneHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.ringbox, parent, false)
            return PlayFragment.NewRingstoneHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: PlayFragment.NewRingstoneHolder, position: Int) {
            val ringstone: NewRingstone = data[position]
            holder.bind(ringstone, clickListener)
        }
    }

    companion object {

    }
}