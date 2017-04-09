package com.einsteiny.einsteiny;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
        private static final int LOGIN_REQUEST = 0;

        private TextView titleTextView;
        private TextView emailTextView;
        private TextView nameTextView;
        private Button loginOrLogoutButton;

        private ParseUser currentUser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Fabric.with(this, new Crashlytics());
            setContentView(R.layout.activity_main);

            titleTextView = (TextView) findViewById(R.id.profile_title);
            emailTextView = (TextView) findViewById(R.id.profile_email);
            nameTextView = (TextView) findViewById(R.id.profile_name);
            loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
            titleTextView.setText(R.string.profile_title_logged_in);

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
                        ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                                MainActivity.this);
                        startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
                    }
                }
            });
        }

        @Override
        protected void onStart() {
            super.onStart();

            currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                showProfileLoggedIn();
            } else {
                showProfileLoggedOut();
            }
        }

        /**
         * Shows the profile of the given user.
         */
        private void showProfileLoggedIn() {
            titleTextView.setText(R.string.profile_title_logged_in);
            emailTextView.setText(currentUser.getEmail());
            String fullName = currentUser.getString("name");
            if (fullName != null) {
                nameTextView.setText(fullName);
            }
            loginOrLogoutButton.setText(R.string.profile_logout_button_label);
        }

        /**
         * Show a message asking the user to log in, toggle login/logout button text.
         */
        private void showProfileLoggedOut() {
            titleTextView.setText(R.string.profile_title_logged_out);
            emailTextView.setText("");
            nameTextView.setText("");
            loginOrLogoutButton.setText(R.string.profile_login_button_label);
        }
}


