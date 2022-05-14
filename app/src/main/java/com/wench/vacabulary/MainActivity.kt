package com.wench.vacabulary

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AlertDialogLayout
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.wench.vacabulary.data.VOCABULARY_LIST
import com.wench.vacabulary.data.VocabularyBean
import com.wench.vacabulary.data.VocabularyList
import com.wench.vacabulary.databinding.ActivityMainBinding
import com.wench.vacabulary.helper.*
import com.wench.vacabulary.helper.RecordHelper.getAudioFilePath
import com.wench.vacabulary.helper.RecordHelper.playAudio

class MainActivity : AppCompatActivity() {
    private var currentPosition = 0//当前adapter所在position
    private val adapter by lazy { VocabularyAdapter() }
    private val viewBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initView()
        initData()
    }

    private fun initView(){
        StatusBarUtils.update(this, true, false, true, true)
        viewBinding.imgvAll?.setOnClickListener {
            viewBinding.imgvAll?.setImageResource(R.mipmap.vocabulary_all_checked)
            viewBinding.imgvMaster?.setImageResource(R.mipmap.master_uncheck)
            viewBinding.imgvUnMaster?.setImageResource(R.mipmap.unmaster_uncheck)
            val vocabularyList = readVocabularyList() ?: VOCABULARY_LIST
            adapter.setVocabularyList(vocabularyList)
        }
        viewBinding.imgvMaster?.setOnClickListener {
            viewBinding.imgvAll?.setImageResource(R.mipmap.vocabulary_all_uncheck)
            viewBinding.imgvMaster?.setImageResource(R.mipmap.master_checked)
            viewBinding.imgvUnMaster?.setImageResource(R.mipmap.unmaster_uncheck)
            val vocabularyList = readVocabularyList() ?: VOCABULARY_LIST
            adapter.setVocabularyList(vocabularyList.filter { it.masterStatus} as MutableList<VocabularyBean>)
        }
        viewBinding.imgvUnMaster?.setOnClickListener {
            viewBinding.imgvAll?.setImageResource(R.mipmap.vocabulary_all_uncheck)
            viewBinding.imgvMaster?.setImageResource(R.mipmap.master_uncheck)
            viewBinding.imgvUnMaster?.setImageResource(R.mipmap.unmaster_checked)
            val vocabularyList = readVocabularyList() ?: VOCABULARY_LIST
            adapter.setVocabularyList(vocabularyList.filter { !it.masterStatus } as MutableList<VocabularyBean>)
        }
        viewBinding.imgvRecording?.setOnClickListener {
            viewBinding.imgvRecording?.setImageResource(R.mipmap.icon_recording)
        }
        viewBinding.imgvRecording?.setOnTouchListener(voiceOnTouchListener)
    }


    private fun initData(){
        checkVocabularyList()
        adapter.setVocabularyList(readVocabularyList() ?: VOCABULARY_LIST)
        viewBinding.vocabularyViewPager.adapter = adapter
        viewBinding.vocabularyViewPager.offscreenPageLimit = 3
        viewBinding.vocabularyViewPager.setPageTransformer(MarginPageTransformer(dp2px(20, this)))
        adapter.setOnItemClickListener(object :VocabularyAdapter.OnItemClickListener{
            override fun onItemClick(data: VocabularyBean) {
                updateAdapter(data)
                updateMasterStatus(data)
            }

            override fun onAudioPlay(data: VocabularyBean) {
                playAudio(data.audioPath){
                    adapter.notifyItemChanged(currentPosition)
                }
            }
        })

        viewBinding.vocabularyViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position//更新position
            }
        })
    }

    private fun updateAdapter(data: VocabularyBean){
        adapter.dataList.forEachIndexed { index, vocabularyBean ->
            if(vocabularyBean.id == data.id){
                vocabularyBean.masterStatus = !vocabularyBean.masterStatus
                adapter.notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }
    private fun updateMasterStatus(data: VocabularyBean){
        val vocabularyList = readVocabularyList() ?: VOCABULARY_LIST
        vocabularyList.forEach {
            if(it.id == data.id){
                it.masterStatus = !it.masterStatus
                return@forEach
            }
        }
        saveVocabularyList(VocabularyList(list = vocabularyList))
    }

    private fun updateAudioPath(dataId:Int, audioPath: String){
        val vocabularyList = readVocabularyList() ?: VOCABULARY_LIST
        vocabularyList.forEach {
            if(it.id == dataId){
                it.audioPath = audioPath
                return@forEach
            }
        }
        saveVocabularyList(VocabularyList(list = vocabularyList))
    }

    private val voiceOnTouchListener = View.OnTouchListener { v, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                viewBinding.imgvRecording?.setImageResource(R.mipmap.icon_recording)
                ShakeUtil.vibrator(v.context, 300)
                requirePermission(Manifest.permission.RECORD_AUDIO){
                    RecordHelper.startRecord()
                }
            }
            MotionEvent.ACTION_UP -> {
                viewBinding.imgvRecording?.setImageResource(R.mipmap.icon_record)
                val upY = event.getY().toInt()//点击处相对父View的距离
                if(upY < - 100){
                    Toast.makeText(this, "取消录音", Toast.LENGTH_SHORT).show()
                }else{
                    RecordHelper.stopRecord()
                    dealRecord()
                }
            }
        }
        true
    }

    private fun dealRecord(){
        if(adapter.dataList[currentPosition].audioPath.isNullOrEmpty()){
            updateAudio()
        }else{
            AlertDialog.Builder(this)
                .setMessage("录音完成，是否替换?")
                .setPositiveButton("替换") { _, _ ->
                    updateAudio()
                }
                .setNegativeButton("取消") { _, _ -> }
                .create().show()
        }
    }

    private fun updateAudio(){
        val audioPath = getAudioFilePath()
        adapter.dataList[currentPosition].audioPath = audioPath
        adapter.notifyItemChanged(currentPosition)
        updateAudioPath(adapter.dataList[currentPosition].id, audioPath)
        Toast.makeText(this, "录音完成", Toast.LENGTH_SHORT).show()
    }
}