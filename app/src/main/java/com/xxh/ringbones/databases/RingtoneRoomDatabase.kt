package com.xxh.ringbones.databases

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xxh.ringbones.daos.RingtoneDao
import com.xxh.ringbones.data.NewRingstone


@Database(entities = [NewRingstone::class], version = 6,exportSchema = false)
abstract class RingtoneRoomDatabase : RoomDatabase() {

    abstract fun ringtoneDao(): RingtoneDao

    companion object {

        @Volatile
        private var INSTANCE: RingtoneRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): RingtoneRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RingtoneRoomDatabase::class.java,
                    "/storage/emulated/0/data/ringtone_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(RingtoneDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RingtoneDatabaseCallback() :
            RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE!!).execute()
            }
        }


        /**
         * Populate the database in the background.
         */
        private class PopulateDbAsync(db: RingtoneRoomDatabase) :
            AsyncTask<Void?, Void?, Void?>() {
            private val mDao: RingtoneDao = db.ringtoneDao()
//            var words = arrayOf("dolphin", "crocodile", "cobra")

            override fun doInBackground(vararg params: Void?): Void?{
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
//                mDao.insert(newRingstone)
//                mDao.insert(newRingstone1)
//                mDao.insert(newRingstone2)

                return null
            }


        }


        fun populateDatabase(ringtoneDao: RingtoneDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            ringtoneDao.deleteAll()

            var newRingstone = NewRingstone("Astronomia Mp3 Tone","2020 ringtone",
                "https://www.tonesmp3.com/ringtones/astronomia-mp3-tone.mp3")

            var newRingstone1 = NewRingstone("Reliance Jio Phone","2020 ringtone",
                "https://www.tonesmp3.com/ringtones/reliance-jio-phone.mp3")

            var newRingstone2 = NewRingstone("Senorita Saxophone","2020 ringtone",
                "https://www.tonesmp3.com/ringtones/senorita-saxophone.mp3")

            ringtoneDao.insert(newRingstone)
            ringtoneDao.insert(newRingstone1)
            ringtoneDao.insert(newRingstone2)
        }
    }


}