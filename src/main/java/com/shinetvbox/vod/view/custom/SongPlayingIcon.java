package com.shinetvbox.vod.view.custom;

import android.annotation.SuppressLint;
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

import com.shinetvbox.vod.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SongPlayingIcon extends View {

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

    //子线程
    private Thread myThread;

    //指针波动速率
    private int pointerSpeed;


    public SongPlayingIcon(Context context) {
        this(context,null,0);
    }

    public SongPlayingIcon(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SongPlayingIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.SongPlayingIcon
                , defStyleAttr, 0);
        pointerColor = array.getColor(R.styleable.SongPlayingIcon_pointer_color, Color.RED);
        pointerNum = array.getInt(R.styleable.SongPlayingIcon_pointer_num, 5);
        pointerWidth = array.getFloat(R.styleable.SongPlayingIcon_pointer_width, 3f);
        pointerSpeed = array.getInt(R.styleable.SongPlayingIcon_pointer_speed, 40);
        array.recycle();

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
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(pointers.size()!=0) return;
        //获取逻辑原点的，也就是画布左下角的坐标。这里减去了paddingBottom的距离
        basePointY = getHeight() - getPaddingBottom();
        Random random = new Random();
        if (pointers != null)
            pointers.clear();
        for (int i = 0; i < pointerNum; i++) {
            //创建指针对象，利用0~1的随机数 乘以 可绘制区域的高度。作为每个指针的初始高度。
            pointers.add(new Pointer((float) (0.1 * (random.nextInt(10) + 1) * (getHeight() - getPaddingBottom() - getPaddingTop())),getHeight()));
        }
        //计算每个指针之间的间隔  总宽度 - 左右两边的padding - 所有指针占去的宽度  然后再除以间隔的数量
        pointerPadding = (getWidth() - getPaddingLeft() - getPaddingRight() - pointerWidth * pointerNum) / (pointerNum - 1);
    }

    /**
     * 开始绘画
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将x坐标移动到逻辑原点，也就是左下角
        basePointX = 0f + getPaddingLeft();
        //循环绘制每一个指针。
        for (int i = 0; i < pointers.size(); i++) {
            //绘制指针，也就是绘制矩形
            canvas.drawRect(basePointX,
                    basePointY - pointers.get(i).getHeight(),
                    basePointX + pointerWidth,
                    basePointY,
                    paint);
            basePointX += (pointerPadding + pointerWidth);
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        if (!isPlaying) {
            isPlaying = true;//控制子线程中的循环
            if (myThread == null) {//开启子线程
                myThread = new MyThread();
                myThread.start();
            }
        }
    }

    /**
     * 停止子线程，并刷新画布
     */
    public void stop() {
        if(isPlaying){
            isPlaying = false;
            if (myThread != null) {
                myThread.interrupt();
                myThread = null;
            }
        }
        invalidate();
    }

    /**
     * 处理子线程发出来的指令，然后刷新布局
     */
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };

    Object o = new Object();
    /**
     * 子线程，循环改变每个指针的高度
     */
//    ReentrantLock oo = new ReentrantLock();
    public class MyThread extends Thread {

        @Override
        public void run() {
            synchronized (o) {
                float hei = 0;
                while (isInterrupted()) {
                    try {
                        for (int j = 0; j < pointers.size(); j++) { //循环改变每个指针高度
                            Pointer pointer = pointers.get( j );
                            if (pointer != null) {
                                hei = (int) (pointer.getHeight() + pointer.getSpeed() * pointer.getSymbol());
                                if (hei > pointer.getMaxHeight()) {
                                    hei = pointer.getMaxHeight();
                                    pointer.setSymbol( -1 );
                                } else if (hei < 0) {
                                    hei = 0;
                                    pointer.setSymbol( 1 );
                                    pointer.setSpeed( (float) (Math.random() + 1) );
                                    pointer.setMaxHeight( (float) (Math.random() / 2 + 0.5) * getHeight() );
                                }
                                pointer.setHeight( hei );
                            }
                        }
                        Thread.sleep( pointerSpeed );//休眠一下下，可自行调节
                        if (isPlaying) { //控制开始/暂停
                            myHandler.sendEmptyMessage( 0 );
                        }
                    } catch (InterruptedException e) {
//                    e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 指针类
     */
    public class Pointer {
        private float maxHeight;
        private float height;
        private float symbol = 1;
        private float speed = (float) (Math.random()*1)+1;

        public Pointer(float height,float maxHeight) {
            this.height = height;
            this.maxHeight = maxHeight;
        }

        public float getMaxHeight() {
            return maxHeight;
        }

        public void setMaxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getSymbol() {
            return symbol;
        }

        public void setSymbol(float symbol) {
            this.symbol = symbol;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}