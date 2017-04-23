package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.utils.CoursesUtils;

import org.parceler.Parcels;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by lsyang on 4/8/17.
 */
public class ExploreFragment extends Fragment {

    private static final String ARG_ALL_COURSES = "all_courses";

    private ViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private List<Course> PopularCourses;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {
            populateTopics(allCourses);
        }
        // Instantiate a ViewPager and a PagerAdapter.
        PopularCourses = CoursesUtils.getPopularCourses(allCourses);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
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
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(container, topicListFragment);
            ft.commit();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.newInstance(PopularCourses.get(position));
        }

        @Override
        public int getCount() {
            return PopularCourses.size();
        }
    }
}
