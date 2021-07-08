package com.lwkandroid.widget.indexbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.lwkandroid.indexbar.R;

/**
 * 自定义控件：索引栏
 */

public class IndexBar extends View
{
    private CharSequence[] mCharArray;
    private int mTextColorNormal;
    private int mTextColorPressed;
    private int mTextSizeNormal;
    private int mTextSizePressed;
    private int mBgColorNormal;
    private int mBgColorPressed;
    private Paint mPaintNormal;
    private Paint mPaintPressed;
    private int mTotalWidth;
    private int mTotalHeight;
    private int mItemHeight;
    private int mLastIndex = -1;
    private boolean mPressed;
    private OnIndexLetterChangedListener mListener;

    public IndexBar(Context context)
    {
        super(context);
        init(context, null);
    }

    public IndexBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        //初始化默认属性
        Resources resources = context.getResources();

        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndexBar);
        if (ta != null)
        {
            mTextColorNormal = ta.getColor(R.styleable.IndexBar_text_color_normal, Color.BLACK);
            mTextColorPressed = ta.getColor(R.styleable.IndexBar_text_color_normal, Color.BLUE);
            mBgColorNormal = ta.getColor(R.styleable.IndexBar_bg_color_normal, Color.TRANSPARENT);
            mBgColorPressed = ta.getColor(R.styleable.IndexBar_bg_color_pressed, Color.TRANSPARENT);
            mTextSizeNormal = ta.getDimensionPixelSize(R.styleable.IndexBar_text_size_normal,
                    context.getResources().getDimensionPixelOffset(R.dimen.ib_text_size_normal_default));
            mTextSizePressed = ta.getDimensionPixelSize(R.styleable.IndexBar_text_size_pressed,
                    context.getResources().getDimensionPixelOffset(R.dimen.ib_text_size_pressed_default));
            mCharArray = ta.getTextArray(R.styleable.IndexBar_text_array);
            if (mCharArray == null)
            {
                mCharArray = resources.getStringArray(R.array.index_bar_array);
            }
            ta.recycle();
        }
        //初始化Paint
        mPaintNormal = new Paint();
        mPaintNormal.setAntiAlias(true);
        mPaintNormal.setColor(mTextColorNormal);
        mPaintNormal.setTextSize(mTextSizeNormal);
        mPaintPressed = new Paint();
        mPaintPressed.setAntiAlias(true);
        mPaintPressed.setTextSize(mTextSizePressed);
        mPaintPressed.setColor(mTextColorPressed);
        mPaintPressed.setFakeBoldText(true);
        mPaintPressed.setTypeface(Typeface.DEFAULT_BOLD);
        //设置初始背景
        setBackgroundColor(mBgColorNormal);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mTotalWidth = MeasureSpec.getSize(widthMeasureSpec);
        mTotalHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        float maxCharWidth = 0f;
        float totalCharHeight = 0f;
        if (mCharArray != null)
        {
            //遍历所有CharSequence，找出常态、按下状态对应的最大宽值和总计高度
            for (CharSequence value : mCharArray)
            {
                Paint.FontMetrics normalFontMetrics = mPaintNormal.getFontMetrics();
                Paint.FontMetrics pressedFontMetrics = mPaintPressed.getFontMetrics();

                float normalWidth = mPaintNormal.measureText(value.toString());
                float normalHeight = normalFontMetrics.bottom - normalFontMetrics.top;
                float pressedWidth = mPaintPressed.measureText(value.toString());
                float pressedHeight = pressedFontMetrics.bottom - pressedFontMetrics.top;

                float charWidth = Math.max(normalWidth, pressedWidth);
                float charHeight = Math.max(normalHeight, pressedHeight);

                maxCharWidth = Math.max(maxCharWidth, charWidth);
                totalCharHeight += charHeight;
            }
        }

        //宽度未明确指定时，计算所需宽度
        if (MeasureSpec.EXACTLY != widthMode)
        {
            mTotalWidth = (int) (maxCharWidth + paddingLeft + paddingRight);
        }
        //高度未明确指定时，计算所需高度
        if (MeasureSpec.EXACTLY != heightMode)
        {
            mTotalHeight = (int) (totalCharHeight + paddingTop + paddingBottom);
        }

        //计算最终每个字符所需要的高度(均分整体高度)
        if (mCharArray != null && mCharArray.length > 0)
        {
            mItemHeight = (mTotalHeight - paddingTop - paddingBottom) / mCharArray.length;
        }
        setMeasuredDimension(mTotalWidth, mTotalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (mCharArray != null && mCharArray.length > 0)
        {
            for (int i = 0, length = mCharArray.length; i < length; i++)
            {
                CharSequence c = mCharArray[i];
                Pair<Float, Float> position;
                if (i == mLastIndex)
                {
                    position = calPosition(c, mPaintPressed, i);
                    canvas.drawText(c, 0, c.length(), position.first, position.second, mPaintPressed);
                } else
                {
                    position = calPosition(c, mPaintNormal, i);
                    canvas.drawText(c, 0, c.length(), position.first, position.second, mPaintNormal);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        final int action = event.getAction();
        float y;
        if (action == MotionEvent.ACTION_DOWN)
        {
            getParent().requestDisallowInterceptTouchEvent(true);
            updateBgColor(true);
            if (mListener != null)
            {
                mListener.onTouched(true);
            }
            y = event.getY();
            updateIndex(y);
        } else if (action == MotionEvent.ACTION_MOVE)
        {
            y = event.getY();
            updateIndex(y);
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL)
        {
            getParent().requestDisallowInterceptTouchEvent(false);
            updateBgColor(false);
            if (mListener != null)
            {
                mListener.onTouched(false);
            }
            mLastIndex = -1;
            invalidate();
        }
        return true;
    }

    //更新Index
    private void updateIndex(float y)
    {
        int curIndex = (int) ((y - getPaddingTop()) / mItemHeight);
        if (curIndex != mLastIndex)
        {
            if (curIndex >= 0 && mCharArray != null && curIndex < mCharArray.length)
            {
                if (mListener != null)
                {
                    mListener.onLetterChanged(mCharArray[curIndex], curIndex, y);
                }
                mLastIndex = curIndex;
            }
        }
        invalidate();
    }

    //计算位置
    private Pair<Float, Float> calPosition(CharSequence str, Paint paint, int index)
    {
        // x坐标等于中间-字符串宽度的一半.
        float x = (mTotalWidth - paint.measureText(String.valueOf(str))) / 2;
        Rect rect = new Rect();
        paint.getTextBounds(String.valueOf(str), 0, str.length(), rect);
        float y = mItemHeight * index + (mItemHeight + rect.height()) / 2 + getPaddingTop();
        return new Pair<>(x, y);
    }

    //更新按压背景
    private void updateBgColor(boolean pressed)
    {
        if (mPressed != pressed)
        {
            mPressed = pressed;
            if (mPressed)
            {
                setBackgroundColor(mBgColorPressed);
            } else
            {
                setBackgroundColor(mBgColorNormal);
            }
        }
    }

    /**
     * 自定义字符数组
     */
    public void setTextArray(CharSequence[] array)
    {
        this.mCharArray = array;
        invalidate();
    }

    /**
     * 设置索引字母改变监听
     */
    public void setOnIndexLetterChangedListener(OnIndexLetterChangedListener l)
    {
        this.mListener = l;
    }

    public interface OnIndexLetterChangedListener
    {
        void onTouched(boolean touched);

        void onLetterChanged(CharSequence indexChar, int index, float y);
    }
}
