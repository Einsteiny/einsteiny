package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.adapters.ExploreCourseGridAdapter;
import com.einsteiny.einsteiny.course.CourseActivity;
import com.einsteiny.einsteiny.coursesubscribe.CourseSubscribeActivity;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class SeeAllCoursesActivity extends AppCompatActivity {

    public static final String ALL_COURSES = "all_courses";

    private List<Course> courses;
    private ExploreCourseGridAdapter topicAdapter;

    @BindView(R.id.rvTopics)
    RecyclerView rvTopics;


    @BindView(R.id.emptyView)
    RelativeLayout emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_courses);

        ButterKnife.bind(this);

        courses = Parcels.unwrap(getIntent().getParcelableExtra(ALL_COURSES));

        if (courses == null || courses.isEmpty()) {
            rvTopics.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        topicAdapter = new ExploreCourseGridAdapter(getContext(), courses);
        topicAdapter.setOnItemClickListener((itemView, position) -> {
            Course course = courses.get(position);
            if (CustomUser.isSubscribedCourse(course.getId())) {
                Intent intent = new Intent(getContext(), CourseActivity.class);
                intent.putExtra(CourseActivity.EXTRA_COURSE, Parcels.wrap(course));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, itemView.findViewById(R.id.ivImage), "courseImage");
                startActivity(intent, options.toBundle());

            } else {
                Intent intent = new Intent(getContext(), CourseSubscribeActivity.class);
                intent.putExtra(CourseSubscribeActivity.EXTRA_COURSE, Parcels.wrap(course));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, itemView.findViewById(R.id.ivImage), "courseImage");
                startActivity(intent, options.toBundle());

            }

        });

        rvTopics.setAdapter(topicAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvTopics.setLayoutManager(layoutManager);
    }
}
