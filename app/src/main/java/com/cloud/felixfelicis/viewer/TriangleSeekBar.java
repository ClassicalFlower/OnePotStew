package com.cloud.felixfelicis.viewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.cloud.felixfelicis.R;
import com.cloud.felixfelicis.utils.DensityUtils;


/**
 * 粗细调整的seekbar
 */
public class TriangleSeekBar extends View {
    /**
     * 绘制背景的paint
     */
    private Paint mBgPaint;

    /**
     * 绘制前景的paint
     */
    private Paint mFgPaint;
    private Paint mPaint;

    /**
     * seekbar的thumb按钮
     */
    private Drawable mThumb;

    /**
     * 滑动距离
     */
    private int onMoveDistance;

    /**
     * 起始点x坐标
     */
    Point startPoint = new Point();

    private int thumbHalfH;
    private int thumbStartY;

    /**
     * 默认seekbar高度
     */
    private int defaultHeight;

    /**
     * 默认seekbar宽度
     */
    private int seekBarMaxWidth;

    private int thumbHalfW;
    private int tempY;
    private int bgEndY;

    /**
     * seek背景左侧结束y位置
     */
    private int bgLeftEndX;
    /**
     * seek背景右侧结束y位置
     */
    private int bgRightEndX;

    /**
     * thumb的位置
     */
    private Rect mThumbLocation = new Rect();

    /**
     * 触摸范围, 由于thumb的区域过小, 不好触摸滑动, 所以将触摸范围增大
     */
    private Rect mTouchRect = new Rect();

    private float movePercent;
    private int deltaX;
    private int deltaY;

    /**
     * 当前进度值
     */
    private int currentProgress;

    private OnThumbTouchMoveListener mOnThumbTouchMoveListener;
    private int mTouchThumbWidth;
    private int mTouchThumbHeight;

    /**
     * 默认seekbar 背景色
     */
    private static final int DEFAULT_SEEK_BAR_BACKGROUND_COLOR = 0xFFE5E5E5;
    /**
     * 默认seekbar 前景色
     */
    private static final int DEFAULT_SEEK_BAR_PROGRESS_COLOR = 0xFF00C1DE;

    public TriangleSeekBar(Context context) {
        this(context, null);
    }

