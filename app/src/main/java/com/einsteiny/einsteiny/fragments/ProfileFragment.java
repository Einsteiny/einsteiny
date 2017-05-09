package com.einsteiny.einsteiny.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.db.CourseDatabase;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.CoursesUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.einsteiny.einsteiny.utils.CoursesUtils.getCoursesForIds;


/**
 * Created by lsyang on 4/8/17.
 */

public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = "Einsteiny";

    private DatabaseDefinition database = FlowManager.getDatabase(CourseDatabase.class);

    @BindView(R.id.tvProfileName)
    TextView tvProfileName;

    @BindView(R.id.fabLogout)
    FloatingActionButton btnLogout;

    @BindView(R.id.piechart)
    PieChart mPieChart;

    @BindView(R.id.tvProgressInfo)
    TextView progressInfo;

    private OnLogoutClickListener listener;

    private String showBulletsText = "";
    private SpannableStringBuilder mSSBuilder;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();

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
        ButterKnife.bind(this, view);

        //it was empty for me, retrieve name in the same way as pic
        //tvProfileName.setText(ParseUser.getCurrentUser().get("name").toString());

        // Get the course topics to show user what they have been interested in
        // Note: prefer the below over "String words[]"


        String[] categories = {"Arts", "Entrepreneurship", "Computing & Science", "US History"};
        String[] colors = {"#FE6DA8", "#56B7F1", "#7E57C2", "#FED70E"};

        List<Course> userCourses = new ArrayList<>();
        List<Course> allCourses = new ArrayList<>();

        if (database != null) {
            allCourses = SQLite.select().
                    from(Course.class).queryList();
        }
        if (allCourses != null) {
            List<Course> subscribedCourses = getCoursesForIds(allCourses, CustomUser.getSubscribedCourses());
            List<Course> completedCourses = getCoursesForIds(allCourses, CustomUser.getCompletedCourses());
            List<Course> likedCourses = getCoursesForIds(allCourses, CustomUser.getLikedCourses());

//            String coursePluralsCompleted = getContext().getResources().getQuantityString(R.plurals.courses,
//                    completedCourses.size());
//            String coursePluralsSubsc = getContext().getResources().getQuantityString(R.plurals.courses,
//                    subscribedCourses.size());
//            String coursePluralsLiked = getContext().getResources().getQuantityString(R.plurals.courses,
//                    likedCourses.size());

            String bullet1 = String.format("%s Completed", completedCourses.size());
            String bullet2 = String.format("%s Subscribed", subscribedCourses.size());
            String bullet3 = String.format("%s Liked", likedCourses.size());
            showBulletsText = String.format("%s \n%s \n%s ", bullet1, bullet2, bullet3);

            mSSBuilder = new SpannableStringBuilder(showBulletsText);

            // Generate bulleted list
            showBullet(bullet1);
            showBullet(bullet2);
            showBullet(bullet3);

            progressInfo.setText(mSSBuilder);


            userCourses.addAll(subscribedCourses);
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


            // Put the counts into PieChart
            for (int i = 0; i < categories.length; i++) {
                List<Course> coursesForCategory = CoursesUtils.getCoursesForCategory(userCourses, categories[i]);
                if (coursesForCategory != null) {
                    mPieChart.addPieSlice(new PieModel(categories[i], coursesForCategory.size(),
                            Color.parseColor(colors[i])));
                }
            }

            mPieChart.startAnimation();
        }

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
        } else {
            String name = (String) ParseUser.getCurrentUser().get("name");
            if (name != null && !name.isEmpty()) {
                tvProfileName.setText(name);
            }
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

    protected void showBullet(String textToBullet) {
        // Initialize a new BulletSpan
        BulletSpan bulletSpan = new BulletSpan(
                50, // Gap width
                getResources().getColor(R.color.colorPrimary) // Color for Bullet
        );

        // Apply the bullet to the span
        mSSBuilder.setSpan(
                bulletSpan, // Span to add
                showBulletsText.indexOf(textToBullet), // Start of the span (inclusive)
                showBulletsText.indexOf(textToBullet) + 1,  // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );
    }

}


