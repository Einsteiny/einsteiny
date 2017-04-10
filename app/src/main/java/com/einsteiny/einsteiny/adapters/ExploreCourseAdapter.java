package com.einsteiny.einsteiny.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.CourseSubscribeActivity;
import com.einsteiny.einsteiny.models.Course;

import java.util.ArrayList;

/**
 * Created by lsyang on 4/8/17.
 */

public class ExploreCourseAdapter extends RecyclerView.Adapter<ExploreCourseAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Course course = courses.get(getAdapterPosition());
                    Intent intent = new Intent(context, CourseSubscribeActivity.class);
                    intent.putExtra(CourseSubscribeActivity.EXTRA_COURSE, course);
                    context.startActivity(intent);
                }
            });
        }
    }

    private ArrayList<Course> courses;
    private Context context;

    public ExploreCourseAdapter(Context context, ArrayList<Course> topics) {
        this.context = context;
        this.courses = topics;
    }

    private Context getContext() {
        return context;
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
        Course topic = courses.get(position);

        TextView tvTitle = holder.tvTitle;
        tvTitle.setText(topic.getTitle());

        TextView tvDescription = holder.tvDescription;
        tvDescription.setText(topic.getDescription());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
