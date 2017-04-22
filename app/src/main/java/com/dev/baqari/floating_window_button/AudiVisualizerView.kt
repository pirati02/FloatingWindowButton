package com.dev.baqari.floating_window_button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class AudiVisualizerView : View {
    private var isLoadingToStop = false
    private var isLoadedToStop = false
    private var lower = 0
    private var paint: Paint? = null
    var timerDrawing: Timer? = null
    var timerStopDrawing: Timer? = null
    private val maxHeight = 90

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.color = Color.parseColor("#B9FF5100")
        if (random == null)
            random = Random()
    }

    override fun onDraw(canvas: Canvas) {
        if (isLoadingToStop) {
            lower = getLower() - 5

            var i = 0
            while (i < 49) {
                canvas.drawRect((40 + i).toFloat(), lower.toFloat(), (44 + i).toFloat(), maxHeight.toFloat(), paint!!)
                i += 7
            }

            isLoadedToStop = true
            isLoadingToStop = false
            invalidate()
        } else if (isLoadedToStop) {
            lower += 2
            if (lower > 0) {
                var i = 0
                while (i < 49) {
                    canvas.drawRect((40 + i).toFloat(), lower.toFloat(), (44 + i).toFloat(), maxHeight.toFloat(), paint!!)
                    i += 7
                }

                timerStopDrawing = Timer()
                timerStopDrawing!!.schedule(object : TimerTask() {
                    override fun run() {
                        post(updater)
                    }
                }, 10)
            } else {
                isLoadedToStop = false
                isLoadingToStop = false
                timerStopDrawing = null
                invalidate()
            }
        } else {
            val maxRandomable = maxHeight - 20
            for (i in itemsLastHeight.indices) {
                itemsLastHeight[i] = random!!.nextInt(maxRandomable) + 20
            }

            for (i in itemsLastHeight.indices) {
                canvas.drawRect((40 + i * 7).toFloat(), itemsLastHeight[i].toFloat(), (44 + i * 7).toFloat(), maxHeight.toFloat(), paint!!)
            }

            timerDrawing = Timer()
            timerDrawing!!.schedule(object : TimerTask() {
                override fun run() {
                    post(updater)
                }
            }, 300)
        }
    }

    private val updater = Runnable { invalidate() }

    fun startLoading() {
        timerStopDrawing = null
        isLoadedToStop = false
        isLoadingToStop = false
        invalidate()
    }

    fun stopLoading() {
        timerDrawing = null
        isLoadingToStop = true
        isLoadedToStop = false
        invalidate()
    }

    fun getLower(): Int {
        var minValue = itemsLastHeight[0]
        for (i in 1..itemsLastHeight.size - 1) {
            if (itemsLastHeight[i] < minValue) {
                minValue = itemsLastHeight[i]
            }
        }
        return minValue
    }

    companion object {
        private var random: Random? = null
        private val itemsLastHeight = IntArray(7)
    }
}
