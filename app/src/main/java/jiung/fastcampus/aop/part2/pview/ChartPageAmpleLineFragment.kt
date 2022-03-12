package jiung.fastcampus.aop.part2.pview

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.util.Util
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.utils.Utils
import java.security.KeyStore

class ChartPageAmpleLineFragment : Fragment() {

    private lateinit var chartPageAcneLayout: ConstraintLayout
    private lateinit var chartPageStimulusLayout: ConstraintLayout
    private lateinit var chartPageWhiteningLayout: ConstraintLayout
    private lateinit var chartPageWrinkleLayout: ConstraintLayout
    private lateinit var chartPageMoistureLayout: ConstraintLayout

    private lateinit var chartPageAcneUpDownImageView: ImageView
    private lateinit var chartPageStimulusUpDownImageView: ImageView
    private lateinit var chartPageWhiteningUpDownImageView: ImageView
    private lateinit var chartPageWrinkleUpDownImageView: ImageView
    private lateinit var chartPageMoistureUpDownImageView: ImageView

    private lateinit var chartPageAcneChartLayout: ConstraintLayout
    private lateinit var chartPageStimulusChartLayout: ConstraintLayout
    private lateinit var chartPageWhiteningChartLayout: ConstraintLayout
    private lateinit var chartPageWrinkleChartLayout: ConstraintLayout
    private lateinit var chartPageMoistureChartLayout: ConstraintLayout

    private lateinit var chartPageAcneSeekBar: SeekBar
    private lateinit var chartPageStimulusSeekBar: SeekBar
    private lateinit var chartPageWhiteningSeekBar: SeekBar
    private lateinit var chartPageWrinkleSeekBar: SeekBar
    private lateinit var chartPageMoistureSeekBar: SeekBar


