package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.fragments.SelectTimeDialog;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.models.Lesson;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseSubscribeActivity extends AppCompatActivity implements SelectTimeDialog.ConfirmSubscriptionListener {

    @BindView(R.id.ivCourse)
    ImageView ivCourse;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.btnSubscribe)
    Button btnSubscribe;

    public static final String EXTRA_COURSE = "course";

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subscribe);

        ButterKnife.bind(this);

        course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);

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
                            scheduleStartPostponedTransition(ivCourse);
                        }

                        @Override
                        public void onError() {
                            scheduleStartPostponedTransition(ivCourse);
                            Log.d("Debug", "onError: error loading course image");
                        }
                    });
        } else {
            scheduleStartPostponedTransition(ivCourse);
        }

        btnSubscribe.setOnClickListener(v -> {

            FragmentManager fm = getSupportFragmentManager();
            SelectTimeDialog dialog = SelectTimeDialog.newInstance();
            dialog.show(fm, "fragment_filter_dialog");


        });
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
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
        i.putExtra(CourseActivity.EXTRA_COURSE, course);
        i.putExtra(CourseActivity.EXTRA_TIME, cal.getTimeInMillis());

        //save course with start time
        course.save();


        for (Lesson lesson : course.getLessons()) {
            sendParseNotification(course.getId(), cal.getTimeInMillis());
            cal.add(Calendar.DATE, 1);
        }

        startActivity(i);

    }
}
