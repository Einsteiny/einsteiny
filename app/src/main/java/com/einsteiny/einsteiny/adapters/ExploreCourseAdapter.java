package com.einsteiny.einsteiny.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Course;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lsyang on 4/8/17.
 */

public class ExploreCourseAdapter extends RecyclerView.Adapter<ExploreCourseAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;

//        @BindView(R.id.tvSnippet)
//        TextView tvDescription;

        @BindView(R.id.tvDuration)
        TextView tvDuration;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.rating)
        RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    private List<Course> courses;
    private Context context;

    public ExploreCourseAdapter(Context context, List<Course> topics) {
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
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());

        holder.tvDuration.setText(context.getResources().getQuantityString(R.plurals.days,
                course.getLessons().size(), course.getLessons().size()));

        holder.rating.setRating(course.getComplexity());

        String photoUrl = course.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Picasso.with(context).load(photoUrl).resize(200, 200).centerCrop().placeholder(R.drawable.ic_done).into(holder.ivImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            //holder.tvDescription.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            // holder.tvDescription.setText(course.getDescription());
                        }
                    });
        } else {
            //holder.tvDescription.setText(course.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
