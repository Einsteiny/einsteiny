package com.einsteiny.einsteiny.course;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class CourseActivity extends AppCompatActivity implements UnsubscribeDialogAlertFragment.UnsubscribeCourseListener, CourseContract.View {

    public static final String EXTRA_COURSE = "course";

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

    private CourseContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ButterKnife.bind(this);

        Course course = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_COURSE));

        //create presenter and set it in the view
        new CoursePresenter(course, this, this);
        mPresenter.start();

        //unsubscribing from course
        fab.setOnClickListener(v -> {
            mPresenter.startUnsubscribeDialog();
        });

    }


    @Override
    public void onBackPressed() {
        mPresenter.exitReveal(fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                mPresenter.exitReveal(fab);
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


    @Override
    public void setPresenter(CourseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setFabInvisible() {
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSupportPostponeEnterTransition() {
        supportPostponeEnterTransition();
    }

    @Override
    public void setCourseInfo(String courseInfo) {
        tvCourseInfo.setText(Html.fromHtml(courseInfo));
    }

    @Override
    public void loadCourseImage(String photoUrl) {
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

    }


    @Override
    public void setFabGone() {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void addListener() {
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

    @Override
    public void removeListener() {
        getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
    }

    @Override
    public void initTransitionListener() {
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mPresenter.enterReveal(fab);
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
    }

    @Override
    public void callFinishAfterTransition() {
        supportFinishAfterTransition();
    }

    @Override
    public void initToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initAdapterAndLayoutManager(Course course) {
        LinearLayoutManager lm =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLessons.setLayoutManager(lm);
        adapter = new LessonAdapter(this, course);
        rvLessons.setAdapter(adapter);
    }
}