    private lateinit var chartPageAcneLineChart: LineChart
    private val chartPageAcneArrayList: ArrayList<Entry> = ArrayList() // 데이터 배열


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.chart_page_ample_fragment, container, false)
        setComponents(view)

        return view
    }

    private fun setComponents(view: View) {
        setLayouts(view)
        setImageVies(view)
        setSeekBars(view)
        setLineCharts(view)
    }

    private fun setLayouts(view: View) {
        this.chartPageAcneLayout = view.findViewById(R.id.chartPageAcneLayout)
        this.chartPageStimulusLayout = view.findViewById(R.id.chartPageStimulusLayout)
        this.chartPageWhiteningLayout = view.findViewById(R.id.chartPageWhiteningLayout)
        this.chartPageWrinkleLayout = view.findViewById(R.id.chartPageWrinkleLayout)
        this.chartPageMoistureLayout = view.findViewById(R.id.chartPageMoistureLayout)

        this.chartPageAcneChartLayout = view.findViewById(R.id.chartPageAcneChartLayout)
        this.chartPageStimulusChartLayout = view.findViewById(R.id.chartPageStimulusChartLayout)
        this.chartPageWhiteningChartLayout = view.findViewById(R.id.chartPageWhiteningChartLayout)
        this.chartPageWrinkleChartLayout = view.findViewById(R.id.chartPageWrinkleChartLayout)
        this.chartPageMoistureChartLayout = view.findViewById(R.id.chartPageMoistureChartLayout)

        setLayoutsClickListener()
    }

    private fun setLayoutsClickListener() {
        this.chartPageAcneLayout.setOnClickListener {
            if (CHART_PAGE_ACNE_STATE) {
                this.chartPageAcneUpDownImageView.rotation = 90f
                chartPageAcneChartLayout.isVisible = true
                CHART_PAGE_ACNE_STATE = false
            } else {
                this.chartPageAcneUpDownImageView.rotation = 270f
                chartPageAcneChartLayout.isVisible = false
                CHART_PAGE_ACNE_STATE = true
            }
        }

        this.chartPageStimulusLayout.setOnClickListener {
            if (CHART_PAGE_STIMULUS_STATE) {
                this.chartPageStimulusUpDownImageView.rotation = 90f
                chartPageStimulusChartLayout.isVisible = true
                CHART_PAGE_STIMULUS_STATE = false
            } else {
                this.chartPageStimulusUpDownImageView.rotation = 270f
                chartPageStimulusChartLayout.isVisible = false
                CHART_PAGE_STIMULUS_STATE = true
            }
        }

        this.chartPageWhiteningLayout.setOnClickListener {
            if (CHART_PAGE_WHITENING_STATE) {
                this.chartPageWhiteningUpDownImageView.rotation = 90f
                chartPageWhiteningChartLayout.isVisible = true
                CHART_PAGE_WHITENING_STATE = false
            } else {
                this.chartPageWhiteningUpDownImageView.rotation = 270f
                chartPageWhiteningChartLayout.isVisible = false
                CHART_PAGE_WHITENING_STATE = true
            }
        }

        this.chartPageWrinkleLayout.setOnClickListener {
            if (CHART_PAGE_WRINKLE_STATE) {
                this.chartPageWrinkleUpDownImageView.rotation = 90f
                CHART_PAGE_WRINKLE_STATE = false
                chartPageWrinkleChartLayout.isVisible = true
            } else {
                this.chartPageWrinkleUpDownImageView.rotation = 270f
                chartPageWrinkleChartLayout.isVisible = false
                CHART_PAGE_WRINKLE_STATE = true
            }
        }

        this.chartPageMoistureLayout.setOnClickListener {
            if (CHART_PAGE_MOISTURE_STATE) {
                this.chartPageMoistureUpDownImageView.rotation = 90f
                chartPageMoistureChartLayout.isVisible = true
                CHART_PAGE_MOISTURE_STATE = false
            } else {
                this.chartPageMoistureUpDownImageView.rotation = 270f
                chartPageMoistureChartLayout.isVisible = false
                CHART_PAGE_MOISTURE_STATE = true
            }
        }
    }

    private fun setImageVies(view: View) {
        this.chartPageAcneUpDownImageView = view.findViewById(R.id.chartPageAcneUpDownImageView)
        this.chartPageStimulusUpDownImageView =
            view.findViewById(R.id.chartPageStimulusUpDownImageView)
        this.chartPageWhiteningUpDownImageView =
            view.findViewById(R.id.chartPageWhiteningUpDownImageView)
        this.chartPageWrinkleUpDownImageView =
            view.findViewById(R.id.chartPageWrinkleUpDownImageView)
        this.chartPageMoistureUpDownImageView =
            view.findViewById(R.id.chartPageMoistureUpDownImageView)

    }

    private fun setSeekBars(view: View) {
        this.chartPageAcneSeekBar = view.findViewById(R.id.chartPageAcneSeekBar)
        this.chartPageStimulusSeekBar = view.findViewById(R.id.chartPageStimulusSeekBar)
        this.chartPageWhiteningSeekBar = view.findViewById(R.id.chartPageWhiteningSeekBar)
        this.chartPageWrinkleSeekBar = view.findViewById(R.id.chartPageWrinkleSeekBar)
        this.chartPageMoistureSeekBar = view.findViewById(R.id.chartPageMoistureSeekBar)
    }

    private fun setLineCharts(view: View) {
        this.chartPageAcneLineChart = view.findViewById(R.id.chartPageAcneLineChart)

        makeChart(chartPageAcneLineChart, chartPageAcneArrayList)
    }


    private fun makeChart(chart: LineChart, entries: ArrayList<Entry>) {

        // Entry List Values Input
        entries.add(Entry(0F, 0F))
        entries.add(Entry(1F, 1F))
        entries.add(Entry(2F, 2F))
        entries.add(Entry(3F, 4F))
        entries.add(Entry(4F, 5F))


        // 그래프 구현을 위한 LineDataSet 생성
        var dataset: LineDataSet = LineDataSet(entries, "input")

        // 그래프 data 생성 -> 최종 입력 데이터
        var data: LineData = LineData(dataset)

        // chart.xml에 배치된 lineChart에 데이터 연결
        chart.data = data

        chart.invalidate()

        // // Chart Style // //
        // Background Color
        chart.setBackgroundColor(Color.argb(Integer.parseInt("85", 16), Integer.parseInt("ED", 16), Integer.parseInt("ED", 16), Integer.parseInt("ED", 16)))

        // Disable Description Text
        chart.description.isEnabled = false

        // Enable Touch Gesture
        chart.setTouchEnabled(false)

        // Set Listeners
        chart.setDrawGridBackground(false)

        //크기 조정 및 드래그 활성화
        //@ chart.isDragEnabled = true
        //@ chart.setScaleEnabled(true)
        //@ chart.isScaleXEnabled = true
        //@ chart.isScaleYEnabled = true

        // 두 축을 따라 강제로 핀치 줌
        chart.setPinchZoom(true)

        // // X-Axis Style // //
        val xAxis: XAxis = chart.xAxis

        // 위치: 아래
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // 수직 격자선
        xAxis.enableGridDashedLine(5f, 5f, 0f)

        // 수직 범례 색상
        xAxis.textColor = Color.BLACK
        xAxis.axisLineColor = Color.BLACK

        // 그래프 좌우 여백
        xAxis.spaceMax = 0.3f
        xAxis.spaceMin = 0.3f

        // // Y-Axis Style // //
        val YAxis: YAxis = chart.axisLeft

        // 이중 축 비활성화 (왼쪽 축만 사용)
        chart.axisRight.isEnabled = false

        // 수평 격자선
        YAxis.enableGridDashedLine(10f, 10f, 0f)

        // 수평 범례색상
        YAxis.textColor = Color.BLACK
        YAxis.axisLineColor = Color.BLACK

        // Axis Range
        YAxis.axisMaximum = 30f
        YAxis.axisMinimum = -10f
    }


