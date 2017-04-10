package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.ExploreCourseAdapter;
import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/9/17.
 */

public class CoursesListFragment extends Fragment {

    ArrayList<Course> courses;
    ExploreCourseAdapter topicAdapter;
    RecyclerView recyclerView;

    public static CoursesListFragment newInstance(String title, ArrayList<Course> courses) {
        CoursesListFragment topicListFragment = new CoursesListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("courses", courses);
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.topic_list_recyclerview);
        recyclerView.setAdapter(topicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        TextView tvTitle = (TextView) view.findViewById(R.id.topic_list_title);
        tvTitle.setText(getArguments().getString("title"));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courses = (ArrayList<Course>) getArguments().getSerializable("courses");
        topicAdapter = new ExploreCourseAdapter(getContext(), courses);
    }

}
