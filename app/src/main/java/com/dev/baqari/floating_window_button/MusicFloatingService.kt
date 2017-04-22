package com.dev.baqari.floating_window_button

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.support.v7.widget.AppCompatImageButton
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView

class MusicFloatingService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mRoot: View? = null
    private var mFloatingView: View? = null
    private var expandedView: View? = null
    private var collapsedView: View? = null
    private var closeView: View? = null
    private var closeExpanded: ImageView? = null
    private var closeCollapsed: ImageView? = null
    private var open: ImageView? = null
    private var close: ImageView? = null
    private var play: AppCompatImageButton? = null
    private var next: AppCompatImageButton? = null
    private var prev: AppCompatImageButton? = null
    private var params: WindowManager.LayoutParams? = null

    private var windowWidth: Float = 0.toFloat()
    private var windowHeight: Float = 0.toFloat()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.music_floating_view, null)
        closeView = LayoutInflater.from(this).inflate(R.layout.close_view, null)
        close = closeView!!.findViewById(R.id.music_floating_view_close) as ImageView


        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        mWindowManager!!.defaultDisplay.getMetrics(displayMetrics)
        windowHeight = (displayMetrics.heightPixels / 2).toFloat()
        windowWidth = (displayMetrics.widthPixels / 2).toFloat()

        PointUtil.height = mWindowManager!!.defaultDisplay.height
        PointUtil.width = mWindowManager!!.defaultDisplay.width

        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        params?.gravity = Gravity.TOP or Gravity.LEFT
        params?.x = displayMetrics.widthPixels
        params?.y = 0
        if (closeViewParams == null) {
            closeViewParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)

            closeViewParams!!.gravity = Gravity.TOP or Gravity.LEFT
            closeViewParams!!.x = displayMetrics.widthPixels / 2 - 40
            closeViewParams!!.y = displayMetrics.heightPixels - 150
        }

        mWindowManager!!.addView(mFloatingView, params)

        collapsedView = mFloatingView!!.findViewById(R.id.collapse_view)
        expandedView = mFloatingView!!.findViewById(R.id.expanded_container)

        closeView!!.visibility = View.GONE
        mWindowManager!!.addView(closeView, params)

        initUi(mFloatingView as View)
    }

    fun initUi(view: View) {
        next = view.findViewById(R.id.next_btn) as AppCompatImageButton
        prev = view.findViewById(R.id.prev_btn) as AppCompatImageButton
        play = view.findViewById(R.id.play_btn) as AppCompatImageButton
        closeExpanded = view.findViewById(R.id.close_button) as ImageView

        closeExpanded!!.setOnClickListener {
            collapsedView!!.visibility = View.VISIBLE
            expandedView!!.visibility = View.GONE
        }
        play!!.setOnClickListener { }

        next!!.setOnClickListener { }

        prev!!.setOnClickListener { }


        closeCollapsed = mFloatingView!!.findViewById(R.id.close_btn) as ImageView
        closeCollapsed!!.setOnClickListener { stopSelf() }

        open = mFloatingView!!.findViewById(R.id.open_button) as ImageView
        open!!.setOnClickListener {
            val intent = Intent(this@MusicFloatingService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            stopSelf()
        }

        mRoot = mFloatingView!!.findViewById(R.id.root_container)
        mRoot!!.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()


            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        initialX = params!!.x
                        initialY = params!!.y

                        initialTouchX = event.rawX
                        initialTouchY = event.rawY


                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val xDiff = (event.rawX - initialTouchX).toInt()
                        val yDiff = (event.rawY - initialTouchY).toInt()


                        if (xDiff < 2 && yDiff < 2 && xDiff > -2 && yDiff > -2) {
                            if (isViewCollapsed) {
                                collapsedView!!.visibility = View.GONE
                                expandedView!!.visibility = View.VISIBLE
                            }
                        } else if (event.rawX > windowWidth && event.rawY > windowHeight) {
                            params!!.x = mWindowManager!!.defaultDisplay.width - 20
                            params!!.y = mWindowManager!!.defaultDisplay.height - 20
                        } else if (event.rawX < windowWidth && event.rawY > windowHeight) {
                            params!!.x = 0
                            params!!.y = mWindowManager!!.defaultDisplay.height - 20
                        } else if (event.rawX < windowWidth && event.rawY < windowHeight) {
                            params!!.x = 0
                            params!!.y = 0
                        } else if (event.rawX > windowWidth && event.rawY < windowHeight) {
                            params!!.x = mWindowManager!!.defaultDisplay.width - 20
                            params!!.y = 0
                        }

                        if (!PointUtil.isInsidePoint(event.rawX, event.rawY)) {
                            closeView!!.visibility = View.VISIBLE
                            params!!.x = closeViewParams!!.x
                            params!!.y = closeViewParams!!.y

                            mWindowManager!!.updateViewLayout(mFloatingView, params)
                        } else {
                            closeView!!.visibility = View.GONE
                        }

                        if (params!!.x == closeViewParams!!.x && params!!.y == closeViewParams!!.y) {
                            mFloatingView!!.visibility = View.GONE
                            closeView!!.visibility = View.GONE
                            mWindowManager!!.updateViewLayout(mFloatingView, params)
                            mWindowManager!!.updateViewLayout(closeView, closeViewParams)
                        }

                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (!PointUtil.isInsidePoint(event.rawX, event.rawY)) {
                            closeView!!.visibility = View.VISIBLE
                            params!!.x = closeViewParams!!.x - 10
                            params!!.y = closeViewParams!!.y - 50
                        } else {
                            closeView!!.visibility = View.GONE
                            params!!.x = initialX + (event.rawX - initialTouchX).toInt()
                            params!!.y = initialY + (event.rawY - initialTouchY).toInt()
                        }

                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        mWindowManager!!.updateViewLayout(closeView, closeViewParams)
                        return true
                    }
                }
                return false
            }
        })
    }

    private val isViewCollapsed: Boolean
        get() = mFloatingView!!.findViewById(R.id.collapse_view).visibility == View.VISIBLE

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null)
            mWindowManager!!.removeView(mFloatingView)
    }

    companion object {
        private var closeViewParams: WindowManager.LayoutParams? = null
    }
}