package dmangames.team4.reap.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import dmangames.team4.reap.R;

public class IconView extends View {
    private float mNumIcons;
    private Bitmap mIcon;

    private TextPaint mTextPaint;

    public IconView(Context context) {
        super(context);
        init(null, 0);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void changeIcon(int iconID){
//        mIcon = ResourcesCompat.getDrawable(getResources(), iconID, null);
        mIcon = BitmapFactory.decodeResource(getResources(), iconID);
        invalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.IconView, defStyle, 0);

        if(a.hasValue(R.styleable.IconView_numIcons))
            mNumIcons = a.getFloat(R.styleable.IconView_numIcons, 1);
        else
            throw new RuntimeException("Must declare numIcons");

        if (a.hasValue(R.styleable.IconView_iconDrawable)) {
//            mIcon = a.getDrawable(R.styleable.IconView_iconDrawable);
            mIcon = BitmapFactory.decodeResource(getResources(),R.styleable.IconView_iconDrawable);
        }
        else
            throw new RuntimeException("Must declare iconDrawable");

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int width = mIcon.getWidth();
        int height = mIcon.getHeight();


        double scale = (double)contentHeight / height;
        int scaledHeight = (int)Math.floor(height * scale);
        int scaledWidth = (int)Math.floor(width * scale);
        int spacing = 40;


        // Number of icons that will fit on the screen
        int num = getWidth()/(scaledWidth+spacing);
        // If there are too many icons to fit on the screen, go into xmode
        boolean xMode = false;
        if(num<mNumIcons)
            xMode = true;

        num = (int)Math.floor(mNumIcons);

        Rect dst = new Rect();
        Rect src = new Rect();

        float fraction = mNumIcons - num;

        if (!xMode&& mIcon != null) {
            for(int i = 0; i < num; i ++){
                dst.set(paddingLeft + (i*scaledWidth) + (i*spacing), paddingTop,
                        ((i+1)*scaledWidth) + paddingRight + (i*spacing), scaledHeight + paddingBottom);
                canvas.drawBitmap(mIcon, null, dst, null);
            }
            if(getWidth()/(scaledWidth+spacing)>num) {
                dst.set(paddingLeft + (num*scaledWidth) + (num*spacing), paddingTop,
                        ((num)*scaledWidth) + paddingRight + (num*spacing) + (int)Math.floor(fraction*scaledWidth), scaledHeight + paddingBottom);
                src.set(0,0,(int)Math.floor(fraction*mIcon.getWidth()), mIcon.getHeight());
                canvas.drawBitmap(mIcon, src, dst, null);
            }

        }
        if(xMode && mIcon != null){
            mTextPaint.setTextSize(scaledHeight / 3);
            int textY = paddingTop + scaledHeight * 3 / 4;
            int textX = scaledWidth + paddingLeft + spacing;

            dst.set(paddingLeft, paddingTop,
                    scaledWidth + paddingRight, scaledHeight + paddingBottom);
            canvas.drawBitmap(mIcon, null, dst, null);
            String text = "x"+(int)Math.floor(mNumIcons);
            canvas.drawText(text, textX, textY,
                    mTextPaint);

            Rect textBound = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), textBound);
            if(textX+textBound.right+scaledWidth+spacing<contentWidth) {
                dst.set(textX + textBound.right + spacing, paddingTop, textX + spacing + textBound.right + (int) Math.floor(fraction * scaledWidth), scaledHeight + paddingBottom);
                src.set(0, 0, (int) Math.floor(fraction * mIcon.getWidth()), mIcon.getHeight());
                canvas.drawBitmap(mIcon, src, dst, null);
            }

        }

//
//        if (!xMode&& mIcon != null) {
//            for(int i = 0; i < num; i ++){
//                mIcon.setBounds(paddingLeft + (i*scaledWidth) + (i*spacing), paddingTop,
//                        ((i+1)*scaledWidth) + paddingRight + (i*spacing), scaledHeight + paddingBottom);
//                mIcon.draw(canvas);
//            }
//        }
//        if(xMode&& mIcon != null){
//            mIcon.setBounds(paddingLeft, paddingTop,
//                    scaledWidth + paddingLeft, scaledHeight + paddingTop);
//            mIcon.draw(canvas);
//            canvas.drawText("x"+mNumIcons,textX, textY,
//                    mTextPaint);
//        }
    }
//
//    public Drawable getIcon() {
//        return mIcon;
//    }
//
//    public void setIcon(Drawable icon) {
//        mIcon = icon;
//    }

    public float getNumIcons() {
        return mNumIcons;
    }

    public void setNumIcons(float mNumIcons) {
        this.mNumIcons = mNumIcons;
        invalidate();
    }

}
