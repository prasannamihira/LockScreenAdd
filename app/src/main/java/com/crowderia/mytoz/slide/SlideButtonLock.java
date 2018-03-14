package com.crowderia.mytoz.slide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by crowderia on 11/2/16.
 */

public class SlideButtonLock extends SeekBar {

    private Drawable thumb;
    private SlideButtonListener listener;

    public SlideButtonLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        this.thumb = thumb;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (thumb.getBounds().contains((int) event.getX(), (int) event.getY())) {
                super.onTouchEvent(event);
            } else
                return false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getProgress() > 100)
                handleSlide();

            setProgress(0);
        } else
            super.onTouchEvent(event);

        return true;
    }

    private void handleSlide() {
        listener.handleSlide();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void setSlideButtonListener(SlideButtonListener listener) {
        this.listener = listener;
    }

}


