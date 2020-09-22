package com.xxh.ringbones.fragments

import android.animation.Animator
import android.animation.ValueAnimator
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private var currentUrl = ""
    private lateinit var recyclerView: RecyclerView

    private var oldViewHolder: MainFragment.RingstoneHolder? = null

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

        recyclerView = binding.root.findViewById<RecyclerView>(R.id.ring_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = MainFragment.ListAdapter(ringtonesArray) { ringstone, holder,position ->
                ringstoneItemClicked(ringstone, holder,position)
            }
            setItemViewCacheSize(1000)
        }

        return binding.root
    }

    /**
     * 状态：  Normal / Loading / Play / Pause / Stop / Repeat / End
     *
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun ringstoneItemClicked(
        ringstone: NewRingstone,
        holder: MainFragment.RingstoneHolder,
        position: Int
    ) {

        val url = ringstone.url
        Log.i(TAG,url)
        Log.i(TAG,ringstone.title)
        //换了一个歌曲
        if (this.currentUrl != url && this.currentUrl != "") {
            mediaHolder!!.reset(object: MediaHolder.MediaAction{
                override fun doAction() {
                    valueAnimator.removeAllListeners()
                    valueAnimator.cancel()
                }
            })
            this.currentUrl = url
            this.oldViewHolder!!.reset()
            this.ringstoneItemClicked(ringstone,holder,position)
            return
        }

        this.currentUrl = url
        val imageView = holder.getPlay()
        var progressBar: ProgressBar? = holder.getProgressBar()
        var state = imageView!!.tag
        this.oldViewHolder = holder

        when (state) {
            "Normal" -> {
//                progressBar?.visibility = View.VISIBLE
                ringstoneStartState(imageView,progressBar,url,holder)
            }
            "Loading" -> {

            }
            "Play" -> {
                if (valueAnimator.isRunning) {
                    imageView.tag = "Pause"
                    mediaHolder!!.pause()
                    valueAnimator.pause()
                    imageView?.setImageResource(R.drawable.ic_play)
                }
            }
            "Pause" -> {
                imageView.tag = "Play"
                imageView?.setImageResource(R.drawable.ic_pause)
                mediaHolder!!.start()
                valueAnimator.resume()
            }
            "End" -> {
                imageView.tag = "Play"

                Log.i(TAG, "动画结束了End")
                valueAnimator.start()
                mediaHolder!!.seekTo(0)
                mediaHolder!!.start()
                imageView?.setImageResource(R.drawable.ic_pause)
            }
            "Repeat" -> {

            }
            "Stop" -> {

            }
        }
//        this.recyclerView.adapter!!.notifyItemChanged(position)
    }

    private fun ringstoneStartState(
        imageView: ImageView,
        progressBar: ProgressBar?,
        url: String,
        holder: MainFragment.RingstoneHolder
    ) {
        imageView.tag = "Loading"
        progressBar?.visibility = View.VISIBLE

        mediaHolder!!.setDataSource(url, object : MediaHolder.MediaAction {
            override fun doAction() {
                imageView?.setImageResource(R.drawable.ic_pause)
                progressBar?.visibility = View.INVISIBLE

                //设置属性动画
                val backgroundView: View? = holder.getBackgroundView()
                updateBackgroundWidthAnimator(backgroundView, mediaHolder!!.getDuration())
                valueAnimator.start()

                imageView.tag = "Play"

                mediaHolder!!.setOnCompletionListener(object :
                    MediaHolder.CompletionListner {
                    override fun doAction() {
                        imageView.tag = "End"
                        imageView?.setImageResource(R.drawable.ic_play)
                        valueAnimator.setupStartValues()
                    }
                })
                doActionWhenAnimationEnd(backgroundView)
            }
        })
    }

    fun updateBackgroundWidthAnimator(backgroundView: View?, duration: Int) {
        this.valueAnimator.removeAllUpdateListeners()

        var animator: ValueAnimator = this.valueAnimator

        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            var curValue = animator.animatedValue

            backgroundView!!.layoutParams.width = curValue as Int
            backgroundView!!.requestLayout()
            if (backgroundView!!.layoutParams.width > 1) {
                backgroundView!!.visibility = View.VISIBLE
            }
        }


        setValueAnimationDuration(duration)
    }

    fun setValueAnimationDuration(duration: Int) {
        if (duration == -1){
            return
        }
        this.valueAnimator.duration = duration.toLong()
    }

    fun doActionWhenAnimationEnd(backgroundView: View?) {
        var animator: ValueAnimator = this.valueAnimator
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
//                backgroundView!!.visibility = View.INVISIBLE
                backgroundView!!.layoutParams.width = 0

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })
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