package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/8/17.
 */
public class UserCourseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        populateUserCourses();
        return inflater.inflate(R.layout.fragment_user_course, container, false);
    }

    private void populateUserCourses() {
        //Todo: find better way to access user
        ParseUser currentUser = ParseUser.getCurrentUser();
        CustomUser customUser = new CustomUser(currentUser);
        ArrayList<Course> courses = customUser.getCourses();
        if (courses != null) {
            CoursesListFragment courseListFragment = CoursesListFragment.newInstance("In Progress", courses);
            // todo need to catch a null exception here
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.user_coureses_in_progress, courseListFragment);
            ft.commit();
        }
    }
}
