package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.room.ColumnInfo
import androidx.room.Room
import jiung.fastcampus.aop.part2.pview.model.History
import kotlin.collections.ArrayList


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
        Thread(Runnable{
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
                        if (it.size > 5){
                            dbLog.clear()
                            for (i in 0..4){
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
            if (allPermissionGranted()) {
                startActivity(Intent(this, PictureActivity::class.java))
            } else {
                ActivityCompat.requestPermissions(this,
                    PictureActivity.REQUESTED_PERMISSIONS,
                    PictureActivity.REQUEST_CODE_PERMISSIONS)
            }
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

        textView.text = simpleTextViewList[idx-1]
        textView.textSize = 25f
        textView.setTextColor(Color.rgb(30, 167, 172))
    }

    private fun upDateSeekBars() {
        var careDataSeekBars = arrayOf(ampleLineCalmAcneSeekBar, ampleLineWhiteningSeekBar, ampleLineCalmStimulusSeekBar, ampleLineWrinkleSeekBar, ampleLineMoistureSeekBar, skinLineMoisturizingSeekBar, skinLineOilSeekBar)

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
        internal var recommendDataAry: Array<String> = arrayOf("미측정", "미측정", "미측정", "미측정", "미측정", "미측정", "미측정")

        // Wrinkle, SkinTone, PoreDetect, DeadSkin, Oilly, Pih
        internal var skinDataAry: Array<String> = arrayOf("None", "None", "None", "None", "None", "None")

        internal var personalSex = "여"
        internal var personalAge = "24"

        // Room
        internal lateinit var db: AppDatabase
        internal var dbLog : ArrayList<History> = arrayListOf()

        //gradient
        internal lateinit var gradient : Drawable
    }
}