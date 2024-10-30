package com.zxd.getrich

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room.*
import okhttp3.*
import java.io.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * @author  zhongxd
 * @date 2024/7/23.
 * description：
 */
class RoomActivity : AppCompatActivity() {
    private var mPage = 1
    private val url = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry"
    private val gameNo = "85"
    private val provinceId = "0"
    private val isVerify = "1"
    private val pageSize = "30"
    private val mOkHttpClient: OkHttpClient? = HttpUtil.buildOKHttpClient()?.build()
    private val mGson = Gson()
    private val lotteryDao = DbManager.db.lotteryDao()
    private lateinit var progressDialog: ProgressDialog
    private val coefficient = 5.0f

    private val frontAllCombinationMap = HashMap<String, Int>()
    private val backAllCombinationMap = HashMap<String, Int>()
    private var lotteryCount = 0
    var frontCardinalityCount = 0
    var frontEvenCount = 0
    var v7Count = 0
    var v14Count = 0
    var v21Count = 0
    var v28Count = 0
    var v35Count = 0
    var backCardinalityCount = 0
    var backEvenCount = 0
    var v4Count = 0
    var v8Count = 0
    var v12Count = 0

    var totalFrontCardinalityCount = 0f
    var totalFrontEvenCount = 0f
    var totalV7Count = 0f
    var totalV14Count = 0f
    var totalV21Count = 0f
    var totalV28Count = 0f
    var totalV35Count = 0f
    var totalBackCardinalityCount = 0f
    var totalBackEvenCount = 0f
    var totalV4Count = 0f
    var totalV8Count = 0f
    var totalV12Count = 0f

    private val totalFrontAllCombinationMap = HashMap<String, Float>()
    private val totalBackAllCombinationMap = HashMap<String, Float>()
    private var v10CountPercent : Double = 0.0
    private var v20CountPercent: Double = 0.0
    private var v30CountPercent: Double = 0.0
    private var v35CountPercent: Double = 0.0

    private var frontCardinalityPercent: Double = 0.0
    private var frontEvenCountPercent: Double = 0.0

    private val hot15FrontList = ArrayList<String>()
    private val cold15FrontList = ArrayList<String>()
    private val veryHot15FrontList = ArrayList<String>()
    private val veryCold15FrontList = ArrayList<String>()

    private var v6CountPercent: Double = 0.0
    private var v12CountPercent: Double = 0.0

    private var backCardinalityPercent : Double = 0.0
    private var backEvenCountPercent : Double = 0.0

    private val hot15BackList = ArrayList<String>()
    private val cold15BackList = ArrayList<String>()

    private val veryHot15BackList = ArrayList<String>()
    private val veryCold15BackList = ArrayList<String>()


    private val myOffset = 0
    private val mapFrontFor15 = HashMap<String, Int>()
    private val mapBackFor15 = HashMap<String, Int>()
    private val mapFrontFor49 = HashMap<String, Int>()
    private val mapBackFor49 = HashMap<String, Int>()
    private val mapFrontFor50 = HashMap<String, Int>()
    private val mapBackFor50 = HashMap<String, Int>()
    private val mapFrontFor100 = HashMap<String, Int>()
    private val mapBackFor100 = HashMap<String, Int>()
    private val mapFrontFor150 = HashMap<String, Int>()
    private val mapBackFor150 = HashMap<String, Int>()
    private val mapFrontFor200 = HashMap<String, Int>()
    private val mapBackFor200 = HashMap<String, Int>()

    var frontCardinalityCountFor49 = 0
    var frontEvenCountFor49 = 0
    var v7CountFor49 = 0
    var v14CountFor49 = 0
    var v21CountFor49 = 0
    var v28CountFor49 = 0
    var v35CountFor49 = 0
    var backCardinalityCountFor49 = 0
    var backEvenCountFor49 = 0
    var v4CountFor49 = 0
    var v8CountFor49 = 0
    var v12CountFor49 = 0

    var frontCardinalityCountFor50 = 0
    var frontEvenCountFor50 = 0
    var v7CountFor50 = 0
    var v14CountFor50 = 0
    var v21CountFor50 = 0
    var v28CountFor50 = 0
    var v35CountFor50 = 0
    var backCardinalityCountFor50 = 0
    var backEvenCountFor50 = 0
    var v4CountFor50 = 0
    var v8CountFor50 = 0
    var v12CountFor50 = 0

    var frontCardinalityCountForA = 0.0
    var frontEvenCountForA  = 0.0
    var v7CountForA = 0.0
    var v14CountForA = 0.0
    var v21CountForA = 0.0
    var v28CountForA = 0.0
    var v35CountForA = 0.0
    var backCardinalityCountForA = 0.0
    var backEvenCountForA = 0.0
    var v4CountForA = 0.0
    var v8CountForA = 0.0
    var v12CountForA = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        checkPermission()
        delete("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/lottery/calculateResult.txt")
        progressDialog = ProgressDialog(this)
        initRoomView.setOnClickListener {
            progressDialog.setMessage("加载数据中，请等待")
            progressDialog.setCancelable(false)
            progressDialog.show()
            lotteryDao.deleteAllLottery()
            initDate()
        }
        getResultView.setOnClickListener { getResult() }

        predictResultView.setOnClickListener { predictResult() }

