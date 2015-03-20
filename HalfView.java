
package com.cleanmaster.base.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Transformation;

public class HalfView extends View {

    public HalfView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HalfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HalfView(Context context) {
        super(context);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int w = getWidth();
                int h = getHeight();

                hw = w;
                hh = h;
                if (mHalfType == HORIZONTAL || mHalfType == QUARTER) {
                    hw = w / 2;
                }

                if (mHalfType == VERTICAL || mHalfType == QUARTER) {
                    hh = h / 2;
                }

                if (getBackground() != null) {
                    getBackground().setBounds(0, 0, hw, hh);
                }
                return true;
            }
        });
    }

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 1 << 1;
    public static final int QUARTER = 1 << 2;
    private int mHalfType;

    public void setHalfType(int type) {
        mHalfType = type;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        int width = super.getSuggestedMinimumWidth();
        int k = 1;
        if (mHalfType == HORIZONTAL || mHalfType == QUARTER)
            k = 2;
        return width * k;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        int height = super.getSuggestedMinimumHeight();
        int k = 1;
        if (mHalfType == VERTICAL || mHalfType == QUARTER)
            k = 2;
        return height * k;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        if (wm != MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(ws, MeasureSpec.UNSPECIFIED);
        }

        int hs = MeasureSpec.getSize(heightMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        if (hm != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(hs, MeasureSpec.UNSPECIFIED);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    int hw, hh;
    final Camera mCamera = new Camera();
    Transformation transformToApply = new Transformation();

    @Override
    public void draw(Canvas canvas) {
        onDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getBackground();
        if (drawable == null)
            return;

        Matrix matrix = transformToApply.getMatrix();

        switch (mHalfType) {
            case HORIZONTAL:
                drawable.draw(canvas);

                transformMatrix(matrix, hw * 2, 0, 0, 180);
                applyToCanvas(canvas, matrix, drawable);
                break;

            case VERTICAL:
                drawable.draw(canvas);

                transformMatrix(matrix, 0, hh * 2, 180, 0);
                applyToCanvas(canvas, matrix, drawable);
                break;

            case QUARTER:
                drawable.draw(canvas);

                transformMatrix(matrix, hw * 2, 0, 0, 180);
                applyToCanvas(canvas, matrix, drawable);

                transformMatrix(matrix, 0, hh * 2, 180, 0);
                applyToCanvas(canvas, matrix, drawable);

                transformMatrix(matrix, hw * 2, hh * 2, 180, 180);
                applyToCanvas(canvas, matrix, drawable);
                break;

            default:
                drawable.draw(canvas);
                break;
        }

    }

    private void applyToCanvas(Canvas canvas, Matrix matrix, Drawable drawable) {
        canvas.save();
        canvas.concat(matrix);
        drawable.draw(canvas);
        canvas.restore();
    }

    private void transformMatrix(Matrix matrix, float cw, float ch, float degressX, float degressY) {
        final Camera camera = mCamera;
        camera.save();
        if (degressX != 0)
            camera.rotateX(180);
        if (degressY != 0)
            camera.rotateY(180);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.postTranslate(cw, ch);
    }

}
s
