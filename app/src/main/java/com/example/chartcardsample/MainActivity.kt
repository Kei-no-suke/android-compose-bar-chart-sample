package com.example.chartcardsample

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.chartcardsample.ui.theme.ChartCardSampleTheme
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        setContent {
            ChartCardSampleTheme {
                val textMeasurer = rememberTextMeasurer()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        BarGraphCard(textMeasurer, context)
                    }
                }
            }
        }
    }
}

fun calcChartTicks(data: List<Int>): List<Int>{
    val numOfDivisions = 4
    val range = data.max() - data.min()
    val tickRange = range.toFloat() / numOfDivisions.toFloat()
    val logCommon = log10(tickRange)
    val order = floor(logCommon)

    val tickWidthCandidates = listOf(
        log10(5f) + order - 1,
        log10(1f) + order,
        log10(2f) + order,
        log10(5f) + order,
        log10(1f) + order + 1
    )

    val diffTickWidthCandidates = tickWidthCandidates.map {
        abs(it - logCommon)
    }

    val minIndex = diffTickWidthCandidates.withIndex()
        .sortedBy { it.value }
        .map{ it.index }
        .first()

    Log.d("calcChartWidthFunc", diffTickWidthCandidates[minIndex].toString())
    Log.d("calcChartWidthFunc", diffTickWidthCandidates.min().toString())

    val tickWidth = 10.toFloat().pow(tickWidthCandidates[minIndex]).toInt()

    val minTickValue = (data.min().toFloat() -
            (data.min().toFloat() % tickWidth)).toInt()

    val maxTickValue = (data.max().toFloat() -
            (data.max().toFloat() % tickWidth)).toInt() + tickWidth

    val tickList: MutableList<Int> = mutableListOf()

    for(i in 0 .. ((maxTickValue - minTickValue) / tickWidth).toInt()){
        tickList.add(minTickValue + tickWidth * i)
    }

    return tickList
}

fun convertDpToPx(dp: Float, context: Context): Float{
    val metrics = context.resources.displayMetrics
    return dp * metrics.density
}

