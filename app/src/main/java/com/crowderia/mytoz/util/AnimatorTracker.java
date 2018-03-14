package com.crowderia.mytoz.util;

import android.animation.Animator;

/**
 * Created by Crowderia on 11/24/2016.
 */

public class AnimatorTracker implements Animator.AnimatorListener {
    int counter;

    public AnimatorTracker() {
        counter = 0;
    }

    public boolean isAnimating() {
        return counter == 0;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        counter++;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        counter--;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // Canceling an animation invokes onAnimationEnd, so nothing needs to be done.
    }
}
