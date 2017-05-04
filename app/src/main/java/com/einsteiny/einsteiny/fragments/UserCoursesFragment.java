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
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.CoursesUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class UserCoursesFragment extends Fragment {

    private static final String ARG_ALL_COURSES = "all_courses";


    public static UserCoursesFragment newInstance(List<Course> allCourses) {
        UserCoursesFragment topicListFragment = new UserCoursesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ALL_COURSES, Parcels.wrap(allCourses));
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

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {

            List<Course> subscribedCourses = CoursesUtils.getCoursesForIds(allCourses, CustomUser.getSubscribedCourses());
            List<Course> completedCourses = CoursesUtils.getCoursesForIds(allCourses, CustomUser.getCompletedCourses());

            List<Course> userCourses = new ArrayList<>();
            userCourses.addAll(subscribedCourses);
            userCourses.addAll(completedCourses);


            FragmentActivity activity = getActivity();
            CoursesVerticalFragment userCoursesFragment = CoursesVerticalFragment.newInstance(userCourses);

            if (activity != null) {
                FragmentTransaction ftActive = getChildFragmentManager().beginTransaction();
                ftActive.replace(R.id.userCourses, userCoursesFragment);
                ftActive.commit();
            }
        }

    }

}

