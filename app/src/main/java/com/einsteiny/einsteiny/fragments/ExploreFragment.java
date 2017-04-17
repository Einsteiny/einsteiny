package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.AllCourses;
import com.einsteiny.einsteiny.models.CourseCategory;

/**
 * Created by lsyang on 4/8/17.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";

    private static final String ARG_ALL_COURSES = "all_courses";

    public static ExploreFragment newInstance(AllCourses allCourses) {
        ExploreFragment topicListFragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALL_COURSES, allCourses);
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AllCourses allCourses = (AllCourses) getArguments().getSerializable(ARG_ALL_COURSES);
        if (allCourses != null) {
            populateTopics(allCourses);
        }

    }

    private void populateTopics(AllCourses allCourses) {
        getTopic(allCourses.artCourses, R.id.topic1);
        getTopic(allCourses.economicsCourses, R.id.topic2);
        getTopic(allCourses.computingCourses, R.id.topic3);
        getTopic(allCourses.scienceCourses, R.id.topic4);

    }


    public void getTopic(final CourseCategory category, final int container) {
        CoursesListFragment topicListFragment = CoursesListFragment.newInstance(category.getTitle(), category.getCourses(), CoursesListFragment.Type.NEW);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(container, topicListFragment);
            ft.commit();
        }
//
    }
}
