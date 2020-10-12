package com.xxh.ringbones.fragments

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.FragmentMainBinding
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.DownloadManagerTest
import com.xxh.ringbones.utils.LocalJsonResolutionUtils
import com.xxh.ringbones.utils.MyMediaPlayerManager
import com.xxh.ringbones.utils.RingtoneAction
import org.json.JSONArray
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    private lateinit var ringtonesArray: MutableList<NewRingstone>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        this.ringtonesArray = prepareRingtonesData(requireContext(), "rings/Airtel.json")


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        binding.recycleMainContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

            adapter = ListAdapter(ringtonesArray,
                { ringtone, holder, position -> ringstoneItemClicked(ringtone, holder, position) },
                { ringtone, url -> setRingtone(ringtone, url) },
                { ringtone,select -> clickFavButton(ringtone,select) })

//            { ringtone, holder, position ->
//                ringstoneItemClicked(ringtone, holder, position)
//            }
            setItemViewCacheSize(1000)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottome_ringstone -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.bottom_fav -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.bottom_person -> {
                    // Respond to navigation item 2 click

                    navController.navigate(R.id.action_mainFragment_to_pageViewFragment)
                    true
                }
                else -> false
            }
            true
        }
    }

    private fun setRingtone(ringtone: NewRingstone, url: String) {

    }

    private fun clickFavButton(ringtone: NewRingstone, select: Boolean) {

    }

    private fun ringstoneItemClicked(
        ringstone: NewRingstone,
        holder: RingstoneHolder,
        position: Int
    ) {
//        Toast.makeText(requireContext(), "Clicked: ${ringstone.title}", Toast.LENGTH_SHORT).show()
//        var bundle = bundleOf("ringstone" to ringstone)
//        navController.navigate(R.id.action_mainFragment_to_downloadFragment, bundle)
//        var tempPath = "ring/" + ringstone.title
//        song = AssetsTest(this.requireActivity(), tempPath)
//        song.play()
//        if (imageView.drawable.current.constantState == this.resources.getDrawable(R.drawable.playwhite).constantState) {
//            song?.stop()
//        } else {
//            if (song != null) {
//                song?.release()
//                song = null
//            }
//            song = AssetsTest(this.requireActivity(), tempPath)
//            song?.play()
//        }

        val url = ringstone.url
        val imageView = holder.getPlay()

        if (imageView!!.tag.equals("unSelect")) {
            val mediaTask = MyMediaPlayerManager.MediaTaskSecond(holder)
            mediaTask.execute(url)

        } else {
            imageView?.setImageResource(R.drawable.ic_play)
            MyMediaPlayerManager.stop()
            imageView.tag = "unSelect"
        }

        Snackbar.make(binding.root, url, Snackbar.LENGTH_LONG)
            .show()
    }

    class RingstoneHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var mTitle: TextView? = null
        private var mTag: TextView? = null
        private var mSet: ImageView? = null

        //        private var mMore: ImageView? = null
        private var mHeart: ImageView? = null
        private var mPlay: ImageView? = null
        private var mDownload: ImageView? = null
        private var mProgressBar: ProgressBar? = null
        private var mView: View? = null

        init {
            mTitle = itemView.findViewById(R.id.ringtone_share_card)
            mSet = itemView.findViewById(R.id.ringtone_set)
            mTag = itemView.findViewById(R.id.ringtone_share_tag)
//            mMore = itemView.findViewById(R.id.ringtone_share)
            mHeart = itemView.findViewById(R.id.ringtone_fav)
            mPlay = itemView.findViewById(R.id.ringtone_card_play)
            mProgressBar = itemView.findViewById(R.id.ringtone_progress_bar)
            mDownload = itemView.findViewById(R.id.ringtone_download)
            mView = itemView.findViewById(R.id.ringtone_background)
        }

        fun getPlay(): ImageView? {
            return mPlay
        }

        fun getProgressBar(): ProgressBar? {
            return mProgressBar
        }

        fun getBackgroundView(): View? {
            return mView
        }

        /**
         * value : View.INVISIBLE | View.VISIBLE
         */
        fun setBackgroundVisibility(value: Int) {
            mView!!.visibility = value
        }

        fun setBackgroundWidth(value: Int) {
            mView!!.layoutParams.width = value
        }

        fun setPlayerButtonPlay() {
            mPlay!!.setImageResource(R.drawable.ic_play)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(
            ringstone: NewRingstone,
            clickPlayListener: (NewRingstone, RingstoneHolder, Int) -> Unit,
            position: Int,
            clickSetListener: (NewRingstone, url: String) -> Unit,
            clickFavListener: (NewRingstone,select: Boolean) -> Unit
        ) {
            mTitle?.text = ringstone.title
            mTag?.text = ringstone.des

//            mMore?.setOnClickListener {
//
//            }

            mSet?.setOnClickListener {
                if (mSet!!.tag == "unSelect") {
                    var url = ringstone.url
                    clickSetListener(ringstone, url)

                    mSet!!.tag = "Select"
                    mSet!!.setImageResource(R.drawable.ring)
                } else {
                    mSet!!.tag = "unSelect"
                    mSet!!.setImageResource(R.drawable.notification)
                }
            }

            mHeart?.setOnClickListener {
                if (mHeart!!.tag == "unSelect") {
                    mHeart!!.tag = "Select"
                    mHeart!!.setImageResource(R.drawable.heart)
                } else {
                    mHeart!!.tag = "unSelect"
                    mHeart!!.setImageResource(R.drawable.emptyheart)
                }
                clickFavListener(ringstone,mHeart!!.tag == "Select")
            }

            mPlay?.setOnClickListener {
                clickPlayListener(ringstone, this, position)
            }

            mDownload?.setOnClickListener {
                MaterialAlertDialogBuilder(it.context)
                    .setTitle(mDownload!!.context.getString(R.string.hi))
                    .setMessage(mDownload!!.context.getString(R.string.download_tips))
                    .setNegativeButton(it.context.resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(it.context.resources.getString(R.string.ok)) { dialog, which ->
                        // Respond to positive button press
                        var url = ringstone.url
                        var title = ringstone.title
                        DownloadManagerTest.download(it.context, url, title)
                    }
                    .show()
            }

        }

        fun reset() {
            this.setBackgroundWidth(0)
            this.setPlayerButtonPlay()

            this.getPlay()!!.tag = "Normal"

            this.setBackgroundVisibility(View.INVISIBLE)
            this.getProgressBar()!!.visibility = View.INVISIBLE
        }

        fun isLoading(): Boolean {
            return this.getPlay()!!.tag == "Loading"
        }
    }

    class ListAdapter(
        private val data: MutableList<NewRingstone>,
        private val clickPlayListener: (NewRingstone, RingstoneHolder, Int) -> Unit,
        private val clickSetListener: (NewRingstone, url: String) -> Unit,
        private val clickFavListener: (NewRingstone,select: Boolean) -> Unit

    ) :
        RecyclerView.Adapter<RingstoneHolder>() {
        private val TAG: String = "ListAdapter"

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingstoneHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.ringbox, parent, false)
            return RingstoneHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: RingstoneHolder, position: Int) {
            val ringstone: NewRingstone = data[position]
            holder.bind(ringstone, clickPlayListener, position, clickSetListener, clickFavListener)

//            Log.i(TAG, "position = $position")
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            ringstone: NewRingstone,
            holder: MainFragment.RingstoneHolder,
            position: Int
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun prepareRingtonesData(context: Context, fileName: String): MutableList<NewRingstone> {
            var ringtonesArray = mutableListOf<NewRingstone>()

            val jsonString = LocalJsonResolutionUtils.getJson(
                context,
                fileName
            )

            val jsonArray: JSONArray = LocalJsonResolutionUtils.getJsonArray(jsonString)

            val len = jsonArray.length() - 1

            for (i in 0..len) {
                var jsonObject = jsonArray.getJSONObject(i)
                var newRingstone = LocalJsonResolutionUtils.jsonToObject(
                    jsonObject.toString(),
                    NewRingstone::class.java
                )
                newRingstone.tag="test"
                newRingstone.ringtoneId= UUID.randomUUID().toString()

                ringtonesArray.add(newRingstone)
            }
            return ringtonesArray
        }
    }


}