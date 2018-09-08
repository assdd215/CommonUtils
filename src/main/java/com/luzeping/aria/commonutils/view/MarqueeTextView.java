package com.luzeping.aria.commonutils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.luzeping.aria.commonutils.R;

/**
 * 走马灯TextView，可设置滚动方向与滚动速度
 *
 */
public class MarqueeTextView extends AppCompatTextView implements Runnable {

    /**
     * 文本宽度
     */
    private float textWidth = 0;
    /**
     * 当前滚动位置
     */
    private int curScrollX = 0;
    /**
     * 滚动方向
     */
    private int direction = 1; //1 表示左滚动 -1表示右滚动
    /**
     * 滚动速度
     */
    private int speed = 4;
    /**
     * 延迟滚动时间
     */
    private int delayTime = 3000;
    /**
     * 提示滚出屏幕外后继续滚动的距离，可以理解为延时多少距离再次滚动到屏幕内
     */
    private int offsetDistance = 300;
    /**
     * 是否从原始位置开始滚动
     */
    private boolean startFromInitPosition = false;
    private boolean stop = false;
    private Paint paint;


    public MarqueeTextView(Context context) {
        this(context,null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
            direction = typedArray.getInteger(R.styleable.MarqueeTextView_direction,direction);
            speed = typedArray.getInteger(R.styleable.MarqueeTextView_speed,speed);
            delayTime = typedArray.getInteger(R.styleable.MarqueeTextView_delayTime,delayTime);
            offsetDistance = typedArray.getInteger(R.styleable.MarqueeTextView_offsetDistance,offsetDistance);
            startFromInitPosition = typedArray.getBoolean(R.styleable.MarqueeTextView_startFromInitPosition,startFromInitPosition);
            setText(getText());
            setSingleLine();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        Paint paint = getPaint();
        textWidth = paint.measureText(text.toString());
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        removeCallbacks(this);
        curScrollX  -= speed * direction;
        post(this);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        stop = true;
        scrollTo(0,0);
    }

    @Override
    public void run() {
        if (stop)return;
        curScrollX  += speed * direction;
        scrollTo(curScrollX,0);
        switch (direction) {
            case 1:
                if (curScrollX >= textWidth) {
                    curScrollX = -(getWidth() + offsetDistance);
                }
                break;
            case -1:
                if (curScrollX <= -getWidth()) {
                    curScrollX = (int) (textWidth + offsetDistance);
                }
                break;
            default:
                break;
        }

        postDelayed(this,
                (curScrollX >= - 0.5 * speed)
                        && (curScrollX < 0.5 * speed ) ? delayTime + 16 : 16); // 设成一帧的刷新速度，文本位于初始位置时会做延时停留
    }
}
