package com.zxd.getrich

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author  zhongxd
 * @date 2024/7/21.
 * description：
 */
data class RichEntity(
    val dataFrom : String,
    val emptyFlag : Boolean,
    val errorCode : String,
    val errorMessage : String,
    val success : Boolean,
    val value : ValueEntity
)

data class ValueEntity(
    val pageNo : Int,
    val pageSize : Int,
    val pages : Int,
    val total : Int,
    val list : ArrayList<LotteryEntity>
)

@Entity(tableName = "lotteryHistory")
data class LotteryEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    val lotteryDrawNum : String,
    @ColumnInfo(name = "result")
    val lotteryDrawResult : String,
    @ColumnInfo(name = "time")
    val lotteryDrawTime : String
)