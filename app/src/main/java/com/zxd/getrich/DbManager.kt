package com.zxd.getrich

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @author  zhongxd
 * @date 2024/7/22.
 * description：
 */
object DbManager {

    //数据库名
    private const val dbName: String = "lotteryRoom"

    //懒加载创建数据库
    val db: LotteryRoom by lazy {
        Room.databaseBuilder(
           MyApp.getContext() , LotteryRoom::class.java, dbName
        ).allowMainThreadQueries()//允许在主线程操作
            .addCallback(DbCreateCallBack)//增加回调监听
            .addMigrations()//增加数据库迁移
            .build()
    }
    private object DbCreateCallBack : RoomDatabase.Callback() {
        //第一次创建数据库时调用
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.e("TAG", "first onCreate db version: " + db.version)
        }
    }
}