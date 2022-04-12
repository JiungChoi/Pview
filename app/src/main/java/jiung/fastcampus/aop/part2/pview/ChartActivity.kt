package jiung.fastcampus.aop.part2.pview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ChartActivity : AppCompatActivity() {
    private val mainActivity : MainActivity = MainActivity.mainActivity as MainActivity

    private val mainHomeButton: AppCompatImageButton by lazy {
        findViewById(R.id.mainHomeButton)
    }

    private val mainPictureButton: AppCompatImageButton by lazy {
        findViewById(R.id.mainPictureButton)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        activityStackClear()
        setComponent()
    }

    private fun activityStackClear() {
        chartActivity = this
        MainActivity.chartActivity = chartActivity as ChartActivity
        mainActivity?.finish()
    }



    private fun setComponent() {
        setViewPager2()
        setButtons()
    }

    private fun setButtons() {
        mainHomeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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
    }

    private fun allPermissionGranted() = PictureActivity.REQUESTED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun setViewPager2() {
        //ViewPager2, TabLayout 참조
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.chartPageTabLayout)
        // FragmentStateAdapter 생성
        val viewpagerFragmentAdapter = ViewpagerFragmentAdapter(this)
        // ViewPager2의 adapter 설정
        viewPager.adapter = viewpagerFragmentAdapter


        // ###### TabLayout과 ViewPager2를 연결
        // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
        val tabTitles = listOf<String>("진단 ", "앰플라인", "스킨라인")
        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout,
            viewPager
        ) { tab, position -> tab.text = tabTitles[position] }.attach()
    }

    companion object{
        internal var chartActivity: Activity? = null
    }

}


