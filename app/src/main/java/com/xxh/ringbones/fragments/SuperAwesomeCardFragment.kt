package com.xxh.ringbones.fragments

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.FragmentSuperAwesomeCardBinding

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
    private lateinit var valueAnimator: ValueAnimator
    private var screen_width: Int = 0


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

        this.ringtonesArray = MainFragment.prepareRingtonesData(
            requireContext(),
            "rings/${ringFileList[position!!]}.json"
        )

        var wm = this.requireActivity().windowManager
        var outMetrics: DisplayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        screen_width = outMetrics.widthPixels

        valueAnimator = ValueAnimator.ofInt(0, screen_width)


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
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
            }.apply {
                setOnItemClickLitener(object : MainFragment.OnItemClickListener {
                    override fun onItemClick(
                        ringstone: NewRingstone,
                        holder: MainFragment.RingstoneHolder,
                        position: Int
                    ) {
//                        ringstoneItemClicked(ringstone, holder)

                    }
                })
            }
            setItemViewCacheSize(1000)
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun ringstoneItemClicked(
        ringstone: NewRingstone,
        holder: MainFragment.RingstoneHolder
    ) {

        val url = ringstone.url
        val imageView = holder.getPlay()
        var progressBar: ProgressBar? = holder.getProgressBar()


        if (imageView!!.tag.equals("unSelect")) {
//            if (valueAnimator.isPaused){
//                mediaHolder!!.start()
//                valueAnimator.start()
//                imageView?.setImageResource(R.drawable.ic_pause)
//
//            }else{
//                mediaHolder!!.setDataSource(url, object : MediaHolder.MediaAction {
//                    override fun doAction() {
//                        imageView?.setImageResource(R.drawable.ic_pause)
//                        progressBar?.visibility = View.INVISIBLE
//
//                        //设置属性动画
//                        val backgroundView: View? = holder.getBackgroundView()
//                        updateBackgroundWidthAnimator(backgroundView, mediaHolder!!.getDuration())
//                        valueAnimator.start()
//                    }
//                })
//                progressBar?.visibility = View.VISIBLE
//            }

            imageView?.setImageResource(R.drawable.ic_pause)
            imageView.tag = "Select"
//            updateBackgroundWidthAnimator(holder.getBackgroundView(), 3000)

            if (valueAnimator.isPaused) {
                valueAnimator.resume()
                mediaHolder!!.start()
            } else {
//              valueAnimator.start()

                mediaHolder!!.setDataSource(url, object : MediaHolder.MediaAction {
                    override fun doAction() {
                        imageView?.setImageResource(R.drawable.ic_pause)
                        progressBar?.visibility = View.INVISIBLE

                        //设置属性动画
                        val backgroundView: View? = holder.getBackgroundView()
                        updateBackgroundWidthAnimator(backgroundView, mediaHolder!!.getDuration())
                        valueAnimator.start()
                    }
                })
                progressBar?.visibility = View.VISIBLE
            }


        } else {
//            if (mediaHolder!!.isPlaying()) {
//                mediaHolder!!.pause()
//
//                imageView?.setImageResource(R.drawable.ic_play)
//                valueAnimator.pause()
//            } else {
//                mediaHolder!!.stop(object : MediaHolder.MediaAction {
//                    override fun doAction() {
//                        imageView?.setImageResource(R.drawable.ic_play)
//                        valueAnimator.setIntValues(0)
//                    }
//                })
//            }
//
            imageView?.setImageResource(R.drawable.ic_play)
            imageView.tag = "unSelect"


            if (valueAnimator.isRunning) {
                valueAnimator.pause()
                mediaHolder!!.pause()
            } else {
                mediaHolder!!.stop(object : MediaHolder.MediaAction {
                    override fun doAction() {
                        imageView?.setImageResource(R.drawable.ic_play)
                        valueAnimator.setIntValues(0)
                    }
                })
            }
        }

    }

    fun updateBackgroundWidthAnimator(backgroundView: View?, duration: Int) {
        this.valueAnimator.removeAllUpdateListeners()

        var animator: ValueAnimator = this.valueAnimator

        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            var curValue = animator.animatedValue

            backgroundView!!.layoutParams.width = curValue as Int
            backgroundView!!.requestLayout()
            if (backgroundView!!.layoutParams.width > 5) {
                backgroundView!!.visibility = View.VISIBLE
            }
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
//                backgroundView!!.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })
        setValueAnimationDuration(duration)
    }

    fun setValueAnimationDuration(duration: Int) {
        this.valueAnimator.duration = duration.toLong()
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