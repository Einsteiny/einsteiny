package com.einsteiny.einsteiny.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.LoginActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.squareup.picasso.Picasso;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.einsteiny.einsteiny.R.id.ivProfileImage;

/**
 * Created by lsyang on 4/8/17.
 */

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private Button btnLogout;

    private OnLogoutClickListener listener;

    public interface OnLogoutClickListener {
        public void profileLogout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
        btnLogout = (Button) view.findViewById(R.id.btnProfileLogout);

        tvProfileName.setText(ParseUser.getCurrentUser().get("name").toString());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() != null) {
                    // User clicked to log out.
                    // ParseUser.logOut();
                    listener.profileLogout();
                }
            }
        });

        // Get the user's image URL from ParseUser (or Facebook?)
        // insert to image view here
        // TODO need to get image from different FB endpoint, currently using cover image
        String imageUri = ParseUser.getCurrentUser().getString("profilePic");
        ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        Picasso.with(this.getContext()).load(imageUri).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(ivProfileImage);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnLogoutClickListener) context;
        } catch (ClassCastException castException) {
            throw new ClassCastException(context.toString()
                    + " must implement ProfileFragment.OnLogoutClickListener");
        }
    }
}
