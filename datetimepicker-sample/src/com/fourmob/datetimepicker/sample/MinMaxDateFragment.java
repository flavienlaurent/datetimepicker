package com.fourmob.datetimepicker.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;


/**
 * This fragment show how to set a min(max) date for the calendar.
 */
public class MinMaxDateFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
  private static final String TAG_MIN_DATE_DIALOG = "min_date_dialog";
  private static final String TAG_MAX_DATE_DIALOG = "max_date_dialog";
  private static final String TAG_DATE_DIALOG = "date_dialog";

  private static final String KEY_MIN_DATE = "min_date";
  private static final String KEY_MAX_DATE = "max_date";

  private Button minDateButton;
  private Button maxDateButton;
  private Button showCalendarButton;
  private DatePickerDialog datePickerDialog;
  private Calendar calendarMin;
  private Calendar calendarMax;
  private DateFormat dateFormat;

  public static MinMaxDateFragment newInstance() {
    return new MinMaxDateFragment();
  }

  public MinMaxDateFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    calendarMin = Calendar.getInstance();
    calendarMax = Calendar.getInstance();

    if (savedInstanceState != null) {
      calendarMin = (Calendar) savedInstanceState.getSerializable(KEY_MIN_DATE);
      calendarMax = (Calendar) savedInstanceState.getSerializable(KEY_MAX_DATE);
    }

    datePickerDialog = DatePickerDialog.newInstance(this, calendarMin);
    dateFormat = DateFormat.getDateInstance();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_min_max_date, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View content = getView();

    minDateButton = (Button) content.findViewById(R.id.minDateButton);
    maxDateButton = (Button) content.findViewById(R.id.maxDateButton);
    showCalendarButton = (Button) content.findViewById(R.id.showCalendarButton);

    minDateButton.setOnClickListener(this);
    maxDateButton.setOnClickListener(this);
    showCalendarButton.setOnClickListener(this);

    minDateButton.setText(dateFormat.format(calendarMin.getTime()));
    maxDateButton.setText(dateFormat.format(calendarMax.getTime()));
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(KEY_MIN_DATE, calendarMin);
    outState.putSerializable(KEY_MAX_DATE, calendarMax);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.minDateButton:
        datePickerDialog.clearMinDate();
        datePickerDialog.clearMaxDate();
        datePickerDialog.show(getActivity().getSupportFragmentManager(), TAG_MIN_DATE_DIALOG);
        break;

      case R.id.maxDateButton:
        datePickerDialog.setMinDate(calendarMin.getTimeInMillis());
        datePickerDialog.clearMaxDate();
        datePickerDialog.show(getActivity().getSupportFragmentManager(), TAG_MAX_DATE_DIALOG);
        break;

      case R.id.showCalendarButton:
        datePickerDialog.setMinDate(calendarMin.getTimeInMillis());
        datePickerDialog.setMaxDate(calendarMax.getTimeInMillis());
        datePickerDialog.show(getActivity().getSupportFragmentManager(), TAG_DATE_DIALOG);
        break;
    }
  }

  @Override
  public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
    if (datePickerDialog.getTag().equals(TAG_MIN_DATE_DIALOG)) {
      calendarMin.set(Calendar.YEAR, year);
      calendarMin.set(Calendar.MONTH, month);
      calendarMin.set(Calendar.DAY_OF_MONTH, day);

      calendarMax.setTimeInMillis(calendarMin.getTimeInMillis());

      minDateButton.setText(dateFormat.format(calendarMin.getTime()));
      maxDateButton.setText(dateFormat.format(calendarMax.getTime()));
      return;
    }

    if (datePickerDialog.getTag().equals(TAG_MAX_DATE_DIALOG)) {
      calendarMax.set(Calendar.YEAR, year);
      calendarMax.set(Calendar.MONTH, month);
      calendarMax.set(Calendar.DAY_OF_MONTH, day);

      maxDateButton.setText(dateFormat.format(calendarMax.getTime()));
    }
  }
}
