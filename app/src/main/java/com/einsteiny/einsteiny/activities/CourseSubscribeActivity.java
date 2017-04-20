package com.einsteiny.einsteiny.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.fragments.ResubscribeDialogAlertFragment;
import com.einsteiny.einsteiny.fragments.SelectTimeDialog;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.models.Lesson;
import com.einsteiny.einsteiny.utils.TransitionUtils;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseSubscribeActivity extends AppCompatActivity implements SelectTimeDialog.ConfirmSubscriptionListener,
        ResubscribeDialogAlertFragment.SubscribeCourseListener {

    @BindView(R.id.ivCourse)
    ImageView ivCourse;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btnSubscribe)
    FloatingActionButton fab;

    private Transition.TransitionListener mEnterTransitionListener;


    public static final String EXTRA_COURSE = "course";

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subscribe);

        ButterKnife.bind(this);

        course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);

        fab.setVisibility(View.INVISIBLE);

        tvDescription.setText(course.getDescription());
        tvTitle.setText(course.getTitle());
        supportPostponeEnterTransition();

        String photoUrl = course.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            int displayWidth = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(photoUrl).resize(displayWidth, 0).into(ivCourse,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseSubscribeActivity.this);
                        }

                        @Override
                        public void onError() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseSubscribeActivity.this);
                            Log.d("Debug", "onError: error loading course image");
                        }
                    });
        } else {
            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseSubscribeActivity.this);
        }


        //check if user already subscribed for the course
        fab.setOnClickListener(v -> {
            List<String> subscribedCourse = CustomUser.getSubscribedCourses();
            if (subscribedCourse != null && subscribedCourse.contains(course.getId())) {
                FragmentManager fm = getSupportFragmentManager();
                ResubscribeDialogAlertFragment dialog = ResubscribeDialogAlertFragment.newInstance(course);
                dialog.show(fm, "resubscribe_dialog");

            } else {
                FragmentManager fm = getSupportFragmentManager();
                SelectTimeDialog dialog = SelectTimeDialog.newInstance();
                dialog.show(fm, "subscribe_dialog");

            }

        });

        setupToolbar("Course Details");

        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

    private void sendParseNotification(String courseId, long time) {
        JSONObject payload = new JSONObject();

        try {
            payload.put("sender", ParseInstallation.getCurrentInstallation().getInstallationId());
            payload.put("course", courseId);
            payload.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());

        ParseCloud.callFunctionInBackground("subscribe", data);
    }

    @Override
    public void confirmSubscription(Calendar cal) {
        CustomUser.unsubscribeCourse(course);
        CustomUser.addSubscribedCourse(course);

        Intent i = new Intent(CourseSubscribeActivity.this, CourseActivity.class);
        i.putExtra(CourseActivity.EXTRA_COURSE, course);
        i.putExtra(CourseActivity.EXTRA_TIME, cal.getTimeInMillis());

        //save course with start time
        course.setStartTime(cal.getTimeInMillis());
        course.save();


        for (Lesson lesson : course.getLessons()) {
            sendParseNotification(course.getId(), cal.getTimeInMillis());
            cal.add(Calendar.DATE, 1);
        }

        startActivity(i);
    }

    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void skipSubscription() {
        finishAfterTransition();
    }

    @Override
    public void subscribeCourse(Course course) {
        FragmentManager fm = getSupportFragmentManager();
        SelectTimeDialog dialog = SelectTimeDialog.newInstance();
        dialog.show(fm, "fragment_filter_dialog");
    }

    //Floating action button circular animation
    private void enterReveal() {
        // previously invisible view
        final View myView = fab;

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
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
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

    void exitReveal() {
        // previously visible view
        final View myView = fab;

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
                supportFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                exitReveal();
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
