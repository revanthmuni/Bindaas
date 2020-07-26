package com.tachyon.bindaas;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class RoundedCornerLayout extends RelativeLayout {
    private Bitmap maskBitmap;
    private Paint paint;
    private float cornerRadius;

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (maskBitmap == null) {
            // This corner radius assumes the image width == height and you want it to be circular
            // Otherwise, customize the radius as needed
            cornerRadius = 25f;
            Log.d("canvas", "draw: "+cornerRadius);
            maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
        }

        canvas.drawBitmap(maskBitmap, 0f, 0f, paint);
    }

    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);

        canvas.drawRect(0, 0, width, height, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

        return mask;
    }
}