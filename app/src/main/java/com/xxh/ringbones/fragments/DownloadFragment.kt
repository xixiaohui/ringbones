package com.xxh.ringbones.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xxh.ringbones.R
import com.xxh.ringbones.data.Ringstone
import com.xxh.ringbones.databinding.FragmentDownloadBinding
import com.xxh.ringbones.databinding.FragmentMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var ringstone: Ringstone? = null

    private lateinit var binding: FragmentDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            ringstone = it.getSerializable("ringstone") as Ringstone
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloadBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.previewButton.setOnClickListener{
            view.findNavController().navigate(R.id.action_downloadFragment_to_mainFragment)
        }

        binding.card.ringtoneTitle.text = ringstone?.title
        binding.card.ringtoneTag.text = ringstone?.tag

        binding.card.ringtoneDownload.setOnClickListener{
            Toast.makeText(requireContext(), "Clicked: ${ringstone?.title} Download", Toast.LENGTH_SHORT).show()
        }

        binding.card.ringtonePlay.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext()).setTitle("Title").setMessage("Message")
                .setPositiveButton("OK", null).show()
//            Toast.makeText(requireContext(), "Clicked: ${ringstone?.title} Play", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DownloadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DownloadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}