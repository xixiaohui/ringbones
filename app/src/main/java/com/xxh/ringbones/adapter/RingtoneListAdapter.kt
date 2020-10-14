package com.xxh.ringbones.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xxh.ringbones.R
import com.xxh.ringbones.data.NewRingstone

import com.xxh.ringbones.utils.DownloadManagerTest
import com.xxh.ringbones.utils.KotlinUtils

class RingtoneListAdapter(
    private var data: MutableList<NewRingstone>?,
    private val clickPlayListener: (NewRingstone, RingstoneHolder, Int) -> Unit,
    private val clickSetListener: (NewRingstone, url: String) -> Unit,
    private val clickFavListener: (NewRingstone, select: Boolean) -> Unit
) :
    RecyclerView.Adapter<RingstoneHolder>() {
    private val TAG: String = "RingtoneListAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingstoneHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ringbox, parent, false)
        return RingstoneHolder(v)
    }

    override fun getItemCount(): Int {

        if (data != null) {
            return data!!.size
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RingstoneHolder, position: Int) {
        val ringstone: NewRingstone = data!![position]
        holder.bind(ringstone, clickPlayListener, position, clickSetListener, clickFavListener)

        holder.setFavButtonSelect(ringstone.isFav)

    }

    internal fun setRingtones(ringtones: List<NewRingstone>) {
        this.data = ringtones as MutableList<NewRingstone>
        notifyDataSetChanged()
    }
}

class RingstoneHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private var mTitle: TextView? = null
    private var mTag: TextView? = null
    private var mSet: ImageView? = null
    private var mHeart: ImageView? = null
    private var mPlay: ImageView? = null
    private var mDownload: ImageView? = null
    private var mProgressBar: ProgressBar? = null
    private var mView: View? = null

    init {
        mTitle = itemView.findViewById(R.id.ringtone_share_card)
        mSet = itemView.findViewById(R.id.ringtone_set)
        mTag = itemView.findViewById(R.id.ringtone_share_tag)
        mHeart = itemView.findViewById(R.id.ringtone_fav)
        mPlay = itemView.findViewById(R.id.ringtone_card_play)
        mProgressBar = itemView.findViewById(R.id.ringtone_progress_bar)
        mDownload = itemView.findViewById(R.id.ringtone_download)
        mView = itemView.findViewById(R.id.ringtone_background)
    }

    fun getPlay(): ImageView? {
        return mPlay
    }

    fun getProgressBar(): ProgressBar? {
        return mProgressBar
    }

    fun getBackgroundView(): View? {
        return mView
    }

    /**
     * value : View.INVISIBLE | View.VISIBLE
     */
    fun setBackgroundVisibility(value: Int) {
        mView!!.visibility = value
    }

    fun setBackgroundWidth(value: Int) {
        mView!!.layoutParams.width = value
    }

    fun setPlayerButtonPlay() {
        mPlay!!.setImageResource(R.drawable.ic_play)
    }

    fun setFavButtonSelect(select: Boolean){

        if(select){
            mHeart!!.tag = KotlinUtils.SELECT
            mHeart!!.setImageResource(R.drawable.heart)
        }else{
            mHeart!!.tag = KotlinUtils.UNSELECT
            mHeart!!.setImageResource(R.drawable.emptyheart)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(
        ringstone: NewRingstone,
        clickPlayListener: (NewRingstone, RingstoneHolder, Int) -> Unit,
        position: Int,
        clickSetListener: (NewRingstone, url: String) -> Unit,
        clickFavListener: (NewRingstone, select: Boolean) -> Unit,
    ) {
        mTitle?.text = ringstone.title
        mTag?.text = ringstone.des

        mSet?.setOnClickListener {
            if (mSet!!.tag == KotlinUtils.UNSELECT) {
                var url = ringstone.url
                clickSetListener(ringstone, url)

                mSet!!.tag = KotlinUtils.SELECT
                mSet!!.setImageResource(R.drawable.ring)
            } else {
                mSet!!.tag = KotlinUtils.UNSELECT
                mSet!!.setImageResource(R.drawable.notification)
            }
        }

        mHeart?.setOnClickListener {
            if (mHeart!!.tag == KotlinUtils.UNSELECT) {
                mHeart!!.tag = KotlinUtils.SELECT
                mHeart!!.setImageResource(R.drawable.heart)
            } else {
                mHeart!!.tag = KotlinUtils.UNSELECT
                mHeart!!.setImageResource(R.drawable.emptyheart)
            }
            clickFavListener(ringstone, mHeart!!.tag == KotlinUtils.SELECT)
        }

        mPlay?.setOnClickListener {
            clickPlayListener(ringstone, this, position)
        }

        mDownload?.setOnClickListener {
            MaterialAlertDialogBuilder(it.context)
                .setTitle(mDownload!!.context.getString(R.string.hi))
                .setMessage(mDownload!!.context.getString(R.string.download_tips))
                .setNegativeButton(it.context.resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(it.context.resources.getString(R.string.ok)) { dialog, which ->
                    // Respond to positive button press
                    var url = ringstone.url
                    var title = ringstone.title
                    DownloadManagerTest.download(it.context, url, title)
                }
                .show()
        }
    }

    fun reset() {
        this.setBackgroundWidth(0)
        this.setPlayerButtonPlay()

        this.getPlay()!!.tag = "Normal"

        this.setBackgroundVisibility(View.INVISIBLE)
        this.getProgressBar()!!.visibility = View.INVISIBLE
    }

    fun isLoading(): Boolean {
        return this.getPlay()!!.tag == "Loading"
    }
}