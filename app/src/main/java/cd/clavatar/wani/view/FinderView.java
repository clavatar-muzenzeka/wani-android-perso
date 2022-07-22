package cd.clavatar.wani.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;


import cd.clavatar.wani.R;


public class FinderView extends View {
    private static final long ANIMATION_DELAY = 100;
    private Paint finderMaskPaint;
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    private int measuredWidth;
    private int measuredHeight;
    private Rect middleRect = new Rect();
    private Rect lineRect = new Rect();

    private static float density;

    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 14;
    /**
     * 字体距离扫描框下面的距离
     */
    private static final int TEXT_PADDING_TOP = 20;
    /**
     * 线条高度
     */
    private static final int LINE_HEIGHT = 15;
    private Resources mResources;
    private Bitmap mScanBG;
    private Bitmap mScanLine;

    public FinderView(Context context) {
        super(context);
        init(context);
    }

    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        finderMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        finderMaskPaint.setColor(0x60000000);
        paint = new Paint();
        density = context.getResources().getDisplayMetrics().density;
        mResources = getResources();
        mScanBG = BitmapFactory.decodeResource(mResources, R.drawable.scan_bg);
        mScanLine = BitmapFactory.decodeResource(mResources, R.drawable.scan_line);
    }

    int borderWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        borderWidth = (int) (measuredWidth * 0.8);
        middleRect.set((measuredWidth - borderWidth) / 2, (measuredHeight - borderWidth) / 2 - 30,
                (measuredWidth - borderWidth) / 2 + borderWidth, (measuredHeight - borderWidth) / 2 + borderWidth - 30);
        lineRect.set(middleRect.left, middleRect.top, middleRect.right, middleRect.top + LINE_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画四周的背景
        canvas.drawRect(0, (measuredHeight - borderWidth) / 2 - 30, (measuredWidth - borderWidth) / 2 + borderWidth, (measuredHeight - borderWidth) / 2 + borderWidth - 30, finderMaskPaint);
        canvas.drawRect(0, 0, measuredWidth, middleRect.top, finderMaskPaint);
        canvas.drawRect(middleRect.right, middleRect.top, measuredWidth, middleRect.bottom, finderMaskPaint);
        canvas.drawRect(0, middleRect.bottom, measuredWidth, measuredHeight, finderMaskPaint);
        //画框
        canvas.drawBitmap(mScanBG, null, middleRect, null);
        //画走动的线条
        drawLine(canvas);
        // 画扫描框下面的字
        drawText(canvas);
        // 只刷新扫描框的内容，其他地方不刷新
        postInvalidateDelayed(ANIMATION_DELAY, middleRect.left, middleRect.top, middleRect.right, middleRect.bottom);
    }

    //画走动的线条
    private void drawLine(Canvas canvas) {
        if (lineRect.bottom >= middleRect.bottom) {
            lineRect.top = middleRect.top;
            lineRect.bottom = middleRect.top + LINE_HEIGHT;
        }
        canvas.drawBitmap(mScanLine, null, lineRect, null);
        lineRect.top += LINE_HEIGHT;
        lineRect.bottom += LINE_HEIGHT;
    }

    // 画扫描框下面的字
    private void drawText(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE * density);
        paint.setAlpha(0x40);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT);
        String text = getResources().getString(R.string.scan_text);
        String[] texts = text.split("\n");
        float add = 0;
        int frameWidth = middleRect.right - middleRect.left;
        for (String str : texts) {
            int start_left = 0;
            float text_width = paint.measureText(str);
            start_left = text_width > frameWidth ? 0 : (int) (frameWidth - text_width) / 2;
            canvas.drawText(str, middleRect.left + start_left, (float) (middleRect.bottom + (float) TEXT_PADDING_TOP * density + add),
                    paint);
            add += TEXT_SIZE * density;
        }
    }


    //////////////新增该方法//////////////////////

    /**
     * 根据图片size求出矩形框在图片所在位置，tip：相机旋转90度以后，拍摄的图片是横着的，所有传递参数时，做了交换
     *
     * @param w
     * @param h
     * @return
     */
    public Rect getScanImageRect(int w, int h) {
        //先求出实际矩形
        Rect rect = new Rect();
        float tempw = w / (float) measuredWidth;
        float temph = h / (float) measuredHeight;
        rect.left = (int) (middleRect.left * tempw);
        rect.right = (int) (middleRect.right * tempw);
        rect.top = (int) (middleRect.top * temph);
        rect.bottom = (int) (middleRect.bottom * temph);
        return rect;
    }
}