/*
private fun optionChart(chart: LineChart){

    // // Set Data // //
    val lineDataSet: LineDataSet

    if (chart.data != null && chart.data.dataSetCount > 0) {
        lineDataSet = chart.data.getDataSetByIndex(0) as LineDataSet
        lineDataSet.values = chartPageAcneArrayList
        lineDataSet.notifyDataSetChanged()
    } else {
        lineDataSet = LineDataSet(chartPageAcneArrayList, "값설명")
    }

    // // Cubic Line Chart // //
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    lineDataSet.setDrawIcons(false)

    // Draw Dashed Line
    lineDataSet.enableDashedLine(10f, 5f, 0f)

    // Lines And Points
    lineDataSet.color = Color.WHITE
    lineDataSet.setCircleColor(Color.WHITE)

    // 선 두께 및 포인트 크기
    lineDataSet.lineWidth = 1f
    lineDataSet.circleRadius = 2f

    // 점을 실선으로 그리기
    lineDataSet.setDrawCircleHole(false)

    // 범례 항목 사용자 정의
    lineDataSet.formLineWidth = 1f
    lineDataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
    lineDataSet.formSize = 15f

    // Values Text Size
    lineDataSet.valueTextSize = 9f

    // 점선으로 선택 선 그리기
    lineDataSet.enableDashedHighlightLine(10f, 5f, 0f)

    // 채워진 영역 설정
    lineDataSet.setDrawFilled(true)
    lineDataSet.fillFormatter = IFillFormatter { _, _ ->
        chart.axisLeft.axisMinimum
    }

    // Set Color Of Filled Area
    if (Utils.getSDKInt() >= 18) {
        // Drawables Only Supported On API Level 18 And Above
        // val drawable: Drawable = ContextCompat.getDrawable(activity, R.drawable.fade_light)
        //lineDataSet.fillDrawable = drawable
    } else {
        //lineDataSet.fillColor = Color.WHITE
    }


}*/


    companion object {
        // true is UpState, false is DownState
        private var CHART_PAGE_ACNE_STATE = true
        private var CHART_PAGE_STIMULUS_STATE = true
        private var CHART_PAGE_WHITENING_STATE = true
        private var CHART_PAGE_WRINKLE_STATE = true
        private var CHART_PAGE_MOISTURE_STATE = true
    }
}