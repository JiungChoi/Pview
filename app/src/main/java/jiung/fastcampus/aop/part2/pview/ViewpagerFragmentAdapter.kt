package jiung.fastcampus.aop.part2.pview

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class ViewpagerFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    // 1. ViewPager2에 연결할 Fragment 들을 생성
    private val fragmentList = listOf<Fragment>(ChartPageAmpleLineFragment(),
        ChartPageSkinLineFragment())

    // 2. ViesPager2에서 노출시킬 Fragment 의 갯수 설정
    override fun getItemCount(): Int {
        return fragmentList.size
    }



    // 3. ViewPager2의 각 페이지에서 노출할 Fragment 설정

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}

