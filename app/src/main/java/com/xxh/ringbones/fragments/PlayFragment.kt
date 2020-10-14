package com.xxh.ringbones.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.FragmentPlayBinding
import com.xxh.ringbones.utils.LocalJsonResolutionUtils
import com.xxh.ringbones.utils.MyMediaPlayerManager
import org.json.JSONArray


/**
 * A simple [Fragment] subclass.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFragment : Fragment() {

    private lateinit var binding: FragmentPlayBinding
    private lateinit var ringtonesArray: MutableList<NewRingstone>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPlayBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRingtonesData()

        binding.recyclePlayContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = MusicListAdapter(ringtonesArray) { ringstone, view ->
                ringstoneItemClicked(ringstone, view)
            }
            setItemViewCacheSize(1000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ringstoneItemClicked(ringstone: NewRingstone, holder: NewRingstoneHolder) {
        val url = ringstone.url

        val imageView = holder.getPlay()


        if (imageView!!.tag.equals("unSelect")) {
            val mediaTask = MyMediaPlayerManager.MediaTask(holder)
            mediaTask.execute(url)

        } else {
            imageView?.setImageResource(R.drawable.ic_play)
            MyMediaPlayerManager.stop()
            imageView.tag = "unSelect"
        }

        Snackbar.make(binding.root, url, Snackbar.LENGTH_LONG)
            .show()
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
    }

    class NewRingstoneHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var mTitle: TextView? = null
        private var mTag: TextView? = null

        private var mMore: ImageView? = null

        private var mHeart: ImageView? = null
        private var mPlay: ImageView? = null

        private var mProgressBar: ProgressBar? = null

        init {
            mTitle = itemView.findViewById(R.id.ringtone_share_card)
            mTag = itemView.findViewById(R.id.ringtone_share_tag)
            mMore = itemView.findViewById(R.id.ringtone_download)

            mHeart = itemView.findViewById(R.id.ringtone_fav)
            mPlay = itemView.findViewById(R.id.ringtone_card_play)

            mProgressBar = itemView.findViewById(R.id.ringtone_progress_bar)
        }

        fun getPlay(): ImageView? {
            return mPlay
        }

        fun getProgressBar(): ProgressBar? {
            return mProgressBar
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(
            ringstone: NewRingstone,
            clickListener: (NewRingstone, NewRingstoneHolder) -> Unit
        ) {
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
                clickListener(ringstone, this)
            }
        }
    }

    class MusicListAdapter(
        private val data: MutableList<NewRingstone>,
        private val clickListener: (NewRingstone, NewRingstoneHolder) -> Unit
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

}