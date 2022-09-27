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
import android.os.*
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.xxh.ringbones.*
import com.xxh.ringbones.adapter.RingstoneHolder
import com.xxh.ringbones.adapter.RingtoneListAdapter
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.databases.RingtoneRoomDatabase
import com.xxh.ringbones.databinding.FragmentSuperAwesomeCardBinding
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.*
import org.json.JSONArray

private const val POSITON = "position"
private const val WHTICH = "which_activity"
private const val SEARCH = "search"

enum class WHICHACTIVITY {
    MAIN_ACTIVITY,
    FAV_ACTVITY,
    DOWNLOAD_ACTIVITY,
    SEARCH_ACTIVITY,
    RINGTONE_ACTIVITY
}

class SuperAwesomeCardFragment : Fragment() {
    private val TAG: String = "SuperAwesomeCardFragment"

    private var position: Int? = null
    private var whichactivity: Int? = null
    private var searchKeyWords: String? = null
    private lateinit var binding: FragmentSuperAwesomeCardBinding
    private var mediaHolder: MediaHolder? = null
    private lateinit var valueAnimator: ValueAnimator
    private var screen_width: Int = 0
    private var currentUrl = ""
    private lateinit var recyclerView: RecyclerView
    private var playView: ImageView? = null
    private var oldViewHolder: RingstoneHolder? = null

