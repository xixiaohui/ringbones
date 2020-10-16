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
import com.xxh.ringbones.data.Ringtone

import com.xxh.ringbones.utils.DownloadManagerTest
import com.xxh.ringbones.utils.RingtoneActionUtils

class RingtoneListAdapter(
    private var data: MutableList<Ringtone>?,
    private val clickPlayListener: (Ringtone, RingstoneHolder, Int) -> Unit,
    private val clickSetListener: (Ringtone, url: String) -> Unit,
    private val clickFavListener: (Ringtone, select: Boolean) -> Unit,
    private val clickDownloadListener: (Ringtone)->Unit,
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
        val ringtone: Ringtone = data!![position]
        holder.bind(ringtone, clickPlayListener, position, clickSetListener, clickFavListener,clickDownloadListener)

        holder.setFavButtonSelect(ringtone.isFav)
        holder.setRingtoneButtonSelect(ringtone.isRingtone)
//        holder.setBackgroundVisibility(View.INVISIBLE)
//        holder.setPlayerButtonPlay()
    }

    internal fun setRingtones(ringtones: List<Ringtone>) {
        this.data = ringtones as MutableList<Ringtone>
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
    private fun setBackgroundVisibility(value: Int) {
        mView!!.visibility = value
    }

    private fun setBackgroundWidth(value: Int) {
        mView!!.layoutParams.width = value
    }

    private fun setPlayerButtonPlay() {
        mPlay!!.setImageResource(R.drawable.ic_play)
    }

    fun setFavButtonSelect(select: Boolean) {
        if (select) {
            mHeart!!.tag = RingtoneActionUtils.SELECT
            mHeart!!.setImageResource(R.drawable.heart)
        } else {
            mHeart!!.tag = RingtoneActionUtils.UNSELECT
            mHeart!!.setImageResource(R.drawable.emptyheart)
        }
    }

    fun setRingtoneButtonSelect(select: Boolean) {
        if (select) {
            mSet!!.tag = RingtoneActionUtils.SELECT
            mSet!!.setImageResource(R.drawable.ring)
        } else {
            mSet!!.tag = RingtoneActionUtils.UNSELECT
            mSet!!.setImageResource(R.drawable.notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(
        ringtone: Ringtone,
        clickPlayListener: (Ringtone, RingstoneHolder, Int) -> Unit,
        position: Int,
        clickSetListener: (Ringtone, url: String) -> Unit,
        clickFavListener: (Ringtone, select: Boolean) -> Unit,
        clickDownloadListener: (Ringtone)->Unit
    ) {
        mTitle?.text = ringtone.title
        mTag?.text = ringtone.des

        mSet?.setOnClickListener {
            if (mSet!!.tag == RingtoneActionUtils.UNSELECT) {
                mSet!!.tag = RingtoneActionUtils.SELECT
//                mSet!!.setImageResource(R.drawable.ring)
            } else {
                mSet!!.tag = RingtoneActionUtils.UNSELECT
//                mSet!!.setImageResource(R.drawable.notification)
            }
            clickSetListener(ringtone, ringtone.url)
        }

        mHeart?.setOnClickListener {
            if (mHeart!!.tag == RingtoneActionUtils.UNSELECT) {
                mHeart!!.tag = RingtoneActionUtils.SELECT
//                mHeart!!.setImageResource(R.drawable.heart)
            } else {
                mHeart!!.tag = RingtoneActionUtils.UNSELECT
//                mHeart!!.setImageResource(R.drawable.emptyheart)
            }
            clickFavListener(ringtone, mHeart!!.tag == RingtoneActionUtils.SELECT)
        }

        mPlay?.setOnClickListener {
            clickPlayListener(ringtone, this, position)
        }

        mDownload?.setOnClickListener {

            clickDownloadListener(ringtone)

//            if (RingtoneActionUtils.isRingtoneInSdcard(it.context,ringtone)){
//                MaterialAlertDialogBuilder(it.context)
//                    .setTitle(mDownload!!.context.getString(R.string.hi))
//                    .setMessage(mDownload!!.context.getString(R.string.download_tips_already_have)).show()
//            }else{
//                MaterialAlertDialogBuilder(it.context)
//                    .setTitle(mDownload!!.context.getString(R.string.hi))
//                    .setMessage(mDownload!!.context.getString(R.string.download_tips))
//                    .setNegativeButton(it.context.resources.getString(R.string.cancel)) { dialog, which ->
//                        // Respond to negative button press
//                    }
//                    .setPositiveButton(it.context.resources.getString(R.string.ok)) { dialog, which ->
//                        // Respond to positive button press
//                        var url = ringtone.url
//                        var title = RingtoneActionUtils.getFileNameFromUrl(ringtone.url)
//                        DownloadManagerTest.download(it.context, url, title!!)
//                    }
//                    .show()
//            }


        }
    }

    fun reset() {
        this.setBackgroundWidth(0)
        this.setPlayerButtonPlay()

        this.getPlay()!!.tag = "Normal"

        this.setBackgroundVisibility(View.INVISIBLE)
        this.getProgressBar()!!.visibility = View.INVISIBLE
    }

}