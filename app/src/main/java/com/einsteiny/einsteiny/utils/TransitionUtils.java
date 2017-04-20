package com.einsteiny.einsteiny.utils;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by tonya on 4/19/17.
 */

public class TransitionUtils {

    public static void scheduleStartPostponedTransition(final View sharedElement, FragmentActivity context) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        context.supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }
}
