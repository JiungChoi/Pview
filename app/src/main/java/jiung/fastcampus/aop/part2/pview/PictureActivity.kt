package jiung.fastcampus.aop.part2.pview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.camera2.internal.annotation.CameraExecutor
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import jiung.fastcampus.aop.part2.pview.databinding.ActivityMainBinding
import jiung.fastcampus.aop.part2.pview.databinding.ActivityPictureBinding
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import jiung.fastcampus.aop.part2.pview.util.PathUtil

import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.jar.Manifest

@Suppress("DEPRECATION")
class PictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private val cameraMainExecutor by lazy {
        ContextCompat.getMainExecutor(this)
    }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private val displayListener = object : DisplayManager.DisplayListener{
        override fun onDisplayChanged(displayId: Int) {
            if (this@PictureActivity.displayId == displayId){
                if (::imageCapture.isInitialized && root != null){
                    imageCapture.targetRotation = root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }
            }
        }

        override fun onDisplayAdded(displayId: Int) {
            Unit
        }

        override fun onDisplayRemoved(displayId: Int) {
            Unit
        }

    }

    private var displayId: Int = -1
    private var camera: Camera? = null
    private var isCapturing: Boolean = false
    private var root: View? = null

    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private val uriList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        root = binding.root

        setContentView(binding.root)

        if (allPermissionGranted()){
            startCamera(binding.viewFinder)
        } else {
            Log.d("here", "here!!")
            ActivityCompat.requestPermissions(
                this, REQUESTED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionGranted() = Companion.REQUESTED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(viewFinder: PreviewView) {
        displayManager.registerDisplayListener(displayListener, null)
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }


    private fun bindCameraUseCase() = with(binding){
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build() // 카메라 설정 완료

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
                // setTargetResolution() // 이거 크기 안 지정해주면 최대 해상도로 지정함
            }.build()

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // 지연을 최소화 한다.
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) // 프리뷰에서 보이는 거랑 촬영했을 때 동일화시킨다.
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            imageCapture = imageCaptureBuilder.build()

            // LifeCycle
            try {
                cameraProvider.unbindAll() //기존에 카메라가 쓰이고 있다면 해제를 해준다.
                camera = cameraProvider.bindToLifecycle(
                    this@PictureActivity, cameraSelector, preview, imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
    }


    private fun bindCaptureListener() = with(binding){
        captureButton.setOnClickListener{
            if (isCapturing.not()){
                isCapturing = true
                captureCamera()
            }
        }
    }

    private fun updateSavedImageContent(){
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
                Handler(Looper.getMainLooper()).post{
                    binding.previewImageView.loadCenterCrop(url = it.toString(), corner = 4f)
                }
                uriList.add(it)
                val intent = Intent(this, BodyActivity::class.java)
                startActivity(intent)
                false
            } catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private var contentUri: Uri? = null

    @SuppressLint("NewApi")
    private fun captureCamera() {
        if (::imageCapture.isInitialized.not()) return // 객체가 초기화가 되어있지 않다면 리턴으로 실행되지 않도록 막는다.
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                contentUri = savedUri
                updateSavedImageContent()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                isCapturing = false
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionGranted()){
                Log.d("he2","he2")
                startCamera(binding.viewFinder)
            } else {
                Log.d("he3","he3")
                Toast.makeText(this, "현재 카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUESTED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm--ss-SSS"
    }
}

