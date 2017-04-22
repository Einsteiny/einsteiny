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
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.utils.CoursesUtils;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by lsyang on 4/8/17.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";

    private static final String ARG_ALL_COURSES = "all_courses";

    public static ExploreFragment newInstance(List<Course> allCourses) {
        ExploreFragment topicListFragment = new ExploreFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ALL_COURSES, Parcels.wrap(allCourses));
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

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {
            populateTopics(allCourses);
        }

    }

    private void populateTopics(List<Course> allCourses) {
        getTopic("Arts", CoursesUtils.getCoursesForCategory(allCourses, "Arts"), R.id.topic1);
        getTopic("Economics & finance", CoursesUtils.getCoursesForCategory(allCourses, "Economics & finance"), R.id.topic2);
        getTopic("Computing", CoursesUtils.getCoursesForCategory(allCourses, "Computing"), R.id.topic3);
        getTopic("Science", CoursesUtils.getCoursesForCategory(allCourses, "Science"), R.id.topic4);

    }


    public void getTopic(String category, final List<Course> courses, final int container) {
        CoursesListFragment topicListFragment = CoursesListFragment.newInstance(category, courses, CoursesListFragment.Type.NEW);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(container, topicListFragment);
            ft.commit();
        }
//
    }
}
