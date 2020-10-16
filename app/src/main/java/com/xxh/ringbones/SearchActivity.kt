package com.xxh.ringbones

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.material.appbar.MaterialToolbar
import com.xxh.ringbones.databinding.ActivitySearchBinding
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment
import com.xxh.ringbones.fragments.WHICHACTIVITY
import com.xxh.ringbones.utils.MyRewardedAdHandler
import com.xxh.ringbones.utils.RewardedAdUtils


class SearchActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySearchBinding

    var handler: Handler? = null
    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = this.supportFragmentManager
        val fa: FragmentTransaction = fm.beginTransaction()
        val superAwesomeCardFragment = SuperAwesomeCardFragment.newInstance(0,
            WHICHACTIVITY.SEARCH_ACTIVITY.ordinal)
        fa.replace(R.id.container_port, superAwesomeCardFragment)
        fa.commit()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        MainActivity.jumpToOtherActivity(this, topAppBar)

        mRewardedAd = RewardedAdUtils.initRewardedAd(this)
        handler = MyRewardedAdHandler(this, mRewardedAd!!)


        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.search.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isIconifiedByDefault = false
            isSubmitButtonEnabled = true
            isQueryRefinementEnabled  = true
        }

        val searchView = binding.search
        val id: Int = searchView.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val textView = searchView.findViewById(id) as TextView
        textView.setTextColor(Color.WHITE)
        textView.setHintTextColor(Color.parseColor("#CCCCCC"))

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
//                doMySearch(query)
                Log.i("SearchActivity", query.toString())

                val fm = this.supportFragmentManager
                val fa: FragmentTransaction = fm.beginTransaction()
                val superAwesomeCardFragment = SuperAwesomeCardFragment.newInstance(0,
                    WHICHACTIVITY.SEARCH_ACTIVITY.ordinal,
                    search = query.toString())
                fa.replace(R.id.container_port, superAwesomeCardFragment)
                fa.commit()

            }
        }




    }
}