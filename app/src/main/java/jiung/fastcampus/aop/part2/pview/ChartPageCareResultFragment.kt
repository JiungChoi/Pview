package jiung.fastcampus.aop.part2.pview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
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
import jiung.fastcampus.aop.part2.pview.MainActivity.Companion.skinDataAry

class ChartPageCareResultFragment : Fragment() {

    private lateinit var chartCareDateTextView: TextView

    private lateinit var chartPageResultWrinkleLayout: ConstraintLayout
    private lateinit var chartPageSkinToneLayout: ConstraintLayout
    private lateinit var chartPagePoreDetectLayout: ConstraintLayout
    private lateinit var chartPageDeadSkinLayout: ConstraintLayout
    private lateinit var chartPageResultOilLayout: ConstraintLayout
    private lateinit var chartPageResultPihLayout: ConstraintLayout

    private lateinit var chartPageUserResultWrinkleResultTextView: TextView
    private lateinit var chartPageUserSkinToneResultTextView: TextView
    private lateinit var chartPageUserPoreDetectResultTextView: TextView
    private lateinit var chartPageUserDeadSkinResultTextView: TextView
    private lateinit var chartPageUserResultOilResultTextView: TextView
    private lateinit var chartPageUserResultPihResultTextView: TextView

    private lateinit var chartPageResultWrinkleUpDownImageView: ImageView
    private lateinit var chartPageSkinToneUpDownImageView: ImageView
    private lateinit var chartPagePoreDetectUpDownImageView: ImageView
    private lateinit var chartPageDeadSkinUpDownImageView: ImageView
    private lateinit var chartPageResultOilUpDownImageView: ImageView
    private lateinit var chartPageResultPihUpDownImageView: ImageView

    private lateinit var chartPageResultWrinkleChartLayout: ConstraintLayout
    private lateinit var chartPageSkinToneChartLayout: ConstraintLayout
    private lateinit var chartPagePoreDetectChartLayout: ConstraintLayout
    private lateinit var chartPageDeadSkinChartLayout: ConstraintLayout
    private lateinit var chartPageResultOilChartLayout: ConstraintLayout
    private lateinit var chartPageResultPihChartLayout: ConstraintLayout

