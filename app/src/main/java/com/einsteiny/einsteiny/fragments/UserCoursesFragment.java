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
import com.einsteiny.einsteiny.db.CourseDatabase;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.CoursesUtils;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;


public class UserCoursesFragment extends Fragment {

    private DatabaseDefinition database = FlowManager.getDatabase(CourseDatabase.class);

    public static UserCoursesFragment newInstance() {
        UserCoursesFragment topicListFragment = new UserCoursesFragment();
        Bundle args = new Bundle();

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

        List<Course> allCourses = new ArrayList<>();

        if (database != null) {
            allCourses = SQLite.select().
                    from(Course.class).queryList();
        }

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

