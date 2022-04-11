package jiung.fastcampus.aop.part2.pview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.dbLog
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.gradient
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.mainCareDate

class ChartPageAmpleLineFragment : Fragment() {

    private lateinit var chartCareDateTextView: TextView

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


    private lateinit var chartPageAcneLineChart: LineChart
    private lateinit var chartPageStimulusLineChart: LineChart
    private lateinit var chartPageWhiteningLineChart: LineChart
    private lateinit var chartPageWrinkleLineChart: LineChart
    private lateinit var chartPageMoistureLineChart: LineChart




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
        setLineCharts(view)
        setTextViews(view)

    }

    private fun setTextViews(view: View) {
        this.chartCareDateTextView = view.findViewById(R.id.charPageCareDateTextView)
        updateCareTime(chartCareDateTextView)
    }

    private fun updateCareTime(textView: TextView){
        val time = mainCareDate?.split(" ")
        if (time.size == 2){
            textView.text = time?.first() + "\n" + time?.last() + " 측정"
        } else {
            textView.text = "미측정"
        }

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


    private fun setLineCharts(view: View) {
        this.chartPageAcneLineChart = view.findViewById(R.id.chartPageAcneLineChart)
        this.chartPageStimulusLineChart = view.findViewById(R.id.chartPageStimulusLineChart)
        this.chartPageWhiteningLineChart = view.findViewById(R.id.chartPageWhiteningLineChart)
        this.chartPageWrinkleLineChart = view.findViewById(R.id.chartPageWrinkleLineChart)
        this.chartPageMoistureLineChart = view.findViewById(R.id.chartPageMoistureLineChart)

        makeWrinkleResultChart(chartPageAcneLineChart)

    //TODO else Charts
    //        makeStimulusChart(chartPageStimulusLineChart)
//        makeWhiteningChart(chartPageWhiteningLineChart)
//        makeWrinkleChart(chartPageWrinkleLineChart)
//        makeMoistureChart(chartPageMoistureLineChart)
    }


    private fun makeWrinkleResultChart(mpLineChart: LineChart) {

        val acneLineDataSet : LineDataSet = LineDataSet(resultWrinkleDataValue(), "주름")
        val dataSets : ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(acneLineDataSet)


        // ** STYLE
        mpLineChart.setNoDataText("No Data")
        //mpLineChart.setNoDataTextColor(Color.RED)

        mpLineChart.setDrawGridBackground(true)
        mpLineChart.setDrawBorders(true)
        mpLineChart.setBorderColor(Color.argb(Integer.parseInt("85", 16),
            Integer.parseInt("ED", 16),
            Integer.parseInt(
                "ED",
                16),
            Integer.parseInt("ED", 16)))
        mpLineChart.setBorderWidth(4f)

        val description : Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        acneLineDataSet.color = Color.CYAN
        acneLineDataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        acneLineDataSet.setDrawCircles(true)
        acneLineDataSet.setDrawCircleHole(true)
        acneLineDataSet.setCircleColor(Color.GRAY)
        acneLineDataSet.setCircleColorHole(Color.CYAN)
        acneLineDataSet.circleRadius = 3f
        acneLineDataSet.circleHoleRadius = 2f
        acneLineDataSet.fillAlpha = 50
        acneLineDataSet.fillFormatter
        acneLineDataSet.setDrawFilled(true)
        acneLineDataSet.fillDrawable = gradient
        acneLineDataSet.valueTextSize = 10f
        acneLineDataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(60f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis : XAxis = mpLineChart.xAxis
        val yAxis : YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth= 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data : LineData = LineData(acneLineDataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultWrinkleDataValue() : ArrayList<Entry>{
        var entry : ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size){

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
            Log.d("mymymy", "${date}")
            Log.d("mymymy", "${time}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size-1-i].resultWrinkle?.toFloat()!!))
        }
        return entry
    }

    private class ValueFormatter : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?,
        ): String {
            return "$value"
        }
    }

    private class XAxisValueFormatter(cnt: Int) : IAxisValueFormatter {
        private val cnt: Int = cnt

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            axis?.setLabelCount(this.cnt, true)
            return value.toString()

        }
    }




    companion object {
        // true is UpState, false is DownState
        private var CHART_PAGE_ACNE_STATE = true
        private var CHART_PAGE_STIMULUS_STATE = true
        private var CHART_PAGE_WHITENING_STATE = true
        private var CHART_PAGE_WRINKLE_STATE = true
        private var CHART_PAGE_MOISTURE_STATE = true
    }
}