package com.einsteiny.einsteiny.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by lsyang on 4/8/17.
 */

public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = "Einsteiny";

    private TextView tvProfileName;
    private FloatingActionButton btnLogout;
    private ToggleButton tglNotifications;
    private ToggleButton tglFacebook;
    private TextView lblConnectedWithFacebookSetting;

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
        btnLogout = (FloatingActionButton) view.findViewById(R.id.fabLogout);
        tglFacebook = (ToggleButton) view.findViewById(R.id.tglConnectFacebook);
        lblConnectedWithFacebookSetting = (TextView) view.findViewById(R.id.lblConnectedWithFacebookSetting);

        tvProfileName.setText(ParseUser.getCurrentUser().get("name").toString());

        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
            tglFacebook.setChecked(true);
        } else {
            tglFacebook.setChecked(false);
            lblConnectedWithFacebookSetting.setText(R.string.connect_with_facebook);
            tglFacebook.setEnabled(false);
        }

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

        tglFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    lblConnectedWithFacebookSetting.setText("Log out and log in with Facebook to connect");
                } else {
                    // The toggle is disabled
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex == null) {
                                    Log.d("MyApp", "The user is no longer associated with their Facebook account.");
                                    Toast.makeText(getContext(), "Disconnecting Facebook", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        tglFacebook.setChecked(false);
                        tglFacebook.setEnabled(false);
                        lblConnectedWithFacebookSetting.setText(R.string.connect_with_facebook);
                    }
                }

            }
        });

        // Get the user's image URL from ParseUser (profilePic from Facebook if account linked)
        // insert to image view here
        // TODO have the user set fbID
        if (ParseUser.getCurrentUser() != null && ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            // TODO check for FB or profile_pic
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                            try {
                                ProfilePictureView profilePictureView;
                                profilePictureView = (ProfilePictureView) view.findViewById(R.id.friendProfilePicture);
                                profilePictureView.setProfileId(response.getJSONObject().get("id").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            request.executeAsync();
        }
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


