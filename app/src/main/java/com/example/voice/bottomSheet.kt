package com.example.voice

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class bottomSheet : BottomSheetDialogFragment() {

    private lateinit var seek : SeekBar
    private lateinit var startTime : TextView
    private lateinit var  allTime : TextView
    private var media : MediaPlayer ?= null
    private lateinit var handler : Handler
    private lateinit var play : ImageButton
    private lateinit var next : ImageButton
    private lateinit var previous : ImageButton
    private lateinit var down : ImageButton
    private val songList = listOf(R.raw.rick,R.raw.gun)
    private var currentSongIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.activity_bottom_sheet, container, false)



        seek = view.findViewById(R.id.seek)
        startTime = view.findViewById(R.id.startTime)
        allTime = view.findViewById(R.id.allTime)
        handler = Handler(Looper.getMainLooper())
        play = view.findViewById(R.id.play)
        next = view.findViewById(R.id.next)
        previous = view.findViewById(R.id.previous)
        down = view.findViewById(R.id.down)


        setupPlayer()
        setupSeekbar()

        down.setOnClickListener {
            dismiss()
        }

        play.setOnClickListener {
            if(media?.isPlaying == true){
                media?.pause()
               play.setImageResource(R.drawable.baseline_play_circle_24)
            }else{
                media?.start()
                play.setImageResource(R.drawable.baseline_pause_circle_24)
            }
        }


        next.setOnClickListener {
            currentSongIndex = (currentSongIndex+1) % songList.size
            playSong(currentSongIndex)
        }

        previous.setOnClickListener {
            currentSongIndex = (currentSongIndex - 1 + songList.size) % songList.size
            playSong(currentSongIndex)
        }
        return  view
    }

    private fun playSong(currentSongIndex: Int) {
        media?.release()
        media = MediaPlayer.create(context,songList[currentSongIndex])
        media?.start()
        updateSeek()
        allTime.text = formatTime(media!!.duration)
    }

    private fun setupSeekbar() {
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    media?.seekTo(progress)
                    startTime.text = formatTime(progress)
                } //更新當前時間
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                media?.pause()//暫停播放
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                media?.start()//停止播放
            }

        })
    }

    private fun setupPlayer() {
        media = MediaPlayer.create(context,R.raw.rick)
        media?.setOnPreparedListener {
            seek.max = media!!.duration
            allTime.text = formatTime(media!!.duration)
            play.setImageResource(R.drawable.baseline_play_circle_24
            )
            updateSeek()
        }
    }

    private fun formatTime(duration: Int): String {
        val minutes = duration/1000/60
        val seconds = duration/1000%60
        return String.format("%02d : %02d", minutes,seconds)
    }

    override  fun onDestroy(){
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        media?.release()
        media = null
    }

    private fun updateSeek() {
        handler.postDelayed(object : Runnable{
            override fun run() {
                media?.let {
                    seek.progress = it.currentPosition
                    startTime.text = formatTime(it.currentPosition)
                    handler.postDelayed(this,500)
                }
            }
        },500)
    }

}