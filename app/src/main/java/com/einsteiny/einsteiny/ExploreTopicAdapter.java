package com.einsteiny.einsteiny;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.models.Topic;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/8/17.
 */

public class ExploreTopicAdapter extends RecyclerView.Adapter<ExploreTopicAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }

    private ArrayList<Topic> _topics;
    private Context _context;

    public ExploreTopicAdapter(Context context, ArrayList<Topic> topics) {
        _context = context;
        _topics = topics;
    }

    private Context getContext() {
        return _context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View topicView = inflater.inflate(R.layout.explore_item_topic, parent, false);
        ViewHolder viewHolder = new ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic topic = _topics.get(position);

        TextView tvTitle = holder.tvTitle;
        tvTitle.setText(topic.getTitle());

        TextView tvDescription = holder.tvDescription;
        tvDescription.setText(topic.getDescription());
    }

    @Override
    public int getItemCount() {
        return _topics.size();
    }
}
