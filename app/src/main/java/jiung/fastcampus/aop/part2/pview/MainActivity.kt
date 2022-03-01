package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private val mainCareDateTextView: TextView by lazy {
        findViewById(R.id.mainCareDateTextView)
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
            startActivity(Intent(this, PictureActivity::class.java))
        }
        mainChartButton.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }
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


    companion object {

    }

}