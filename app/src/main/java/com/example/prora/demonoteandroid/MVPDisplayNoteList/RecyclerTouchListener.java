package com.example.prora.demonoteandroid.MVPDisplayNoteList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by prora on 3/9/2017.
 */

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

	public interface ClickListener {
		void onClick(View view, int position);
	}

	private GestureDetector gestureDetector;
	private ClickListener clickListener;

	public RecyclerTouchListener(Context context, final ClickListener clickListener) {
		this.clickListener = clickListener;
		gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return false;
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		View child = rv.findChildViewUnder(e.getX(), e.getY());
		if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
			clickListener.onClick(child, rv.getChildPosition(child));
		}

	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}
}
