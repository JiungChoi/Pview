package jiung.fastcampus.aop.part2.pview

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

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
            viewPager,
            { tab, position -> tab.text = tabTitles[position] }).attach()


    }

}