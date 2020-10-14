package com.xxh.ringbones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.adapter.RingstoneHolder
import com.xxh.ringbones.adapter.RingtoneListAdapter
import com.xxh.ringbones.data.NewRingstone
import com.xxh.ringbones.databinding.ActivityDownloadBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.fragments.WHICHACTIVITY
import com.xxh.ringbones.models.RingtoneViewModel
import com.xxh.ringbones.utils.KotlinUtils

/**
 * 把 /storage/emulated/0/Ringtones下的全部音乐文件展示出来
 */
class DownloadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = this.supportFragmentManager
        val fa: FragmentTransaction = fm.beginTransaction()
        val superAwesomeCardFragment = SuperAwesomeCardFragment.newInstance(0, WHICHACTIVITY.DOWNLOAD_ACTIVITY.ordinal)
        fa.replace(R.id.container_port_download,superAwesomeCardFragment)
        fa.commit()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

    }

}