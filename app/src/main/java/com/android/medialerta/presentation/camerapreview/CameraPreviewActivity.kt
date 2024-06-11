package com.android.medialerta.presentation.camerapreview

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import medialerta.databinding.ActivityCameraPreviewBinding
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraPreviewBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var byteArray : ByteArray
    private lateinit var bm : Bitmap
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        checkCameraPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setupListeners() {
        binding.btnPhoto.setOnClickListener {
            previewPhoto()
        }
        binding.btnConfirm.setOnClickListener {
            takePhoto()
        }
        binding.btnNewPhoto.setOnClickListener {
            startCamera()
        }
    }

    private fun checkCameraPermission() {
        // Request camera permissions
        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            requestCameraPermissions()
        }
    }

    private fun isCameraPermissionGranted() =
        ContextCompat.checkSelfPermission(
            baseContext, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermissions() {
        activityResultLauncher.launch(Manifest.permission.CAMERA)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { permission ->
            if (!permission) {
                Toast.makeText(
                    baseContext,
                    "Permissão negada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        setPreviewVisibility(false)
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    baseContext,
                    exc.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun previewPhoto() {
        val imageCapture = imageCapture
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmapImage = previewToBitmap(image)
                    image.close()
                    setPreviewVisibility(true)
                    binding.imgView.setImageBitmap(bitmapImage)
                    binding.imgView.rotation = image.imageInfo.rotationDegrees.toFloat()
                    super.onCaptureSuccess(image)
                }
                override fun onError(exception: ImageCaptureException) {
                    setPreviewVisibility(false)
                    Toast.makeText(baseContext, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun takePhoto() {
        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // COMO LIMITAR O TAMANHO DA IMAGEM SALVA? - 2.5MB NO TESTE
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext, "Photo capture failed", Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    Intent().apply {
                        putExtra("imgURI", output.savedUri.toString())
                        setResult(RESULT_OK, this)
                    }
                    finish()
                }
            }
        )
    }

    private fun previewToBitmap(image: ImageProxy) : Bitmap
    {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        byteArray = bytes
        buffer.get(bytes)
        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        return bm
    }

    private fun setPreviewVisibility(isPreview : Boolean)
    {
        if(isPreview) {
            binding.imgView.visibility = View.VISIBLE
            binding.btnNewPhoto.visibility = View.VISIBLE
            binding.btnConfirm.visibility = View.VISIBLE
            binding.btnPhoto.visibility = View.INVISIBLE
            binding.viewFinder.visibility = View.INVISIBLE
        } else{
            binding.imgView.visibility = View.INVISIBLE
            binding.btnNewPhoto.visibility = View.INVISIBLE
            binding.btnConfirm.visibility = View.INVISIBLE
            binding.btnPhoto.visibility = View.VISIBLE
            binding.viewFinder.visibility = View.VISIBLE
        }
    }
}