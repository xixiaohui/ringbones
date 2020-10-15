package com.xxh.ringbones.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xxh.ringbones.data.Ringtone
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

    private lateinit var ringtonesArray: MutableList<Ringtone>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

//        this.ringtonesArray = prepareRingtonesData(requireContext(), "rings/Airtel.json")


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
            
        }

//        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.bottome_ringstone -> {
//                    // Respond to navigation item 1 click
//                    true
//                }
//                R.id.bottom_fav -> {
//                    // Respond to navigation item 2 click
//                    true
//                }
//                R.id.bottom_person -> {
//                    // Respond to navigation item 2 click
//
//                    navController.navigate(R.id.action_mainFragment_to_pageViewFragment)
//                    true
//                }
//                else -> false
//            }
//            true
//        }
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