package com.example.lab11

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var infoTextView: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private val imageIds = intArrayOf(
        R.drawable.image,
        R.drawable.image1



    )
    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        imageView = findViewById(R.id.imageView)
        infoTextView = findViewById(R.id.infoTextView)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        updateImageAndInfo()

        prevButton.setOnClickListener { onPrevButtonClick(it) }
        nextButton.setOnClickListener { onNextButtonClick(it) }
    }

    private fun updateImageAndInfo() {
        imageView.setImageResource(imageIds[currentImageIndex])
        val infoText = "Image ${currentImageIndex + 1} of ${imageIds.size}"
        infoTextView.text = infoText
    }

    fun onPrevButtonClick(view: View) {
        currentImageIndex = (currentImageIndex - 1 + imageIds.size) % imageIds.size
        updateImageAndInfo()
    }

    fun onNextButtonClick(view: View) {
        currentImageIndex = (currentImageIndex + 1) % imageIds.size
        updateImageAndInfo()
    }
}
