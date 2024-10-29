package com.zxd.getrich

import android.app.Application
import android.content.Context

/**
 * @author  zhongxd
 * @date 2024/7/22.
 * description：
 */
class MyApp : Application() {

    companion object{
        private lateinit var mContext: Context

        fun getContext(): Context{
            return mContext
        }
    }
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }


}