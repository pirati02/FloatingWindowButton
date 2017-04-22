package com.dev.baqari.floating_window_button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AudiVisualizerView extends View {
    private boolean isLoadingToStop = false;
    private boolean isLoadedToStop = false;
    private int lower = 0;
    private Paint paint;
    Timer timerDrawing, timerStopDrawing;
    private static Random random;
    private static int[] itemsLastHeight = new int[7];
    private int maxHeight = 90;

    public AudiVisualizerView(Context context) {
        super(context);
        init();
    }

    public AudiVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudiVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#B9FF5100"));
        if (random == null)
            random = new Random();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isLoadingToStop) {
            lower = getLower() - 5;

            for (int i = 0; i < 49; i += 7) {
                canvas.drawRect(40 + i, lower, 44 + i, maxHeight, paint);
            }

            isLoadedToStop = true;
            isLoadingToStop = false;
            invalidate();
        } else if (isLoadedToStop) {
            lower += 2;
            if (lower > 0) {
                for (int i = 0; i < 49; i += 7) {
                    canvas.drawRect(40 + i, lower, 44 + i, maxHeight, paint);
                }

                timerStopDrawing = new Timer();
                timerStopDrawing.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        post(updater);
                    }
                }, 10);
            } else {
                isLoadedToStop = false;
                isLoadingToStop = false;
                timerStopDrawing = null;
                invalidate();
            }
        } else {
            int maxRandomable = maxHeight - 20;
            for (int i = 0; i < itemsLastHeight.length; i++) {
                itemsLastHeight[i] = random.nextInt(maxRandomable) + 20;
            }

            for (int i = 0; i < itemsLastHeight.length; i++) {
                canvas.drawRect(40 + (i * 7), itemsLastHeight[i], 44 + (i * 7), maxHeight, paint);
            }

            timerDrawing = new Timer();
            timerDrawing.schedule(new TimerTask() {
                @Override
                public void run() {
                    post(updater);
                }
            }, 300);
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public void startLoading() {
        timerStopDrawing = null;
        isLoadedToStop = false;
        isLoadingToStop = false;
        invalidate();
    }

    public void stopLoading() {
        timerDrawing = null;
        isLoadingToStop = true;
        isLoadedToStop = false;
        invalidate();
    }

    public int getLower() {
        int minValue = itemsLastHeight[0];
        for (int i = 1; i < itemsLastHeight.length; i++) {
            if (itemsLastHeight[i] < minValue) {
                minValue = itemsLastHeight[i];
            }
        }
        return minValue;
    }
}
