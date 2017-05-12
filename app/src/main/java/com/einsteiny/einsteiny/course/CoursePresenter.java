package com.einsteiny.einsteiny.course;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.einsteiny.einsteiny.fragments.UnsubscribeDialogAlertFragment;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import java.text.SimpleDateFormat;

/**
 * Created by tonya on 4/30/17.
 */

public class CoursePresenter implements CourseContract.Presenter {

    private final CourseContract.View mCourseView;
    private final Course mCourse;
    private final Context mContext;


    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
    private final SimpleDateFormat tdf = new SimpleDateFormat("hh:mm");


    public CoursePresenter(@NonNull Course course, @NonNull CourseContract.View courseView, Context context) {
        mCourseView = courseView;
        mCourse = course;
        mContext = context;

        mCourseView.setPresenter(this);
    }

    @Override
    public void start() {
        mCourseView.setSupportPostponeEnterTransition();
        mCourseView.setFabInvisible();


        long date = CustomUser.getDateForCourse(mCourse.getId());
        int progress = CustomUser.getProgressForCourse(mCourse.getId());
        String courseInfo = String.format("Course %s on <b>%s</b>. Lessons are scheduled daily at <b>%s</b>.",
                progress > 0 ? "started" : "will start", sdf.format(date), tdf.format(date));

        mCourseView.setCourseInfo(courseInfo);

        mCourseView.loadCourseImage(mCourse.getPhotoUrl());

        mCourseView.initAdapterAndLayoutManager(mCourse);

        mCourseView.initToolbar(mCourse.getTitle());

        if (!CustomUser.isCompletedCourse(mCourse.getId())) {
            mCourseView.initTransitionListener();
            mCourseView.addListener();
        } else {
            mCourseView.setFabGone();
        }

    }


    @Override
    public void exitReveal(View myView) {
        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);

                // Finish the activity after the exit transition completes.
                mCourseView.callFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }

    //Floating action button circular animation
    @Override
    public void enterReveal(View myView) {
        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCourseView.removeListener();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    public void startUnsubscribeDialog() {
        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        UnsubscribeDialogAlertFragment dialog = UnsubscribeDialogAlertFragment.newInstance(mCourse);
        dialog.show(fm, "unsubscribe_dialog");
    }
}
