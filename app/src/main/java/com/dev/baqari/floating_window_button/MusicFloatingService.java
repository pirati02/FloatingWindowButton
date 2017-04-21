package com.dev.baqari.floating_window_button;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v7.widget.AppCompatImageButton;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MusicFloatingService extends Service {
    private WindowManager mWindowManager;
    private View mRoot, mFloatingView, expandedView, collapsedView, closeView;
    private ImageView closeExpanded, closeCollapsed, open, close;
    private AppCompatImageButton play, next, prev;
    private WindowManager.LayoutParams params;
    private static WindowManager.LayoutParams closeViewParams;

    private float windowWidth, windowHeight;

    public MusicFloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.music_floating_view, null);
        closeView = LayoutInflater.from(this).inflate(R.layout.close_view, null);
        close = (ImageView) closeView.findViewById(R.id.music_floating_view_close);


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        windowHeight = displayMetrics.heightPixels / 2;
        windowWidth = displayMetrics.widthPixels / 2;

        PointUtil.height = mWindowManager.getDefaultDisplay().getHeight();
        PointUtil.width = mWindowManager.getDefaultDisplay().getWidth();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = displayMetrics.widthPixels;
        params.y = 0;
        if (closeViewParams == null) {
            closeViewParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            closeViewParams.gravity = Gravity.TOP | Gravity.LEFT;
            closeViewParams.x = (displayMetrics.widthPixels / 2) - 40;
            closeViewParams.y = displayMetrics.heightPixels - 150;
        }

        mWindowManager.addView(mFloatingView, params);

        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);

        closeView.setVisibility(View.GONE);
        mWindowManager.addView(closeView, params);

        initUi(mFloatingView);
    }

    public void initUi(View view) {
        next = (AppCompatImageButton) view.findViewById(R.id.next_btn);
        prev = (AppCompatImageButton) view.findViewById(R.id.prev_btn);
        play = (AppCompatImageButton) view.findViewById(R.id.play_btn);
        closeExpanded = (ImageView) view.findViewById(R.id.close_button);

        closeExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        closeCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        open = (ImageView) mFloatingView.findViewById(R.id.open_button);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicFloatingService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                stopSelf();
            }
        });

        mRoot = mFloatingView.findViewById(R.id.root_container);
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        return true;
                    case MotionEvent.ACTION_UP:
                        int xDiff = (int) (event.getRawX() - initialTouchX);
                        int yDiff = (int) (event.getRawY() - initialTouchY);


                        if ((xDiff < 2 && yDiff < 2) && (xDiff > -2 && yDiff > -2)) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        } else if (event.getRawX() > windowWidth && event.getRawY() > windowHeight) {
                            params.x = mWindowManager.getDefaultDisplay().getWidth() - 20;
                            params.y = mWindowManager.getDefaultDisplay().getHeight() - 20;
                        } else if (event.getRawX() < windowWidth && event.getRawY() > windowHeight) {
                            params.x = 0;
                            params.y = mWindowManager.getDefaultDisplay().getHeight() - 20;
                        } else if (event.getRawX() < windowWidth && event.getRawY() < windowHeight) {
                            params.x = 0;
                            params.y = 0;
                        } else if (event.getRawX() > windowWidth && event.getRawY() < windowHeight) {
                            params.x = mWindowManager.getDefaultDisplay().getWidth() - 20;
                            params.y = 0;
                        }

                        if (!PointUtil.isInsidePoint(event.getRawX(), event.getRawY())) {
                            closeView.setVisibility(View.VISIBLE);
                            params.x = closeViewParams.x;
                            params.y = closeViewParams.y;

                            mWindowManager.updateViewLayout(mFloatingView, params);
                        } else {
                            closeView.setVisibility(View.GONE);
                        }

                        if (params.x == closeViewParams.x && params.y == closeViewParams.y) {
                            mFloatingView.setVisibility(View.GONE);
                            closeView.setVisibility(View.GONE);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            mWindowManager.updateViewLayout(closeView, closeViewParams);
                        }

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!PointUtil.isInsidePoint(event.getRawX(), event.getRawY())) {
                            closeView.setVisibility(View.VISIBLE);
                            params.x = closeViewParams.x - 10;
                            params.y = closeViewParams.y - 50;
                        } else {
                            closeView.setVisibility(View.GONE);
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        }

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        mWindowManager.updateViewLayout(closeView, closeViewParams);
                        return true;
                }
                return false;
            }
        });
    }

    private boolean isViewCollapsed() {
        return mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null)
            mWindowManager.removeView(mFloatingView);
    }
}