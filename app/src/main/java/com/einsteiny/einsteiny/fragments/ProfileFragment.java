package com.einsteiny.einsteiny.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.CoursesUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import static com.einsteiny.einsteiny.utils.CoursesUtils.getCoursesForIds;


/**
 * Created by lsyang on 4/8/17.
 */

public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = "Einsteiny";
    private static final String ARG_ALL_COURSES = "all_courses";

    private TextView tvProfileName;
    private FloatingActionButton btnLogout;
    private ToggleButton tglNotifications;
    private ToggleButton tglFacebook;
    private TextView lblConnectedWithFacebookSetting;

    private OnLogoutClickListener listener;

    public static ProfileFragment newInstance(List<Course> allCourses) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ALL_COURSES, Parcels.wrap(allCourses));
        profileFragment.setArguments(args);
        return profileFragment;
    }

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

        //it was empty for me, retrieve name in the same way as pic
        //tvProfileName.setText(ParseUser.getCurrentUser().get("name").toString());

        if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            tglFacebook.setChecked(true);
        } else {
            tglFacebook.setChecked(false);
            lblConnectedWithFacebookSetting.setText(R.string.connect_with_facebook);
            tglFacebook.setEnabled(false);
        }

        // Get the course topics to show user what they have been interested in
        // Note: prefer the below over "String words[]"
        String[] categories = {"Arts", "Economics & finance", "Computing", "Science"};
        String[] colors = {"#FE6DA8", "#56B7F1", "#CDA67F", "#FED70E"};
        Multiset<String> categoryMap = HashMultiset.create(Arrays.asList(categories));
        List<Course> userCourses = null;

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {

            userCourses = getCoursesForIds(allCourses, CustomUser.getSubscribedCourses());
            userCourses.addAll(getCoursesForIds(allCourses, CustomUser.getCompletedCourses()));
            userCourses.addAll(getCoursesForIds(allCourses, CustomUser.getLikedCourses()));
        }

        if (userCourses != null) {
            for (int x = 0; x < userCourses.size(); x++) {
                // Build the map of categories for courses
                categoryMap.add(userCourses.get(x).getCategory().toString(), 1);
            }

        }

        PieChart mPieChart = (PieChart) view.findViewById(R.id.piechart);

        int color_count = 0;
        // Put the counts into PieChart
        for (Multiset.Entry<String> entry : categoryMap.entrySet()) {
            System.out.println(entry.getElement() + ": " + entry.getCount());
            mPieChart.addPieSlice(new PieModel(entry.getElement(), entry.getCount(), Color.parseColor(colors[color_count])));
            color_count++;
        }

        mPieChart.startAnimation();

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
                                tvProfileName.setText(response.getJSONObject().get("name").toString());
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


