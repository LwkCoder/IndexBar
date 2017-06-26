package com.lwkandroid.indexbar;

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
    private Paint mPaintNormal;
    private Paint mPaintPressed;
    private int mWidth, mHeight, mItemHeight;
    private int mLastIndex = -1;
    private boolean mPressed;
    private static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
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
        mCharArray = resources.getStringArray(R.array.index_bar_array);
        mTextSizeNormal = resources.getDimensionPixelSize(R.dimen.ib_text_size_normal_default);
        mTextSizePressed = resources.getDimensionPixelSize(R.dimen.ib_text_size_pressed_default);
        mTextColorNormal = Color.BLACK;
        mTextColorPressed = Color.BLUE;
        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndexBar);
        if (ta != null)
        {
            int count = ta.getIndexCount();
            for (int i = 0; i < count; i++)
            {
                int index = ta.getIndex(i);
                if (index == R.styleable.IndexBar_text_color_normal)
                    mTextColorNormal = ta.getColor(index, Color.GRAY);
                else if (index == R.styleable.IndexBar_text_color_pressed)
                    mTextColorPressed = ta.getColor(index, Color.BLACK);
                else if (index == R.styleable.IndexBar_text_size_normal)
                    mTextSizeNormal = ta.getDimensionPixelSize(index,
                            context.getResources().getDimensionPixelSize(R.dimen.ib_text_size_normal_default));
                else if (index == R.styleable.IndexBar_text_size_pressed)
                    mTextSizePressed = ta.getDimensionPixelSize(index,
                            context.getResources().getDimensionPixelSize(R.dimen.ib_text_size_pressed_default));
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mCharArray != null && mCharArray.length > 0)
            mItemHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mCharArray.length;
        //如果没有指定具体的宽度，修改宽度为Item高度+paddingTop+paddingBottom
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY)
            mWidth = mItemHeight + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(mWidth, mHeight);
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
            setPressedState(true);
            y = event.getY();
            checkY(y);
        } else if (action == MotionEvent.ACTION_MOVE)
        {
            y = event.getY();
            checkY(y);
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL)
        {
            getParent().requestDisallowInterceptTouchEvent(false);
            setPressedState(false);
            mLastIndex = -1;
            invalidate();
        }
        return true;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace)
    {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (mPressed)
            mergeDrawableStates(states, STATE_PRESSED);
        return states;
    }

    private void checkY(float y)
    {
        int curIndex = (int) ((y - getPaddingTop()) / mItemHeight);
        if (curIndex != mLastIndex)
        {
            if (curIndex >= 0 && curIndex < mCharArray.length)
            {
                if (mListener != null)
                    mListener.onLetterChanged(mCharArray[curIndex], curIndex, y);
                mLastIndex = curIndex;
            }
        }
        invalidate();
    }

    //计算位置
    private Pair<Float, Float> calPosition(CharSequence str, Paint paint, int index)
    {
        // x坐标等于中间-字符串宽度的一半.
        float x = mWidth / 2 - paint.measureText(String.valueOf(str)) / 2;
        Rect rect = new Rect();
        paint.getTextBounds(String.valueOf(str), 0, str.length(), rect);
        float y = mItemHeight * index + (mItemHeight + rect.height()) / 2 + getPaddingTop();
        return new Pair<>(x, y);
    }

    //更新按压状态
    private void setPressedState(boolean pressed)
    {
        if (mPressed != pressed)
        {
            mPressed = pressed;
            refreshDrawableState();
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
        void onLetterChanged(CharSequence indexChar, int index, float y);
    }
}
