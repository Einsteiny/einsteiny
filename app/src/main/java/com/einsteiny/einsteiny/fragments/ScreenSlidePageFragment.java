package com.einsteiny.einsteiny.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.CourseSubscribeActivity;
import com.einsteiny.einsteiny.models.Course;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/21/17.
 */

public class ScreenSlidePageFragment extends Fragment {

    private static final String COURSE_KEY = "course";

    @BindView(R.id.popularImage)
    ImageView ivImage;

    public static ScreenSlidePageFragment newInstance(Course course) {
        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();

        Bundle args = new Bundle();
        args.putParcelable(COURSE_KEY, Parcels.wrap(course));
        screenSlidePageFragment.setArguments(args);
        return screenSlidePageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        Course course = Parcels.unwrap(getArguments().getParcelable(COURSE_KEY));
        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        Picasso.with(getContext()).load(course.getPhotoUrl()).resize(displayWidth, 0).into(ivImage);

        ivImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CourseSubscribeActivity.class);
                intent.putExtra(CourseSubscribeActivity.EXTRA_COURSE, Parcels.wrap(course));
                Pair<View, String> p1 = Pair.create(ivImage, "courseImage");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1);
                getContext().startActivity(intent, options.toBundle());
            }
        });
    }
}

