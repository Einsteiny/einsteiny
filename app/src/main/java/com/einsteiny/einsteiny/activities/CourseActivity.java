package com.einsteiny.einsteiny.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.adapters.LessonAdapter;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.Course_Table;
import com.einsteiny.einsteiny.models.CustomUser;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE = "course";
    public static final String EXTRA_TIME = "time";


    @BindView(R.id.rvLessons)
    RecyclerView rvLessons;

    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ButterKnife.bind(this);

        Course course = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        long date = getIntent().getLongExtra(EXTRA_TIME, 0);

        if (date == 0) {
            date = SQLite.select().
                    from(Course.class).where(Course_Table.id.is(course.getId())).querySingle().getStartTime();
        }

        int progress = CustomUser.getProgressForCourse(course.getId());

        LinearLayoutManager lm =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLessons.setLayoutManager(lm);
        adapter = new LessonAdapter(this, course.getLessons(), date, progress);
        rvLessons.setAdapter(adapter);
    }
}
