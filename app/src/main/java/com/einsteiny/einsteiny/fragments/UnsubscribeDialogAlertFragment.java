package com.einsteiny.einsteiny.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.einsteiny.einsteiny.models.Course;


public class UnsubscribeDialogAlertFragment extends DialogFragment {

    private final static String EXTRA_COURSE = "course";

    public UnsubscribeDialogAlertFragment() {
    }

    public static UnsubscribeDialogAlertFragment newInstance(Course course) {
        UnsubscribeDialogAlertFragment dialog = new UnsubscribeDialogAlertFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_COURSE, course);
        dialog.setArguments(args);

        return dialog;

    }

    public interface UnsubscribeCourseListener {
        void unsubscribeCourse(Course course);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Course course = (Course) getArguments().getSerializable(EXTRA_COURSE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(course.getTitle());
        alertDialogBuilder.setMessage("Are you sure you want to unsubscribe from this course ?");
        alertDialogBuilder.setPositiveButton("YES", (dialog, which) -> {
            // on success
            UnsubscribeCourseListener listener = (UnsubscribeCourseListener) getActivity();
            listener.unsubscribeCourse(course);
        });
        alertDialogBuilder.setNegativeButton("NO", (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }


}
