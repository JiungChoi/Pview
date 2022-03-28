package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.forEachIndexed
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jiung.fastcampus.aop.part2.pview.databinding.ActivityMainBinding


class ChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        setComponent()
    }


    private fun setComponent() {
        setViewPager2()
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
        val tabTitles = listOf<String>("앰플라인", "스킨라인")
        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout,
            viewPager
        ) { tab, position -> tab.text = tabTitles[position] }.attach()
    }

}


