package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTopics();
    }

    private void populateTopics() {
        getTopic("humanities", R.id.topic1);
        getTopic("economics-finance-domain", R.id.topic2);
        getTopic("computing", R.id.topic3);
        getTopic("science", R.id.topic4);

    }


    public void getTopic(final String topic_slug, final int container) {
//        String url = "https://www.khanacademy.org/api/v1/topic/" + topic_slug;
        //send request to our own einsteiny backend
        String url = "https://einsteiny.herokuapp.com/" + topic_slug;
        AsyncHttpClient client = new AsyncHttpClient();
        final FragmentActivity activity = getActivity();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Course> courses = Course.fromJSONArray(response.getJSONArray("courses"));
                    String title = response.getString("title");
                    CoursesListFragment topicListFragment = CoursesListFragment.newInstance(title, courses);
                    // todo need to catch a null exception here
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
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
