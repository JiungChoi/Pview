package jiung.fastcampus.aop.part2.pview

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.ImageView
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
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Room
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.mainCareDate
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.personalSkinRank
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.recommendDataAry
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.skinDataAry
import jiung.fastcampus.aop.part2.pview.databinding.ActivityPictureBinding
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import jiung.fastcampus.aop.part2.pview.model.History
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
import java.text.DateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import android.content.Context as Context1


@Suppress("DEPRECATION")
class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private val mainActivity: MainActivity = MainActivity.mainActivity as MainActivity

    private var personalSex: String? = null
    private var personalAge: Int? = null

    private var imgUrl: String = ""

    private val cameraMainExecutor by lazy {
        ContextCompat.getMainExecutor(this)
    }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }
    private val cameraProvider by lazy{ cameraProviderFuture.get()}

    private val switchCameraImageView: ImageView by lazy { findViewById(R.id.switchCameraImageView) }

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
        setButtons()
    }

    private fun setButtons() {
        switchCameraImageView.setOnClickListener {
            cameraId = !cameraId

            startCamera(binding.viewFinder)
        }

    }

    private fun activityStackClear() {
        pictureActivity = this
        MainActivity.pictureActivity = pictureActivity as PictureActivity
        mainActivity?.finish()
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

        buttonConfirm.text = "??????"
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

        activityStackClear()
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.finish_picture_context_popup, null)

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val buttonConfirm = view.findViewById<TextView>(R.id.button_confirm)

        buttonConfirm.text = "??????"
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


        var cameraSelector = if (cameraId){
            CameraSelector.DEFAULT_BACK_CAMERA // ????????? ?????? ??????
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA // ????????? ?????? ??????
        }



        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
                // setTargetResolution() // ?????? ?????? ??? ??????????????? ?????? ???????????? ?????????
            }.build()

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // ????????? ????????? ??????. -> ????????? ????????? ??????.
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) // ??????????????? ????????? ?????? ???????????? ??? ??????????????????.
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            imageCapture = imageCaptureBuilder.build()

            // LifeCycle
            try {
                cameraProvider.unbindAll() //????????? ???????????? ????????? ????????? ????????? ?????????.
                camera = cameraProvider.bindToLifecycle(
                    this@PictureActivity, cameraSelector, preview, imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListener()
                bindZoomListener()
                bindFocusListener()
                initFlashAndAddListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)


    }

    //Tab to Focus
    private fun bindFocusListener() = with(binding) {

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
                if (isFlashEnabled) flashLight(false)

                val fileUriAry = "$it".split("://")

                postUriToServer("${fileUriAry.last()}")
                Log.d("myTag PostImage", "PostImage ??????")

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

        //creating our api
        val apiClient = ApiClient
        val service = apiClient.getApiClient().create(RetrofitService::class.java)

        if (file.exists()) {
            val call: Call<getResoponseDto> = service.postSkinImg(body)

            call.enqueue(object : Callback<getResoponseDto> {

                override fun onResponse(
                    call: Call<getResoponseDto>,
                    response: Response<getResoponseDto>,
                ) {
                    if (response?.isSuccessful) {
                        Toast.makeText(applicationContext,
                            "File Uploaded Successfully...",
                            Toast.LENGTH_LONG).show()
                        Log.d("myTag PostImg01", response?.body().toString().split("^").toString())

                        parsingData(response?.body().toString().split("^"))
                        finishPictureContextPopup()
                    } else {
                        Toast.makeText(applicationContext,
                            "Some error occurred...",
                            Toast.LENGTH_LONG).show()
                        finishPictureContextPopup()
                    }
                }

                private fun parsingData(resultArray: List<String>) {
                    personalSkinRank = resultArray[0]

                    val skinData = resultArray[1]
                        .replace("[{", "")
                        .replace("}]", "")
                        .split(",")
                    val recommendData = resultArray[2]
                        .replace("[{", "")
                        .replace("}]", "")
                        .split(",")

                    skinData.forEachIndexed { index, it ->
                        if (index == 1) {
                            skinDataAry[index] =
                                "${100 - (it.split(":")[1].toFloat().div(6f) * 100).toInt()}"

                        } else {
                            skinDataAry[index] =
                                "${100 - (it.split(":")[1].toFloat() * 100).toInt()}"
                        }
                    }
                    recommendData.forEachIndexed { index, it ->
                        recommendDataAry[index] = it.split(":")[1]
                    }

                    setAppdataBase()

                }

                override fun onFailure(call: Call<getResoponseDto>, t: Throwable) {
                    Log.d("myTag PostImg02", "${t.localizedMessage}")
                }
            })
        } else {
            Log.d("myTAG PostImg03", "????????? ???????????? ??????")
        }
    }

    private var contentUri: Uri? = null

    @SuppressLint("NewApi")
    private fun captureCamera() {
        if (::imageCapture.isInitialized.not()) return // ????????? ???????????? ???????????? ????????? ???????????? ???????????? ????????? ?????????.
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
        val hasFlash = camera?.cameraInfo?.hasFlashUnit()
        if (true == hasFlash) {
            camera?.cameraControl?.enableTorch(light)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera(binding.viewFinder)
            } else {
                Toast.makeText(this, "?????? ????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun nowTime(): String? {
        val dateFormat: DateFormat =
            java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.KOREAN)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    private fun setAppdataBase() {
        mainCareDate = nowTime().toString()

        Thread(Runnable {
            MainActivity.db.historyDao().insertHistory(
                History(null, MainActivity.personalSex, MainActivity.personalAge,
                    recommendDataAry[0],
                    recommendDataAry[1],
                    recommendDataAry[2],
                    recommendDataAry[3],
                    recommendDataAry[4],
                    recommendDataAry[5],
                    recommendDataAry[6],

                    skinDataAry[0],
                    skinDataAry[1],
                    skinDataAry[2],
                    skinDataAry[3],
                    skinDataAry[4],
                    skinDataAry[5],
                    mainCareDate))
        }).start()
    }

    companion object {
        // cameraId .. true : Back, false : Front
        private var cameraId: Boolean = true
        internal var pictureActivity: Activity? = null
        internal const val REQUEST_CODE_PERMISSIONS = 10
        internal val REQUESTED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK
        private const val PICTURE_GUIDE_QUOTE =
            "????????? ????????? ??????\n" +
                    "?????? ?????? ????????? ?????? ???\n" +
                    "?????? ?????? ?????????\n" +
                    "?????????????????? ????????????."

        internal const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm--ss-SSS"
    }
}

