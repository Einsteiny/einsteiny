package com.einsteiny.einsteiny.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.models.Lesson;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayYoutubeActivity extends YouTubeBaseActivity {

    public static final String YT_API_KEY = "AIzaSyC2_EXzzOVaOsoQMPi7-WDD4HlcVah-hrE";

    public static final String EXTRA_LESSON = "lesson";

    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_youtube);
        ButterKnife.bind(this);

        Lesson lesson = (Lesson) getIntent().getSerializableExtra(EXTRA_LESSON);

        youTubePlayerView.initialize(YT_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        final YouTubePlayer youTubePlayer, boolean b) {

                        PlayYoutubeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Handle UI here
                                youTubePlayer.setFullscreen(true);
                                String[] parts = lesson.getVideoUrl().split("/");
                                String video = parts[parts.length - 1];
                                String videoId = video.substring(0, video.indexOf('.'));
                                youTubePlayer.loadVideo(videoId);
                            }
                        });


                        String url = String.format(" https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed",
                                getIntent().getIntExtra("movieId", 0));


                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(PlayYoutubeActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
