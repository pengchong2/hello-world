package com.flyaudio.flyradioonline.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ManyCircle extends View {
    private Paint paint;
    private int maxRadius = 10;
    private ValueAnimator valueAnimator;
    private boolean init = false;
    public boolean isLoading = false;
    private float radiu = 10;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                start();
                invalidate();
            }
        }
    };

    public ManyCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManyCircle(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    private int width;
    private int height;
    private float pi2;
    private float r;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!init) {
            init = true;
            start();
            width = getWidth() / 2;
            height = getHeight() / 2;
            pi2 = 2*(float) Math.PI;
            r = width-maxRadius;
        }

        canvas.drawCircle((float) (width + r * Math.sin(0)), (float) (height + r * Math.cos(0)), f(radiu+0), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8)), (float) (height + r * Math.cos(pi2 /8)), f(radiu+2), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*2)), (float) (height + r * Math.cos(pi2 /8*2)), f(radiu+4), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*3)), (float) (height + r * Math.cos(pi2 /8*3)), f(radiu+6), paint);

        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*4)), (float) (height + r * Math.cos(pi2 /8*4)), f(radiu+8), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*5)), (float) (height + r * Math.cos(pi2 /8*5)), f(radiu+10), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*6)), (float) (height + r * Math.cos(pi2 /8*6)), f(radiu+12), paint);
        canvas.drawCircle((float) (width + r * Math.sin(pi2 /8*7)), (float) (height + r * Math.cos(pi2 /8*7)), f(radiu+14), paint);

        if (valueAnimator.isRunning()) {
            radiu = (float) valueAnimator.getAnimatedValue();
            invalidate();
        }


    }


    public boolean isLoading(){
        return isLoading;
    }

    public void start() {
        isLoading = true;
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, maxRadius);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(1500);
            valueAnimator.start();
        }else {
            valueAnimator.start();
        }
        mHandler.sendEmptyMessageDelayed(0, valueAnimator.getDuration());
        /*postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
                invalidate();
            }
        }, valueAnimator.getDuration());*/
        invalidate();
    }

    public void stop(){
        isLoading =false;
        if(valueAnimator != null){
            valueAnimator.cancel();
        }
        mHandler.removeMessages(0);
    }

    //分段函数
    private float f(float x) {
        if (x <=maxRadius / 2) {
            return x;
        } else if(x<maxRadius){
            return maxRadius - x;
        }else
        if(x<maxRadius*3/2)
        {
            return x-maxRadius;
        }else {
            return 2*maxRadius-x;
        }
    }
}
