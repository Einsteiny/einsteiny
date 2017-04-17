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
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/8/17.
 */
public class UserCourseFragment extends Fragment {

    private static final String ARG_ALL_COURSES = "all_courses";


    public static UserCourseFragment newInstance(AllCourses allCourses) {
        UserCourseFragment topicListFragment = new UserCourseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALL_COURSES, allCourses);
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_course, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AllCourses allCourses = (AllCourses) getArguments().getSerializable(ARG_ALL_COURSES);
        if (allCourses != null) {

            ArrayList<Course> subscribedCourses = (ArrayList<Course>) allCourses.getCoursesForIds(CustomUser.getSubscribedCourses());
            ArrayList<Course> completedCourses = (ArrayList<Course>) allCourses.getCoursesForIds(CustomUser.getCompletedCourses());


            FragmentActivity activity = getActivity();
            CoursesListFragment activeListFragment = CoursesListFragment.newInstance("Active", subscribedCourses, CoursesListFragment.Type.ALREADY_SUBSCRIBED);
            CoursesListFragment completedListFragment = CoursesListFragment.newInstance("Completed", completedCourses, CoursesListFragment.Type.ALREADY_SUBSCRIBED);
            if (activity != null) {
                FragmentTransaction ftActive = activity.getSupportFragmentManager().beginTransaction();
                ftActive.replace(R.id.activeCourses, activeListFragment);
                ftActive.commit();

                FragmentTransaction ftCompleted = activity.getSupportFragmentManager().beginTransaction();
                ftCompleted.replace(R.id.completedCourses, completedListFragment);
                ftCompleted.commit();
            }

        }

    }

}

