package com.wench.vacabulary.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.util.SparseIntArray
import android.util.TypedValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tencent.mmkv.MMKV
import com.wench.vacabulary.data.VOCABULARY_LIST
import com.wench.vacabulary.data.VocabularyBean
import com.wench.vacabulary.data.VocabularyList

/**
 * CreateTime:5/13/2022
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */

fun dp2px(dpValue: Int, context: Context): Int {
    val result = SparseIntArray()[dpValue]
    if (result > 0) return result
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), context.resources.displayMetrics).toInt().also {
        SparseIntArray().put(dpValue, it)
    }
}

fun saveVocabularyList(dataList: VocabularyList){
    MMKV.defaultMMKV().encode("VOCABULARY", dataList)
}

fun readVocabularyList():MutableList<VocabularyBean>?{
    val vocabulary = MMKV.defaultMMKV().decodeParcelable("VOCABULARY", VocabularyList::class.java)
    return vocabulary?.list
}

//检查词典仓库是否有更新
fun checkVocabularyList(){
    val dataList = readVocabularyList() ?: mutableListOf()
    if(dataList.size < VOCABULARY_LIST.size){//说明有更新
        VOCABULARY_LIST.forEach { vocabulary->
            Log.d("新消息", "$dataList")
            if(dataList.isEmpty() || dataList.find { it.id == vocabulary.id } == null){//mmkv中不存在，就新增
                dataList.add(vocabulary)
            }
        }
        saveVocabularyList(VocabularyList(list = dataList))
    }
}

//Manifest.permission.RECORD_AUDIO

fun FragmentActivity.requirePermission(permission: String, callback: () -> Unit) {
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
    } else {
        callback()
    }
}