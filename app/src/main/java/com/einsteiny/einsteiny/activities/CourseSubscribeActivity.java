package com.einsteiny.einsteiny.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

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

    public static final String EXTRA_COURSE = "courseId";
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subscribe);

        ButterKnife.bind(this);
        String courseId = getIntent().getStringExtra(EXTRA_COURSE);
        populateCourse(courseId);
    }

    private void populateCourse(String courseId) {
        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.getInBackground(courseId, new GetCallback<Course>() {
            public void done(Course item, ParseException e) {
                if (e == null) {
                    course = item;
                    tvDescription.setText(course.getDescription());
                    tvTitle.setText(course.getTitle());

                    String photoUrl = course.getPhotoUrl();
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        int displayWidth = getResources().getDisplayMetrics().widthPixels;
                        Picasso.with(getApplicationContext()).load(photoUrl).resize(displayWidth, 0).into(ivCourse);
                    }
                }
            }
        });
    }

    public void onSubscribeCourse(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        CustomUser customUser = new CustomUser(currentUser);
        customUser.addUserCourse(course);
    }
}
