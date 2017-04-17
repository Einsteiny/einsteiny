package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.fragments.ProfileFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.parse.ParseUser.getCurrentUser;

public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST = 0;
    private static final String LOG_TAG = "Einsteiny";

    private String fbId;
    private String fbName;
    private String email;
    private String profilePic;

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
                if (getCurrentUser() != null) {
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
                                        Log.d(LOG_TAG, "Uh oh. Error occurred" + err.toString());
                                        showProfileLoggedOut();
                                    } else if (user == null) {
                                        Log.d(LOG_TAG, "Uh oh. The user cancelled the Facebook login.");
                                        showProfileLoggedOut();
                                    } else if (user.isNew()) {
                                        Log.d(LOG_TAG, "User signed up and logged in through Facebook!");
                                        // TODO register new user
                                        showProfileLoggedIn();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                                                .show();
                                        Log.d(LOG_TAG, "User logged in through Facebook!");
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
                if (getCurrentUser() != null) {
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

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        Log.d(LOG_TAG,"onCompleted jsonObject: "+jsonObject);
                        Log.d(LOG_TAG,"onCompleted response: "+response);
                        // Save Facebook user info to ParseUser
                        try {
                            fbId = (String) response.getJSONObject().get("id");
                            fbName = response.getJSONObject().getString("name");
                            email = response.getJSONObject().getString("email");
                            profilePic = response.getJSONObject().getJSONObject("cover").getString("source");

                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("fbID", fbId);
                            user.put("name", fbName);
                            user.put("email", email);
                            user.put("profilePic", profilePic);
                            user.saveInBackground();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Login successful -- go to EinsteinyActivity
                        Intent i = new Intent(LoginActivity.this, EinsteinyActivity.class);
                        startActivity(i);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,cover,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getCurrentUser() != null) {
            Intent i = new Intent(LoginActivity.this, EinsteinyActivity.class);
            startActivity(i);
        }
    }

    /**
     * Shows the profile of the given user.
     */
    private void showProfileLoggedIn() {
        titleTextView.setText(R.string.profile_title_logged_in);

        if (getCurrentUser().getEmail() != null){
            emailTextView.setText(getCurrentUser().getEmail());
        }
        if (getCurrentUser().has("name")) {
            String fullName = getCurrentUser().getString("name");
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


//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
//        }
//    };
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(EinsteinyBroadcastReceiver.intentAction));
//    }

}


