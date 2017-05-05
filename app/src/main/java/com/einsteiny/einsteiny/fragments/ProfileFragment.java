package com.einsteiny.einsteiny.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.CoursesUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.parceler.Parcels;

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

    private OnLogoutClickListener listener;

    public static ProfileFragment newInstance(List<Course> allCourses) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_ALL_COURSES, Parcels.wrap(allCourses));
        profileFragment.setArguments(args);
        return profileFragment;
    }

    public interface OnLogoutClickListener {
        void profileLogout();
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

        //it was empty for me, retrieve name in the same way as pic
        //tvProfileName.setText(ParseUser.getCurrentUser().get("name").toString());

        // Get the course topics to show user what they have been interested in
        // Note: prefer the below over "String words[]"
        String[] categories = {"Arts", "Economics & finance", "Computing", "Science"};
        String[] colors = {"#FE6DA8", "#56B7F1", "#7E57C2", "#FED70E"};
        List<Course> userCourses = null;

        List<Course> allCourses = Parcels.unwrap(getArguments().getParcelable(ARG_ALL_COURSES));
        if (allCourses != null) {

            userCourses = getCoursesForIds(allCourses, CustomUser.getSubscribedCourses());
            List<Course> completedCourses = getCoursesForIds(allCourses, CustomUser.getCompletedCourses());
            List<Course> likedCourses = getCoursesForIds(allCourses, CustomUser.getLikedCourses());

            for (Course course : completedCourses) {
                if (!userCourses.contains(course)) {
                    userCourses.add(course);
                }
            }

            for (Course course : likedCourses) {
                if (!userCourses.contains(course)) {
                    userCourses.add(course);
                }
            }
        }

        PieChart mPieChart = (PieChart) view.findViewById(R.id.piechart);


        // Put the counts into PieChart
        for (int i = 0; i < categories.length; i++) {
            List<Course> coursesForCategory = CoursesUtils.getCoursesForCategory(userCourses, categories[i]);
            if (coursesForCategory != null) {
                mPieChart.addPieSlice(new PieModel(categories[i], coursesForCategory.size(),
                        Color.parseColor(colors[i])));
            }
        }

        mPieChart.startAnimation();

        btnLogout.setOnClickListener(v -> {
            if (ParseUser.getCurrentUser() != null) {
                // User clicked to log out.
                // ParseUser.logOut();
                listener.profileLogout();
            }
        });

        // Get the user's image URL from ParseUser (profilePic from Facebook if account linked)
        // insert to image view here
        // TODO have the user set fbID
        if (ParseUser.getCurrentUser() != null && ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            // TODO check for FB or profile_pic
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    (jsonObject, response) -> {
                        try {
                            ProfilePictureView profilePictureView;
                            profilePictureView = (ProfilePictureView) view.findViewById(R.id.friendProfilePicture);
                            profilePictureView.setProfileId(response.getJSONObject().get("id").toString());
                            tvProfileName.setText(response.getJSONObject().get("name").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
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