    private lateinit var keyword: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(POSITON)
            whichactivity = it.getInt(WHTICH)
            searchKeyWords = it.getString(SEARCH)
        }


        mediaHolder = MediaHolder(requireContext())
        keyword = ringFileList[position!!]

        var wm = this.requireActivity().windowManager
        var outMetrics: DisplayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        screen_width = outMetrics.widthPixels

        valueAnimator = ValueAnimator.ofInt(0, screen_width)

        //通知用于下载
        if (myBroadcastReceiver == null){
            Log.i(TAG,"myBroadcastReceiver")
            myBroadcastReceiver = MyBroadcastReceiver()
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                myBroadcastReceiver!!,
                IntentFilter(SuperAwesomeCardFragment.ACTION_THREAD_STATUS)
            )
        }

        activityForSetRingtone = this.requireActivity()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuperAwesomeCardBinding.inflate(inflater)

        recyclerView = binding.root.findViewById<RecyclerView>(R.id.ring_list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = RingtoneListAdapter(null,
                { ringtone, holder, position ->
                    ringstoneItemClicked(ringtone, holder, position)
                },
                { ringtone, url -> setRingtone(ringtone, url) },
                { ringtone, select -> clickFavButton(ringtone, select) },
                { ringtone -> clickDownloadButton(ringtone) })
            setItemViewCacheSize(1000)
        }

        if (DBHelper.exist(this.requireContext(), RingtoneRoomDatabase.databaseName)) {
            setDatabase()
        }
        return binding.root
    }


    fun setDatabase() {
        if (!RingtoneActionUtils.checkPermission(this.requireActivity())) {
            return
        }
        ringtoneViewModel =
            ViewModelProvider(this.requireActivity()).get(RingtoneViewModel::class.java)
        val adapter = recyclerView.adapter as RingtoneListAdapter
        this.setAdapterData(ringtoneViewModel, adapter)

    }


    private fun setAdapterData(ringtoneViewModel: RingtoneViewModel, adapter: RingtoneListAdapter) {

        when (whichactivity) {
            WHICHACTIVITY.MAIN_ACTIVITY.ordinal -> {
                ringtoneViewModel.getAllRingtones()
                    .observe(this.requireActivity(), Observer { ringtones ->
                        ringtones?.let { rings ->
                            adapter.setRingtones(rings.filter { it.des.startsWith(keyword) })
                        }
                    })
            }
            WHICHACTIVITY.FAV_ACTVITY.ordinal -> {
                ringtoneViewModel.getAllRingtones()
                    .observe(this.requireActivity(), Observer { ringtones ->


                        ringtones?.let { rings ->
                            adapter.setRingtones(rings.filter { it.isFav })
                        }

                        recoveryUiState()
                    })
            }
            WHICHACTIVITY.DOWNLOAD_ACTIVITY.ordinal -> {
                val names =
                    RingtoneActionUtils.getDownloadRingtoneList(context = this.requireContext())
                ringtoneViewModel.getAllRingtones()
                    .observe(this.requireActivity(), Observer { ringtones ->
                        ringtones?.let { rings ->
                            adapter.setRingtones(rings.filter { it.url in names })
                        }
                    })
            }
            WHICHACTIVITY.SEARCH_ACTIVITY.ordinal -> {
                ringtoneViewModel.getAllRingtones()
                    .observe(this.requireActivity(), Observer { ringtones ->
                        ringtones?.let { rings ->
                            adapter.setRingtones(rings.filter { it.title.contains(this.searchKeyWords!!) })
                        }
                    })
            }
            WHICHACTIVITY.RINGTONE_ACTIVITY.ordinal -> {
                ringtoneViewModel.getAllRingtones()
                    .observe(this.requireActivity(), Observer { ringtones ->
                        ringtones?.let { rings ->
                            adapter.setRingtones(rings.filter { it.isRingtone })
                        }
                    })
            }
        }


    }

    private fun clickFavButton(ringtone: Ringtone, select: Boolean) {

        ringtone.isFav = select
        ringtoneViewModel.update(ringtone)
    }


    private fun clickDownloadButton(ringtone: Ringtone) {

        val context = this.requireContext()
        if (RingtoneActionUtils.isRingtoneInSdcard(context, ringtone)) {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.hi))
                .setMessage(context.getString(R.string.download_tips_already_have)).show()
        } else {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.hi))
                .setMessage(context.getString(R.string.download_tips))
                .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(context.resources.getString(R.string.ok)) { dialog, which ->
                    // Respond to positive button press

                    val message: Message = Message.obtain()
                    message.what = LOAD_REWARDED_AD
                    val bundle = Bundle()
                    bundle.putString("url", ringtone.url)
                    message.data = bundle

                    if (this.requireActivity() is MainActivity) {
                        val activity = this.requireActivity() as MainActivity
                        activity.handler!!.sendMessage(message)
                    } else if (this.requireActivity() is FavActivity) {
                        val activity = this.requireActivity() as FavActivity
                        activity.handler!!.sendMessage(message)
                    } else if (this.requireActivity() is DownloadActivity) {
                        val activity = this.requireActivity() as DownloadActivity
                        activity.handler!!.sendMessage(message)
                    } else if (this.requireActivity() is RingtonesActivity) {
                        val activity = this.requireActivity() as RingtonesActivity
                        activity.handler!!.sendMessage(message)
                    } else if (this.requireActivity() is SearchActivity) {
                        val activity = this.requireActivity() as SearchActivity
                        activity.handler!!.sendMessage(message)
                    }

                    setLoadingVisible()
                }
                .show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setRingtone(ringtone: Ringtone, url: String) {
        val context = requireActivity()
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                context,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        }

        myDesirePermissionCode(context, ringtone)
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun myDesirePermissionCode(activity: Activity, ringtone: Ringtone) {
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

            val filename = RingtoneActionUtils.getFileNameFromUrl(ringtone.url)
            if (RingtoneActionUtils.fileIsExistsInRingtonesHolder(filename!!)) {
                RingtoneActionUtils.setMyRingtoneWithFileName(activityForSetRingtone, filename)
                //数据库更新
                ringtone.isRingtone = true
                ringtoneViewModel.update(ringtone)
            } else {
                val context = this.requireContext()
                MaterialAlertDialogBuilder(this.requireContext())
                    .setTitle(context.getString(R.string.hi))
                    .setMessage(context.getString(R.string.setringtone_tips))
                    .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(context.resources.getString(R.string.ok)) { dialog, which ->

                        val message: Message = Message.obtain()
                        message.what = LOAD_REWARDED_AD_SETRINGTONE
                        message.obj = ringtone

                        if (this.requireActivity() is MainActivity) {
                            val activity = this.requireActivity() as MainActivity
                            activity.handler!!.sendMessage(message)
                        } else if (this.requireActivity() is FavActivity) {
                            val activity = this.requireActivity() as FavActivity
                            activity.handler!!.sendMessage(message)
                        } else if (this.requireActivity() is DownloadActivity) {
                            val activity = this.requireActivity() as DownloadActivity
                            activity.handler!!.sendMessage(message)
                        } else if (this.requireActivity() is RingtonesActivity) {
                            val activity = this.requireActivity() as RingtonesActivity
                            activity.handler!!.sendMessage(message)
                        } else if (this.requireActivity() is SearchActivity) {
                            val activity = this.requireActivity() as SearchActivity
                            activity.handler!!.sendMessage(message)
                        }

                        setLoadingVisible()
                    }.show()
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivityForResult(
                    intent,
                    CODE_WRITE_SETTINGS_PERMISSION
                )

            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    CODE_WRITE_SETTINGS_PERMISSION
                )

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION
            && Settings.System.canWrite(
                this.requireContext()
            )
        ) {


        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

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


    private fun recoveryUiState() {
        mediaHolder!!.reset(object : MediaHolder.MediaAction {
            override fun doAction() {
                valueAnimator.removeAllListeners()
                valueAnimator.cancel()
            }
        })
        if (this.oldViewHolder != null) {
            this.oldViewHolder!!.reset()
        }
    }

    /**
     * 状态：  Normal / Loading / Play / Pause / End
     *
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun ringstoneItemClicked(
        ringtone: Ringtone,
        holder: RingstoneHolder,
        position: Int,
    ) {

        var url = ringtone.url
        if (whichactivity == WHICHACTIVITY.DOWNLOAD_ACTIVITY.ordinal || RingtoneActionUtils.isRingtoneInSdcard(
                requireContext(),
                ringtone)
        ) {
            url = RingtoneActionUtils.getRingtoneLocalPath(url)
            Log.i(TAG, "$url")
        }

        //换了一个歌曲
        if (this.currentUrl != url && this.currentUrl != "") {

            recoveryUiState()
            this.currentUrl = url

            this.ringstoneItemClicked(ringtone, holder, position)
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
                    imageView.setImageResource(R.drawable.ic_play)
                }
            }
            "Pause" -> {
                mediaHolder!!.start()
                valueAnimator.resume()
                imageView.tag = "Play"
                imageView.setImageResource(R.drawable.ic_pause)
            }
            "End" -> {
                imageView.tag = "Play"

                Log.i(TAG, "动画结束了End")
                valueAnimator.start()
                mediaHolder!!.seekTo(0)
                mediaHolder!!.start()
                imageView.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun ringstoneStartState(
        imageView: ImageView,
        progressBar: ProgressBar?,
        url: String,
        holder: RingstoneHolder,
    ) {
        imageView.tag = "Loading"
        progressBar?.visibility = View.VISIBLE

        mediaHolder!!.setDataSource(url, object : MediaHolder.MediaAction {
            override fun doAction() {
                imageView.setImageResource(R.drawable.ic_pause)
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
                        imageView.setImageResource(R.drawable.ic_play)
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
            backgroundView.requestLayout()
            if (backgroundView.layoutParams.width > 1) {
                backgroundView.visibility = View.VISIBLE
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
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                backgroundView!!.layoutParams.width = 0
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })
    }

    override fun onResume() {
        super.onResume()

        mediaHolder!!.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaHolder!!.release()

        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(myBroadcastReceiver!!)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()
        if (mediaHolder!!.isPlaying()) {
            this.playView!!.tag = "Pause"
            this.playView!!.setImageResource(R.drawable.ic_play)
            mediaHolder!!.pause()
            valueAnimator.pause()
        }

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
                    var filename = intent.getStringExtra(MyIntentService.FILENAME)
                    Log.i(TAG, filename!!)
                    var status = intent.getIntExtra(MyIntentService.STATUS, 0)
                    Log.i(TAG, "status = $status")
                    var title = intent.getStringExtra(MyIntentService.TITLE)

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {

//                            Snackbar.make(SuperAwesomeCardFragment.rootView,
//                                "Download successful.",
//                                Snackbar.LENGTH_LONG).show()
                            Log.i(TAG,"ACTION_THREAD_STATUS")
                            Toast.makeText(context,"Download successful.",Toast.LENGTH_SHORT).show()

                            RingtoneActionUtils.setMyRingtoneWithFileName(activityForSetRingtone,
                                filename)

                            ringtoneViewModel.updateByTitle(title!!, true)
                        }
                        DownloadManager.STATUS_FAILED -> {

//                            Snackbar.make(SuperAwesomeCardFragment.rootView,
//                                "Download failed.",
//                                Snackbar.LENGTH_LONG).show()

                            Toast.makeText(context,"Download failed.",Toast.LENGTH_LONG).show()
                        }
                    }

                    Log.i(TAG, "SuperAwesomeCardFragment.ACTION_THREAD_STATUS")
                }
            }
        }

    }

    fun startDownloadService(activity: Activity, ringtone: Ringtone) {
        var intent = Intent(activity, MyIntentService(ringtone.title)::class.java)

        val filename = RingtoneActionUtils.getFileNameFromUrl(ringtone.url)
        intent.putExtra(MyIntentService.URL, ringtone.url)
        intent.putExtra(MyIntentService.FILENAME, filename)
        intent.putExtra(MyIntentService.TITLE, ringtone.title)

        activity.baseContext.startService(intent)
    }

    companion object {
        val CODE_WRITE_SETTINGS_PERMISSION = 10
        val ACTION_THREAD_STATUS = "action_thread_status"
        val ACTION_INTENTSERVICE_STATUS = "action_intentservice_status"

        lateinit var activityForSetRingtone: Activity
        lateinit var ringtoneViewModel: RingtoneViewModel

        var myBroadcastReceiver: MyBroadcastReceiver? = null

        val EXTRA_REPLY = "com.xxh.ringbones.REPLY"
        val ringFileList = arrayOf(
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

        @JvmStatic
        fun newInstance(position: Int, whichActivity: Int, search: String = "2020") =
            SuperAwesomeCardFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITON, position)
                    putInt(WHTICH, whichActivity)
                    putString(SEARCH, search)
                }
            }

        fun prepareRingtonesData(context: Context, fileName: String): MutableList<Ringtone> {
            var ringtonesArray = mutableListOf<Ringtone>()

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
                    Ringtone::class.java
                )
                newRingstone.tag = "test"
//                newRingstone.ringtoneId = UUID.randomUUID().toString()
                newRingstone.ringtoneId = 0

                ringtonesArray.add(newRingstone)
            }
            return ringtonesArray
        }
    }

    fun setLoadingVisible() {
        binding.adsLoadingBg.visibility = View.VISIBLE
    }

    fun setLoadingInVisible() {
        binding.adsLoadingBg.visibility = View.INVISIBLE
    }

    fun test(ringtone: Ringtone) {
        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY, Gson().toJson(ringtone))
        this.requireActivity().setResult(AppCompatActivity.RESULT_OK, replyIntent)
    }
}