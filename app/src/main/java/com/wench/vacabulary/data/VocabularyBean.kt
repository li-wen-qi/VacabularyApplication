package com.wench.vacabulary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * CreateTime:5/13/2022
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */

@Parcelize
data class VocabularyList(var list: MutableList<VocabularyBean> = mutableListOf()) : Parcelable

@Parcelize
data class VocabularyBean(
    val id:Int,
    val name:String,
    val paraphrase:String,//释义
    val pronounce:String,
    var example:String = "",//例句
    var audioPath:String = "",
    var masterStatus:Boolean = false
): Parcelable
