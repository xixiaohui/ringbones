package com.xxh.ringbones.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "ringtone")
data class NewRingstone(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "des")
    val des: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "tag")
    val tag: String = "test"
) {
    @PrimaryKey
    var ringtoneId: String = UUID.randomUUID().toString()

}