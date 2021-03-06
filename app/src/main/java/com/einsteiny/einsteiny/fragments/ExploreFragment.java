package com.einsteiny.einsteiny.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.SeeAllCoursesActivity;
import com.einsteiny.einsteiny.db.CourseDatabase;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.utils.CoursesUtils;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;


/**
 * Created by lsyang on 4/8/17.
 */
public class ExploreFragment extends Fragment {

    private DatabaseDefinition database = FlowManager.getDatabase(CourseDatabase.class);

    @BindView(R.id.topic1SeeAll)
    TextView topic1SeeAll;

    @BindView(R.id.topic2SeeAll)
    TextView topic2SeeAll;

    @BindView(R.id.topic3SeeAll)
    TextView topic3SeeAll;

    private ViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private List<Course> popularCourses;
    Handler handler;
    int page;
    private int delay = 3000; //milliseconds

    Runnable runnable = new Runnable() {
        public void run() {
            page = mPager.getCurrentItem();
            if (mPagerAdapter.getCount() == page + 1) {
                page = 0;
            } else {
                page++;
            }
            mPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };

    public static ExploreFragment newInstance() {
        ExploreFragment topicListFragment = new ExploreFragment();
        Bundle args = new Bundle();

        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        ButterKnife.bind(this, view);
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

        List<Course> allCourses = new ArrayList<>();

        if (database != null) {
            allCourses = SQLite.select().
                    from(Course.class).queryList();
        }

        if (allCourses != null) {
            List<Course> artCourses = CoursesUtils.getCoursesForCategory(allCourses, "Arts");
            List<Course> enterCourses = CoursesUtils.getCoursesForCategory(allCourses, "Entrepreneurship");
            List<Course> compCourses = CoursesUtils.getCoursesForCategory(allCourses, "Computing & Science");
            getTopic("Arts", artCourses, R.id.topic1);
            getTopic("Entrepreneurship", enterCourses, R.id.topic2);
            getTopic("Computing & Science", compCourses, R.id.topic3);
//        getTopic("US History", CoursesUtils.getCoursesForCategory(allCourses, "US History"), R.id.topic4);

            topic1SeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SeeAllCoursesActivity.class);
                intent.putExtra(SeeAllCoursesActivity.EXTRA_TITLE, "Arts");
                intent.putExtra(SeeAllCoursesActivity.ALL_COURSES, Parcels.wrap(artCourses));
                startActivity(intent);
            });

            topic2SeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SeeAllCoursesActivity.class);
                intent.putExtra(SeeAllCoursesActivity.EXTRA_TITLE, "Entrepreneurship");
                intent.putExtra(SeeAllCoursesActivity.ALL_COURSES, Parcels.wrap(enterCourses));
                startActivity(intent);
            });

            topic3SeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SeeAllCoursesActivity.class);
                intent.putExtra(SeeAllCoursesActivity.EXTRA_TITLE, "Computing & Science");
                intent.putExtra(SeeAllCoursesActivity.ALL_COURSES, Parcels.wrap(compCourses));
                startActivity(intent);
            });
        }

        // Instantiate a ViewPager and a PagerAdapter.
        handler = new Handler();
        popularCourses = CoursesUtils.getPopularCourses(allCourses);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);


    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void populateTopics(List<Course> allCourses) {


    }


    public void getTopic(String category, final List<Course> courses, final int container) {
        CoursesListFragment topicListFragment = CoursesListFragment.newInstance(category, courses);
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
            return PopularCourseBannerFragment.newInstance(popularCourses.get(position));
        }

        @Override
        public int getCount() {
            return popularCourses.size();
        }
    }
}
