package com.einsteiny.einsteiny.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.einsteiny.einsteiny.models.Course;


public class ResubscribeDialogAlertFragment extends DialogFragment {

    private final static String EXTRA_COURSE = "course";

    public ResubscribeDialogAlertFragment() {
    }

    public static ResubscribeDialogAlertFragment newInstance(Course course) {
        ResubscribeDialogAlertFragment dialog = new ResubscribeDialogAlertFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_COURSE, course);
        dialog.setArguments(args);

        return dialog;

    }

    public interface SubscribeCourseListener {
        void subscribeCourse(Course course);

        void skipSubscription();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Course course = (Course) getArguments().getSerializable(EXTRA_COURSE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(course.getTitle());
        alertDialogBuilder.setMessage("You are already subscribed for this course. Do you want to start again?");
        alertDialogBuilder.setPositiveButton("YES", (dialog, which) -> {
            // on success
            SubscribeCourseListener listener = (SubscribeCourseListener) getActivity();
            listener.subscribeCourse(course);
        });
        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> {
            if (dialog != null) {
                SubscribeCourseListener listener = (SubscribeCourseListener) getActivity();
                listener.skipSubscription();
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }


}
