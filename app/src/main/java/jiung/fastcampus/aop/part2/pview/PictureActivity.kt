package jiung.fastcampus.aop.part2.pview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import jiung.fastcampus.aop.part2.pview.databinding.ActivityPictureBinding
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import jiung.fastcampus.aop.part2.pview.util.PathUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.Context as Context1


@Suppress("DEPRECATION")
class PictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private var personalSex: String? = null
    private var personalAge: Int? = null

    private var imgUrl: String = ""

    private val cameraMainExecutor by lazy {
        ContextCompat.getMainExecutor(this)
    }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            if (this@PictureActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation =
                        root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
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
    private var isFlashEnabled: Boolean = false
    private var root: View? = null

    private val displayManager by lazy {
        getSystemService(Context1.DISPLAY_SERVICE) as DisplayManager
    }

    private val uriList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        root = binding.root

        setContentView(binding.root)
        startPictureContextPopup()
        startCamera(binding.viewFinder)
    }


    private fun startPictureContextPopup() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.start_picture_context_popup, null)


        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val guideTextView = view.findViewById<TextView>(R.id.guideTextView)

        val buttonConfirm = view.findViewById<TextView>(R.id.button_confirm)

        guideTextView.text = PICTURE_GUIDE_QUOTE

        buttonConfirm.text = "확인"
        buttonConfirm.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun isCaringSkinContextPopup() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.caring_picture_context_popup, null)

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val caringPercentTextView = view.findViewById<TextView>(R.id.caringPercentTextView)

        startCountDown(caringPercentTextView, alertDialog)
        alertDialog.show()
    }

    private fun finishPictureContextPopup() {

        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.finish_picture_context_popup, null)

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val buttonConfirm = view.findViewById<TextView>(R.id.button_confirm)

        buttonConfirm.text = "확인"
        buttonConfirm.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        alertDialog.show()

    }

    private var currentCountDownTimer: CountDownTimer? = null

    private fun startCountDown(caringPercentTextView: TextView, alertDialog: AlertDialog) {
        currentCountDownTimer = createCountDownTimer(1000, caringPercentTextView, alertDialog)
        currentCountDownTimer?.start()
    }

    private fun createCountDownTimer(
        initialMillis: Int,
        caringPercentTextView: TextView,
        alertDialog: AlertDialog,
    ): android.os.CountDownTimer {
        return object : android.os.CountDownTimer(initialMillis.toLong(), 10L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainPercent(initialMillis - millisUntilFinished, caringPercentTextView)
            }

            override fun onFinish() {
                stopCountDown(alertDialog)
            }
        }
    }


    private fun updateRemainPercent(percent: Long, textView: TextView) {
        textView.text = "%02d".format(
            percent / 10)
    }

    private fun stopCountDown(alertDialog: AlertDialog) {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        alertDialog.dismiss()
    }


    private fun allPermissionGranted() = Companion.REQUESTED_PERMISSIONS.all {
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


    private fun bindCameraUseCase() = with(binding) {
        val rotation = viewFinder.display.rotation
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(LENS_FACING).build() // 카메라 설정 완료

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
                bindZoomListener()
                initFlashAndAddListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
    }

    private fun bindZoomListener() = with(binding) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector!!.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(this@PictureActivity, listener)
        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private fun bindCaptureListener() = with(binding) {
        captureButton.setOnClickListener {
            if (isCapturing.not()) {
                isCapturing = true
                captureCamera()
                isCaringSkinContextPopup()
                Handler().postDelayed({ finishPictureContextPopup() }, 2000L)
            }
        }
    }

    private fun initFlashAndAddListener() = with(binding) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        flashSwitch.isGone = hasFlash.not()
        if (hasFlash) {
            flashSwitch.setOnCheckedChangeListener { _, isChecked ->
                isFlashEnabled = isChecked
            }
        } else {
            flashSwitch.setOnCheckedChangeListener(null)
        }
    }

    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null)


                Handler(Looper.getMainLooper()).post {
                    imgUrl = it.toString()
                    binding.previewImageView.loadCenterCrop(url = it.toString(), corner = 4f)
                }

                uriList.add(it)
                flashLight(false)

                val fileUriAry = "$it".split("://")

                postUriToServer("${fileUriAry.last()}")
                Log.d("myTag PostImage", "PostImage 완료")

                false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }



    private fun postUriToServer(filePath: String) {
        //creating a file
        val file = File(filePath)



        //multipart/form-data
        var requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
            file)


        var body: MultipartBody.Part = MultipartBody.Part.createFormData("file",
            file.name,
            requestBody)

        val body2 : RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("uploaded_file", file.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
                ).build()


        //creating our api
        val apiClient = ApiClient
        val service = apiClient.getApiClient().create(RetrofitService::class.java)
        //apiClient.setBody(body)

        if (file.exists()) {
            val call:Call<getResoponseDto> = service.postSkinImg(body)

            call.enqueue(object : Callback<getResoponseDto> {

                override fun onResponse(
                    call: Call<getResoponseDto>,
                    response: Response<getResoponseDto>,
                ) {
                    if (response?.isSuccessful) {
                        Toast.makeText(applicationContext,
                            "File Uploaded Successfully...",
                            Toast.LENGTH_LONG).show()
                        Log.d("myTag PostImg01", response?.body().toString())
                    } else {
                        Toast.makeText(applicationContext,
                            "Some error occurred...",
                            Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<getResoponseDto>, t: Throwable) {
                    Log.d("myTag PostImg02", "${t.localizedMessage}")
                }
            })
        } else {
            Log.d("myTAG 03", "파일이 존재하지 않음")
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
        if (isFlashEnabled) flashLight(true)
        imageCapture.takePicture(outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    contentUri = savedUri

                    updateSavedImageContent()

                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCapturing = false
                    flashLight(false)
                }

            })
    }

    private fun flashLight(light: Boolean) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        if (hasFlash) {
            camera?.cameraControl?.enableTorch(light)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera(binding.viewFinder)
            } else {
                Toast.makeText(this, "현재 카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        internal const val REQUEST_CODE_PERMISSIONS = 10
        internal val REQUESTED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK
        private const val PICTURE_GUIDE_QUOTE =
            "정확한 진단을 위해\n" +
                    "피뷰 렌즈 장착을 하신 후\n" +
                    "뺨의 넓은 부분을\n" +
                    "촬영해주시기 바랍니다."

        internal const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm--ss-SSS"
    }
}

