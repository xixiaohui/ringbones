package com.xxh.ringbones.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xxh.ringbones.databinding.FragmentPageViewBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PageViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PageViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPageViewBinding.inflate(layoutInflater)

        var pager: ViewPager = binding.pager

        pager.adapter = this.activity?.supportFragmentManager?.let { MyPagerAdapter(it, 0) }

        var tabs = binding.pageTabs
        tabs.setViewPager(pager)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PageViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PageViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class MyPagerAdapter : FragmentPagerAdapter {


        constructor(fm: FragmentManager, behavior: Int) : super(fm, behavior) {

        }

        private val TITLES = arrayOf(
            "2020", "Funny", "Malayalam", "Bollywood", "Romantic", "English",
            "Animal", "Love"
        )

        override fun getCount(): Int {
            return TITLES.size
        }

        override fun getItem(position: Int): Fragment {
            return SuperAwesomeCardFragment.newInstance(position)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return TITLES[position]
        }
    }

}