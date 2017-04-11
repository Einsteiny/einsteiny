package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lsyang on 4/8/17.
 */
public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        populateTopics();
        return view;
    }

    private void populateTopics() {
        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.findInBackground(new FindCallback<Course>() {
            public void done(List<Course> courses, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    CoursesListFragment topicListFragment = CoursesListFragment.newInstance("humanities",
                            (ArrayList<Course>) courses);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.topic1, topicListFragment);
                    ft.commit();

                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });

//        getTopic("humanities", R.id.topic1);
//        getTopic("economics-finance-domain", R.id.topic2);
//        getTopic("computing", R.id.topic3);
//        getTopic("science", R.id.topic4);

    }

    public void getTopic(final String topic_slug, final int container) {
        // Depricated: can use this method to save new courses in Course table
        String url = "https://einsteiny.herokuapp.com/" + topic_slug;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String title = response.getString("standalone_title");
                    ArrayList<Course> courses = Course.fromJSONArray(response.getJSONArray("children"));
                    CoursesListFragment topicListFragment = CoursesListFragment.newInstance(title, courses);
                    // todo need to catch a null exception here
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(container, topicListFragment);
                    ft.commit();

                    Log.d(TAG, "onSuccess: " + courses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.d(TAG, "onFailure: " + errorResponse);
            }
        });
    }
}
