package com.service.Recyclers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollViewScroll extends ScrollView {
    private boolean isTouchingScrollbar = false;

    public ScrollViewScroll(Context context) {
        super(context);
    }

    public ScrollViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewScroll(Context context, AttributeSet attrs, int defStyle) {
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
                    float y = event.getY();
                    int scrollRange = getChildAt(0).getHeight() - getHeight();
                    float proportion = y / (float) getHeight();
                    int targetScroll = (int) (scrollRange * proportion);
                    scrollTo(0, targetScroll);
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
        int scrollBarWidth = getVerticalScrollbarWidth();
        int x = (int) event.getX();
        int viewWidth = getWidth();
        return x >= (viewWidth - scrollBarWidth);
    }
}
