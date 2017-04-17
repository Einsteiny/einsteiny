package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.models.Lesson;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseSubscribeActivity extends AppCompatActivity {

    @BindView(R.id.ivCourse)
    ImageView ivCourse;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.btnSubscribe)
    Button btnSubscribe;

    public static final String EXTRA_COURSE = "course";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subscribe);

        ButterKnife.bind(this);

        final Course course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);

        tvDescription.setText(course.getDescription());
        tvTitle.setText(course.getTitle());

        String photoUrl = course.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            int displayWidth = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(photoUrl).resize(displayWidth, 0).into(ivCourse);
        }

        btnSubscribe.setOnClickListener(v -> {
            CustomUser.addSubscribedCourse(course);

            Intent i = new Intent(CourseSubscribeActivity.this, CourseActivity.class);
            i.putExtra(CourseActivity.EXTRA_COURSE, course);

            for (Lesson lesson : course.getLessons()) {
                sendParseNotification(lesson.getVideoUrl());
            }


            startActivity(i);

        });


    }

    private void sendParseNotification(String videoUrl) {
        JSONObject payload = new JSONObject();

        try {
            payload.put("sender", ParseInstallation.getCurrentInstallation().getInstallationId());
            payload.put("course", videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());

        ParseCloud.callFunctionInBackground("subscribe", data);
    }
}
