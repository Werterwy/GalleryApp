package com.example.lab11

import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MediaActivity : AppCompatActivity() {

    private lateinit var mediaSurfaceView: SurfaceView
    private lateinit var mediaPathEditText: EditText
    private lateinit var bStart: Button
    private lateinit var bPause: Button
    private lateinit var bResume: Button
    private lateinit var bStop: Button
    private lateinit var loopCheckBox: CheckBox

    private var mediaPlayer: MediaPlayer? = null
    private var isLooping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        mediaSurfaceView = findViewById(R.id.mediaSurfaceView)
        mediaPathEditText = findViewById(R.id.mediaPathEditText)
        bStart = findViewById(R.id.bStart)
        bPause = findViewById(R.id.bPause)
        bResume = findViewById(R.id.bResume)
        bStop = findViewById(R.id.bStop)
        loopCheckBox = findViewById(R.id.loopCheckBox)

        // Initialize SurfaceHolder.Callback and surface type
        val surfaceHolder = mediaSurfaceView.holder
        surfaceHolder.addCallback(SurfaceCallback())
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        // Set up click listeners
        bStart.setOnClickListener { onClickStart(it) }
        bPause.setOnClickListener { onClickPause(it) }
        bResume.setOnClickListener { onClickResume(it) }
        bStop.setOnClickListener { onClickStop(it) }
    }

    inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceCreated(holder: SurfaceHolder) {
            // Set up MediaPlayer
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDisplay(holder)

            // Set up completion listener for looping
            mediaPlayer?.setOnCompletionListener {
                if (isLooping) {
                    mediaPlayer?.start()
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun onClickStart(view: View) {
        if (mediaPathEditText.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter a valid media path", Toast.LENGTH_SHORT).show()
            return
        }

        val mediaPath = mediaPathEditText.text.toString()

        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(mediaPath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            mediaPlayer?.isLooping = loopCheckBox.isChecked
            isLooping = loopCheckBox.isChecked
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickPause(view: View) {
        mediaPlayer?.pause()
    }

    fun onClickResume(view: View) {
        mediaPlayer?.start()
    }

    fun onClickStop(view: View) {
        mediaPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
