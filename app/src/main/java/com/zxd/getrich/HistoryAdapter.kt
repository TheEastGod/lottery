package com.zxd.getrich

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView

/**
 * @author  zhongxd
 * @date 2024/7/21.
 * description：
 */
class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var mLottery = ArrayList<LotteryEntity>()
    private var mNeedLoadMore = false
    fun setDate(valueEntity: ArrayList<LotteryEntity>, isNeedLoadMore : Boolean) {
        mLottery =  valueEntity
        mNeedLoadMore = isNeedLoadMore
    }
    // 添加加载更多的监听器
    fun setOnLoadMoreListener(onLoadMoreListener : OnLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.timeView.text = mLottery[position].lotteryDrawTime
            holder.resultView.text = mLottery[position].lotteryDrawResult

            if (position == mLottery.size -1 && mNeedLoadMore && mOnLoadMoreListener != null)  {
                holder.itemView.post {
                    mOnLoadMoreListener!!.onLoadMore()
                }
            }

    }

    override fun getItemCount(): Int {
        return mLottery.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeView : TextView = itemView.findViewById(R.id.lotteryDrawTime)
        val resultView : TextView = itemView.findViewById(R.id.lotteryDrawResult)
    }

    // 接口，用于加载更多数据
    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}