package com.einsteiny.einsteiny.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.CourseSubscribeActivity;
import com.einsteiny.einsteiny.adapters.ExploreCourseAdapter;
import com.einsteiny.einsteiny.models.Course;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/9/17.
 */

public class CoursesListFragment extends Fragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_COURSES = "courses";

    private ArrayList<Course> courses;
    private ExploreCourseAdapter topicAdapter;

    @BindView(R.id.rvTopics)
    RecyclerView rvTopics;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    public static CoursesListFragment newInstance(String title, ArrayList<Course> courses) {
        CoursesListFragment topicListFragment = new CoursesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_COURSES, courses);
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

        courses = (ArrayList<Course>) getArguments().getSerializable(ARG_COURSES);
        topicAdapter = new ExploreCourseAdapter(getContext(), courses);
        topicAdapter.setOnItemClickListener(new ExploreCourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Course course = courses.get(position);
                Intent intent = new Intent(getContext(), CourseSubscribeActivity.class);
                intent.putExtra(CourseSubscribeActivity.EXTRA_COURSE, course);
                Pair<View, String> p1 = Pair.create(itemView.findViewById(R.id.ivImage), "courseImage");
                Pair<View, String> p2 = Pair.create(itemView.findViewById(R.id.tvTitle), "courseText");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1, p2);
                getContext().startActivity(intent, options.toBundle());
            }
        });

        rvTopics.setAdapter(topicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTopics.setLayoutManager(layoutManager);

        tvTitle.setText(getArguments().getString(ARG_TITLE));


    }


}
