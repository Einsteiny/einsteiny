package com.einsteiny.einsteiny.course;


import com.einsteiny.einsteiny.BasePresenter;
import com.einsteiny.einsteiny.BaseView;
import com.einsteiny.einsteiny.models.Course;

public interface CourseContract {

    interface View extends BaseView<Presenter> {

        void setFabInvisible();

        void setSupportPostponeEnterTransition();

        void setCourseInfo(String courseInfo);

        void callFinishAfterTransition();

        void setFabGone();

        void loadCourseImage(String photoUrl);

        void addListener();

        void removeListener();

        void initTransitionListener();

        void initToolbar(String title);

        void initAdapterAndLayoutManager(Course mCourse);

    }

    interface Presenter extends BasePresenter {

        void exitReveal(android.view.View myView);

        void enterReveal(android.view.View myView);

        void startUnsubscribeDialog();


    }
}
