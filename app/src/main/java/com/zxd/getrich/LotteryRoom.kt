package com.zxd.getrich

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @author  zhongxd
 * @date 2024/7/22.
 * description：
 */
@Database(entities = [LotteryEntity::class] , version = 1, exportSchema = true)
abstract class LotteryRoom : RoomDatabase() {

    abstract fun lotteryDao() : LotteryDao
}