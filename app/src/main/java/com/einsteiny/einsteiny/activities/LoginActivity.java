package com.einsteiny.einsteiny.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.einsteiny.einsteiny.R;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;
import java.util.Arrays;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import static rx.schedulers.Schedulers.start;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST = 0;
    private static final String LOG_TAG = "Einsteiny";

    private Button loginOrLogoutButton;
    private ImageView launchView;

    private ParseUser currentUser;

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
        launchView = (ImageView) findViewById(R.id.launchGif);

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            Glide.with(this).load(R.drawable.launch_einsteiny).asGif().into(launchView);
        }

        loginOrLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    // User clicked to log out.
                    ParseUser.logOut();
                    currentUser = null;
                    showProfileLoggedOut();
                } else {
                    // User clicked to log in.
                    ParseLoginBuilder builder = new ParseLoginBuilder(
                            LoginActivity.this);
                    Intent parseLoginIntent = builder.setParseLoginEnabled(true)
                            .setParseLoginButtonText(R.string.com_parse_ui_parse_login_button_label)
                            .setParseSignupButtonText(R.string.com_parse_ui_parse_signup_button_label)
                            .setParseLoginHelpText("Forgot password?")
                            .setParseLoginInvalidCredentialsToastText("Your email and/or password is not correct")
                            .setParseLoginEmailAsUsername(true)
                            .setParseSignupSubmitButtonText("Submit registration")
                            .setFacebookLoginEnabled(true)
                            .setFacebookLoginButtonText("Facebook")
                            .setFacebookLoginPermissions(Arrays.asList("email", "public_profile"))
                            .build();
                    startActivityForResult(parseLoginIntent, LOGIN_REQUEST);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        currentUser = ParseUser.getCurrentUser();

        if (data != null){
            Log.d(LOG_TAG, "Here's the data: " + data.getExtras().toString());
        }

        if (currentUser != null) {
            if (!ParseFacebookUtils.isLinked(currentUser)) {
                ParseFacebookUtils.linkWithReadPermissionsInBackground(currentUser, this, Arrays.asList("email", "public_profile"), new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ParseFacebookUtils.isLinked(currentUser)) {
                            Log.d("MyApp", "Woohoo, user logged in with Facebook!");
                        }
                    }
                });
            }
            userLoggedIn();
        } else {
            Log.d(LOG_TAG, "Not logged in");
            showProfileLoggedOut();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            userLoggedIn();
        } else {
            showProfileLoggedOut();
        }
    }

    /**
     * Shows the profile of the given user.
     */
    private void userLoggedIn() {
        // Ok you get access to the app
        Intent i = new Intent(LoginActivity.this, EinsteinyActivity.class);
        startActivity(i);
    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {
        loginOrLogoutButton.setText(R.string.login_button_label);
    }

}


