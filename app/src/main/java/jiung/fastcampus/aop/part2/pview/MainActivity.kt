package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.room.Room
import jiung.fastcampus.aop.part2.pview.extensions.loadCenterCrop
import jiung.fastcampus.aop.part2.pview.model.History
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.DateFormat
import java.util.*


@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class MainActivity : AppCompatActivity() {
    private val loginActivity : LoginActivity = LoginActivity.loginActivity as LoginActivity


    private val mainCenterGuideTextView: TextView by lazy {
        findViewById(R.id.mainCenterGuideTextView)
    }

    private val mainCenterNumberTextView: TextView by lazy {
        findViewById(R.id.mainCenterNumberTextView)
    }

    private val mainCenterNumberPercentage: TextView by lazy {
        findViewById(R.id.mainCenterNumberPercentage)
    }

    private val mainCareDateTextView: TextView by lazy {
        findViewById(R.id.mainCareDateTextView)
    }

    private val mainSimpleSkinTextureInfo: TextView by lazy {
        findViewById(R.id.mainSimpleSkinTextureInfo)
    }

    private val mainSimpleSkinToneInfo: TextView by lazy {
        findViewById(R.id.mainSimpleSkinToneInfo)
    }

    private val mainSimpleSkinOilInfo: TextView by lazy {
        findViewById(R.id.mainSimpleSkinOilInfo)
    }

    private val mainCenterRectangleControllerTextView: Button by lazy {
        findViewById(R.id.mainCenterRectangleControllerTextView)
    }

    private val simpleInfoLayout: ConstraintLayout by lazy {
        findViewById(R.id.simpleInfoLayout)
    }

    private val detailInfoLayout: ConstraintLayout by lazy {
        findViewById(R.id.detailInfoLayout)
    }

    private val mainCenterRectangleView: View by lazy {
        findViewById(R.id.mainCenterRectangleView)
    }

    private val mainBottomView: View by lazy {
        findViewById(R.id.mainBottomView)
    }
    // SeekBars
    private val ampleLineCalmAcneSeekBar: SeekBar by lazy {
        findViewById(R.id.ampleLineCalmAcneSeekBar)
    }

    private val ampleLineCalmStimulusSeekBar: SeekBar by lazy {
        findViewById(R.id.ampleLineCalmStimulusSeekBar)
    }

    private val ampleLineWhiteningSeekBar: SeekBar by lazy {
        findViewById(R.id.ampleLineWhiteningSeekBar)
    }

    private val ampleLineWrinkleSeekBar: SeekBar by lazy {
        findViewById(R.id.ampleLineWrinkleSeekBar)
    }

    private val ampleLineMoistureSeekBar: SeekBar by lazy {
        findViewById(R.id.ampleLineMoistureSeekBar)
    }

    private val skinLineMoisturizingSeekBar: SeekBar by lazy {
        findViewById(R.id.skinLineMoisturizingSeekBar)
    }

    private val skinLineOilSeekBar: SeekBar by lazy {
        findViewById(R.id.skinLineOilSeekBar)
    }

    // Bottom Buttons
    private val mainPictureButton: AppCompatImageButton by lazy {
        findViewById(R.id.mainPictureButton)
    }

    private val mainChartButton: AppCompatImageButton by lazy{
        findViewById(R.id.mainChartButton)
    }

    private val mainPersonalButton: AppCompatImageButton by lazy{
        findViewById(R.id.mainPersonalButton)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this

        activityStackClear()
        setGradients()
        initAppdataBase()
        loadAppdataBase()
        setComponent()

    }

    private fun activityStackClear(){
        loginActivity?.finish()
        pictureActivity?.finish()
        chartActivity?.finish()
        personalActivity?.finish()
    }

    private fun setGradients() {
        gradient = ContextCompat.getDrawable(this,
            R.drawable.bg_gradient_pink_cyan)!!
    }

    private fun initAppdataBase() {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    // DB에서 모든 데이터 삭제하는 기능 : 개발자용
    private fun deleteAppdataBase(){
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }

    private fun loadAppdataBase() {
        Thread(Runnable {
            db.historyDao().getAll().reversed().let {
                runOnUiThread {
                    Log.d("myTag LoadAppData 00", "$it")
                    if (it.isEmpty()) {

                    } else {
                        if (it.size > 5) {
                            dbLog.clear()
                            for (i in 0..4) {
                                dbLog.add(it[i])
                            }
                            Log.d("mytag DBLog", "$dbLog")
                        } else {
                            dbLog.clear()
                            for (element in it) {
                                dbLog.add(element)
                            }
                        }

                        recommendDataAry[0] = it.first().recommendAcne.toString()
                        recommendDataAry[1] = it.first().recommendWhitening.toString()
                        recommendDataAry[2] = it.first().recommendStimulus.toString()
                        recommendDataAry[3] = it.first().recommendWrinkle.toString()
                        recommendDataAry[4] = it.first().recommendMoisture.toString()
                        recommendDataAry[5] = it.first().recommendMoisturizing.toString()
                        recommendDataAry[6] = it.first().recommendOilly.toString()

                        skinDataAry[0] = it.first().resultWrinkle.toString()
                        skinDataAry[1] = it.first().resultSkinTone.toString()
                        skinDataAry[2] = it.first().resultPoreDetect.toString()
                        skinDataAry[3] = it.first().resultDeadSkin.toString()
                        skinDataAry[4] = it.first().resultOilly.toString()
                        skinDataAry[5] = it.first().resultPih.toString()

                        mainCareDate = it.first().time.toString()

                        // Update Sync
                        var wait = true
                        while (wait) {
                            wait = updateSimpleInfoLayout()
                        }
                        upDateSeekBars()
                        updateCareTime(mainCareDateTextView)
                    }
                }
            }
        }).start()
    }


    private var mainBottomSimpleHeight: Int? = null
    private var mainCenterRectangleViewSimpleHeight: Int? = null
    private fun setComponent() {
        mainCenterRectangleControllerTextView.setOnClickListener {
            if (mainCenterRectangleControllerTextView.text == "상세보기") {

                simpleInfoLayout.isVisible = false
                detailInfoLayout.isVisible = true

                this.mainBottomSimpleHeight = mainBottomView.height
                this.mainCenterRectangleViewSimpleHeight = mainCenterRectangleView.height
                mainBottomView.setHeight(2100)
                mainCenterRectangleView.setHeight(1400)
                mainCenterRectangleControllerTextView.text = "간단히보기"

            } else if (mainCenterRectangleControllerTextView.text == "간단히보기") {

                simpleInfoLayout.isVisible = true
                detailInfoLayout.isVisible = false

                mainCenterRectangleView.setHeight(mainCenterRectangleViewSimpleHeight!!)
                mainBottomView.setHeight(mainBottomSimpleHeight!!)
                mainCenterRectangleControllerTextView.text = "상세보기"

            }

        }
        mainPictureButton.setOnClickListener {
            var pictureType: String = "None"
            pictureType = selectPictureTypeContextPopup()

        }
        mainChartButton.setOnClickListener {
            if (recommendDataAry[0] == "미측정"){
                Toast.makeText(this, "진단 후 이용 가능한 서비스입니다.", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, ChartActivity::class.java))
            }
        }

        mainPersonalButton.setOnClickListener {
            startActivity(Intent(this, PersonalActivity::class.java))
        }
    }

    private fun selectPictureTypeContextPopup() :String {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.select_picture_context_popup, null)

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        val globalCareButton = view.findViewById<TextView>(R.id.global_care_button)
        val detailCareButton = view.findViewById<TextView>(R.id.detail_care_button)

        var type = "None"
        var loadType: String = "None"

        globalCareButton.setOnClickListener {
            "Global".also { type = it }
            loadType = selectLoadTypeContextPopup(type)
            alertDialog.dismiss()
        }

        detailCareButton.setOnClickListener {
            "Detail".also { type = it }
            loadType = selectLoadTypeContextPopup(type)
            alertDialog.dismiss()
        }

        alertDialog.show()
        return type
    }

    private fun selectLoadTypeContextPopup(type: String) :String {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.select_load_context_popup, null)


        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        // 찍어서 진단할건지 불러와서 진단할건지 선택
        val pictureButton = view.findViewById<TextView>(R.id.button_picture)
        val loadButton = view.findViewById<TextView>(R.id.button_load)


        pictureButton.setOnClickListener {
            if (allPermissionGranted()) {
                startActivity(Intent(this, PictureActivity::class.java))
            } else {
                ActivityCompat.requestPermissions(this,
                    PictureActivity.REQUESTED_PERMISSIONS,
                    PictureActivity.REQUEST_CODE_PERMISSIONS)
            }
            alertDialog.dismiss()
        }

        loadButton.setOnClickListener {
            if (allPermissionGranted()) {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        //권한이 잘 부여 되었을 때 갤러리에서 사진을 선택하는 기능
                        navigatePhoto()
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        //교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                        showPermissionContextPopup()
                    }
                    else -> {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            1000
                        )
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this,
                    PictureActivity.REQUESTED_PERMISSIONS,
                    PictureActivity.REQUEST_CODE_PERMISSIONS)
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
        return type
    }

    private fun navigatePhoto() {
        val intent =
            Intent(Intent.ACTION_GET_CONTENT) //ACTION_GET_CONTENT 스트링은 이 인덴트는 SAF 기능으로 안드로이드 내장해 있는 액티비티 실행
        intent.type = "image/*" // png jpg 등등 이미지 파일 필터링
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            // 정상적으로 데이터를 받아오지 못 했을 때
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    isCaringSkinContextPopup(selectedImageUri.toString())

                    val filePath = getRealPathFromURI(selectedImageUri)

                    postUriToServer("$filePath")
                    Log.d("onActivityResult:main", "PostImage 완료")

                } else {
                    Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못 했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isCaringSkinContextPopup(uri: String) {
        val layoutInflater = LayoutInflater.from(this)
        val loadSkinView = layoutInflater.inflate(R.layout.load_and_send_picture_popup, null)
        val loadSkinImageImageView = loadSkinView.findViewById<ImageView>(R.id.loadSkinImageImageView)
        val alertDialogLoadAndSend = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(loadSkinView)
            .create()
        alertDialogLoadAndSend.show()
        loadSkinImageImageView.loadCenterCrop(url = uri, corner = 4f)

        val scdTextView = loadSkinView.findViewById<TextView>(R.id.scdTextView)
        val caringPercentTextView = loadSkinView.findViewById<TextView>(R.id.caringPercentTextView)

        startCountDown(caringPercentTextView, alertDialogLoadAndSend, scdTextView)
        alertDialogLoadAndSend.show()
    }

    private var currentCountDownTimer: CountDownTimer? = null
    private fun startCountDown(caringPercentTextView: TextView, alertDialog: AlertDialog, scdTextView:TextView) {

        currentCountDownTimer = createCountDownTimer(1000 + Random().nextInt(1000), caringPercentTextView, alertDialog, scdTextView)
        currentCountDownTimer?.start()
    }

    private fun createCountDownTimer(
        initialMillis: Int,
        caringPercentTextView: TextView,
        alertDialog: AlertDialog,
        scdTextView : TextView,
    ): android.os.CountDownTimer {
        return object : android.os.CountDownTimer(initialMillis.toLong(), 10L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainPercent(initialMillis, millisUntilFinished, caringPercentTextView, scdTextView)
            }

            override fun onFinish() {
                stopCountDown(alertDialog)
            }
        }
    }

    private fun stopCountDown(alertDialog: AlertDialog) {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        alertDialog.dismiss()
    }

    private var skinCareTime: String? = null
    private fun updateRemainPercent(initialMillis: Int, millisUntilFinished:Long, textView: TextView, scdTextView: TextView) {
        textView.text = "%02d".format(
            ((initialMillis - millisUntilFinished)*100 / (initialMillis)))
        skinCareTime = "%04dms".format(initialMillis - millisUntilFinished)
        scdTextView.text = skinCareTime
    }


    private fun postUriToServer(filePath: String) {
        //creating a file
        val file = File(filePath)
        Log.d("myTAG HERE:main", "$filePath")

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
            Log.d("myTAG PostImg03", "파일이 존재하지 않음")
        }
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

    private fun nowTime(): String? {
        val dateFormat: DateFormat =
            java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.KOREAN)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해서는 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun updateSimpleInfoLayout() :Boolean{
        if (recommendDataAry[0] != "미측정"){
            updateTextView(mainSimpleSkinTextureInfo, 1)
            updateTextView(mainSimpleSkinToneInfo, 2)
            updateTextView(mainSimpleSkinOilInfo, 3)

            mainCenterNumberPercentage.isVisible = true
            mainCenterGuideTextView.isVisible = true

            val skinStateScore = (skinDataAry[0].toInt()+skinDataAry[1].toInt()+skinDataAry[2].toInt()+skinDataAry[3].toInt()+skinDataAry[4].toInt()+skinDataAry[5].toInt()).toFloat()/6
            mainCenterNumberTextView.text = (100 - skinStateScore.toInt()).toString()
            mainCenterNumberTextView.textSize = 55f
            mainCenterNumberTextView.setTextColor(Color.rgb(30, 167, 172))
        } else {

        }
        return false
    }

    private fun updateTextView(textView: TextView, idx: Int){
        val simpleTextViewList : ArrayList<String> = arrayListOf("관리\n필요", "균형\n잡힘", "건조함")

        textView.text = simpleTextViewList[idx - 1]
        textView.textSize = 25f
        textView.setTextColor(Color.rgb(30, 167, 172))
    }

    private fun upDateSeekBars() {
        var careDataSeekBars = arrayOf(ampleLineCalmAcneSeekBar,
            ampleLineWhiteningSeekBar,
            ampleLineCalmStimulusSeekBar,
            ampleLineWrinkleSeekBar,
            ampleLineMoistureSeekBar,
            skinLineMoisturizingSeekBar,
            skinLineOilSeekBar)

        recommendDataAry.forEachIndexed { index, it ->
            upData(careDataSeekBars[index], it)
        }

    }

    private fun upData(seekBar: SeekBar, it: String) {
        if (it == "미측정"){
            seekBar.isVisible = false
        } else {
            seekBar.isVisible = true
            seekBar.progress =  seekBar.max - it.toInt()
        }
    }


    private fun allPermissionGranted() = PictureActivity.REQUESTED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":".toRegex()).toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor: Cursor? = contentResolver.query(MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            null)
        try {
            val columnIndex: Int = cursor!!.getColumnIndex(columns[0])
            if (cursor?.moveToFirst()) {
                return cursor?.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /** * Extension method to set View's height. */
    private fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    /** * Extension method to set View's width. */
    private fun View.setWidth(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.width = value
            layoutParams = lp
        }
    }

    private fun updateCareTime(textView: TextView){
        textView.text = "$mainCareDate"
    }


    companion object {

        // other Activity
        internal var pictureActivity : PictureActivity? = null
        internal var chartActivity : ChartActivity? = null
        internal var personalActivity : PersonalActivity? = null

        // this Activity
        internal var mainActivity: Activity? = null

        internal var mainCareDate : String = "미측정"
        internal var personalSkinRank : String = "미측정"

        // Acne, Whitening, Stimulus, Wrinkle, Moisture, Moisturizing, Oilly
        internal var recommendDataAry: Array<String> = arrayOf("미측정",
            "미측정",
            "미측정",
            "미측정",
            "미측정",
            "미측정",
            "미측정")

        // Wrinkle, SkinTone, PoreDetect, DeadSkin, Oilly, Pih
        internal var skinDataAry: Array<String> = arrayOf("None",
            "None",
            "None",
            "None",
            "None",
            "None")

        internal var personalSex = "여"
        internal var personalAge = "24"

        private const val SELECT_PICTURE_GUIDE_QUOTE = "원하는 진단 방법을\n선택해주시기 바랍니다"

        // Room
        internal lateinit var db: AppDatabase
        internal var dbLog : ArrayList<History> = arrayListOf()

        //gradient
        internal lateinit var gradient : Drawable
    }
}