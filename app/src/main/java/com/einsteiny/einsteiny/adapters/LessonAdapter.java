package com.einsteiny.einsteiny.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.PlayYoutubeActivity;
import com.einsteiny.einsteiny.models.Lesson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private Context context;
    private long startTime;
    private int progress;
    private boolean completed;

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

    public LessonAdapter(Context context, List<Lesson> Lessons, long date, int progress, boolean completed) {
        this.context = context;
        this.lessons = Lessons;
        this.startTime = date;
        this.progress = progress;
        this.completed = completed;
    }


    @Override
    public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_lesson_with_image, parent, false);
        return new LessonViewHolder(view);
    }


    @Override
    public void onBindViewHolder(LessonViewHolder viewHolder, int position) {
        Lesson lesson = lessons.get(position);

        viewHolder.tvTitle.setText(lesson.getTitle());
        viewHolder.tvSnippet.setText(lesson.getDescription());
        viewHolder.tvDescription.setText("Lesson " + (position + 1));

        Calendar cal = Calendar.getInstance().getInstance();
        cal.setTimeInMillis(startTime);
        cal.add(Calendar.DATE, position);
        viewHolder.tvDate.setText(sdf.format(cal.getTime()));


        String thumbnail = lesson.getImageUrl();
        if (!TextUtils.isEmpty(thumbnail)) {
            //Measure parent width
            int displayWidth = context.getResources().getDisplayMetrics().widthPixels;

            Picasso.with(context).load(thumbnail).into(viewHolder.ivImage);
        }


        if (!completed && position >= progress) {
            viewHolder.cardView.setAlpha(0.5f);
            viewHolder.statusInfo.setText("Scheduled");
            viewHolder.statusInfo.setTextColor(context.getResources().getColor(R.color.lesson_scheduled));
            viewHolder.ivStatusInfo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_timer));
        } else {
            viewHolder.cardView.setAlpha(1.0f);
            viewHolder.statusInfo.setText("Done");
            viewHolder.statusInfo.setTextColor(context.getResources().getColor(R.color.lesson_done));
            viewHolder.ivStatusInfo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));
        }


    }


    @Override
    public int getItemCount() {
        return lessons.size();
    }


    public class LessonViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvSnippet)
        TextView tvSnippet;

        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvDescription)
        TextView tvDescription;

        @BindView(R.id.cardView)
        CardView cardView;

        @BindView(R.id.tvStatusInfo)
        TextView statusInfo;

        @BindView(R.id.ivStatusInfo)
        ImageView ivStatusInfo;


        public LessonViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (!completed && position >= progress) {
                    return;
                }
                Intent i = new Intent(context, PlayYoutubeActivity.class);
                Lesson lesson = lessons.get(position);
                i.putExtra(PlayYoutubeActivity.EXTRA_LESSON, lesson.getVideoUrl());
                context.startActivity(i);
            });

        }
    }


}
