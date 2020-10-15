package com.xxh.ringbones.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "ringtone",primaryKeys = ["title", "id"])
data class Ringtone(

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "des")
    val des: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "tag")
    var tag: String = "test",

    @ColumnInfo(name="isFav")
    var isFav: Boolean = false,

    //是否被设置过铃声
    @ColumnInfo(name="isRingtone")
    var isRingtone: Boolean = false,

    @ColumnInfo(name = "id")
    var ringtoneId: Int = 0,
) {

}