package com.example.kursovaya.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class PieView extends View {
    private float donePercent = 0.0f; // 0..1

    public PieView(Context c, AttributeSet a){ super(c, a); }

    public void setDonePercent(float p){ donePercent = Math.max(0, Math.min(1, p)); invalidate(); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float pad = 12f;
        RectF oval = new RectF(pad, pad, getWidth()-pad, getHeight()-pad);

        Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
        base.setStyle(Paint.Style.STROKE);
        base.setStrokeWidth(32f);

        base.setColor(Color.parseColor("#424242"));
        c.drawArc(oval, -90, 360, false, base);

        base.setColor(Color.parseColor("#BDBDBD"));
        c.drawArc(oval, -90, 360 * donePercent, false, base);
    }
}
