package com.wench.vacabulary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wench.vacabulary.data.VocabularyBean


/**
 * CreateTime:2022/05/13
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */
class VocabularyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    interface OnItemClickListener {
        fun onItemClick(data: VocabularyBean)
        fun onAudioPlay(data: VocabularyBean)
    }

    private var mOnItemClickListener: OnItemClickListener?= null

    fun setOnItemClickListener(listener: OnItemClickListener){
        mOnItemClickListener = listener
    }

    var dataList = mutableListOf<VocabularyBean>()

    fun setVocabularyList(results:MutableList<VocabularyBean>?){
        Log.d("数据", "results ${results}")
        dataList = results ?: mutableListOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("数据", "onCreateViewHolder")
        return VocabularyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("数据", "${dataList.get(position)}")
        when(holder){
            is VocabularyViewHolder -> {
                holder.bind(dataList.get(position), mOnItemClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("数据", "size --- ${dataList.size}")
        return dataList.size
    }
}