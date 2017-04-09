package com.einsteiny.einsteiny.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.ExploreTopicAdapter;
import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Topic;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/9/17.
 */

public class TopicListFragment extends Fragment {

    ArrayList<Topic> topics;
    ExploreTopicAdapter topicAdapter;
    RecyclerView recyclerView;

    public static TopicListFragment newInstance(String title, ArrayList<Topic> topics) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("topics", topics);
        topicListFragment.setArguments(args);
        return topicListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.topic_list_recyclerview);
        recyclerView.setAdapter(topicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        TextView tvTitle = (TextView) view.findViewById(R.id.topic_list_title);
        tvTitle.setText(getArguments().getString("title"));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topics = (ArrayList<Topic>) getArguments().getSerializable("topics");
        topicAdapter = new ExploreTopicAdapter(getContext(), topics);
    }

}