    private lateinit var chartPageResultWrinkleLineChart: LineChart
    private lateinit var chartPageSkinToneLineChart: LineChart
    private lateinit var chartPagePoreDetectLineChart: LineChart
    private lateinit var chartPageDeadSkinLineChart: LineChart
    private lateinit var chartPageResultOilLineChart: LineChart
    private lateinit var chartPageResultPihLineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.chart_page_result_fragment, container, false)
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

        chartPageUserResultWrinkleResultTextView = view.findViewById(R.id.chartPageUserResultWrinkleResultTextView)
        chartPageUserSkinToneResultTextView = view.findViewById(R.id.chartPageUserSkinToneResultTextView)
        chartPageUserPoreDetectResultTextView = view.findViewById(R.id.chartPageUserPoreDetectResultTextView)
        chartPageUserDeadSkinResultTextView = view.findViewById(R.id.chartPageUserDeadSkinResultTextView)
        chartPageUserResultOilResultTextView = view.findViewById(R.id.chartPageUserResultOilResultTextView)
        chartPageUserResultPihResultTextView = view.findViewById(R.id.chartPageUserResultPihResultTextView)

        chartPageUserResultWrinkleResultTextView.text = skinDataAry[0]
        chartPageUserSkinToneResultTextView.text = skinDataAry[1]
        chartPageUserPoreDetectResultTextView.text = skinDataAry[2]
        chartPageUserDeadSkinResultTextView.text = skinDataAry[3]
        chartPageUserResultOilResultTextView.text = skinDataAry[4]
        chartPageUserResultPihResultTextView.text = skinDataAry[5]

    }

    private fun updateCareTime(textView: TextView) {
        val time = mainCareDate?.split(" ")
        if (time.size == 2) {
            textView.text = time?.first() + "\n" + time?.last() + " 측정"
        } else {
            textView.text = "미측정"
        }

    }

    private fun setLayouts(view: View) {
        this.chartPageResultWrinkleLayout = view.findViewById(R.id.chartPageResultWrinkleLayout)
        this.chartPageSkinToneLayout = view.findViewById(R.id.chartPageSkinToneLayout)
        this.chartPagePoreDetectLayout = view.findViewById(R.id.chartPagePoreDetectLayout)
        this.chartPageDeadSkinLayout = view.findViewById(R.id.chartPageDeadSkinLayout)
        this.chartPageResultOilLayout = view.findViewById(R.id.chartPageResultOilLayout)
        this.chartPageResultPihLayout = view.findViewById(R.id.chartPageResultPihLayout)


        this.chartPageResultWrinkleChartLayout =
            view.findViewById(R.id.chartPageResultWrinkleChartLayout)
        this.chartPageSkinToneChartLayout = view.findViewById(R.id.chartPageSkinToneChartLayout)
        this.chartPagePoreDetectChartLayout = view.findViewById(R.id.chartPagePoreDetectChartLayout)
        this.chartPageDeadSkinChartLayout = view.findViewById(R.id.chartPageDeadSkinChartLayout)
        this.chartPageResultOilChartLayout = view.findViewById(R.id.chartPageResultOilChartLayout)
        this.chartPageResultPihChartLayout = view.findViewById(R.id.chartPageResultPihChartLayout)

        setLayoutsClickListener()
    }

    private fun setLayoutsClickListener() {
        this.chartPageResultWrinkleLayout.setOnClickListener {
            if (CHART_PAGE_WRINKLE_STATE) {
                this.chartPageResultWrinkleUpDownImageView.rotation = 90f
                chartPageResultWrinkleChartLayout.isVisible = true
                CHART_PAGE_WRINKLE_STATE = false
            } else {
                this.chartPageResultWrinkleUpDownImageView.rotation = 270f
                chartPageResultWrinkleChartLayout.isVisible = false
                CHART_PAGE_WRINKLE_STATE = true
            }
        }

        this.chartPageSkinToneLayout.setOnClickListener {
            if (CHART_PAGE_SKINTONE_STATE) {
                this.chartPageSkinToneUpDownImageView.rotation = 90f
                chartPageSkinToneChartLayout.isVisible = true
                CHART_PAGE_SKINTONE_STATE = false
            } else {
                this.chartPageSkinToneUpDownImageView.rotation = 270f
                chartPageSkinToneChartLayout.isVisible = false
                CHART_PAGE_SKINTONE_STATE = true
            }
        }

        this.chartPagePoreDetectLayout.setOnClickListener {
            if (CHART_PAGE_POREDETECT_STATE) {
                this.chartPagePoreDetectUpDownImageView.rotation = 90f
                chartPagePoreDetectChartLayout.isVisible = true
                CHART_PAGE_POREDETECT_STATE = false
            } else {
                this.chartPagePoreDetectUpDownImageView.rotation = 270f
                chartPagePoreDetectChartLayout.isVisible = false
                CHART_PAGE_POREDETECT_STATE = true
            }
        }

        this.chartPageDeadSkinLayout.setOnClickListener {
            if (CHART_PAGE_DEADSKIN_STATE) {
                this.chartPageDeadSkinUpDownImageView.rotation = 90f
                CHART_PAGE_DEADSKIN_STATE = false
                chartPageDeadSkinChartLayout.isVisible = true
            } else {
                this.chartPageDeadSkinUpDownImageView.rotation = 270f
                chartPageDeadSkinChartLayout.isVisible = false
                CHART_PAGE_DEADSKIN_STATE = true
            }
        }

        this.chartPageResultOilLayout.setOnClickListener {
            if (CHART_PAGE_OIL_STATE) {
                this.chartPageResultOilUpDownImageView.rotation = 90f
                chartPageResultOilChartLayout.isVisible = true
                CHART_PAGE_OIL_STATE = false
            } else {
                this.chartPageResultOilUpDownImageView.rotation = 270f
                chartPageResultOilChartLayout.isVisible = false
                CHART_PAGE_OIL_STATE = true
            }
        }

        this.chartPageResultPihLayout.setOnClickListener {
            if (CHART_PAGE_PIH_STATE) {
                this.chartPageResultPihUpDownImageView.rotation = 90f
                chartPageResultPihChartLayout.isVisible = true
                CHART_PAGE_PIH_STATE = false
            } else {
                this.chartPageResultPihUpDownImageView.rotation = 270f
                chartPageResultPihChartLayout.isVisible = false
                CHART_PAGE_PIH_STATE = true
            }
        }
    }

    private fun setImageVies(view: View) {
        this.chartPageResultWrinkleUpDownImageView =
            view.findViewById(R.id.chartPageResultWrinkleUpDownImageView)
        this.chartPageSkinToneUpDownImageView =
            view.findViewById(R.id.chartPageSkinToneUpDownImageView)
        this.chartPagePoreDetectUpDownImageView =
            view.findViewById(R.id.chartPagePoreDetectUpDownImageView)
        this.chartPageDeadSkinUpDownImageView =
            view.findViewById(R.id.chartPageDeadSkinUpDownImageView)
        this.chartPageResultOilUpDownImageView =
            view.findViewById(R.id.chartPageResultOilUpDownImageView)
        this.chartPageResultPihUpDownImageView =
            view.findViewById(R.id.chartPageResultPihUpDownImageView)
    }


    private fun setLineCharts(view: View) {
        this.chartPageResultWrinkleLineChart = view.findViewById(R.id.chartPageResultWrinkleLineChart)
        this.chartPageSkinToneLineChart = view.findViewById(R.id.chartPageSkinToneLineChart)
        this.chartPagePoreDetectLineChart = view.findViewById(R.id.chartPagePoreDetectLineChart)
        this.chartPageDeadSkinLineChart = view.findViewById(R.id.chartPageDeadSkinLineChart)
        this.chartPageResultOilLineChart = view.findViewById(R.id.chartPageResultOilLineChart)
        this.chartPageResultPihLineChart = view.findViewById(R.id.chartPageResultPihLineChart)

        makeWrinkleResultChart(chartPageResultWrinkleLineChart)

        //TODO else Charts
        makeSkinToneChart(chartPageSkinToneLineChart)
        makePoreDetectChart(chartPagePoreDetectLineChart)
        makeDeadSkinChart(chartPageDeadSkinLineChart)
        makeResultOilChart(chartPageResultOilLineChart)
        makeResultPihChart(chartPageResultPihLineChart)

    }


    private fun makeWrinkleResultChart(mpLineChart: LineChart) {

        val dataSet: LineDataSet = LineDataSet(resultWrinkleDataValue(), "주름")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(dataSet)


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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        dataSet.color = Color.CYAN
        dataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.setCircleColor(Color.GRAY)
        dataSet.setCircleColorHole(Color.CYAN)
        dataSet.circleRadius = 3f
        dataSet.circleHoleRadius = 2f
        dataSet.fillAlpha = 50
        dataSet.fillFormatter
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gradient
        dataSet.valueTextSize = 10f
        dataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(60f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(dataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultWrinkleDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultWrinkle?.toFloat()!!))
        }
        return entry
    }

    private fun makeSkinToneChart(mpLineChart: LineChart) {

        val dataSet: LineDataSet = LineDataSet(resultSkinToneDataValue(), "피부톤")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(dataSet)


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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        dataSet.color = Color.CYAN
        dataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.setCircleColor(Color.GRAY)
        dataSet.setCircleColorHole(Color.CYAN)
        dataSet.circleRadius = 3f
        dataSet.circleHoleRadius = 2f
        dataSet.fillAlpha = 50
        dataSet.fillFormatter
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gradient
        dataSet.valueTextSize = 10f
        dataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(65f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(dataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultSkinToneDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultSkinTone?.toFloat()!!))
        }
        return entry
    }

    private fun makePoreDetectChart(mpLineChart: LineChart) {

        val poreDetectDataSet: LineDataSet = LineDataSet(resultPoreDetectDataValue(), "모공")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(poreDetectDataSet)

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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        poreDetectDataSet.color = Color.CYAN
        poreDetectDataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        poreDetectDataSet.setDrawCircles(true)
        poreDetectDataSet.setDrawCircleHole(true)
        poreDetectDataSet.setCircleColor(Color.GRAY)
        poreDetectDataSet.setCircleColorHole(Color.CYAN)
        poreDetectDataSet.circleRadius = 3f
        poreDetectDataSet.circleHoleRadius = 2f
        poreDetectDataSet.fillAlpha = 50
        poreDetectDataSet.fillFormatter
        poreDetectDataSet.setDrawFilled(true)
        poreDetectDataSet.fillDrawable = gradient
        poreDetectDataSet.valueTextSize = 10f
        poreDetectDataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(52f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(poreDetectDataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultPoreDetectDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultPoreDetect?.toFloat()!!))
        }

        return entry
    }

    private fun makeDeadSkinChart(mpLineChart: LineChart) {

        val dataSet: LineDataSet = LineDataSet(resultDeadSkinDataValue(), "각질")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(dataSet)


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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        dataSet.color = Color.CYAN
        dataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.setCircleColor(Color.GRAY)
        dataSet.setCircleColorHole(Color.CYAN)
        dataSet.circleRadius = 3f
        dataSet.circleHoleRadius = 2f
        dataSet.fillAlpha = 50
        dataSet.fillFormatter
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gradient
        dataSet.valueTextSize = 10f
        dataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(67f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(dataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultDeadSkinDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultDeadSkin?.toFloat()!!))
        }
        return entry
    }

    private fun makeResultOilChart(mpLineChart: LineChart) {

        val dataSet: LineDataSet = LineDataSet(resultResultOilDataValue(), "유분")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(dataSet)


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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        dataSet.color = Color.CYAN
        dataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.setCircleColor(Color.GRAY)
        dataSet.setCircleColorHole(Color.CYAN)
        dataSet.circleRadius = 3f
        dataSet.circleHoleRadius = 2f
        dataSet.fillAlpha = 50
        dataSet.fillFormatter
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gradient
        dataSet.valueTextSize = 10f
        dataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(40f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(dataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultResultOilDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultOilly?.toFloat()!!))
        }
        return entry
    }

    private fun makeResultPihChart(mpLineChart: LineChart) {

        val dataSet: LineDataSet = LineDataSet(resultResultPihDataValue(), "색소침착")
        val dataSets: ArrayList<ILineDataSet> = arrayListOf()
        dataSets.add(dataSet)


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

        val description: Description = Description()
        description.text = "Pview"
        description.textColor = Color.WHITE
        description.textSize = 20f
        mpLineChart.description = description

        dataSet.color = Color.CYAN
        dataSet.lineWidth = 2f
//        acneLineDataSet.enableDashedLine(5F, 10F, 0F)


        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.setCircleColor(Color.GRAY)
        dataSet.setCircleColorHole(Color.CYAN)
        dataSet.circleRadius = 3f
        dataSet.circleHoleRadius = 2f
        dataSet.fillAlpha = 50
        dataSet.fillFormatter
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gradient
        dataSet.valueTextSize = 10f
        dataSet.lineWidth = 2f

        mpLineChart.setDrawGridBackground(true)


        var limitLine = LimitLine(72f, "Average")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        limitLine.textSize = 10f
        limitLine.lineColor = Color.GREEN

        // ** Axis
        val xAxis: XAxis = mpLineChart.xAxis
        val yAxis: YAxis = mpLineChart.axisLeft
        mpLineChart.axisRight.isEnabled = false
        xAxis.valueFormatter = XAxisValueFormatter(5)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.textSize = 10f
//        yAxis.textSize = 5f
        yAxis.setDrawGridLines(true)
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 1f
        yAxis.setDrawLimitLinesBehindData(true)
        yAxis.addLimitLine(limitLine)


        val data: LineData = LineData(dataSet)
        data.setValueFormatter(ValueFormatter())
        mpLineChart.data = data
//        mpLineChart.animateX(1000)
        mpLineChart.invalidate()
    }

    private fun resultResultPihDataValue(): ArrayList<Entry> {
        var entry: ArrayList<Entry> = arrayListOf()

        for (i in 0 until dbLog.size) {

            var date = dbLog[i].time?.split(" ")?.first()?.split("/")
            var time = dbLog[i].time?.split(" ")?.last()?.split(":")
//            Log.d("mymymy", "${date}")
//            Log.d("mymymy", "${time}")
//            Log.d("mymymy", "${dbLog}")

            entry.add(Entry(i.toFloat(), dbLog[dbLog.size - 1 - i].resultPih?.toFloat()!!))
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
        private var CHART_PAGE_WRINKLE_STATE = true
        private var CHART_PAGE_SKINTONE_STATE = true
        private var CHART_PAGE_POREDETECT_STATE = true
        private var CHART_PAGE_DEADSKIN_STATE = true
        private var CHART_PAGE_OIL_STATE = true
        private var CHART_PAGE_PIH_STATE = true
    }
}