        initProbability()
    }

    private fun initProbability() {
        Thread{
            //500次总共的数据，然后算50次平均数据
            lotteryCount = lotteryDao.getLotteryCount()
            computeLottery(lotteryCount, 0, false)
            val averageCount = 50.0f / lotteryCount
            val allNumberString = java.lang.StringBuilder()

            for (entry in frontAllCombinationMap) {
                totalFrontAllCombinationMap[entry.key] = entry.value * averageCount
                allNumberString.append( entry.key + ":" + entry.value * averageCount + ", ")
            }
            allNumberString.append("\n\n")

            for (entry in backAllCombinationMap) {
                totalBackAllCombinationMap[entry.key] = entry.value * averageCount
                allNumberString.append( entry.key + ":" + entry.value * averageCount + ", ")
            }
            totalFrontCardinalityCount = frontCardinalityCount * averageCount
            totalFrontEvenCount = frontEvenCount * averageCount
            totalV7Count = v7Count * averageCount
            totalV14Count = v14Count * averageCount
            totalV21Count = v21Count * averageCount
            totalV28Count = v28Count * averageCount
            totalV35Count = v35Count * averageCount
            totalBackCardinalityCount = backCardinalityCount * averageCount
            totalBackEvenCount = backEvenCount * averageCount
            totalV4Count = v4Count * averageCount
            totalV8Count = v8Count * averageCount
            totalV12Count = v12Count * averageCount


            computeLottery(50,0 + myOffset,false)
            mapFrontFor50.putAll(frontAllCombinationMap)
            mapBackFor50.putAll(backAllCombinationMap)
            frontCardinalityCountFor50 = frontCardinalityCount
            frontEvenCountFor50 = frontEvenCount
            v7CountFor50 = v7Count
            v14CountFor50 = v14Count
            v21CountFor50 = v21Count
            v28CountFor50 = v28Count
            v35CountFor50 = v35Count
            backCardinalityCountFor50 = backCardinalityCount
            backEvenCountFor50 = backEvenCount
            v4CountFor50 = v4Count
            v8CountFor50 = v8Count
            v12CountFor50 = v12Count

            frontCardinalityCountForA += frontCardinalityCount
            frontEvenCountForA += frontEvenCount
            v7CountForA += v7Count
            v14CountForA += v14Count
            v21CountForA += v21Count
            v28CountForA += v28Count
            v35CountForA += v35Count
            backCardinalityCountForA += backCardinalityCount
            backEvenCountForA += backEvenCount
            v4CountForA += v4Count
            v8CountForA += v8Count
            v12CountForA += v12Count


            computeLottery(50,50 + myOffset,false)
            mapFrontFor100.putAll(frontAllCombinationMap)
            mapBackFor100.putAll(backAllCombinationMap)
            frontCardinalityCountForA += frontCardinalityCount
            frontEvenCountForA += frontEvenCount
            v7CountForA += v7Count
            v14CountForA += v14Count
            v21CountForA += v21Count
            v28CountForA += v28Count
            v35CountForA += v35Count
            backCardinalityCountForA += backCardinalityCount
            backEvenCountForA += backEvenCount
            v4CountForA += v4Count
            v8CountForA += v8Count
            v12CountForA += v12Count

            computeLottery(50,100 + myOffset,false)
            mapFrontFor150.putAll(frontAllCombinationMap)
            mapBackFor150.putAll(backAllCombinationMap)
            frontCardinalityCountForA += frontCardinalityCount
            frontEvenCountForA += frontEvenCount
            v7CountForA += v7Count
            v14CountForA += v14Count
            v21CountForA += v21Count
            v28CountForA += v28Count
            v35CountForA += v35Count
            backCardinalityCountForA += backCardinalityCount
            backEvenCountForA += backEvenCount
            v4CountForA += v4Count
            v8CountForA += v8Count
            v12CountForA += v12Count

            computeLottery(50,150 + myOffset,false)
            mapFrontFor200.putAll(frontAllCombinationMap)
            mapBackFor200.putAll(backAllCombinationMap)
            frontCardinalityCountForA = (frontCardinalityCountForA + frontCardinalityCount) / 4f
            frontEvenCountForA = (frontEvenCountForA + frontEvenCount) / 4f
            v7CountForA = (v7CountForA + v7Count) / 4f
            v14CountForA = (v14CountForA + v14Count) / 4f
            v21CountForA = (v21CountForA + v21Count) / 4f
            v28CountForA = (v28CountForA + v28Count) / 4f
            v35CountForA = (v35CountForA + v35Count) /4f
            backCardinalityCountForA = (backCardinalityCountForA + backCardinalityCount) / 4f
            backEvenCountForA = (backEvenCountForA + backEvenCount) /4f
            v4CountForA = (v4CountForA + v4Count) / 4f
            v8CountForA = (v8CountForA + v8Count) / 4f
            v12CountForA = (v12CountForA + v12Count)  /4f


            //算49次的基偶，区间概率
            computeLottery(49,0 + myOffset,false)
            frontCardinalityCountFor49 = frontCardinalityCount
            frontEvenCountFor49 = frontEvenCount
            v7CountFor49 = v7Count
            v14CountFor49 = v14Count
            v21CountFor49 = v21Count
            v28CountFor49 = v28Count
            v35CountFor49 = v35Count
            backCardinalityCountFor49 = backCardinalityCount
            backEvenCountFor49 = backEvenCount
            v4CountFor49 = v4Count
            v8CountFor49 = v8Count
            v12CountFor49 = v12Count
            mapFrontFor49.putAll(frontAllCombinationMap)
            mapBackFor49.putAll(backAllCombinationMap)

            var hot15FrontString = ""
            var cold15FrontString = ""
            //计算15次内热冷数
            computeLottery(15, 0 + myOffset, false)
            for (entry in frontAllCombinationMap) {
                if (entry.value > 2) {
                    hot15FrontList.add(entry.key)
                    hot15FrontString += entry.key + ","
                } else {
                    cold15FrontList.add(entry.key)
                    cold15FrontString += entry.key + ","
                }
            }

            var hot15BackString = ""
            var cold15BackString = ""

            for (entry in backAllCombinationMap) {
                if (entry.value > 2) {
                    hot15BackList.add(entry.key)
                    hot15BackString += entry.key + ","
                } else {
                    cold15BackList.add(entry.key)
                    cold15BackString += entry.key + ","
                }
            }

            //计算15到45次内热冷数
            computeLottery(30, 15 + myOffset, false)
            val hot30FrontList = ArrayList<String>()
            val cold30FrontList = ArrayList<String>()
            for (entry in frontAllCombinationMap) {
                if (entry.value > 4) {
                    hot30FrontList.add(entry.key)
                } else {
                    cold30FrontList.add(entry.key)
                }
            }
            val hot30BackList = ArrayList<String>()
            val cold30BackList = ArrayList<String>()
            for (entry in backAllCombinationMap) {
                if (entry.value > 4) {
                    hot30BackList.add(entry.key)
                } else {
                    cold30BackList.add(entry.key)
                }
            }

            //计算非常热，非常冷
            var veryHot15FrontString = ""
            var veryCold15FrontString = ""

            for (string in hot30FrontList) {
                if (cold15FrontList.contains(string)) {
                    veryCold15FrontString += "$string,"
                    cold15FrontString = cold15FrontString.replace("$string,", "")
                    veryCold15FrontList.add(string)
                    cold15FrontList.remove(string)
                }
            }
            for (string in cold30FrontList) {
                if (hot15FrontList.contains(string)) {
                    veryHot15FrontString += "$string,"
                    hot15FrontString = hot15FrontString.replace("$string,", "")
                    veryHot15FrontList.add(string)
                    hot15FrontList.remove(string)
                }
            }
            var veryHot5BackString = ""
            var veryCold15BackString = ""

            for (string in hot30BackList) {
                if (cold15BackList.contains(string)) {
                    veryCold15BackString += "$string,"
                    cold15BackString = cold15BackString.replace("$string,", "")
                    veryCold15BackList.add(string)
                    cold15BackList.remove(string)
                }
            }
            for (string in cold30BackList) {
                if (hot15BackList.contains(string)) {
                    veryHot5BackString += "$string,"
                    hot15BackString = hot15BackString.replace("$string,", "")
                    veryHot15BackList.add(string)
                    hot15BackList.remove(string)
                }
            }
            Log.i("mTag", "hot15FrontString $hot15FrontString")
            Log.i("mTag", "cold15FrontString $cold15FrontString")
            Log.i("mTag", "veryHot5FrontString $veryHot15FrontString")
            Log.i("mTag", "veryCold15FrontString $veryCold15FrontString")
            Log.i("mTag", "hot15BackString $hot15BackString")
            Log.i("mTag", "cold15BackString $cold15BackString")
            Log.i("mTag", "veryHot5BackString $veryHot5BackString")
            Log.i("mTag", "veryCold15BackString $veryCold15BackString")
        }.start()
    }

    private fun predictResult() {
        val stringBuilder = java.lang.StringBuilder()

        val frontResultBuilder = HashMap<String,Int>()
        val backResultBuilder = HashMap<String,Int>()

//        for (i in 0 until 20) {
            val frontResultSet = CopyOnWriteArrayList<String>()
            var currentV7Count = v7CountFor49
            var currentV14Count = v14CountFor49
            var currentV21Count = v21CountFor49
            var currentV28Count = v28CountFor49
            var currentV35Count = v35CountFor49

            var currentCardinalityCount = frontCardinalityCountFor49
            var currentEventCount = frontEvenCountFor49
            while (frontResultSet.size < 5) {
                val v7Percent = (0.2 + v7CountForA / 250 - v7CountFor49 / (currentV7Count + currentV14Count + currentV21Count + currentV28Count + currentV35Count)) * 10000
                val v14Percent = (0.2 + v14CountForA / 250 - v14CountFor49 / (currentV7Count + currentV14Count + currentV21Count + currentV28Count + currentV35Count)) * 10000
                val v21Percent = (0.2 + v21CountForA / 250 - v21CountFor49 / (currentV7Count + currentV14Count + currentV21Count + currentV28Count + currentV35Count)) * 10000
                val v28Percent = (0.2 + v28CountForA / 250 - v28CountFor49 / (currentV7Count + currentV14Count + currentV21Count + currentV28Count + currentV35Count)) * 10000
                val v35Percent = (0.2 + v35CountForA / 250 - v35CountFor49 / (currentV7Count + currentV14Count + currentV21Count + currentV28Count + currentV35Count)) * 10000

                val cardinalPercent =
                    (0.5 + frontCardinalityCountForA / 250 - currentCardinalityCount / (currentCardinalityCount + currentEventCount)) * 10000
                val eventPercent =
                    (0.5 + frontEvenCountForA / 250 - currentEventCount / (currentCardinalityCount + currentEventCount)) * 10000

                Log.i("myTag", "predictResult  v7Percent $v7Percent")
                Log.i("myTag", "predictResult  v14Percent $v14Percent")
                Log.i("myTag", "predictResult  v21Percent $v21Percent")
                Log.i("myTag", "predictResult  v28Percent $v28Percent")
                Log.i("myTag", "predictResult  v35Percent $v35Percent")

                Log.i("myTag", "predictResult  frontCardinalPercent $cardinalPercent")
                Log.i("myTag", "predictResult  frontEventPercent $eventPercent")

                val random = (Math.random() * 10000).toInt()
                Log.i("myTag", "predictResult   random front v35 $random")
                val frontResult = when {
                    random < v7Percent -> {
                        Log.i("myTag", "predictResult   v7Percent")
                        currentV7Count++
                        getFinalNum(1, 7,cardinalPercent,veryHot15FrontList,hot15FrontList,cold15FrontList,veryCold15FrontList,mapFrontFor49)
                    }
                    random < v7Percent + v14Percent -> {
                        Log.i("myTag", "predictResult   v14Percent")
                        currentV14Count++
                        getFinalNum(8, 14,cardinalPercent,veryHot15FrontList,hot15FrontList,cold15FrontList,veryCold15FrontList,mapFrontFor49)
                    }
                    random < v7Percent + v14Percent + v21Percent -> {
                        Log.i("myTag", "predictResult   v21Percent")
                        currentV21Count++
                        getFinalNum(15, 21,cardinalPercent,veryHot15FrontList,hot15FrontList,cold15FrontList,veryCold15FrontList,mapFrontFor49)
                    }
                    random < v7Percent + v14Percent + v21Percent + v28Percent -> {
                        Log.i("myTag", "predictResult   v28Percent")
                        currentV28Count++
                        getFinalNum(22, 28,cardinalPercent,veryHot15FrontList,hot15FrontList,cold15FrontList,veryCold15FrontList,mapFrontFor49)
                    }
                    else -> {
                        Log.i("myTag", "predictResult   v35CountNum")
                        currentV35Count++
                        getFinalNum(29, 35,cardinalPercent,veryHot15FrontList,hot15FrontList,cold15FrontList,veryCold15FrontList,mapFrontFor49)
                    }
                }
                if (frontResult.isNotEmpty() && !frontResultSet.contains(frontResult)) {
                    if (frontResult.toInt() % 2 == 0) {
                        currentEventCount++
                    }else {
                        currentCardinalityCount++
                    }
                    frontResultSet.add(frontResult)
                    if (frontResultBuilder.contains(frontResult)) {
                        val get = frontResultBuilder[frontResult]
                        frontResultBuilder[frontResult] =  1 + get!!
                    }else {
                        frontResultBuilder[frontResult] = 1
                    }
                    stringBuilder.append("$frontResult,")
                }
            }

            stringBuilder.append("--")

            val backResultSet = CopyOnWriteArrayList<String>()
            var currentV4Count = v4CountFor49
            var currentV8Count = v8CountFor49
            var currentV12Count = v12CountFor49

            var currentBackCardinalityCount = backCardinalityCountFor49
            var currentBackEventCount = backEvenCountFor49

            while (backResultSet.size < 2) {
                val v4Percent = (1/3 + currentV4Count / 100 - currentV4Count / (currentV4Count + currentV8Count + currentV12Count)) * 10000
                val v8Percent = (1/3 + currentV8Count / 100 - currentV8Count / (currentV4Count + currentV8Count + currentV12Count)) * 10000
                val v12Percent = (1/3 + currentV12Count / 100 - currentV12Count / (currentV4Count + currentV8Count + currentV12Count)) * 10000

                val cardinalPercent =
                    (0.5 + backCardinalityCountForA / 100 - currentBackCardinalityCount / (currentBackCardinalityCount + currentBackEventCount)) * 10000
                val eventPercent =
                    (0.5 + backEvenCountForA / 100 - currentBackEventCount / (currentBackCardinalityCount + currentBackEventCount)) * 10000

                Log.i("myTag", "predictResult  v4Percent $v4Percent")
                Log.i("myTag", "predictResult  v8Percent $v8Percent")
                Log.i("myTag", "predictResult  v12Percent $v12Percent")

                Log.i("myTag", "predictResult  backCardinalPercent $cardinalPercent")
                Log.i("myTag", "predictResult  backEventPercent $eventPercent")

                val random = (Math.random() * 10000).toInt()
                Log.i("myTag", "predictResult   random back v12 $random")
                val frontResult = when {
                    random < v4Percent -> {
                        Log.i("myTag", "predictResult   v4Percent")
                        currentV4Count++
                        getFinalNum(
                            1,
                            4,
                            cardinalPercent,
                            hot15BackList,
                            cold15BackList,
                            veryHot15BackList,
                            veryCold15BackList,
                            mapBackFor49
                        )
                    }
                    random < v4Percent + v8Percent-> {
                        Log.i("myTag", "predictResult   v8Percent")
                        currentV8Count++
                        getFinalNum(
                            5,
                            8,
                            cardinalPercent,
                            hot15BackList,
                            cold15BackList,
                            veryHot15BackList,
                            veryCold15BackList,
                            mapBackFor49
                        )
                    }
                    else -> {
                        Log.i("myTag", "predictResult   v12Percent")
                        currentV12Count++
                        getFinalNum(
                            9,
                            12,
                            cardinalPercent,
                            hot15BackList,
                            cold15BackList,
                            veryHot15BackList,
                            veryCold15BackList,
                            mapBackFor49
                        )
                    }
                }
                if (frontResult.isNotEmpty() && !backResultSet.contains(frontResult)) {
                    if (frontResult.toInt() % 2 == 0) {
                        currentBackEventCount++
                    }else {
                        currentBackCardinalityCount++
                    }
                    backResultSet.add(frontResult)
                    if (backResultBuilder.contains(frontResult)) {
                        val get = backResultBuilder[frontResult]
                        backResultBuilder[frontResult] =  1 + get!!
                    }else {
                        backResultBuilder[frontResult] = 1
                    }
                    stringBuilder.append("$frontResult,")
                }
            }
            stringBuilder.append("\n")
//        }

        frontResultView.text = stringBuilder.toString()

        val stringBuilder3 = java.lang.StringBuilder()
        for (i in 1..35) {
            var string = i.toString()
            if (frontResultBuilder.containsKey(string)) {
                stringBuilder3.append(string + ":" + frontResultBuilder[string] + ",  ")
            }
        }
        stringBuilder3.append("\n")
        stringBuilder3.append("\n")
        for (i in 1..12) {
            var string = i.toString()
            if (backResultBuilder.containsKey(string)) {
                stringBuilder3.append(string + ":" + backResultBuilder[string] + ",  ")
            }
        }

        backResultView.text = stringBuilder3.toString()
    }

    @Synchronized
    private fun getFinalNum(min: Int, max: Int, cardinalPercent: Double,
                            veryHot15List:ArrayList<String>, hot15List: ArrayList<String> ,
                            cold15List:ArrayList<String>,veryCold15List: ArrayList<String>,
                            currentMap: HashMap<String, Int>): String {
        val random1 = (Math.random() * 10000).toInt()
        val isEventNum = when {
            random1 < cardinalPercent -> {
                false
            }
            else -> {
                true
            }
        }
        Log.i("myTag", "getFinalNum   isEventNum $isEventNum")

        val random2 = (Math.random() * 100).toInt()
        val result = when {
            random2 < 20 -> {
                Log.i("myTag", "getFinalNum   veryHot15List")
                predictFinalNum(
                    min,
                    max,
                    veryHot15List,
                    isEventNum,
                    totalFrontAllCombinationMap,
                    currentMap
                )
            }

            random2 < 40 -> {
                Log.i("myTag", "getFinalNum   hot15List")
                predictFinalNum(
                    min,
                    max,
                    hot15List,
                    isEventNum,
                    totalFrontAllCombinationMap,
                    currentMap
                )
            }

            random2 < 70 -> {
                Log.i("myTag", "getFinalNum   cold15List")
                predictFinalNum(
                    min,
                    max,
                    cold15List,
                    isEventNum,
                    totalFrontAllCombinationMap,
                    currentMap
                )
            }

            else -> {
                Log.i("myTag", "getFinalNum   veryCold15List")
                predictFinalNum(
                    min,
                    max,
                    veryCold15List,
                    isEventNum,
                    totalFrontAllCombinationMap,
                    currentMap
                )
            }
        }
        Log.i("myTag", "getFinalNum  result $result")
        return if (result == -1) {
            ""
        } else {
            result.toString()
        }
    }

    private fun predictFinalNum(min : Int , max : Int , frontList : ArrayList<String> ,isEventNum : Boolean,
                                totalFrontAllCombinationMap : HashMap<String,Float>,
                                currentFrontAllCombinationMap :HashMap<String,Int>) : Int{

        val predictList = ArrayList<Int>()
        var totalCount = 0.0
        var currentCount = 0

        for (string in frontList) {
            val num = string.toInt()
            if (num in min..max) {
                if (isEventNum && num % 2 == 0) {
                    predictList.add(num)
                    totalCount += totalFrontAllCombinationMap[string]!!
                    currentCount += currentFrontAllCombinationMap[string]!!
                }else if (!isEventNum && num % 2 != 0) {
                    predictList.add(num)
                    totalCount += totalFrontAllCombinationMap[string]!!
                    currentCount += currentFrontAllCombinationMap[string]!!
                }
            }
        }
        if (predictList.size == 0) return -1

        val baseProbability : Double = (1.0000/ predictList.size).toDouble()
        val percentMap = HashMap<Int,Int>()
        var percent = 0
        for (i in predictList) {
            var string = i.toString()
            if ( i < 10) {
                string = "0$i"
            }
            percent += ((baseProbability +
                    totalFrontAllCombinationMap[string]!!.toDouble() / totalCount -
                    currentFrontAllCombinationMap[string]!!.toDouble() / currentCount) * 10000).toInt()
            Log.i("myTag", "i $i  percent $percent")
            percentMap[i] = percent
        }

        val random = (Math.random() * 10000).toInt()
        var temp = 10001
        for (entry in percentMap.entries) {
            if (entry.value in (random + 1) until temp) {
                temp = entry.value
            }
        }
        var finalReset = 0
        for (entry in percentMap.entries) {
            if (entry.value == temp) {
                finalReset = entry.key
            }
        }
        Log.i("Mytag", "random $random   finalReset $finalReset")
        return finalReset
    }

    private fun getResult() {
        Thread{
            computeLottery(14,0 + myOffset,true)
            computeLottery(15,0 + myOffset,true)
            mapFrontFor15.putAll(frontAllCombinationMap)
            mapBackFor15.putAll(backAllCombinationMap)
            computeLottery(15,15 + myOffset,true)
            computeLottery(15,30 + myOffset,true)
            computeLottery(15,45 + myOffset,true)

            computeLottery(49,0 + myOffset,true)

            computeLottery(50,0 + myOffset,true)

            computeLottery(50,50 + myOffset,true)

            computeLottery(50,100 + myOffset,true)

            computeLottery(50,150 + myOffset,true)

            allNumber()
        }.start()
    }

    private fun allNumber() {
        val stringBuilder = java.lang.StringBuilder()
        for (i in 1..9) {
            stringBuilder.append("0$i")
            stringBuilder.append("\t\t")
            when {
                hot15FrontList.contains("0$i") -> {
                    stringBuilder.append("h")
                }
                veryHot15FrontList.contains("0$i") -> {
                    stringBuilder.append("vh")
                }
                cold15FrontList.contains("0$i") -> {
                    stringBuilder.append("c")
                }
                else -> {
                    stringBuilder.append("vc")
                }
            }
            stringBuilder.append("\t\t")
            stringBuilder.append(mapFrontFor15["0$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append(mapFrontFor50["0$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append((mapFrontFor50["0$i"]!! + mapFrontFor100["0$i"]!! + mapFrontFor150["0$i"]!! + mapFrontFor200["0$i"]!!) / 4.00f)
            stringBuilder.append("\t\t")
            stringBuilder.append(totalFrontAllCombinationMap["0$i"]!!)
            stringBuilder.append("\n")
        }

        for (i in 10..35) {
            stringBuilder.append(i)
            stringBuilder.append("\t\t")
            when {
                hot15FrontList.contains("$i") -> {
                    stringBuilder.append("h")
                }
                veryHot15FrontList.contains("$i") -> {
                    stringBuilder.append("vh")
                }
                cold15FrontList.contains("$i") -> {
                    stringBuilder.append("c")
                }
                else -> {
                    stringBuilder.append("vc")
                }
            }
            stringBuilder.append("\t\t")
            stringBuilder.append(mapFrontFor15["$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append(mapFrontFor50["$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append((mapFrontFor50["$i"]!! + mapFrontFor100["$i"]!! + mapFrontFor150["$i"]!! + mapFrontFor200["$i"]!!) / 4.00f)
            stringBuilder.append("\t\t")
            stringBuilder.append(totalFrontAllCombinationMap["$i"]!!)
            stringBuilder.append("\n")
        }

        for (i in 1..9) {
            stringBuilder.append("0$i")
            stringBuilder.append("\t\t")
            when {
                hot15BackList.contains("0$i") -> {
                    stringBuilder.append("h")
                }
                veryHot15BackList.contains("0$i") -> {
                    stringBuilder.append("vh")
                }
                cold15BackList.contains("0$i") -> {
                    stringBuilder.append("c")
                }
                else -> {
                    stringBuilder.append("vc")
                }
            }
            stringBuilder.append("\t\t")
            stringBuilder.append(mapBackFor15["0$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append(mapBackFor50["0$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append((mapBackFor50["0$i"]!! + mapBackFor100["0$i"]!! + mapBackFor150["0$i"]!! + mapBackFor200["0$i"]!!) / 4.00f)
            stringBuilder.append("\t\t")
            stringBuilder.append(totalBackAllCombinationMap["0$i"]!!)
            stringBuilder.append("\n")
        }

        for (i in 10..12){
            stringBuilder.append("$i")
            stringBuilder.append("\t\t")
            when {
                hot15BackList.contains("$i") -> {
                    stringBuilder.append("h")
                }
                veryHot15BackList.contains("$i") -> {
                    stringBuilder.append("vh")
                }
                cold15BackList.contains("$i") -> {
                    stringBuilder.append("c")
                }
                else -> {
                    stringBuilder.append("vc")
                }
            }
            stringBuilder.append("\t\t")
            stringBuilder.append(mapBackFor15["$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append(mapBackFor50["$i"])
            stringBuilder.append("\t\t")
            stringBuilder.append((mapBackFor50["$i"]!! + mapBackFor100["$i"]!! + mapBackFor150["$i"]!! + mapBackFor200["$i"]!!) / 4.00f)
            stringBuilder.append("\t\t")
            stringBuilder.append(totalBackAllCombinationMap["$i"]!!)
            stringBuilder.append("\n")
        }
        stringBuilder.append("49前基数：$frontCardinalityCountFor49   \t50前基数：$frontCardinalityCountFor50  " +
                "\t200均前基数：${frontCardinalityCountForA}  \t总前基数：$totalFrontCardinalityCount")
        stringBuilder.append("\n")
        stringBuilder.append("49前偶数：$frontEvenCountFor49  \t50前偶数：$frontEvenCountFor50  " +
                "\t200均前偶数：${frontEvenCountForA}  \t总前偶数：$totalFrontEvenCount")
        stringBuilder.append("\n")
        stringBuilder.append("49 v7：$v7CountFor49  \t50 v7：$v7CountFor50  " +
                "\t200均 v7：${v7CountForA}  \t总v7：$totalV7Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v14：$v14CountFor49  \t50 v14：$v14CountFor50  " +
                "\t200均 v14：${v14CountForA}  \t总v14：$totalV14Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v21：$v21CountFor49  \t50 v21：$v21CountFor50  " +
                "\t200均 v21：${v21CountForA}  \t总v21：$totalV21Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v28：$v28CountFor49  \t50 v28：$v28CountFor50  " +
                "\t200均 v28：${v28CountForA}  \t总v28：$totalV28Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v35：$v35CountFor49  \t50 v35：$v35CountFor50  " +
                "\t200均 v35：${v35CountForA}  \t总v35：$totalV35Count")
        stringBuilder.append("\n")

        stringBuilder.append("49后基数：$backCardinalityCountFor49  \t50后基数：$backCardinalityCountFor50  " +
                "\t200均后基数：${backCardinalityCountForA}  \t总后基数：$totalBackCardinalityCount")
        stringBuilder.append("\n")
        stringBuilder.append("49后偶数：$backEvenCountFor49  \t50后偶数：$backEvenCountFor50  " +
                "\t200均后偶数：${backEvenCountForA}  \t总后偶数：$totalBackEvenCount")
        stringBuilder.append("\n")
        stringBuilder.append("49 v4：$v4CountFor49  \t50 v4：$v4CountFor50  " +
                "\t200均 v6：${v4CountForA}  \t总v4：$totalV4Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v8：$v8CountFor49  \t50 v8：$v8CountFor50  " +
                "\t200均 v8：${v8CountForA}  \t总v8：$totalV8Count")
        stringBuilder.append("\n")
        stringBuilder.append("49 v12：$v12CountFor49  \t50 v12：$v12CountFor50  " +
                "\t200均 v12：${v12CountForA}  \t总v12：$totalV12Count")
        stringBuilder.append("\n")
        writeSdcard(stringBuilder.toString(), "calculateResult.txt", true)
    }


    private fun computeLottery(limit: Int, offset: Int, writerFile: Boolean = false) {
        Log.i("mTag", "$limit-$offset")
        clearLastDate()
        var limitNum = limit
        val lotteryCount = lotteryDao.getLotteryCount()
        if (limit > lotteryCount) {
            limitNum = lotteryCount
        }

        for (i in 1..35) {
            if (i < 10) {
                frontAllCombinationMap["0$i"] = 0
            } else {
                frontAllCombinationMap[i.toString()] = 0
            }
        }
        for (i in 1..12) {
            if (i < 10) {
                backAllCombinationMap["0$i"] = 0
            } else {
                backAllCombinationMap[i.toString()] = 0
            }
        }

        val queryLottery = lotteryDao.queryLottery(limitNum, offset)

        for (lottery in queryLottery) {
            val lotteryTrim = lottery.lotteryDrawResult.replace(" ", "")
            val results: ArrayList<String> = ArrayList()
            for (i in lotteryTrim.indices step 2) {
                val substring = lotteryTrim.substring(i, i + 2)
                results.add(substring)
            }
            for (a in 0..4) {
                val string = results[a]
                val number = string.toInt()
                if (number % 2 == 0) {
                    frontEvenCount++
                } else {
                    frontCardinalityCount++
                }
                when {
                    number <= 7 -> {
                        v7Count++
                    }
                    number <= 14 -> {
                        v14Count++
                    }
                    number <= 21 -> {
                        v21Count++
                    }
                    number <= 28 -> {
                        v28Count++
                    }
                    else -> {
                        v35Count++
                    }
                }
                if (frontAllCombinationMap.containsKey(string)) {
                    frontAllCombinationMap[string] =
                        frontAllCombinationMap[string]!! + 1
                } else {
                    frontAllCombinationMap[string] = 1
                }
            }

            for (f in 5..6) {
                val string = results[f]
                val number = string.toInt()
                if (number % 2 == 0) {
                    backEvenCount++
                } else {
                    backCardinalityCount++
                }
                when {
                    number <= 4 -> {
                        v4Count++
                    }
                    number <= 8 -> {
                        v8Count++
                    }
                    else -> {
                        v12Count++
                    }
                }
                if (backAllCombinationMap.containsKey(string)) {
                    backAllCombinationMap[string] =
                        backAllCombinationMap[string]!! + 1
                } else {
                    backAllCombinationMap[string] = 1
                }
            }
        }

        val orderFrontNum = LinkedList<String>()
        val orderBackNum = LinkedList<String>()
        for (entry in frontAllCombinationMap.entries) {
            if (orderFrontNum.isEmpty()) {
                orderFrontNum.addFirst(entry.key)
            } else {
                var isMax = true
                for (j in orderFrontNum.size - 1 downTo 0) {
                    if (frontAllCombinationMap[orderFrontNum[j]]!! > entry.value) {
                        orderFrontNum.add(j + 1, entry.key)
                        isMax = false
                        break
                    }
                }
                if (isMax) {
                    orderFrontNum.addFirst(entry.key)
                }
            }
        }

        for (entry in backAllCombinationMap.entries) {
            if (orderBackNum.isEmpty()) {
                orderBackNum.addFirst(entry.key)
            } else {
                var isMax = true
                for (j in orderBackNum.size - 1 downTo 0) {
                    if (backAllCombinationMap[orderBackNum[j]]!! > entry.value) {
                        orderBackNum.add(j + 1, entry.key)
                        isMax = false
                        break
                    }
                }
                if (isMax) {
                    orderBackNum.addFirst(entry.key)
                }
            }
        }

        if (writerFile) {
            val calculateResultBuilder = StringBuilder()
            calculateResultBuilder.append("lottery${limitNum}-${offset}:\n")
            calculateResultBuilder.append("前区:\n")
            for (i in 0 until orderFrontNum.size) {
                if (i == 17) {
                    calculateResultBuilder.append("\n")
                }
                calculateResultBuilder.append("${orderFrontNum[i]}:${frontAllCombinationMap[orderFrontNum[i]]}，")
            }
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("基数个数：$frontCardinalityCount  \n")
            calculateResultBuilder.append("偶数个数：$frontEvenCount  \n")
            calculateResultBuilder.append("1-7个数：$v7Count ，8-14个数：$v14Count，15-21个数：$v21Count，22-28个数：$v28Count，29-35个数：$v35Count \n")
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("后区:\n")
            for (i in 0 until orderBackNum.size) {
                calculateResultBuilder.append("${orderBackNum[i]}:${backAllCombinationMap[orderBackNum[i]]}，")
            }
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("基数个数：$backCardinalityCount  \n")
            calculateResultBuilder.append("偶数个数：$backEvenCount  \n")
            calculateResultBuilder.append("1-4个数：$v4Count，5-8个数：$v8Count ，9-12个数：$v12Count \n")
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("\n")
            calculateResultBuilder.append("\n")

            writeSdcard(calculateResultBuilder.toString(), "calculateResult.txt", true)
        }

        Log.i("mTag", "finish")
    }

    private fun clearLastDate() {
        frontAllCombinationMap.clear()
        backAllCombinationMap.clear()
        frontCardinalityCount = 0
        frontEvenCount = 0
        v7Count = 0
        v14Count = 0
        v21Count = 0
        v28Count = 0
        v35Count = 0
        backCardinalityCount = 0
        backEvenCount = 0
        v4Count = 0
        v8Count = 0
        v12Count = 0
    }

    private fun initDate() {
        val httpBuild = HttpUrl.parse(url).newBuilder()
            .addQueryParameter("gameNo", gameNo).addQueryParameter("provinceId", provinceId)
            .addQueryParameter("isVerify", isVerify).addQueryParameter("pageSize", pageSize)
            .addQueryParameter("pageNo", mPage.toString())
        val build = httpBuild.build()
        val request = Request.Builder().url(build).build()
        try {
            mOkHttpClient?.newCall(request)?.enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    progressDialog.cancel()
                    Log.i("mTag", e.toString())
                }

                override fun onResponse(call: Call?, response: Response?) {
                    if (response?.body() != null) {
                        val result =
                            mGson.fromJson(response.body().string(), RichEntity::class.java)
                        lotteryDao.insertBatchLottery(result.value.list)
                        if (mPage < result.value.pages) {
                            mPage++
                            initDate()
                        } else {
                            progressDialog.cancel()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("mTag", e.toString())
        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )

    private fun writeSdcard(text: String,fileName: String,append : Boolean = false) {
        val storage = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/lottery"
        val tmepfile = File(storage)
        if (!tmepfile.exists()) {
            tmepfile.mkdirs()
        }
        val file1 = File(tmepfile, fileName)
        if (!file1.exists()) {
            try {
                file1.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Log.i("mTag", file1.path)
        var bufferedWriter: BufferedWriter? = null
        try {
            bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file1,append)))
            bufferedWriter.write(text)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }
            //申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        } else {
            Toast.makeText(this, "已授权成功！", Toast.LENGTH_SHORT).show()
        }
    }


    private fun delete(delFile: String): Boolean {
        val file = File(delFile)
        return if (!file.exists()) {
            Toast.makeText(applicationContext, "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT)
                .show()
            false
        } else {
            if (file.isFile) deleteSingleFile(delFile)
            true
        }
    }

    /** 删除单个文件
     * @param  要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private fun deleteSingleFile(`filePath$Name`: String): Boolean {
        val file = File(`filePath$Name`)
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        return if (file.exists() && file.isFile) {
            if (file.delete()) {
                Log.e(
                    "--Method--",
                    "Copy_Delete.deleteSingleFile: 删除单个文件" + `filePath$Name` + "成功！"
                )
                true
            } else {
                Toast.makeText(
                    applicationContext,
                    "删除单个文件" + `filePath$Name` + "失败！",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        } else {
            Toast.makeText(
                applicationContext,
                "删除单个文件失败：" + `filePath$Name` + "不存在！",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

}