fun convertPxToDp(px: Float, context: Context): Float{
    val metrics = context.resources.displayMetrics
    return px / metrics.density
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun BarGraphCard(
    textMeasurer: TextMeasurer,
    context: Context
){
    BoxWithConstraints {
        // dimension value
        val width = constraints.maxWidth
        val topChartAreaGap = convertDpToPx(20f, context)
        val chartAreaGap = convertDpToPx(12f, context)
        val textGap = convertDpToPx(4f, context)
        val axisStrokeWidth = 3f

        // color
        val xAxisColor: Color = Color.Black
        val yAxisColor: Color = Color.Black

        // ratio
        // ratio of bar to gap between bar and bar
        val ratioOfBarToGap = 5f / 1f
        // ratio of bar to gap between bar and axis
        val ratioOfBarToAxisGap = 3f / 1f
        // aspect ratio of chart area
        val chartAspectRatio = 2f / 3f

        val dataY = listOf(
            2003,
            3000,
            1000,
            0,
            902,
            3400,
            4502
        )
        val dataX = listOf(
            "8/27",
            "8/28",
            "8/29",
            "8/30",
            "8/31",
            "9/1",
            "9/2"
        )

        if(dataX.size != dataY.size){
            throw IllegalArgumentException("The x-axis list and the y-axis list must have the same length.")
        }

        // text of axis
        val yAxisText = "Steps"
        val xAxisText = "Date"

        // Step1: get label text list of yAxis
        // ===================================
        // list of yAxisTick
        val yAxisTicks = calcChartTicks(dataY)

        // Step2: get text size on chart
        // =============================
        // xAxis
        val xAxisTextLayoutResult = textMeasurer.measure(text = xAxisText)
        val xAxisTextSize = xAxisTextLayoutResult.size
        val xAxisTickTextLayoutResults: MutableList<TextLayoutResult> = mutableListOf()
        val xAxisTickTextSize: MutableList<IntSize> = mutableListOf()
        for(i in dataX.indices){
            val textLayoutResult =
                textMeasurer.measure(
                    text = dataX[i]
                )
            val textSize = textLayoutResult.size
            xAxisTickTextLayoutResults.add(textLayoutResult)
            xAxisTickTextSize.add(textSize)
        }
        val maxXAxisTickTextHeight = xAxisTickTextSize.maxOfOrNull{ it.height } ?: 0
        // yAxis
        val yAxisTextLayoutResult = textMeasurer.measure(text = yAxisText)
        val yAxisTextSize = yAxisTextLayoutResult.size
        val yAxisTickTextLayoutResults: MutableList<TextLayoutResult> = mutableListOf()
        val yAxisTickTextSize: MutableList<IntSize> = mutableListOf()
        for(i in yAxisTicks.indices) {
            val textLayoutResult =
                textMeasurer.measure(
                    text = yAxisTicks[i].toString()
                )
            val textSize = textLayoutResult.size
            yAxisTickTextLayoutResults.add(textLayoutResult)
            yAxisTickTextSize.add(textSize)
        }
        val maxYAxisTickTextWidth = yAxisTickTextSize.maxOfOrNull{ it.width } ?: 0

        // Step3: calculation of the position and size of text and shapes in chart
        // size of axis text area
        val xAxisTextAreaHeight = xAxisTextSize.height + maxXAxisTickTextHeight + textGap * 2f
        val yAxisTextAreaWidth = yAxisTextSize.height + maxYAxisTickTextWidth + textGap * 2f
        // size of chart area
        val chartWidth = width - chartAreaGap * 2f - yAxisTextAreaWidth
        val chartHeight = chartWidth * chartAspectRatio

        // height of chart Card
        val chartCardHeight = convertPxToDp(
            chartHeight + topChartAreaGap + axisStrokeWidth / 2f + xAxisTextAreaHeight
                    + chartAreaGap,
            context)

        // xAxisPosition
        val xAxisStartXPos = yAxisTextAreaWidth + chartAreaGap
        val xAxisEndXPos = width - chartAreaGap
        val xAxisYPos = topChartAreaGap + chartHeight
        // yAxisPosition
        val yAxisXPos = yAxisTextAreaWidth + chartAreaGap
        val yAxisStartYPos = topChartAreaGap
        val yAxisEndYPos = topChartAreaGap + chartHeight

        // bar width
        val barWidth = ratioOfBarToGap * chartWidth / ((ratioOfBarToGap + 1f) *
                dataY.size.toFloat() - 1f + 2 * ratioOfBarToGap / ratioOfBarToAxisGap)
        // gap between bar and axis
        val chartAxisGap = barWidth / ratioOfBarToAxisGap

        // range of yAxisTicks
        val yAxisTicksRange = yAxisTicks.max() - yAxisTicks.min()
        // bar height of data
        val barHeightOfData = dataY.map{
            (it - yAxisTicks.min()) * chartHeight / yAxisTicksRange
        }

        // bar positions
        val barXPositions: MutableList<Float> = mutableListOf()
        val barYPositions: MutableList<Float> = mutableListOf()
        for(i in dataX.indices){
            val barXPosition = yAxisXPos + chartAxisGap +
                    i.toFloat() * (ratioOfBarToGap + 1) *
                    barWidth / ratioOfBarToGap
            barXPositions.add( barXPosition )
            barYPositions.add(
                xAxisYPos - barHeightOfData[i]
            )
        }

        // xAxis text position
        val xAxisTextCenterXPosition: Float = yAxisXPos + chartWidth / 2f
        val xAxisTextCenterYPosition: Float = xAxisYPos + 2 * textGap + maxXAxisTickTextHeight +
                xAxisTextSize.height / 2f
        val xAxisTextXPosition: Float = xAxisTextCenterXPosition - xAxisTextSize.width / 2f
        val xAxisTextYPosition: Float = xAxisTextCenterYPosition - xAxisTextSize.height / 2f

        // yAxis text position
        val yAxisTextCenterXPosition: Float = yAxisXPos - 2 * textGap - maxYAxisTickTextWidth -
                yAxisTextSize.height
        val yAxisTextCenterYPosition: Float = topChartAreaGap + chartHeight / 2f
        val yAxisTextXPosition: Float = yAxisTextCenterXPosition - yAxisTextSize.width / 2f
        val yAxisTextYPosition: Float = yAxisTextCenterYPosition - yAxisTextSize.height / 2f

        // xAxis tick text positions
        val xAxisTickTextXPositions: MutableList<Float> = mutableListOf()
        val xAxisTickTextYPosition = xAxisYPos + textGap
        for(i in dataX.indices){
            xAxisTickTextXPositions.add(
                barXPositions[i] + barWidth / 2f - xAxisTickTextSize[i].width / 2f
            )
        }

        // yAxis tick text positions
        // height of one yAxis tick
        val yAxisTickHeight = chartHeight / (yAxisTicks.size - 1)
        val yAxisTickTextXPositions: MutableList<Float> = mutableListOf()
        val yAxisTickTextYPositions: MutableList<Float> = mutableListOf()
        for(i in yAxisTicks.indices){
            yAxisTickTextXPositions.add(
                yAxisXPos - yAxisTickTextSize[i].width - textGap
            )
            yAxisTickTextYPositions.add(
                xAxisYPos - yAxisTickTextSize[i].height / 2f - yAxisTickHeight * i.toFloat()
            )
        }

        Card(modifier = Modifier.height(chartCardHeight.dp)){
            Canvas(
                modifier = Modifier.fillMaxWidth()
            ){

                drawText(
                    textLayoutResult = xAxisTextLayoutResult,
                    topLeft = Offset(xAxisTextXPosition, xAxisTextYPosition)
                )

                rotate(
                    -90f,
                    Offset(yAxisTextCenterXPosition,yAxisTextCenterYPosition)
                ){
                    drawText(
                        textLayoutResult = yAxisTextLayoutResult,
                        topLeft = Offset(yAxisTextXPosition, yAxisTextYPosition)
                    )
                }

                // xAxis text
                for(i in xAxisTickTextLayoutResults.indices){
                    drawText(
                        xAxisTickTextLayoutResults[i],
                        topLeft = Offset(
                            xAxisTickTextXPositions[i],
                            xAxisTickTextYPosition
                        )
                    )
                }

                // yAxis text
                for(i in yAxisTickTextLayoutResults.indices){
                    drawText(
                        yAxisTickTextLayoutResults[i],
                        topLeft = Offset(
                            yAxisTickTextXPositions[i],
                            yAxisTickTextYPositions[i]
                        )
                    )
                }

                // bar graph
                for(i in dataY.indices){
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(
                            barXPositions[i],
                            barYPositions[i]
                        ),
                        size = Size(barWidth, barHeightOfData[i])
                    )
                }

                // xAxis
                drawLine(
                    color = xAxisColor,
                    start = Offset(xAxisStartXPos, xAxisYPos),
                    end = Offset(xAxisEndXPos, xAxisYPos),
                    strokeWidth = axisStrokeWidth
                )

                // yAxis
                drawLine(
                    color = yAxisColor,
                    start = Offset(yAxisXPos, yAxisStartYPos),
                    end = Offset(yAxisXPos, yAxisEndYPos),
                    strokeWidth = axisStrokeWidth
                )
            }
        }
    }
}