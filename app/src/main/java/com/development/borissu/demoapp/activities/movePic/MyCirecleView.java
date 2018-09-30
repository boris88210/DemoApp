package com.development.borissu.demoapp.activities.movePic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyCirecleView extends View {


    public float X = 50f;
    public float Y = 50f;

    Paint paint = new Paint();

    public MyCirecleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(X, Y, 30, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            this.X = event.getX();
            this.Y = event.getY();
            //通知元件進行重新繪製
            this.invalidate();
        }
        return true;
    }
}
