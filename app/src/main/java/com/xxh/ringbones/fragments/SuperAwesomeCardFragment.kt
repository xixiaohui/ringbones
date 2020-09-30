package com.xxh.ringbones.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.FragmentSuperAwesomeCardBinding
import com.xxh.ringbones.utils.*
import java.io.File
import java.io.RandomAccessFile


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
    private var playView: ImageView? = null
    private var oldViewHolder: MainFragment.RingstoneHolder? = null

    var localBroadcastManager: LocalBroadcastManager? = null
    var myBroadcastReceiver: MyBroadcastReceiver? = null


    private val ringFileList = arrayOf(
        "2020", "Airtel", "Alarm", "Animal", "Arabic",
        "Attitude", "Bengali", "BGM", "Bhojpuri", "Blackberry",
        "Bollywood", "Call", "Christmas", "Classical",
        "DeshBhakti", "Dialogue", "Electronica", "English", "Funny",
        "Google", "Infinix", "Instrumental", "iPhone", "IPL",
        "Islamic", "Joker", "Kannada", "LG", "Love",
        "Malayalam", "Marathi", "Mashup", "MoodOff",
        "Music", "Nokia", "Oneplus", "Oppo", "PakistaniSong",
        "Poetry", "PSL5", "Punjabi", "Remix", "Romantic",
        "Sad", "Samsung", "Scary", "SMS", "Sounds",
        "Spanish", "Tamil", "Techno", "Telugu", "TikTok",
        "Vivo", "Warning", "Xiaomi"
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


        myBroadcastReceiver = MyBroadcastReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            myBroadcastReceiver!!,
            IntentFilter(SuperAwesomeCardFragment.ACTION_THREAD_STATUS)
        )

        activityForSetRingtone = this.requireActivity()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuperAwesomeCardBinding.inflate(inflater)
        rootView = binding.root

        recyclerView = binding.root.findViewById<RecyclerView>(R.id.ring_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = MainFragment.ListAdapter(ringtonesArray,
                { ringstone, holder, position ->
                    ringstoneItemClicked(ringstone, holder, position)
                },
                { ringstone, url -> setRingtone(ringstone, url) },
                {clickFavButton()})
            setItemViewCacheSize(1000)
        }

        return binding.root
    }

    private fun clickFavButton() {
//        RingtoneAction.playDefaultRingtone(this.requireActivity())
//        var path = RingtoneAction.getCurrentRingtoneFilePathFromUri(this.requireContext())
//        Log.i("clickFavButton","file path is = $path")


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setRingtone(ringstone: NewRingstone, url: String) {

//        Log.i(TAG, url)

        val context = requireActivity()
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            );
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                context,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            );
        }

        myDesirePermissionCode(context, ringstone)
    }



    fun downloadFile(activity: Activity, ringstone: NewRingstone) {
        val filename = Utils.getFileNameFromUrl(ringstone.url)
        Log.i(TAG, filename)
        val url = ringstone.url
        DownloadManagerTest.download(activity.baseContext, url, filename, true)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun myDesirePermissionCode(activity: Activity, ringstone: NewRingstone) {
        val permission: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(activity)
        } else {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (permission) {
            //do your code
            Log.i(TAG, "获得了写入权限")


            val filename = Utils.getFileNameFromUrl(ringstone.url)
            if (!RingtoneAction.fileIsExistsInRingtonesHolder(filename)){
                startDownloadService(activity, ringstone)
            }else{
                RingtoneAction.setMyRingtoneWithFileName(activityForSetRingtone,filename)
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivityForResult(
                    intent,
                    CODE_WRITE_SETTINGS_PERMISSION
                )
//                startActivity(intent)
                Log.i(TAG, "youDesirePermissionCode....1")
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    CODE_WRITE_SETTINGS_PERMISSION
                )
                Log.i(TAG, "youDesirePermissionCode....2")
            }
        }
    }

    private fun startDownloadService(activity: Activity, ringstone: NewRingstone) {
        var intent =
            Intent(activity, MyIntentService(ringstone.title)::class.java)

        val filename = Utils.getFileNameFromUrl(ringstone.url)

        intent.putExtra(MyIntentService.URL, ringstone.url)
        intent.putExtra(MyIntentService.FILENAME, filename)

        activity.baseContext.startService(intent)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION
            && Settings.System.canWrite(
                this.requireContext()
            )
        ) {
            Log.d(TAG, "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success..1")

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success..2")
        }
    }

    fun test2() {
//        RingtoneAction.setMyRingtone(requireActivity(), url)
        val uri = RingtoneManager.getActualDefaultRingtoneUri(
            requireContext(),
            RingtoneManager.TYPE_RINGTONE
        )
        val uri2 = RingtoneManager.getActualDefaultRingtoneUri(
            requireContext(),
            RingtoneManager.TYPE_ALARM
        )
        val uri3 = RingtoneManager.getActualDefaultRingtoneUri(
            requireContext(),
            RingtoneManager.TYPE_NOTIFICATION
        )
        Log.i(TAG, uri.toString())
        Log.i(TAG, uri2.toString())
        Log.i(TAG, uri3.toString())
    }

    private fun test() {
        val rm = RingtoneManager(this.activity)
        val cursor = rm.cursor
        val columnNames: Array<String> = cursor.columnNames
        columnNames.forEach {
            var position = cursor.getColumnIndex(it)
            var uri = rm.getRingtoneUri(position)
            Log.i(TAG, uri.toString())
        }
    }

    /**
     * 状态：  Normal / Loading / Play / Pause / End
     *
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun ringstoneItemClicked(
        ringstone: NewRingstone,
        holder: MainFragment.RingstoneHolder,
        position: Int
    ) {

        val url = ringstone.url

        //换了一个歌曲
        if (this.currentUrl != url && this.currentUrl != "") {
            mediaHolder!!.reset(object : MediaHolder.MediaAction {
                override fun doAction() {
                    valueAnimator.removeAllListeners()
                    valueAnimator.cancel()
                }
            })
            this.currentUrl = url
            this.oldViewHolder!!.reset()
            this.ringstoneItemClicked(ringstone, holder, position)
            return
        }

        this.currentUrl = url
        val imageView = holder.getPlay()
        this.playView = imageView
        var progressBar: ProgressBar? = holder.getProgressBar()
        var state = imageView!!.tag
        this.oldViewHolder = holder

        when (state) {
            "Normal" -> {
//                progressBar?.visibility = View.VISIBLE
                ringstoneStartState(imageView, progressBar, url, holder)
            }
            "Loading" -> {

            }
            "Play" -> {
                if (valueAnimator.isRunning) {
                    mediaHolder!!.pause()
                    valueAnimator.pause()
                    imageView.tag = "Pause"
                    imageView?.setImageResource(R.drawable.ic_play)
                }
            }
            "Pause" -> {
                mediaHolder!!.start()
                valueAnimator.resume()
                imageView.tag = "Play"
                imageView?.setImageResource(R.drawable.ic_pause)
            }
            "End" -> {
                imageView.tag = "Play"

                Log.i(TAG, "动画结束了End")
                valueAnimator.start()
                mediaHolder!!.seekTo(0)
                mediaHolder!!.start()
                imageView?.setImageResource(R.drawable.ic_pause)
            }
        }
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
        if (duration == -1) {
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

    override fun onResume() {
        super.onResume()

//        Log.i(TAG,"onResume")

        mediaHolder!!.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
//        Log.i(TAG,"onDestroy")

        mediaHolder!!.release()

        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(myBroadcastReceiver!!)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()
//        Log.i(TAG,"onPause")

        if (mediaHolder!!.isPlaying()) {
            this.playView!!.tag = "Pause"
            this.playView!!.setImageResource(R.drawable.ic_play)
            mediaHolder!!.pause()
            valueAnimator.pause()
        }
    }

    override fun onStop() {
        super.onStop()
//        Log.i(TAG,"onStop")
    }


    class MyBroadcastReceiver : BroadcastReceiver() {

        private val TAG = "MyBroadcastReceiver"

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onReceive(context: Context?, intent: Intent?) {



            when (intent!!.action) {
                ACTION_INTENTSERVICE_STATUS -> {
                    Log.i(TAG, "SuperAwesomeCardFragment.ACTION_INTENTSERVICE_STATUS")
                }
                ACTION_THREAD_STATUS -> {
                    var filename = intent!!.getStringExtra(MyIntentService.FILENAME)
                    Log.i(TAG, filename)
                    var status = intent!!.getIntExtra(MyIntentService.STATUS,0)
                    Log.i(TAG, "status = $status")

                    when(status){
                        DownloadManager.STATUS_SUCCESSFUL->{

                            Snackbar.make(SuperAwesomeCardFragment.rootView,"Download successful.",Snackbar.LENGTH_LONG).show()

//                            val path = RingtoneAction.combine(filename)
//                            RingtoneAction.setMyRingtone(activityForSetRingtone,path)
                            RingtoneAction.setMyRingtoneWithFileName(activityForSetRingtone,filename)

                        }
                        DownloadManager.STATUS_FAILED ->{

                            Snackbar.make(SuperAwesomeCardFragment.rootView,"Download failed.",Snackbar.LENGTH_LONG).show()
                        }
                    }

                    Log.i(TAG, "SuperAwesomeCardFragment.ACTION_THREAD_STATUS")
                }
            }
        }

    }

    companion object {
        val CODE_WRITE_SETTINGS_PERMISSION = 10
        val ACTION_THREAD_STATUS = "action_thread_status"
        val ACTION_INTENTSERVICE_STATUS = "action_intentservice_status"

        lateinit var rootView: FrameLayout
        private lateinit var activityForSetRingtone: Activity
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