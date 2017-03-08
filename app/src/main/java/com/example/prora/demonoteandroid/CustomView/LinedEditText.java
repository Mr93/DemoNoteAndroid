package com.example.prora.demonoteandroid.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by prora on 3/8/2017.
 */

public class LinedEditText extends EditText {

	private Paint mPaint = new Paint();

	public LinedEditText(Context context) {
		super(context);
		initPaint();
	}

	public LinedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public LinedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	private void initPaint(){
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(0x80000000);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int left = getLeft();
		int right = getRight();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int height = getHeight();
		int lineHeight = getLineHeight();
		int count = (height-paddingTop-paddingBottom) / lineHeight;

		for (int i = 0; i < count; i++) {
			int baseline = lineHeight * (i+1) + paddingTop;
			canvas.drawLine(left, baseline, right-paddingRight, baseline, mPaint);
		}

		super.onDraw(canvas);
	}
}
