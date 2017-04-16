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
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.LoginActivity;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import org.w3c.dom.Text;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by lsyang on 4/8/17.
 */

public class ProfileFragment extends Fragment {

    private TextView tvTitle;
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

        tvTitle = (TextView) view.findViewById(R.id.tvProfileName);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);

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