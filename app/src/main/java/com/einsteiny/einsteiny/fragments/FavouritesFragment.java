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

public class FavouritesFragment extends Fragment {

    private static final String ARG_ALL_COURSES = "all_courses";

    public static FavouritesFragment newInstance(List<Course> allCourses) {
        FavouritesFragment topicListFragment = new FavouritesFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ALL_COURSES, Parcels.wrap(allCourses));
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favourites_fragment, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {
            List<Course> favouritesCourses = CoursesUtils.getFavouritesCourses(allCourses);
            CoursesVerticalFragment topicListFragment = CoursesVerticalFragment.newInstance(favouritesCourses);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.favourites, topicListFragment);
                ft.commit();
            }
        }
    }

}
