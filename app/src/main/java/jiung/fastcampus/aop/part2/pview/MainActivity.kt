package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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

    // FIXME : Bottom Buttons
    private val mainPictureButton: AppCompatImageButton by lazy {
        findViewById(R.id.mainPictureButton)
    }

    private val mainChartButton: AppCompatImageButton by lazy{
        findViewById(R.id.mainChartButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setComponent()
        updateCareTime(mainCareDateTextView)
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
            updateSimpleInfoLayout()
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

        internal var careResultAcne : String = "미측정"
        internal var careResultStimulus : String = "미측정"
        internal var careResultWhitening : String = "미측정"
        internal var careResultWrinkle : String = "미측정"
        internal var careResultMoisture : String = "미측정"
    }
}