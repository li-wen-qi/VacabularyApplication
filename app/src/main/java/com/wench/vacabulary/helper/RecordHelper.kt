package com.wench.vacabulary.helper

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaRecorder
import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import com.wench.vacabulary.VocabularyApp.Companion.appContext
import java.io.File
import java.io.IOException
import java.util.*


object RecordHelper {
    private var fileName = ""
    private var filePath = ""
    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private val sdCardParentDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        Log.d("路径-判断外", "${appContext?.getExternalFilesDir(null)}")
        appContext?.getExternalFilesDir(null)
    } else {
        Log.d("路径-判断内", "${appContext?.filesDir}")
        appContext?.filesDir
    }

    fun getAudioFilePath(): String {
        return filePath
    }

    /**
     * 60秒自动停止
     */
    fun startRecord() {
        if (mMediaRecorder == null) mMediaRecorder = MediaRecorder()
        try {
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)// 设置麦克风
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)).toString() + ".aac"
            filePath = sdCardParentDir?.absolutePath +"/"+ fileName
            Log.d("录音", filePath)
            mMediaRecorder?.setOutputFile(filePath)
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
        } catch (e: IllegalStateException) {
            Log.d("录音failed!", e.message ?: "")
        } catch (e: IOException) {
            Log.d("录音failed!", e.message ?: "")
        }
    }

    fun stopRecord() {
        try {
            mMediaRecorder?.stop()
            mMediaRecorder?.release()
            mMediaRecorder = null

        } catch (e: RuntimeException) {
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            mMediaRecorder = null
            val file = File(filePath)
            if (file.exists()) file.delete()
            filePath = ""
        }
    }

    fun playAudio(audioPath:String, callback:()->Unit) {
        if (mMediaPlayer == null) mMediaPlayer = MediaPlayer()
        if (mMediaPlayer?.isPlaying == true) {
            return
        }
        //设置数据源
        try {
            mMediaPlayer?.setDataSource(audioPath)
            mMediaPlayer?.prepare()
            mMediaPlayer?.start()
            mMediaPlayer?.setOnCompletionListener(OnCompletionListener { mp ->
                mp.reset()
                callback()
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}