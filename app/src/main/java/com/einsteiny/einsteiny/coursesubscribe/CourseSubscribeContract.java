package com.einsteiny.einsteiny.coursesubscribe;


import com.einsteiny.einsteiny.BasePresenter;
import com.einsteiny.einsteiny.BaseView;

public interface CourseSubscribeContract {

    interface View extends BaseView<Presenter> {
        void setFabInvisible();

        void initTvDescription(String tvDescription);

        void initTvTitle(String tvTitle);

        void initTvDuration(String tvDuration);

        void setRating(float rating);

        void setSupportPostponeEnterTransition();

        void loadCourseImage(String photoUrl);

        void initTransitionListener();

        void initToolbar(String title);

        void addListener();

        void removeListener();

        void callFinishAfterTransition();


    }

    interface Presenter extends BasePresenter {

        void exitReveal(android.view.View myView);

        void enterReveal(android.view.View myView);


    }
}
