package com.einsteiny.einsteiny.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.einsteiny.einsteiny.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tonya on 2/15/17.
 */

public class SelectTimeDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.etDate)
    EditText etDate;

    @BindView(R.id.etTime)
    EditText etTime;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    Calendar cal = Calendar.getInstance();

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat tdf = new SimpleDateFormat("hh:mm");


    public interface ConfirmSubscriptionListener {
        void confirmSubscription(Calendar cal);
    }


    public SelectTimeDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SelectTimeDialog newInstance() {
        SelectTimeDialog frag = new SelectTimeDialog();

        //frag.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialogTheme);
//        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogTheme);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_time, container);
//        getDialog().setTitle(R.string.add_item);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        cal.add(Calendar.DATE, 1);
        etDate.setText(sdf.format(cal.getTime()));

        etDate.setOnClickListener(v -> {

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dp = new DatePickerDialog(getActivity(), SelectTimeDialog.this, year, month, day);
            dp.show();
        });

        etTime.setOnClickListener(v -> {
            int hour = 9;
            int minute = 0;
            TimePickerDialog tp = new TimePickerDialog(getActivity(), SelectTimeDialog.this, hour, minute, true);
            tp.show();
        });

        etTime.setText("09:00");

        btnConfirm.setOnClickListener(v -> {
            ConfirmSubscriptionListener listener = (ConfirmSubscriptionListener) getActivity();
            listener.confirmSubscription(cal);
            dismiss();
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        cal.set(year, month, dayOfMonth);

        etDate.setText(sdf.format(cal.getTime()));


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day, hourOfDay, minute);

        etTime.setText(tdf.format(cal.getTime()));
    }
}
