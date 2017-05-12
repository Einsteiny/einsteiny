package com.einsteiny.einsteiny.coursesubscribe;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;

/**
 * Created by tonya on 4/30/17.
 */

public class CourseSubscribePresenter implements CourseSubscribeContract.Presenter {

    private final CourseSubscribeContract.View mCourseView;
    private final Course mCourse;
    private final Context mContext;


    public CourseSubscribePresenter(@NonNull Course course, @NonNull CourseSubscribeContract.View courseView, Context context) {
        mCourseView = courseView;
        mCourse = course;
        mContext = context;

        mCourseView.setPresenter(this);
    }

    @Override
    public void start() {
        mCourseView.setFabInvisible();

        mCourseView.initTvDescription(mCourse.getDescription());
        mCourseView.initTvDuration(String.format("Duration: <b>%s</b>", mContext.getResources().getQuantityString(R.plurals.days,
                mCourse.getLessons().size(), mCourse.getLessons().size())));
        mCourseView.initTvTitle(mCourse.getTitle());
        mCourseView.setRating(mCourse.getComplexity());

        mCourseView.setSupportPostponeEnterTransition();

        mCourseView.loadCourseImage(mCourse.getPhotoUrl());

        mCourseView.initTransitionListener();

        mCourseView.loadCourseImage(mCourse.getPhotoUrl());

        mCourseView.initToolbar("Course Details");

        mCourseView.addListener();


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


}
