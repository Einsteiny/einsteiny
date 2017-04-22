package com.einsteiny.einsteiny.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.adapters.LessonAdapter;
import com.einsteiny.einsteiny.fragments.UnsubscribeDialogAlertFragment;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.utils.TransitionUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CourseActivity extends AppCompatActivity implements UnsubscribeDialogAlertFragment.UnsubscribeCourseListener {

    public static final String EXTRA_COURSE = "course";
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat tdf = new SimpleDateFormat("hh:mm");

    private Transition.TransitionListener mEnterTransitionListener;


    @BindView(R.id.rvLessons)
    RecyclerView rvLessons;

    @BindView(R.id.tvCourseInfo)
    TextView tvCourseInfo;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivCourse)
    ImageView ivCourse;

    @BindView(R.id.btnUnsubscribe)
    FloatingActionButton fab;


    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ButterKnife.bind(this);

        supportPostponeEnterTransition();

        fab.setVisibility(View.INVISIBLE);

        Course course = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_COURSE));
        long date = CustomUser.getDateForCourse(course.getId());

        int progress = CustomUser.getProgressForCourse(course.getId());

        String courseInfo = String.format("Course %s on <b>%s</b>. Lessons are scheduled daily at <b>%s</b>.",
                progress > 0 ? "started" : "will start", sdf.format(date), tdf.format(date));
        tvCourseInfo.setText(Html.fromHtml(courseInfo));

        String photoUrl = course.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            int displayWidth = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(photoUrl).resize(displayWidth, 0).into(ivCourse,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
                        }

                        @Override
                        public void onError() {
                            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
                            Log.d("Debug", "onError: error loading course image");
                        }
                    });
        } else {
            TransitionUtils.scheduleStartPostponedTransition(ivCourse, CourseActivity.this);
        }


        LinearLayoutManager lm =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLessons.setLayoutManager(lm);
        adapter = new LessonAdapter(this, course.getLessons(), date, progress, CustomUser.isCompletedCourse(course.getId()));
        rvLessons.setAdapter(adapter);

        setupToolbar(course.getTitle());

        //unsubscribing from course
        fab.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            UnsubscribeDialogAlertFragment dialog = UnsubscribeDialogAlertFragment.newInstance(course);
            dialog.show(fm, "unsubscribe_dialog");

        });

        if (!CustomUser.isCompletedCourse(course.getId())) {
            mEnterTransitionListener = new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    enterReveal();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            };
            getWindow().getEnterTransition().addListener(mEnterTransitionListener);
        }

        if (CustomUser.isCompletedCourse(course.getId())) {
            fab.setVisibility(View.GONE);
        }
    }


    private void setupToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                exitReveal();
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void unsubscribeCourse(Course course) {
        CustomUser.unsubscribeCourse(course);
        finishAfterTransition();
    }

    //Floating action button circular animation
    private void enterReveal() {
        // previously invisible view
        final View myView = fab;

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void exitReveal() {
        // previously visible view
        final View myView = fab;

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);

                // Finish the activity after the exit transition completes.
                supportFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }
}
