package com.einsteiny.einsteiny.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.fragments.LoginIntroFragment;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST = 0;
    private static final String LOG_TAG = "Einsteiny";

    @BindView(R.id.login_or_logout_button)
    Button loginOrLogoutButton;

//    ImageView launchView;

    private ParseUser currentUser;

    @BindView(R.id.loginPager)
    ViewPager pager;

    @BindView(R.id.loginIndicator)
    CircleIndicator indicator;

    @BindView(R.id.loginLayout)
    RelativeLayout loginLayout;


    private FragmentPagerAdapter mPagerAdapter;
    Handler handler;
    int page;
    private int delay = 4000; //milliseconds

    Runnable runnable = new Runnable() {
        public void run() {
            page = pager.getCurrentItem();
            if (mPagerAdapter.getCount() == page + 1) {
                page = 0;
            } else {
                page++;
            }
            pager.setCurrentItem(page, true);
//            Bitmap bitmap;
//            if (page == 0) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein1);
//            } else if (page == 1) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein2);
//
//            } else if (page == 2) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein3);
//            } else {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein4);
//            }

//            Palette palette = Palette.from(bitmap).generate();
//            loginLayout.setBackgroundColor(palette.getDominantColor(0));
            handler.postDelayed(this, delay);
        }
    };

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
        ButterKnife.bind(this);

//        launchView = (ImageView) findViewById(R.id.launchGif);

//        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
//
//        if (!memoryInfo.lowMemory) {
//            Glide.with(this).load(R.drawable.launch_einsteiny).asGif().into(launchView);
//        }

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        handler = new Handler();
        pager.setAdapter(mPagerAdapter);
        indicator.setViewPager(pager);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein1);
//        Palette palette = Palette.from(bitmap).generate();
//        loginLayout.setBackgroundColor(palette.getDominantColor(0));

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
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        currentUser = ParseUser.getCurrentUser();

        if (data != null) {
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


    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("Hi! I am Einsteiny :)", R.drawable.einstein2);
//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein1);
//                    Palette palette = Palette.from(bitmap).generate();
//                    loginLayout.setBackgroundColor(palette.getDominantColor(0));
                    return frag;
                }
                case 1: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("I will help you to learn effortlessly every day", R.drawable.einstein3);
//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein2);
//                    Palette palette = Palette.from(bitmap).generate();
//                    loginLayout.setBackgroundColor(palette.getDominantColor(0));
                    return frag;
                }
                case 2: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("Even when it's hard", R.drawable.einstein4);
//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein3);
//                    Palette palette = Palette.from(bitmap).generate();
//                    loginLayout.setBackgroundColor(palette.getDominantColor(0));
                    return frag;
                }
                default: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("I am with you", R.drawable.einstein1);
//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.einstein4);
//                    Palette palette = Palette.from(bitmap).generate();
//                    loginLayout.setBackgroundColor(palette.getDominantColor(0));
                    return frag;
                }

            }

        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}


