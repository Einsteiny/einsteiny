package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.einsteiny.einsteiny.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/21/17.
 */

public class ScreenSlidePageFragment extends Fragment {

    private static final String URL_KEY = "url";

    @BindView(R.id.popularImage)
    ImageView ivImage;

    public static ScreenSlidePageFragment newInstance(String url) {
        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();

        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
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
        String thumbnail = getArguments().getString(URL_KEY);
        Picasso.with(getContext()).load(thumbnail).into(ivImage);
    }
}

