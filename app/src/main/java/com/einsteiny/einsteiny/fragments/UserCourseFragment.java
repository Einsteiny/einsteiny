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
import java.util.List;

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
            List<Course> courses = allCourses.getAllCourses();
            List<String> subscribedIds = CustomUser.getSubscribedCourses();
            ArrayList<Course> subscribedCourses = new ArrayList<>();
            if (subscribedIds != null && !subscribedIds.isEmpty()) {
                for (Course course : courses) {

                    if (subscribedIds.contains(course.getId())) {
                        subscribedCourses.add(course);
                    }
                }

                FragmentActivity activity = getActivity();
                CoursesListFragment topicListFragment = CoursesListFragment.newInstance("Active", subscribedCourses);
                if (activity != null) {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.activeCourses, topicListFragment);
                    ft.commit();
                }

            }


        }

    }
}
