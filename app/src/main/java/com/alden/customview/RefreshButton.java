package com.alden.customview;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Alden on 2017/11/8.
 * Comment://todo
 */

public class RefreshButton extends View {

    // 圆角矩形属性
    private int borderColor = Color.parseColor("#fb7299");
    private float borderWidth = 0;
    private float borderRadius = 120;

    // 文字属性
    private String text = "点击换一批";
    private int textColor = Color.parseColor("#fb7299");
    private float textSize = 28;

    // 旋转图标属性
    private int iconSrc = R.mipmap.tag_center_refresh_icon;
    private float iconSize = 28;
    private Bitmap iconBitmap;
    private float space4TextAndIcon = 20;

    private float degress = 0;
    private ObjectAnimator mAnimator;

    // 画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //有部分属性需要在构造函数中初始化（也为之后自定义属性做准备），所以，将第1个与第2个构造函数中的super修改为this。
    public RefreshButton(Context context) {
        this(context,null);
    }

    public RefreshButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshButton);
        borderColor = ta.getColor(R.styleable.RefreshButton_refresh_btn_borderColor, Color.parseColor("#fb7299"));
        borderWidth = ta.getDimension(R.styleable.RefreshButton_refresh_btn_borderWidth, dipToPx(0));
        borderRadius = ta.getDimension(R.styleable.RefreshButton_refresh_btn_borderRadius, dipToPx(60));
        text = ta.getString(R.styleable.RefreshButton_refresh_btn_text);
        if (text == null)
            text = "";
        textColor = ta.getColor(R.styleable.RefreshButton_refresh_btn_textColor, Color.parseColor("#fb7299"));
        textSize = ta.getDimension(R.styleable.RefreshButton_refresh_btn_textSize, spToPx(14));
        iconSrc = ta.getResourceId(R.styleable.RefreshButton_refresh_btn_iconSrc, R.mipmap.tag_center_refresh_icon);
        iconSize = ta.getDimension(R.styleable.RefreshButton_refresh_btn_iconSize, dipToPx(14));
        space4TextAndIcon = ta.getDimension(R.styleable.RefreshButton_refresh_btn_space4TextAndIcon, dipToPx(10));

        ta.recycle();


        // 将图标资源实例化为Bitmap
        iconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tag_center_refresh_icon);
        iconBitmap = zoomImg(iconBitmap, iconSize, iconSize);

        // 旋转动画
        mAnimator = ObjectAnimator.ofObject(this, "degress", new FloatEvaluator(), 360, 0);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ObjectAnimator.INFINITE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.画圆角矩形
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(borderWidth);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),borderRadius,borderRadius,mPaint);

        //2.画字
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.setStyle(Paint.Style.FILL);
        float measureText = mPaint.measureText(text);
        float measureAndIcon = measureText + space4TextAndIcon + iconSize;

        float textStartX = getWidth() / 2 - measureAndIcon / 2;
        float textBaseY = getHeight() / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
        canvas.drawText(text, textStartX, textBaseY, mPaint);

        // 3、画刷新图标
        float iconStartX = textStartX + measureText + space4TextAndIcon;
        canvas.save();
        float centerX = iconStartX + iconSize / 2;
        int centerY = getHeight() / 2;
        canvas.rotate(degress, centerX, centerY);
        canvas.drawBitmap(iconBitmap, iconStartX, getHeight() / 2 - iconSize / 2, mPaint);
        canvas.restore();


    }


    //iconSize是我自己定的一个大小，并不是图标的实际大小，所以在往后做旋转动画时获取到的旋转中心会有误差，
    // 将导致图标旋转时不是按中心进行旋转。所以，这里需要对图标大小进行调整：
    public Bitmap zoomImg(Bitmap bm, float newWidth, float newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void start() {
        mAnimator.start();
    }

    public void stop() {
        mAnimator.cancel();
        setDegress(0);
    }

    public float getDegress() {
        return degress;
    }

    public void setDegress(float degress) {
        this.degress = degress;
        invalidate();
    }

    public float dipToPx(float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    public float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getIconSrc() {
        return iconSrc;
    }

    public void setIconSrc(int iconSrc) {
        this.iconSrc = iconSrc;
    }

    public float getIconSize() {
        return iconSize;
    }

    public void setIconSize(float iconSize) {
        this.iconSize = iconSize;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public float getSpace4TextAndIcon() {
        return space4TextAndIcon;
    }

    public void setSpace4TextAndIcon(float space4TextAndIcon) {
        this.space4TextAndIcon = space4TextAndIcon;
    }


}
