package com.flyaudio.flyradioonline.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.flyaudio.flyradioonline.R;
import com.flyaudio.flyradioonline.util.DensityUtils;
import com.flyaudio.flyradioonline.util.Flog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzehao on 18-4-26.
 */

public class VoicePlayingIcon extends View {

    //画笔
    private Paint paint;

    //跳动指针的集合
    private List<Pointer> pointers;

    //跳动指针的数量
    private int pointerNum;

    //逻辑坐标 原点
    private float basePointX;

    private float basePointY;

    //指针间的间隙  默认5dp
    private float pointerPadding;

    //每个指针的宽度 默认3dp
    private float pointerWidth;

    //指针的颜色
    private int pointerColor = Color.RED;

    //控制开始/停止
    private boolean isPlaying = false;

    private boolean isPause = false;

    //子线程
    private Thread myThread;

    //指针波动速率
    private int pointerSpeed;

    public VoicePlayingIcon(Context context) {
        super(context);
        init();
    }

    public VoicePlayingIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //取出自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VoicePlayingIcon);
        pointerColor = ta.getColor(R.styleable.VoicePlayingIcon_pointer_color, Color.RED);
        pointerNum = ta.getInt(R.styleable.VoicePlayingIcon_pointer_num, 4);//指针的数量，默认为4
        pointerWidth = DensityUtils.dp2px(getContext(),
                ta.getFloat(R.styleable.VoicePlayingIcon_pointer_width, 5f));//指针的宽度，默认5dp
        pointerSpeed = ta.getInt(R.styleable.VoicePlayingIcon_pointer_speed, 40);
        ta.recycle();
        init();
    }

    public VoicePlayingIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VoicePlayingIcon);
        pointerColor = ta.getColor(R.styleable.VoicePlayingIcon_pointer_color, Color.RED);
        pointerNum = ta.getInt(R.styleable.VoicePlayingIcon_pointer_num, 4);
        pointerWidth = DensityUtils.dp2px(getContext(), ta.getFloat(R.styleable.VoicePlayingIcon_pointer_width, 5f));
        pointerSpeed = ta.getInt(R.styleable.VoicePlayingIcon_pointer_speed, 40);
        ta.recycle();
        init();
    }

    /**
     * 初始化画笔与指针的集合
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(pointerColor);
        pointers = new ArrayList<>();
    }


    /**
     * 在onLayout中做一些，宽高方面的初始化
     *
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initPoints();
    }

    private void initPoints(){
        //获取逻辑原点的，也就是画布左下角的坐标。这里减去了paddingBottom的距离
        basePointY = getHeight() - getPaddingBottom();
        if (pointers != null){
            pointers.clear();
        }
        for (int i = 0; i < pointerNum; i++) {
            //创建指针对象，利用0~1的随机数 乘以 可绘制区域的高度。作为每个指针的初始高度。
            if(i == 0){
                pointers.add(new Pointer((0)));
            }else if(i == 1){
                pointers.add(new Pointer((float)(0.5 * (getHeight() - getPaddingBottom() - getPaddingTop()))));
            }else if(i == 2){
                pointers.add(new Pointer((float)(0.25 * (getHeight() - getPaddingBottom() - getPaddingTop()))));
            }else if(i == 3){
                pointers.add(new Pointer((float)(0.15 * (getHeight() - getPaddingBottom() - getPaddingTop()))));
            }
        }
        //计算每个指针之间的间隔  总宽度 - 左右两边的padding - 所有指针占去的宽度  然后再除以间隔的数量
        pointerPadding = (getWidth() - getPaddingLeft() - getPaddingRight() - pointerWidth * pointerNum) / (pointerNum - 1);
    }

    /**
     * 开始绘画
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isPlaying){
            initPoints();
        }
        //将x坐标移动到逻辑原点，也就是左下角
        basePointX = 0f + getPaddingLeft();
        //循环绘制每一个指针。
        for(int i = 0; i < pointers.size(); i++){
            canvas.drawRect(basePointX,
                    (basePointY - getPaddingTop())/2,
                    basePointX + pointerWidth,
                    basePointY,
                    paint);
            basePointX += (pointerPadding + pointerWidth);
        }
        basePointX = 0f + getPaddingLeft();
        for (int i = 0; i < pointers.size(); i++) {
            //绘制指针，也就是绘制矩形
            float lPointX = basePointX;
            float lPointY = basePointY - pointers.get(i).getHeight() - ((basePointY - getPaddingTop())/2);
            float rPointX = basePointX + pointerWidth;
            float rPointY = basePointY - ((basePointY - getPaddingTop())/2);
            canvas.drawRect(lPointX, lPointY, rPointX, rPointY, paint);
            basePointX += (pointerPadding + pointerWidth);
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    /**
     * 开始播放
     */
    public synchronized void start() {
        isPlaying = false;
        isPause = false;
        if (!isPlaying) {
            if (myThread == null) {//开启子线程
                initPoints();
                myThread = new Thread(new MyRunnable());
                myThread.start();
            }
            isPlaying = true;//控制子线程中的循环
        }
    }

    /**
     * 停止子线程，并刷新画布
     */
    public void pause() {
        isPlaying = false;
        isPause = true;
    }

    public synchronized void stop(){
        isPlaying = false;
        isPause = true;
        if(myThread != null){
            myThread.interrupt();
            myThread = null;
        }
    }

    /**
     * 处理子线程发出来的指令，然后刷新布局
     */
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                invalidate();
            }else if(msg.what == 1){
                invalidate();
            }

        }
    };

    /**
     * 子线程，循环改变每个指针的高度
     */
    public class MyRunnable implements Runnable {

        @Override
        public void run() {

            for (float i = 0; i < Integer.MAX_VALUE; ) {//创建一个死循环，每循环一次i+0.1
                try {
                    for (int j = 0; j < pointers.size(); j++) { //循环改变每个指针高度

                        float rate = (float) Math.abs(Math.sin(i + j));//利用正弦有规律的获取0~1的数。
                        pointers.get(j).setHeight(((basePointY - getPaddingTop())/2) * rate); //rate 乘以 可绘制高度，来改变每个指针的高度

                    }
                    //Flog.e("liuxiaosheng", "VoicePlayingIcon//MyRunnable//Runnable");
                    if(Thread.currentThread().isInterrupted() || !isPlaying){
                        Flog.e("liuxiaosheng", "VoicePlayingIcon//MyRunnable//Interrupted1");
                        break;
                    }

                    Thread.sleep(pointerSpeed);//休眠一下下，可自行调节
                    if (isPlaying) { //控制开始/暂停
                        myHandler.sendEmptyMessage(0);
                        i += 0.1;
                    } else if(isPause){
                        myHandler.sendEmptyMessageDelayed(1, 500);
                        isPause = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 指针对象
     */
    public class Pointer {
        private float height;

        public Pointer(float height) {
            this.height = height;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "Pointer{" +
                    "height=" + height +
                    '}';
        }
    }
}


