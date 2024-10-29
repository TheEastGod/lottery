package com.zxd.getrich

import androidx.room.*

/**
 * @author  zhongxd
 * @date 2024/7/22.
 * description：
 */
@Dao
interface LotteryDao {

    @Query("select count(*) from lotteryHistory")
    fun getLotteryCount() : Int

    @Query("select * from lotteryHistory limit :limitNum offset :offset")
    fun queryLottery(limitNum : Int,offset : Int) : MutableList<LotteryEntity>

    @Insert
    fun insertLottery(vararg lotteryEntity: LotteryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBatchLottery(list : MutableList<LotteryEntity>)

    @Delete
    fun deleteLottery(vararg  lotteryEntity: LotteryEntity)

    @Query("DELETE FROM lotteryHistory")
    fun deleteAllLottery()
}