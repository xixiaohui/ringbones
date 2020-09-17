package com.xxh.ringbones.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.FragmentSuperAwesomeCardBinding
import com.xxh.ringbones.utils.MyMediaPlayerManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SuperAwesomeCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SuperAwesomeCardFragment : Fragment() {
    private val TAG: String = "SuperAwesomeCardFragment"

    // TODO: Rename and change types of parameters
    private var position: Int? = null



    private lateinit var binding: FragmentSuperAwesomeCardBinding

    private lateinit var ringtonesArray: MutableList<NewRingstone>

    private var mediaHolder: MediaHolder? = null

    private val ringFileList = arrayOf(
        "2020", "Funny", "Malayalam", "Bollywood", "Romantic", "English",
        "Animal", "Love"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_PARAM1)
        }
        mediaHolder = MediaHolder(requireContext())
        this.ringtonesArray = MainFragment.prepareRingtonesData(requireContext(),"rings/${ringFileList[position!!]}.json")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuperAwesomeCardBinding.inflate(inflater)

        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.ring_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = MainFragment.ListAdapter(ringtonesArray) { ringstone, holder ->
                ringstoneItemClicked(ringstone, holder)
            }
            setItemViewCacheSize(1000)
        }

        return binding.root
    }

    private fun ringstoneItemClicked(ringstone: NewRingstone, holder: MainFragment.RingstoneHolder) {

        val url = ringstone.url
        val imageView = holder.getPlay()
        var progressBar: ProgressBar? = holder.getProgressBar()

        if (imageView!!.tag.equals("unSelect")) {

            Log.i(TAG,url)
            mediaHolder!!.setDataSource(url,object: MediaHolder.MediaAction{
                override fun doAction() {
                    imageView?.setImageResource(R.drawable.ic_pause)
                    progressBar?.visibility = View.INVISIBLE
                }
            })
            imageView.tag = "Select"
            progressBar?.visibility = View.VISIBLE
            Snackbar.make(binding.root, "Play", Snackbar.LENGTH_LONG)
                .show()

        } else {
            mediaHolder!!.pause(object: MediaHolder.MediaAction{
                override fun doAction() {
                    imageView?.setImageResource(R.drawable.ic_play)
                }
            })
            imageView?.setImageResource(R.drawable.ic_play)
            imageView.tag = "unSelect"

            Snackbar.make(binding.root, "Pause", Snackbar.LENGTH_LONG)
                .show()
        }

//        Snackbar.make(binding.root, url, Snackbar.LENGTH_LONG)
//            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SuperAwesomeCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(position: Int) =
            SuperAwesomeCardFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                }
            }
    }
}