package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.room.Room
import jiung.fastcampus.aop.part2.pview.model.History
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

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
        initAppdataBase()

        loadAppdataBase()
        setComponent()
        updateCareTime(mainCareDateTextView)
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
                        recommendDataAry[0] = it.first().Acne.toString()
                        recommendDataAry[1] = it.first().Whitening.toString()
                        recommendDataAry[2] = it.first().Stimulus.toString()
                        recommendDataAry[3] = it.first().Wrinkle.toString()
                        recommendDataAry[4] = it.first().Moisture.toString()
                        recommendDataAry[5] = it.first().Moisturizing.toString()
                        recommendDataAry[6] = it.first().Oilly.toString()

                        updateSimpleInfoLayout()
                        upDateSeekBars()
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
                caring = true
                startActivity(Intent(this, PictureActivity::class.java))
            } else {
                ActivityCompat.requestPermissions(this,
                    PictureActivity.REQUESTED_PERMISSIONS,
                    PictureActivity.REQUEST_CODE_PERMISSIONS)
            }


        }
        mainChartButton.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }
        mainPersonalButton.setOnClickListener {
            startActivity(Intent(this, PersonalActivity::class.java))
        }
    }

    private fun updateSimpleInfoLayout() {
        updateTextView(mainSimpleSkinTextureInfo, 1)
        updateTextView(mainSimpleSkinToneInfo, 2)
        updateTextView(mainSimpleSkinOilInfo, 3)

        mainCenterNumberPercentage.isVisible = true
        mainCenterGuideTextView.isVisible = true
        mainCenterNumberTextView.text = "36"
        mainCenterNumberTextView.textSize = 55f
        mainCenterNumberTextView.setTextColor(Color.rgb(30, 167, 172))

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
        if (caring){
            mainCareDate = nowTime().toString()
            textView.text = "$mainCareDate 측정"
            caring = false
        }
    }


    private fun nowTime(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.KOREAN)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    companion object {
        private var caring: Boolean = false

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
    }
}