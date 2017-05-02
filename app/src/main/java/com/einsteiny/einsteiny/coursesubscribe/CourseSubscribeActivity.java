package com.einsteiny.einsteiny.coursesubscribe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.course.CourseActivity;
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
import org.parceler.Parcels;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseSubscribeActivity extends AppCompatActivity implements SelectTimeDialog.ConfirmSubscriptionListener,
        ResubscribeDialogAlertFragment.SubscribeCourseListener, CourseSubscribeContract.View {

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

    @BindView(R.id.tvDuration)
    TextView tvDuration;

    @BindView(R.id.rating)
    RatingBar rating;

    private Transition.TransitionListener mEnterTransitionListener;

    private CourseSubscribeContract.Presenter mPresenter;


    public static final String EXTRA_COURSE = "course";

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subscribe);

        ButterKnife.bind(this);

        course = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_COURSE));

        new CourseSubscribePresenter(course, this, this);
        mPresenter.start();


        //check if user already subscribed for the course
        fab.setOnClickListener(v -> {
            if (CustomUser.isSubscribedCourse(course.getId())) {
                FragmentManager fm = getSupportFragmentManager();
                ResubscribeDialogAlertFragment dialog = ResubscribeDialogAlertFragment.newInstance(course);
                dialog.show(fm, "resubscribe_dialog");

            } else {
                FragmentManager fm = getSupportFragmentManager();
                SelectTimeDialog dialog = SelectTimeDialog.newInstance();
                dialog.show(fm, "subscribe_dialog");

            }

        });


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
        i.putExtra(CourseActivity.EXTRA_COURSE, Parcels.wrap(course));

        //save course with start time
        CustomUser.addDatesForCourse(course.getId(), cal.getTimeInMillis());


        for (Lesson lesson : course.getLessons()) {
            sendParseNotification(course.getId(), cal.getTimeInMillis());
            cal.add(Calendar.DATE, 1);
        }

        startActivity(i);
    }

    @Override
    public void initToolbar(String title) {
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


    @Override
    public void onBackPressed() {
        mPresenter.exitReveal(fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                mPresenter.exitReveal(fab);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setFabInvisible() {
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setPresenter(CourseSubscribeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initTvDescription(String description) {
        tvDescription.setText(description);
    }

    @Override
    public void initTvTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void initTvDuration(String duration) {
        tvDuration.setText(Html.fromHtml(duration));
    }

    @Override
    public void setRating(float complexity) {
        rating.setRating(complexity);
    }

    @Override
    public void setSupportPostponeEnterTransition() {
        supportPostponeEnterTransition();
    }

    @Override
    public void loadCourseImage(String photoUrl) {
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

    }

    @Override
    public void initTransitionListener() {
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mPresenter.enterReveal(fab);
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

    }

    @Override
    public void addListener() {
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);

    }

    @Override
    public void removeListener() {
        getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
    }

    @Override
    public void callFinishAfterTransition() {
        supportFinishAfterTransition();
    }
}
