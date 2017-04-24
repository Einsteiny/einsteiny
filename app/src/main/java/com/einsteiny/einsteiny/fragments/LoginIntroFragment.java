package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/21/17.
 */

public class LoginIntroFragment extends Fragment {

    private static final String TEXT_EXTRA = "text";
    private static final String DRAWABLE_EXTRA = "drawable";

    @BindView(R.id.popularImage)
    ImageView ivImage;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    public static LoginIntroFragment newInstance(String text, int drawable) {
        LoginIntroFragment popularCourseBannerFragment = new LoginIntroFragment();

        Bundle args = new Bundle();
        args.putString(TEXT_EXTRA, text);
        args.putInt(DRAWABLE_EXTRA, drawable);

        popularCourseBannerFragment.setArguments(args);
        return popularCourseBannerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_login_intro, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        tvTitle.setText(getArguments().getString(TEXT_EXTRA));
        ivImage.setImageDrawable(getResources().getDrawable(getArguments().getInt(DRAWABLE_EXTRA)));

    }
}

