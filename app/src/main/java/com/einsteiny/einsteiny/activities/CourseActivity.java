package com.einsteiny.einsteiny.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.adapters.LessonAdapter;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.Course_Table;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.TransitionUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CourseActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE = "course";
    public static final String EXTRA_TIME = "time";
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat tdf = new SimpleDateFormat("hh:mm");


    @BindView(R.id.rvLessons)
    RecyclerView rvLessons;

    @BindView(R.id.tvCourseInfo)
    TextView tvCourseInfo;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivCourse)
    ImageView ivCourse;


    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ButterKnife.bind(this);

        supportPostponeEnterTransition();

        Course course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        long date = getIntent().getLongExtra(EXTRA_TIME, 0);

        if (date == 0) {
            date = SQLite.select().
                    from(Course.class).where(Course_Table.id.is(course.getId())).querySingle().getStartTime();
        }

        int progress = CustomUser.getProgressForCourse(course.getId());

        String courseInfo = String.format("Course %s on %s. Lessons are scheduled daily at %s.",
                progress > 0 ? "started" : "will start", sdf.format(date), tdf.format(date));
        tvCourseInfo.setText(courseInfo);

        String photoUrl = course.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            int displayWidth = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(photoUrl).resize(displayWidth, 0).into(ivCourse,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
                        }

                        @Override
                        public void onError() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
                            Log.d("Debug", "onError: error loading course image");
                        }
                    });
        } else {
            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
        }


        LinearLayoutManager lm =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLessons.setLayoutManager(lm);
        adapter = new LessonAdapter(this, course.getLessons(), date, progress);
        rvLessons.setAdapter(adapter);

        setupToolbar(course.getTitle());
    }

    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
