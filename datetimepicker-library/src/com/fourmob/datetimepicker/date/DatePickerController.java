package com.fourmob.datetimepicker.date;

import java.util.Calendar;

abstract interface DatePickerController {
	public abstract int getFirstDayOfWeek();

	public abstract int getMaxYear();

	public abstract int getMinYear();

	public abstract SimpleMonthAdapter.CalendarDay getSelectedDay();

	public abstract void onDayOfMonthSelected(int year, int month, int day);

	public abstract void onYearSelected(int year);

	public abstract void registerOnDateChangedListener(DatePickerDialog.OnDateChangedListener onDateChangedListener);

	public abstract void tryVibrate();

  public abstract Calendar getMaxDate();

  public abstract Calendar getMinDate();
}