package com.einsteiny.einsteiny.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Lesson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private Context context;

    public LessonAdapter(Context context, List<Lesson> Lessons) {
        this.context = context;
        this.lessons = Lessons;
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

        String thumbnail = lesson.getImageUrl();
        if (!TextUtils.isEmpty(thumbnail)) {
            //Measure parent width
            int displayWidth = context.getResources().getDisplayMetrics().widthPixels;

            Picasso.with(context).load(thumbnail).resize(displayWidth / 2, 0).into(viewHolder.ivImage);
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

        public LessonViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }


}
