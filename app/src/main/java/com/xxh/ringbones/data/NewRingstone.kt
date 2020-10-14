package com.xxh.ringbones.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "ringtone",primaryKeys = ["title", "id"])
data class NewRingstone(

    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "des")
    val des: String,
    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "tag")
    var tag: String = "test",

    @ColumnInfo
    var isFav: Boolean = false,

    @ColumnInfo(name = "id")
    var ringtoneId: Int = 0,
) {

}