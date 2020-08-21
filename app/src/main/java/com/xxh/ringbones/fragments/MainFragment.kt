package com.xxh.ringbones.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentController
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.xxh.ringbones.R
import com.xxh.ringbones.data.Ringstone
import com.xxh.ringbones.databinding.FragmentMainBinding

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

    var ringstones = mutableListOf<Ringstone>(
        Ringstone("Raising Arizona", "1987"),
        Ringstone("Vampire's Kiss", "1980"),
        Ringstone("Con Air", "1981"),
        Ringstone("Gone in 60 Seconds", "1982"),
        Ringstone("National Treasure", "1983"),
        Ringstone("The Wicker Man", "1984"),
        Ringstone("Ghost Rider", "1985"),
        Ringstone("Knowing", "1989")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        ringstones.add(Ringstone("Raising Arizona", "1987"))
        ringstones.add(Ringstone("Vampire's Kiss", "1980"))
        ringstones.add(Ringstone("Con Air", "1981"))
        ringstones.add(Ringstone("Gone in 60 Seconds", "1982"))
        ringstones.add(Ringstone("National Treasure", "1983"))
        ringstones.add(Ringstone("The Wicker Man", "1984"))
        ringstones.add(Ringstone("Ghost Rider", "1985"))
        ringstones.add(Ringstone("Knowing", "1989"))
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
            adapter = ListAdapter(ringstones) { ringstone ->
                ringstoneItemClicked(ringstone)
            }
        }

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
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
                    true
                }
                else -> false
            }
        }


    }



    private fun ringstoneItemClicked(ringstone: Ringstone) {
//        Toast.makeText(requireContext(), "Clicked: ${ringstone.title}", Toast.LENGTH_SHORT).show()

        var bundle = bundleOf("ringstone" to ringstone)
        navController.navigate(R.id.action_mainFragment_to_downloadFragment,bundle)
    }

    class RingstoneHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var mTitle: TextView? = null
        private var mTag: TextView? = null

        private var mDownload:MaterialButton? = null

        init {
            mTitle = itemView.findViewById(R.id.ringtone_title)
            mTag = itemView.findViewById(R.id.ringtone_tag)
            mDownload = itemView.findViewById(R.id.ringtone_download)
        }

        fun bind(ringstone: Ringstone, clickListener: (Ringstone) -> Unit) {
            mTitle?.text = ringstone.title
            mTag?.text = ringstone.tag

            mDownload?.setOnClickListener {
                clickListener(ringstone)
            }
        }
    }

    class ListAdapter(
        private val data: MutableList<Ringstone>,
        private val clickListener: (Ringstone) -> Unit
    ) :
        RecyclerView.Adapter<RingstoneHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingstoneHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
            return RingstoneHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RingstoneHolder, position: Int) {
            val ringstone: Ringstone = data[position]
            holder.bind(ringstone,clickListener)
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
}