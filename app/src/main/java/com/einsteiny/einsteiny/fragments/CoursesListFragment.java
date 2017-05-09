package com.einsteiny.einsteiny.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.adapters.ExploreCourseAdapter;
import com.einsteiny.einsteiny.course.CourseActivity;
import com.einsteiny.einsteiny.coursesubscribe.CourseSubscribeActivity;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/9/17.
 */

public class CoursesListFragment extends Fragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_COURSES = "courses";

    private List<Course> courses;
    private ExploreCourseAdapter topicAdapter;

    @BindView(R.id.rvTopics)
    RecyclerView rvTopics;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.emptyView)
    RelativeLayout emptyView;

    public static CoursesListFragment newInstance(String title, List<Course> courses) {
        CoursesListFragment topicListFragment = new CoursesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelable(ARG_COURSES, Parcels.wrap(courses));
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        courses = Parcels.unwrap(getArguments().getParcelable(ARG_COURSES));
        tvTitle.setText(getArguments().getString(ARG_TITLE));

        if (courses == null || courses.isEmpty()) {
            rvTopics.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        topicAdapter = new ExploreCourseAdapter(getContext(), courses);
        topicAdapter.setOnItemClickListener((itemView, position) -> {
            Course course = courses.get(position);
            if (CustomUser.isSubscribedCourse(course.getId()) || CustomUser.isCompletedCourse(course.getId())) {
                Intent intent = new Intent(getContext(), CourseActivity.class);
                intent.putExtra(CourseActivity.EXTRA_COURSE, Parcels.wrap(course));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), itemView.findViewById(R.id.ivImage), "courseImage");
                getContext().startActivity(intent, options.toBundle());

            } else {
                Intent intent = new Intent(getContext(), CourseSubscribeActivity.class);
                intent.putExtra(CourseSubscribeActivity.EXTRA_COURSE, Parcels.wrap(course));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), itemView.findViewById(R.id.ivImage), "courseImage");
                getContext().startActivity(intent, options.toBundle());

            }

        });

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvTopics);

        rvTopics.setAdapter(topicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvTopics.setLayoutManager(layoutManager);
    }
}
