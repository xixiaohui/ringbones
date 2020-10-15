package com.xxh.ringbones.databases

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.Ringtone
import com.xxh.ringbones.fragments.SuperAwesomeCardFragment


@Database(entities = [Ringtone::class], version = 2, exportSchema = false)
abstract class RingtoneRoomDatabase : RoomDatabase() {

    abstract fun ringtoneDao(): RingtoneDao

    companion object {

        @Volatile
        private var INSTANCE: RingtoneRoomDatabase? = null

        fun getDatabase(
            context: Context,
        ): RingtoneRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RingtoneRoomDatabase::class.java,
                    "/storage/emulated/0/data/ringtone_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(RingtoneDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RingtoneDatabaseCallback(val context: Context) :
            RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE!!).execute(context)
            }
        }


        /**
         * Populate the database in the background.
         */
        private class PopulateDbAsync(db: RingtoneRoomDatabase) :
            AsyncTask<Context, Void?, Void?>() {
            private val mDao: RingtoneDao = db.ringtoneDao()
//            var words = arrayOf("dolphin", "crocodile", "cobra")

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

            override fun doInBackground(vararg params: Context): Void? {
                // Start the app with a clean database every time.
                // Not needed if you only populate the database
                // when it is first created

//                mDao.deleteAll()
//                var newRingstone = NewRingstone("Astronomia Mp3 Tone","2020 ringtone",
//                    "https://www.tonesmp3.com/ringtones/astronomia-mp3-tone.mp3")
//
//                var newRingstone1 = NewRingstone("Reliance Jio Phone","2020 ringtone",
//                    "https://www.tonesmp3.com/ringtones/reliance-jio-phone.mp3")
//
//                var newRingstone2 = NewRingstone("Senorita Saxophone","2020 ringtone",
//                    "https://www.tonesmp3.com/ringtones/senorita-saxophone.mp3")
//
//                mDao.insertAll(newRingstone,newRingstone1,newRingstone2)


                ringFileList.forEach {
                    val ringtonesArray = SuperAwesomeCardFragment.prepareRingtonesData(
                        params[0],
                        "rings/${it}.json"
                    )
                    ringtonesArray.forEach{
                        mDao.insert(it)
                    }
                }

                return null
            }

        }


    }


}