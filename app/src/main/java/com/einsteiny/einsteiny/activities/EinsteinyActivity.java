package com.einsteiny.einsteiny.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.db.CourseDatabase;
import com.einsteiny.einsteiny.fragments.ExploreFragment;
import com.einsteiny.einsteiny.fragments.FavouritesFragment;
import com.einsteiny.einsteiny.fragments.ProfileFragment;
import com.einsteiny.einsteiny.fragments.UserCourseFragment;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CourseCategory;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.models.Lesson;
import com.einsteiny.einsteiny.network.EinsteinyBroadcastReceiver;
import com.einsteiny.einsteiny.network.EinsteinyServerClient;
import com.parse.ParseUser;
import com.plattysoft.leonids.ParticleSystem;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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

    private Fragment fromFragment;


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

        List<Course> courses = null;

        if (database != null) {
            courses = SQLite.select().
                    from(Course.class).queryList();
        }

        if (courses == null || courses.isEmpty()) {
            Observable<CourseCategory> allCoursesObs = EinsteinyServerClient.getInstance().getAllCourses();
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
                            pb.setVisibility(ProgressBar.INVISIBLE);

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
        } else {
            setBottomNavigationBar(courses);
            pb.setVisibility(ProgressBar.INVISIBLE);
        }

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
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // define fragments
        final Fragment explore = ExploreFragment.newInstance(courses);
        final Fragment userCourse = UserCourseFragment.newInstance(courses);
        final Fragment profile = ProfileFragment.newInstance(courses);
        final Fragment saved = FavouritesFragment.newInstance(courses);

        // set passed in tab as default
        fragmentTransaction.replace(R.id.flContainer, explore).commit();
        fromFragment = explore;

        // handle navigation selection
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.action_explore:
                            fragmentTransaction1.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                            fragmentTransaction1.replace(R.id.flContainer, explore).commit();
                            fromFragment = explore;
                            return true;
                        case R.id.action_user_course:
                            if (CustomUser.getNewlyFinishedCourse() != null) {
                                Course course = CustomUser.getNewlyFinishedCourse();
                                Toast.makeText(this, "Congrats on finishing " + course.getTitle(), Toast.LENGTH_SHORT).show();
                                CustomUser.setNewlyFinishedCourse(null);
                                ParticleSystem ps = new ParticleSystem(this, 100, R.drawable.ic_yellow_star, 800);
                                ps.setScaleRange(0.7f, 1.3f);
                                ps.setSpeedRange(0.1f, 0.25f);
                                ps.setRotationSpeedRange(90, 180);
                                ps.setFadeOut(200, new AccelerateInterpolator());
                                ps.oneShot(bottomNavigationView, 70);
                            }

                            if (fromFragment == explore) {
                                // slide fragment in from the right
                                fragmentTransaction1.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else if (fromFragment == profile) {
                                // slide in from the right
                                fragmentTransaction1.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                            fragmentTransaction1.replace(R.id.flContainer, userCourse).commit();
                            fromFragment = userCourse;
                            return true;
                        case R.id.action_saved:
                            fragmentTransaction1.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                            fragmentTransaction1.replace(R.id.flContainer, saved).commit();
                            fromFragment = saved;
                            return true;
                        case R.id.action_profile:
                            fragmentTransaction1.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                            fragmentTransaction1.replace(R.id.flContainer, profile).commit();
                            fromFragment = profile;
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
