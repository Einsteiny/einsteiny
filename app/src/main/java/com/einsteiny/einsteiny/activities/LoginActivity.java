package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.fragments.LoginIntroFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST = 0;
    private static final String LOG_TAG = "Einsteiny";

    final List<String> permissions = Arrays.asList("public_profile", "email");

    @BindView(R.id.btnLogin)
    Button loginOrLogoutButton;

    private ParseUser currentUser;

    @BindView(R.id.loginPager)
    ViewPager pager;

    @BindView(R.id.loginIndicator)
    CircleIndicator indicator;

    @BindView(R.id.loginLayout)
    RelativeLayout loginLayout;

    @BindView(R.id.tvIntro)
    TextView tvIntro;

    @BindView(R.id.btnLoginFacebook)
    Button facebookButton;


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
            changePage(page);

            handler.postDelayed(this, delay);
        }
    };

    private void changePage(int page) {
        if (page == 0) {
            loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny2));
            tvIntro.setText("Hi! I am Einsteiny :)");
        } else if (page == 1) {
            loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny3));
            tvIntro.setText("I will help you to learn every day");

        } else {
            tvIntro.setText("Even when it's hard, I am with you");
            loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny1));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        tvIntro.setText("Hi! I am Einsteiny :)");
        loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny2));

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        handler = new Handler();
        pager.setAdapter(mPagerAdapter);
        indicator.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // changePage(position);

            }

            @Override
            public void onPageSelected(int position) {
                changePage(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getKeyHash();

        facebookButton.setOnClickListener(v -> ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, (user, err) -> {
            if (user == null) {
//                        alertUtil.showAlert("Log in failed", "Log in with Facebook failed, please try again.");
            } else {
                if (user.isNew()) { // New user
                    saveUserInfoFromFacebook(); // Save email to parse
                } else {
                    userLoggedIn();
                }
            }
        }));


        loginOrLogoutButton.setOnClickListener(v -> {
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
        });
    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.einsteiny.einsteiny", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            //something
        } catch (NoSuchAlgorithmException e) {
            //something
        }
    }

    private void saveUserInfoFromFacebook() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String userName = object.getString("name");
                    String userEmail = object.getString("email");
                    saveNewUser(userName, userEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void saveNewUser(String userName, String userEmail) {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(userName);
        user.setEmail(userEmail);
        user.saveInBackground(e -> {
            userLoggedIn();
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
        //loginOrLogoutButton.setText(R.string.login_button_label);
    }


    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("", R.drawable.einstein2);
                    // loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny2));
//
                    return frag;
                }
                case 1: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("", R.drawable.einstein3);
                    // loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny3));
//
                    return frag;
                }

                default: {
                    LoginIntroFragment frag = LoginIntroFragment.newInstance("", R.drawable.einstein1);
                    // loginLayout.setBackgroundColor(getResources().getColor(R.color.einsteiny1));
//
                    return frag;
                }

            }

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}


