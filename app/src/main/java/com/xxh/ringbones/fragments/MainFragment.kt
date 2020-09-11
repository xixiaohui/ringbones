package com.xxh.ringbones.fragments

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.xxh.ringbones.R
import com.xxh.ringbones.data.Ringstone
import com.xxh.ringbones.databinding.FragmentMainBinding
import com.xxh.ringbones.utils.AssetsTest
import com.xxh.ringbones.utils.HttpUtils
import com.xxh.ringbones.utils.PlayMusic
import java.io.File

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

    var ringstones = mutableListOf<Ringstone>()

    private var song: AssetsTest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
              arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        this.initRingtones()


    }

    fun initRingtones() {
        var list = AssetsTest.getList(this.requireActivity())
//        var pre: String = "ring/"
        list?.forEach {
            var title = it.toString()
            ringstones.add(Ringstone(title, ""))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)

//        binding.nextButton.setOnClickListener {
//            MaterialAlertDialogBuilder(requireContext()).setTitle("Title").setMessage("Message")
//                .setPositiveButton("OK", null).show()
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

//        binding.nextButton.setOnClickListener {
//            navController.navigate(R.id.action_mainFragment_to_downloadFragment)
//        }

        binding.recycleMainContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = ListAdapter(ringstones) { ringstone, imageView ->
                ringstoneItemClicked(ringstone, imageView)
            }
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

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

                        navController.navigate(R.id.action_mainFragment_to_playFragment)
                        true
                    }
                    else -> false
                }
               return true
            }
        })

    }


    private fun ringstoneItemClicked(ringstone: Ringstone, imageView: ImageView) {
//        Toast.makeText(requireContext(), "Clicked: ${ringstone.title}", Toast.LENGTH_SHORT).show()
//        var bundle = bundleOf("ringstone" to ringstone)
//        navController.navigate(R.id.action_mainFragment_to_downloadFragment, bundle)

        var tempPath = "ring/" + ringstone.title
//        song = AssetsTest(this.requireActivity(), tempPath)
//        song.play()

        if (imageView.drawable.current.constantState == this.resources.getDrawable(R.drawable.playwhite).constantState) {
            song?.stop()
        } else {
            if (song != null) {
                song?.release()
                song = null
            }
            song = AssetsTest(this.requireActivity(), tempPath)
            song?.play()
        }
    }

    class RingstoneHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var mTitle: TextView? = null
        private var mTag: TextView? = null

        private var mMore: ImageView? = null

        private var mHeart: ImageView? = null
        private var mPlay: ImageView? = null

        private var mCopy: Button? = null
        init {
            mTitle = itemView.findViewById(R.id.ringtone_share_card)
            mTag = itemView.findViewById(R.id.ringtone_share_tag)
            mMore = itemView.findViewById(R.id.ringtone_download)

            mHeart = itemView.findViewById(R.id.ringtone_fav)
            mPlay = itemView.findViewById(R.id.ringtone_card_play)

//            mCopy = itemView.findViewById(R.id.copy)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(ringstone: Ringstone, clickListener: (Ringstone, ImageView) -> Unit) {
            mTitle?.text = ringstone.title
            mTag?.text = ringstone.tag

            mMore?.setOnClickListener {
//                clickListener(ringstone)
                PlayMusic.click(it)
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
                } else {
                    mPlay?.setImageDrawable(res.getDrawable(R.drawable.playwhite))
                }

                clickListener(ringstone, mPlay!!)
            }
        }
    }

    class ListAdapter(
        private val data: MutableList<Ringstone>,
        private val clickListener: (Ringstone, ImageView) -> Unit
    ) :
        RecyclerView.Adapter<RingstoneHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingstoneHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.sharecard, parent, false)
            return RingstoneHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: RingstoneHolder, position: Int) {
            val ringstone: Ringstone = data[position]
            holder.bind(ringstone, clickListener)
        }
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
    }

    override fun onDestroy() {
        super.onDestroy()
        song?.release()
    }
}