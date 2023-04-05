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
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.globalSkinAry
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.mainCareDate
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.personalSkinRank
import jiung.fastcampus.aop.part2.pview.databinding.ActivityPictureBinding
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import jiung.fastcampus.aop.part2.pview.model.History
import jiung.fastcampus.aop.part2.pview.util.PathUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.text.DateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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


    private var careType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        root = binding.root

        val intent:Intent = intent
        careType = intent.getStringExtra("type")!!

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

        val scdTextView = view.findViewById<TextView>(R.id.scdTextView)
        val caringPercentTextView = view.findViewById<TextView>(R.id.caringPercentTextView)

        startCountDown(caringPercentTextView, alertDialog, scdTextView)
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
        val scdTextView = view.findViewById<TextView>(R.id.scdTextView)

        buttonConfirm.text = "확인"
        scdTextView.text = "걸린시간 : $skinCareTime"
        buttonConfirm.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        alertDialog.show()

    }

    private var currentCountDownTimer: CountDownTimer? = null

    private fun startCountDown(
        caringPercentTextView: TextView,
        alertDialog: AlertDialog,
        scdTextView: TextView
    ) {

        currentCountDownTimer = createCountDownTimer(2000 + Random().nextInt(500),
            caringPercentTextView,
            alertDialog,
            scdTextView)
        currentCountDownTimer?.start()
    }

    private fun createCountDownTimer(
        initialMillis: Int,
        caringPercentTextView: TextView,
        alertDialog: AlertDialog,
        scdTextView: TextView,
    ): android.os.CountDownTimer {
        return object : android.os.CountDownTimer(initialMillis.toLong(), 10L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainPercent(initialMillis,
                    millisUntilFinished,
                    caringPercentTextView,
                    scdTextView)
            }

            override fun onFinish() {
                stopCountDown(alertDialog)
            }
        }
    }


    private var skinCareTime: String? = null
    private fun updateRemainPercent(
        initialMillis: Int,
        millisUntilFinished: Long,
        textView: TextView,
        scdTextView: TextView
    ) {
        textView.text = "%02d".format(
            ((initialMillis - millisUntilFinished) * 100 / (initialMillis)))
        skinCareTime = "%04dms".format(initialMillis - millisUntilFinished)
        scdTextView.text = skinCareTime
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
            CameraSelector.DEFAULT_BACK_CAMERA // 카메라 설정 완료
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA // 카메라 설정 완료
        }



        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
                // setTargetResolution() // 이거 크기 안 지정해주면 최대 해상도로 지정함
            }.build()

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // 지연을 최소화 한다. -> 화질을 최대화 한다.
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

        //creating our api
        val apiClient = ApiClient
        val service = apiClient.getApiClient().create(RetrofitService::class.java)

        if (file.exists()) {


            if (careType == "Global"){
                var call: Call<getResoponseDtoGlobal> = service.postGlobalSkinImg(body)
                call.enqueue(object : Callback<getResoponseDtoGlobal> {

                    override fun onResponse(
                        call: Call<getResoponseDtoGlobal>,
                        response: Response<getResoponseDtoGlobal>,
                    ) {
                        if (response?.isSuccessful) {
                            Toast.makeText(applicationContext,
                                "File Uploaded Successfully...",
                                Toast.LENGTH_LONG).show()

                            parsingData(response?.body().toString().split(","))
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
                        Log.d("myTag PostImg010", "${personalSkinRank.split(',')}")


                        resultArray.forEachIndexed { index, it ->
                            if (index == 2){
                                globalSkinAry[index] = it
                            } else if (index ==3){
                                globalSkinAry[index] = it
                            } else if (index ==5){
                                globalSkinAry[index] = it
                            } else if (index ==6){
                                globalSkinAry[index] = it
                            }
                        }

                        setAppdataBase()

                    }

                    override fun onFailure(call: Call<getResoponseDtoGlobal>, t: Throwable) {
                        Log.d("myTag PostImg02", "${t.localizedMessage}")
                    }
                })
            } else if (careType=="Detail"){
                var call: Call<getResoponseDtoDetail> = service.postDetailSkinImg(body)
                call.enqueue(object : Callback<getResoponseDtoDetail> {

                    override fun onResponse(
                        call: Call<getResoponseDtoDetail>,
                        response: Response<getResoponseDtoDetail>,
                    ) {
                        if (response?.isSuccessful) {
                            Toast.makeText(applicationContext,
                                "File Uploaded Successfully...",
                                Toast.LENGTH_LONG).show()
                            Log.d("myTag PostImg01", response?.body().toString().split(",").toString())

                            parsingData(response?.body().toString().split(","))
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

                        // FIXME 1. DATA Shape 다 변경하고 2. 디테일 이미지 진단 (dto랑 다 수정) 3. 불러오기도 적용
                        // "$email,$wrinkle,$skin_tone,$pore_detect,$dead_skin,$oilly,$pih"

                        resultArray.forEachIndexed { index, it ->
                            if (index == 1){
                                globalSkinAry[index-1] = it
                            } else if (index ==4){
                                globalSkinAry[index-1] = it
                            }

                        }

                        setAppdataBase()

                    }


                    @Override
                    override fun onFailure(call: Call<getResoponseDtoDetail>, t: Throwable) {
                        Log.d("myTag PostImg02", "${t.localizedMessage}")
                    }

                })
            }
            
        } else {
            Log.d("myTAG PostImg03", "파일이 존재하지 않음")
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
                Toast.makeText(this, "현재 카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
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

                    globalSkinAry[0],
                    globalSkinAry[1],
                    globalSkinAry[2],
                    globalSkinAry[3],
                    globalSkinAry[4],
                    globalSkinAry[5],
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
            "정확한 진단을 위해\n" +
                    "피뷰 렌즈 장착을 하신 후\n" +
                    "뺨의 넓은 부분을\n" +
                    "촬영해주시기 바랍니다."

        internal const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm--ss-SSS"
    }
}

