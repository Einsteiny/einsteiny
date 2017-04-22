package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.db.CourseDatabase;
import com.einsteiny.einsteiny.fragments.ExploreFragment;
import com.einsteiny.einsteiny.fragments.ProfileFragment;
import com.einsteiny.einsteiny.fragments.UserCourseFragment;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CourseCategory;
import com.einsteiny.einsteiny.models.Lesson;
import com.einsteiny.einsteiny.network.EinsteinyBroadcastReceiver;
import com.einsteiny.einsteiny.network.EinsteinyServerClient;
import com.parse.ParseUser;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class EinsteinyActivity extends AppCompatActivity implements ProfileFragment.OnLogoutClickListener {

    private Subscription subscription;

    private DatabaseDefinition database = FlowManager.getDatabase(CourseDatabase.class);

    private EinsteinyBroadcastReceiver receiver = new EinsteinyBroadcastReceiver();


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("com.parse.push.intent.RECEIVE");
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_einsteiny);
        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

//        Observable<CourseCategory> artCoursesObs = EinsteinyServerClient.getInstance().getArtsCourses();
//        Observable<CourseCategory> econCoursesObs = EinsteinyServerClient.getInstance().getEconomicsCourses();
//        Observable<CourseCategory> compCoursesObs = EinsteinyServerClient.getInstance().getComputingCourses();
//        Observable<CourseCategory> scienceCoursesObs = EinsteinyServerClient.getInstance().getScienceCourses();
        Observable<CourseCategory> allCoursesObs = EinsteinyServerClient.getInstance().getAllCourses();

//        Observable<AllCourses> allCombined = Observable.zip(artCoursesObs, econCoursesObs, compCoursesObs,
//                scienceCoursesObs, (art, econ, computing, science) -> new AllCourses(art, econ, computing, science));


        subscription = allCoursesObs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseCategory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("DEBUG", "inside on failure");

                    }

                    @Override
                    public void onNext(CourseCategory allCourses) {
                        if (allCourses != null) {
                            saveDB(allCourses.getCourses());
                            setBottomNavigationBar(allCourses.getCourses());
                            pb.setVisibility(ProgressBar.INVISIBLE);

                        }

                    }
                });
    }

    private void saveDB(List<Course> courses) {
        //saving courses
        FastStoreModelTransaction<Course> fsmt = FastStoreModelTransaction
                .saveBuilder(FlowManager.getModelAdapter(Course.class))
                .addAll(courses)
                .build();

        Transaction transaction = database.beginTransactionAsync(fsmt)
                .success(transactionSuccess -> {
                    // This runs on UI thread

                }).error((transactionError, error) -> Log.e("ServiceError", error.getMessage())).build();
        transaction.execute();

        //saving lessons
        List<Lesson> lessons = new ArrayList<>();
        for (Course course : courses) {
            lessons.addAll(course.getLessons());
        }

        FastStoreModelTransaction<Lesson> fsmtLessons = FastStoreModelTransaction
                .saveBuilder(FlowManager.getModelAdapter(Lesson.class))
                .addAll(lessons)
                .build();

        Transaction transactionLessons = database.beginTransactionAsync(fsmtLessons)
                .success(transactionSuccess -> {
                    // This runs on UI thread

                }).error((transactionError, error) -> Log.e("ServiceError", error.getMessage())).build();
        transactionLessons.execute();


    }

    private void setBottomNavigationBar(List<Course> courses) {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define fragments
        final Fragment explore = ExploreFragment.newInstance(courses);
        final Fragment userCourse = UserCourseFragment.newInstance(courses);
        final Fragment profile = new ProfileFragment();

        // handle navigation selection
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.action_explore:
                            fragmentTransaction1.replace(R.id.flContainer, explore).commit();
                            return true;
                        case R.id.action_user_course:
                            fragmentTransaction1.replace(R.id.flContainer, userCourse).commit();
                            return true;
                        case R.id.action_profile:
                            fragmentTransaction1.replace(R.id.flContainer, profile).commit();
                            return true;
                    }
                    return false;
                });
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        unregisterReceiver(receiver);
    }

    @Override
    public void profileLogout() {
        ParseUser.logOut();
        if (ParseUser.getCurrentUser() == null) {
            Intent i = new Intent(EinsteinyActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

}
