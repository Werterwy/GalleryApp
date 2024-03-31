package com.example.lab11

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val camera_capture_button: ImageButton = findViewById(R.id.capture_button)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        camera_capture_button.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = createPreviewUseCase()
            imageCapture = createImageCaptureUseCase()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun createPreviewUseCase(): Preview {

        val preview = Preview.Builder().build()


        val previewView: PreviewView = findViewById(R.id.previewView)
        preview.setSurfaceProvider(previewView.surfaceProvider)

        return preview
    }
    private fun createImageCaptureUseCase(): ImageCapture {

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        return imageCapture as ImageCapture
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )


        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()


        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Implement what to do with the saved image
                    // For example, display it in an ImageView
                    val savedUri = Uri.fromFile(photoFile)

                    // Add the image to the system gallery
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScanIntent.data = savedUri
                    sendBroadcast(mediaScanIntent)
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // Handle the case where some permissions are not granted
                // You might want to show an explanation to the user
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
