package com.dev.baqari.floating_window_button


open class PointUtil {

    companion object {
        var width: Int = 0
        var height: Int = 0
        private val insideRadius = 300

        fun isInsidePoint(x: Float, y: Float): Boolean {
            var result = false

            val x1: Int = width / 2 + (insideRadius + 10)
            val y1: Int = height - 2 * insideRadius
            val x2: Int = width / 2 + (insideRadius + 20)
            val y2: Int = height - (insideRadius - 100)

            if (x1 < x && y1 < y)
                result = true
            else if (x2 > x && y2 > y)
                result = true

            return result
        }
    }
}
