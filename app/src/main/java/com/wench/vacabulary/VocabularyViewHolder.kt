package com.wench.vacabulary

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wench.vacabulary.data.VocabularyBean
import com.wench.vacabulary.databinding.ItemVocabularyBinding
import com.wench.vacabulary.helper.MultiTouchListener

/**
 * CreateTime:2022/05/13
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */
class VocabularyViewHolder (
    parent: ViewGroup,
    private val viewBinding: ItemVocabularyBinding = ItemVocabularyBinding.inflate(LayoutInflater.from(parent.context), parent,false)
) : RecyclerView.ViewHolder(viewBinding.root) {
    fun bind(
        data: VocabularyBean,
        callback: VocabularyAdapter.OnItemClickListener?
    ) {
        if(data.masterStatus){
            itemView.background = itemView.context.getDrawable(R.drawable.shape_blue_10)
            viewBinding.tvName.setTextColor(Color.parseColor("#FFFFFF"))
            viewBinding.tvPronounce.setTextColor(Color.parseColor("#FFFFFF"))
            viewBinding.tvParaphrase.setTextColor(Color.parseColor("#FFFFFF"))
            viewBinding.tvExample.setTextColor(Color.parseColor("#FFFFFF"))
            viewBinding.imgvAudio.setImageResource(R.mipmap.icon_audio_white)
        }else{
            itemView.background = itemView.context.getDrawable(R.drawable.shape_ffffff_10)
            viewBinding.tvName.setTextColor(Color.parseColor("#071131"))
            viewBinding.tvPronounce.setTextColor(Color.parseColor("#071131"))
            viewBinding.tvParaphrase.setTextColor(Color.parseColor("#071131"))
            viewBinding.tvExample.setTextColor(Color.parseColor("#071131"))
            viewBinding.imgvAudio.setImageResource(R.mipmap.icon_audio_purple)
        }
        viewBinding.imgvAudio.visibility = if(data.audioPath.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
        viewBinding.tvName.text = data.name
        viewBinding.tvPronounce.text = data.pronounce
        viewBinding.tvParaphrase.text = data.paraphrase
        viewBinding.tvExample.text = data.example
        viewBinding.tvIdView.text = "${data.id + 1}"
        viewBinding.imgvAudio.setOnClickListener {
            if(data.masterStatus){
                viewBinding.imgvAudio.setImageResource(R.mipmap.icon_audio_white_playing)
            }else{
                viewBinding.imgvAudio.setImageResource(R.mipmap.icon_audio_purple_playing)
            }
            callback?.onAudioPlay(data)
        }
        itemView.setOnTouchListener(object : MultiTouchListener() {
            override fun onMultiTouch(v: View?, event: MotionEvent?, touchCount: Int): Boolean {
                if(touchCount>1) callback?.onItemClick(data)
                return true
            }
        })
    }
}