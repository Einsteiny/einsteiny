package com.einsteiny.einsteiny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.einsteiny.einsteiny.fragments.UserCourseFragment;
import com.einsteiny.einsteiny.fragments.ExploreFragment;
import com.einsteiny.einsteiny.fragments.ProfileFragment;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setBottomNavigationBar();
    }

    private void setBottomNavigationBar() {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define fragments
        final Fragment explore = new ExploreFragment();
        final Fragment userCourse = new UserCourseFragment();
        final Fragment profile = new ProfileFragment();

        // set Explore Fragment by default
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, explore).commit();

        // handle navigation selection
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_explore:
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.flContainer, explore).commit();
                                return true;
                            case R.id.action_user_course:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.flContainer, userCourse).commit();
                                return true;
                            case R.id.action_profile:
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.flContainer, profile).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }
}
