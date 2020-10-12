package com.xxh.ringbones

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.ActivityFavBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.KotlinUtils
import androidx.activity.viewModels as viewModels

class FavActivity : AppCompatActivity() {
    private val TAG = "FavActivity"



    private lateinit var binding: ActivityFavBinding
    private lateinit var ringtoneViewModel: RingtoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_fav)
        setContentView(binding.root)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val recyclerView = binding.recyclerview

        val adapter = RingtoneListAdapter(this.baseContext)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setItemViewCacheSize(500)

        val permission: Boolean = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (permission) {
            Log.i(TAG, "有写入权限")
            this.ringtoneViewModel = ViewModelProvider(this).get(RingtoneViewModel::class.java)

            ringtoneViewModel.getAllRingtones().observe(this, Observer { ringtones ->
                ringtones?.let {
                    adapter.setRingtones(it)
                }
            })

        }

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//            var newRingstone = NewRingstone("Senotttttttttttttt","2020 ringtone",
//                "https://www.tonesmp3.com/ringtones/senorita-saxophone.mp3")
//            this.ringtoneViewModel.insert(newRingstone)
//        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode === RESULT_OK) {
            val jsonData = data!!.getStringExtra(SuperAwesomeCardFragment.EXTRA_REPLY)
            val ringtone = Gson().fromJson(jsonData,NewRingstone::class.java)
            this.ringtoneViewModel.insert(ringtone)

        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    class RingtoneListAdapter internal constructor(context: Context) :
        RecyclerView.Adapter<RingtoneListAdapter.RingtoneViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var ringtones = emptyList<NewRingstone>() // Cached copy of words

        inner class RingtoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.ringtone_share_card)
            val tagTextView: TextView = itemView.findViewById(R.id.ringtone_share_tag)
        }

        internal fun setRingtones(ringtones: List<NewRingstone>) {
            this.ringtones = ringtones
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
            val itemView = inflater.inflate(R.layout.ringbox, parent, false)
            return RingtoneViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
            val current = ringtones[position]
            holder.titleTextView.text = current.title
            holder.tagTextView.text = current.des
        }

        override fun getItemCount(): Int {
            return ringtones.size
        }

    }

    companion object{
        val NEW_WORD_ACTIVITY_REQUEST_CODE = 1
    }


}