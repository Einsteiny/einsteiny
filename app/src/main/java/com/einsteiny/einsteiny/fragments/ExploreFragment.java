package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einsteiny.einsteiny.ExploreTopicAdapter;
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

    ArrayList<Topic> topics;
    ExploreTopicAdapter topicAdapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.explore_recyclerview);
        recyclerView.setAdapter(topicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        getTopic("humanities");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topics = new ArrayList<>();
        topicAdapter = new ExploreTopicAdapter(getContext(), topics);
    }

    public void getTopic(String topic_slug) {

        String url = "https://www.khanacademy.org/api/v1/topic/" + topic_slug;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    topics.addAll(fromJsonArray(response.getJSONArray("children")));
                    topicAdapter.notifyDataSetChanged();
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
