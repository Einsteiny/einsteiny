package com.einsteiny.einsteiny.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.network.EinsteinyBroadcastReceiver;
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

        IntentFilter intentFilter = new IntentFilter("com.parse.push.intent.RECEIVE");
        registerReceiver(new EinsteinyBroadcastReceiver(), intentFilter);

        String videoUrl = getIntent().getStringExtra(EXTRA_LESSON);

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
                                youTubePlayer.loadVideo(videoUrl);
                            }
                        });

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(PlayYoutubeActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