    public TriangleSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initThumbResource(context);
        initPaint();
        initDefaultAttr();
        setBackground(getResources().getDrawable(R.drawable.bg_menu_expand_shape));
    }



    /**
     * 初始化默认属性
     */
    private void initDefaultAttr() {
        seekBarMaxWidth = DensityUtils.dip2px(getContext(), 6);
        defaultHeight = DensityUtils.dip2px(getContext(), 180);
    }

    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(DEFAULT_SEEK_BAR_BACKGROUND_COLOR);
        mBgPaint.setDither(true);

        mFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFgPaint.setStyle(Paint.Style.FILL);
        mFgPaint.setColor(DEFAULT_SEEK_BAR_PROGRESS_COLOR);
        mFgPaint.setDither(true);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
    }

    private void initThumbResource(Context context) {
        mThumb = context.getResources().getDrawable(R.drawable.seekbar_icon);
        if (mThumb != null) {
            thumbHalfW = DensityUtils.dip2px(getContext(), 11);
            thumbHalfH = DensityUtils.dip2px(getContext(), 5);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // 背景起始点x
        startPoint.x = width >> 1;
        // 背景起始点y
        startPoint.y = 0;
        // 背景左侧x点
        bgLeftEndX = startPoint.x - seekBarMaxWidth;
        // 背景右侧x点
        bgRightEndX = startPoint.x + seekBarMaxWidth;

        // thumb y方向起始坐标
        thumbStartY = startPoint.y + thumbHalfH;

        // thumb的bound (初始)位置, 该view中可触摸的范围, 暂时不管seek到点击位置的需求
        // left: 背景的x方向起始点 - thumbWidth / 2;
        mThumbLocation.left = startPoint.x -thumbHalfW;
        // top: 背景的y方向起始点
        mThumbLocation.top = startPoint.y;
        // right: 背景的x方向起始点 + thumbWidth / 2
        mThumbLocation.right = startPoint.x + thumbHalfW;
        // bottom: 背景的y方向起始点 + thumbHeight
        mThumbLocation.bottom = startPoint.y + thumbHalfH << 1;

        mThumb.setBounds(mThumbLocation);
        mTouchThumbWidth = mThumb.getIntrinsicWidth();
        mTouchThumbHeight = mThumb.getIntrinsicWidth();



        if (currentProgress > 0) {
            handleThumbLocation(convertPercentToDistance(currentProgress));
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(0, getPaddingTop());
        // 绘制背景
        drawBackgroundArea(canvas);
        // 绘制前景填充
        drawForegroundArea(canvas);
        if (thumbStartY < startPoint.y) {
            thumbStartY = startPoint.y;
        } else if (thumbStartY > defaultHeight) {
            thumbStartY = defaultHeight;
        }

        // 绘制thumb
        canvas.drawRect(mThumbLocation, mPaint);
        mThumb.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                computeToucheRect(mThumbLocation);
                if (!mTouchRect.contains(x, y)) {
                    //没有在矩形上点击，不处理触摸消息
                    return false;
                }
                //deltaX = x - mThumbLocation.left;
                deltaY = y - mThumbLocation.top;
                break;
            case MotionEvent.ACTION_MOVE:
                //更新矩形的位置, 只允许纵向滑动
                //mThumbLocation.left = x - deltaX;
                //mThumbLocation.right = mThumbLocation.left + thumbHalfW;
                Rect rect = handleThumbLocation(y);

                computeToucheRect(rect);

                computeMoveDistance((int)event.getY());

                //要刷新的区域，求新矩形区域与旧矩形区域的并集
                rect.union(mThumbLocation);
                //出于效率考虑，设定脏区域，只进行局部刷新，不是刷新整个view
                invalidate(rect);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mOnThumbTouchMoveListener != null) {
                    mOnThumbTouchMoveListener.onMoveEnd((int)movePercent);
                }
                break;

            default:break;
        }
        return true;
    }

    /**
     * 计算触摸的矩形范围
     * @param rect Rect
     */
    private void computeToucheRect(Rect rect) {
        mTouchRect.left = rect.left - thumbHalfW << 1;
        mTouchRect.top = rect.top - (thumbHalfH + 20);
        mTouchRect.right = rect.right + thumbHalfW << 1 ;
        mTouchRect.bottom = rect.bottom + (thumbHalfH + 20);
    }
    /**
     * 计算滑动距离
     * @param moveY
     */
    private void computeMoveDistance(int moveY) {
        onMoveDistance = moveY - startPoint.y;
        if (onMoveDistance > defaultHeight) {
            onMoveDistance = defaultHeight;
        }

        if (onMoveDistance < startPoint.y) {
            onMoveDistance = startPoint.y;
        }

        // 滑动距离的百分比
        movePercent = convertDistanceToPercent(onMoveDistance, defaultHeight);
    }

    /**
     * 绘制背景
     *
     * @param canvas Canvas
     */
    private void drawBackgroundArea(Canvas canvas) {
        Path path = new Path();
        // moveTo和和最后结尾的lineTo x方向各加减1 是为了让顶端看起来不那么尖
        path.moveTo(startPoint.x - 1, startPoint.y);
        bgEndY = startPoint.y + defaultHeight;
        path.lineTo(bgLeftEndX, bgEndY);
        path.lineTo(bgRightEndX, bgEndY);
        path.lineTo(startPoint.x + 1, startPoint.y);
        path.close();
        canvas.drawPath(path, mBgPaint);

    }

    /**
     * 绘制前景
     *
     * @param canvas Canvas
     */
    private void drawForegroundArea(Canvas canvas) {
        Path path = new Path();
        // moveTo和和最后结尾的lineTo x方向各加减1 是为了让顶端看起来不那么尖
        path.moveTo(startPoint.x - 1, startPoint.y);
        // 计算横向滑动距离, +2是为了让前景完全覆盖背景
        int move = (int)(movePercent / 100 * seekBarMaxWidth) + 2;
        int leftX = startPoint.x - move;
        int rightX = startPoint.x + move;
        if (leftX < bgLeftEndX) {
            leftX = bgLeftEndX;
        }

        if (rightX > bgRightEndX) {
            rightX = bgRightEndX;
        }

        path.lineTo(leftX, mThumbLocation.top);
        path.lineTo(rightX, mThumbLocation.top);
        path.lineTo(startPoint.x + 1, startPoint.y);
        path.close();
        canvas.drawPath(path, mFgPaint);
    }
    /**
     * thumb 位置更新
     */
    private Rect handleThumbLocation(int y) {
        Rect rect = new Rect(mThumbLocation);
        if (mOnThumbTouchMoveListener != null) {
            mOnThumbTouchMoveListener.onMoveStart((int)movePercent);
        }

        mThumbLocation.top = y - deltaY;
        if (mThumbLocation.top < startPoint.y) {
            mThumbLocation.top = startPoint.y;
        }

        if (mThumbLocation.top + thumbHalfH > defaultHeight) {
            mThumbLocation.top = defaultHeight - (thumbHalfH << 1);
        }

        mThumbLocation.bottom = mThumbLocation.top + thumbHalfW;
        if (mThumbLocation.bottom > defaultHeight) {
            mThumbLocation.bottom = defaultHeight;
        }

        mThumb.setBounds(mThumbLocation);

        return rect;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = thumbHalfW << 1 + getPaddingRight() + getPaddingLeft();
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = defaultHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, widthMode),
            MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }


    /**
     * 获取当前进度
     * @return 当前进度百分比 (取值[0, 100])
     */
    public int getProgress() {
        return convertPercentToDistance(currentProgress);
    }

    /**
     * 设置进度
     * @param currentProgress 进度值 (取值[0, 100] 的百分比)
     */
    public void setProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        computeMoveDistance(convertPercentToDistance(currentProgress));
        handleThumbLocation(convertPercentToDistance(currentProgress));
    }

    /**
     * 设置seekbar 底色
     *
     * @param color 颜色值
     */
    public void setSeekBarBackground(int color) {
        mBgPaint.setColor(color);
    }

    /**
     * 设置seekbar进度值颜色
     *
     * @param color
     */
    public void setSeekBarProgressColor(int color) {
        mFgPaint.setColor(color);
    }

    /**
     * 根据滑动位置距离计算百分比
     * @return
     */
    private int convertDistanceToPercent(int movePosition, int maxPosition) {
        // 滑动距离的百分比 = 移动距离 / 最大距离
        return (int)((float)movePosition / (float)maxPosition * 100);
    }

    /**
     * 根据百分比转换为距离
     * @param percent 百分比 取值[0, 100]
     * @return
     */
    private int convertPercentToDistance(int percent) {
        return (int)((float)percent / 100 * defaultHeight);
    }


    public interface OnThumbTouchMoveListener {
        /**
         * 滑动开始
         * @param percent 百分比
         */
        void onMoveStart(int percent);

        /**
         * 滑动结束
         * @return 返回结束时的进度值
         * @param percent
         */
        void onMoveEnd(int percent);
    }

    /**
     * 设置滑动监听
     * @param listener
     */
    public void setOnThumbTouchMoveListener(OnThumbTouchMoveListener listener) {
        this.mOnThumbTouchMoveListener = listener;
    }
}
