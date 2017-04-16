package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.einsteiny.einsteiny.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST = 0;

    private TextView titleTextView;
    private TextView emailTextView;
    private TextView nameTextView;
    private Button loginOrLogoutButton;
    private Button fbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        titleTextView = (TextView) findViewById(R.id.profile_title);
        emailTextView = (TextView) findViewById(R.id.profile_email);
        nameTextView = (TextView) findViewById(R.id.profile_name);
        loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
        fbButton = (Button) findViewById(R.id.btnFacebook);

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() != null) {
                    // User Clicked Log out Facebook
                    ParseUser.logOut();
                    showProfileLoggedOut();
                } else {
                    ArrayList<String> permissions = new ArrayList();
                    permissions.add("email");
                    permissions.add("public_profile");
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                            new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException err) {
                                    if (err != null) {
                                        Log.d("Einsteiny", "Uh oh. Error occurred" + err.toString());
                                        showProfileLoggedOut();
                                    } else if (user == null) {
                                        Log.d("Einsteiny", "Uh oh. The user cancelled the Facebook login.");
                                        showProfileLoggedOut();
                                    } else if (user.isNew()) {
                                        Log.d("Einsteiny", "User signed up and logged in through Facebook!");
                                        showProfileLoggedIn();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                                                .show();
                                        Log.d("Einsteiny", "User logged in through Facebook!");
                                        showProfileLoggedIn();
                                    }
                                }
                            });
                }
            }
        });

        loginOrLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() != null) {
                    // User clicked to log out.
                    ParseUser.logOut();
                    showProfileLoggedOut();
                } else {
                    // User clicked to log in.
                    ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                            LoginActivity.this);
                    startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        // Login successful -- go to Einsteiny
        Intent i = new Intent(LoginActivity.this, EinsteinyActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(LoginActivity.this, EinsteinyActivity.class);
            startActivity(i);
        }
    }

    /**
     * Shows the profile of the given user.
     */
    private void showProfileLoggedIn() {
        titleTextView.setText(R.string.profile_title_logged_in);
        if (ParseUser.getCurrentUser().getEmail() != null){
            emailTextView.setText(ParseUser.getCurrentUser().getEmail());
        }
        if (ParseUser.getCurrentUser().has("name")) {
            String fullName = ParseUser.getCurrentUser().getString("name");
            if (fullName != null) {
                nameTextView.setText(fullName);
            }
        } else {
            // get the info from Facebook
        }
        fbButton.setText(R.string.com_facebook_loginview_log_out_action);
        loginOrLogoutButton.setText(R.string.profile_logout_button_label);
    }

    /**
     * Show a message asking the user to log in, toggle login/logout button text.
     */
    private void showProfileLoggedOut() {
        titleTextView.setText("");
        emailTextView.setText("");
        nameTextView.setText("");
        fbButton.setText(R.string.com_parse_ui_facebook_login_button_label);
        loginOrLogoutButton.setText(R.string.profile_login_button_label);
    }

}


