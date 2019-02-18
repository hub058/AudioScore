package com.yc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 20252365 on 2015/11/18.
 */
public class AudioWaveView extends View {

    private double[] audioData;
    private Paint mPaint;
    private int mViewHeight;
    private int mViewWidth;

    public AudioWaveView(Context context) {
        this(context, null);
        initPaint();
    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initPaint();
    }

    public AudioWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    public void setAudioData(double[] audioData) {
        this.audioData = audioData;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth((float) 3.0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = this.getMeasuredHeight();
        mViewWidth = this.getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);
    }

    private void drawWave(Canvas canvas) {
        if (null != audioData) {

            float stepSize = (float) ((double) mViewWidth / audioData.length);
            Log.d("lzy", "audioDataLen : " + audioData.length + "   " + stepSize);
            for (int i = 10; i < audioData.length; i++) {
                if (i % 10 == 0) {
                    canvas.drawLine((i - 10) * stepSize, (mViewHeight / 2 - (float) (audioData[i - 10] * mViewHeight / 2)), i * stepSize, (mViewHeight / 2 - (float) (audioData[i] * mViewHeight / 2)), mPaint);
                }
            }
        }
    }
}
