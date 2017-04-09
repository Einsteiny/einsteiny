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
import com.einsteiny.einsteiny.models.Topic;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.einsteiny.einsteiny.models.Topic.fromJsonArray;

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
//        String[] topicList = new String[] {"humanities"};
//        for (int i = 0; i < topicList.length; i++) {
//            getTopic(topicList[i], R.id.topic1);
//        }
        return view;
    }

    private void populateTopics() {
        getTopic("humanities", R.id.topic1);
        getTopic("economics-finance-domain", R.id.topic2);
        getTopic("computing", R.id.topic3);
        getTopic("science", R.id.topic4);

    }

    public void getTopic(final String topic_slug, final int container) {
        String url = "https://www.khanacademy.org/api/v1/topic/" + topic_slug;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Topic> topics = fromJsonArray(response.getJSONArray("children"));
                    String title = response.getString("standalone_title");
                    TopicListFragment topicListFragment = TopicListFragment.newInstance(title, topics);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(container, topicListFragment);
                    ft.commit();

                    Log.d(TAG, "onSuccess: " + topics);
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
