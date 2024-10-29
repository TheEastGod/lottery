package com.zxd.getrich

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_history.*
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * @author  zhongxd
 * @date 2024/7/21.
 * description：
 */
class HistoryActivity : AppCompatActivity(), HistoryAdapter.OnLoadMoreListener {
    private lateinit var mAdapter: HistoryAdapter
    private lateinit var mHandler: MyHandler
    private val lotteryDao = DbManager.db.lotteryDao()
    private var mLottery = ArrayList<LotteryEntity>()
    private var mPage = 1
    private val mLimitNum = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        mAdapter = HistoryAdapter()
        mAdapter.setOnLoadMoreListener(this)
        mHandler = MyHandler(this)
        initDate()
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = mAdapter
    }

    private fun initDate() {
        val lotteryCount = lotteryDao.getLotteryCount()
        val queryLottery = lotteryDao.queryLottery(mLimitNum,(mPage-1)*mLimitNum)
        mLottery.addAll(queryLottery)
        val isNeedLoadMore = mLottery.size < lotteryCount
        mAdapter.setDate(mLottery ,isNeedLoadMore)
        mAdapter.notifyDataSetChanged()
    }

    override fun onLoadMore() {
        mPage++
        initDate()
    }


    class MyHandler(activity: HistoryActivity) : Handler(Looper.getMainLooper()) {
        private var weakReference = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val activity = weakReference.get()
            if (activity != null && !activity.isFinishing) {

            }
        }
    }
}