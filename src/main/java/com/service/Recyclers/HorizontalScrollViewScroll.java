package com.service.Recyclers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewScroll extends HorizontalScrollView {
    private boolean isTouchingScrollbar = false;

    public HorizontalScrollViewScroll(Context context) {
        super(context);
    }

    public HorizontalScrollViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollViewScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchingScrollbar = isTouchOnScrollbar(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchingScrollbar) {
                    float x = event.getX();
                    int scrollRange = getChildAt(0).getWidth() - getWidth();
                    float proportion = x / (float) getWidth();
                    int targetScroll = (int) (scrollRange * proportion);
                    scrollTo(targetScroll, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouchingScrollbar = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchOnScrollbar(MotionEvent event) {
        int scrollBarHeight = getHorizontalScrollbarHeight();
        int y = (int) event.getY();
        int viewHeight = getHeight();
        return y >= (viewHeight - scrollBarHeight);
    }
}