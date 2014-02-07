package com.fourmob.datetimepicker.date;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

public class SimpleMonthAdapter extends BaseAdapter implements SimpleMonthView.OnDayClickListener {
	protected static int WEEK_7_OVERHANG_HEIGHT = 7;
	private final Context mContext;
	private final DatePickerController mController;
	private CalendarDay mSelectedDay;

	public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
		this.mContext = context;
		this.mController = datePickerController;
		init();
		setSelectedDay(this.mController.getSelectedDay());
	}

	private boolean isSelectedDayInMonth(int year, int month) {
		return (this.mSelectedDay.year == year) && (this.mSelectedDay.month == month);
	}

	public int getCount() {
		return 12 * (1 + (this.mController.getMaxYear() - this.mController.getMinYear()));
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SimpleMonthView simpleMonthView;
		if (convertView != null)
			simpleMonthView = (SimpleMonthView) convertView;
		else {
			simpleMonthView = new SimpleMonthView(this.mContext);
			simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			simpleMonthView.setClickable(true);
			simpleMonthView.setOnDayClickListener(this);
		}
		HashMap<String, Integer> monthParams = (HashMap<String, Integer>) simpleMonthView.getTag();
		if (monthParams == null)
			monthParams = new HashMap<String, Integer>();
		monthParams.clear();
		int month = position % 12;
		int year = position / 12 + this.mController.getMinYear();
		int selectedDay = -1;
		if (isSelectedDayInMonth(year, month))
			selectedDay = this.mSelectedDay.day;
		simpleMonthView.reuse();
		monthParams.put("selected_day", Integer.valueOf(selectedDay));
		monthParams.put("year", Integer.valueOf(year));
		monthParams.put("month", Integer.valueOf(month));
		monthParams.put("week_start", Integer.valueOf(this.mController.getFirstDayOfWeek()));
		simpleMonthView.setMonthParams(monthParams);
		simpleMonthView.invalidate();
		return simpleMonthView;
	}

	protected void init() {
		this.mSelectedDay = new CalendarDay(System.currentTimeMillis());
	}

	public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
		if (calendarDay != null)
			onDayTapped(calendarDay);
	}

	protected void onDayTapped(CalendarDay calendarDay) {
		this.mController.tryVibrate();
		this.mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
		setSelectedDay(calendarDay);
	}

	public void setSelectedDay(CalendarDay calendarDay) {
		this.mSelectedDay = calendarDay;
		notifyDataSetChanged();
	}

	public static class CalendarDay {
		private Calendar calendar;
		int day;
		int month;
		int year;

		public CalendarDay() {
			setTime(System.currentTimeMillis());
		}

		public CalendarDay(int year, int month, int day) {
			setDay(year, month, day);
		}

		public CalendarDay(long timeInMillis) {
			setTime(timeInMillis);
		}

		public CalendarDay(Calendar calendar) {
			this.year = calendar.get(Calendar.YEAR);
			this.month = calendar.get(Calendar.MONTH);
			this.day = calendar.get(Calendar.DAY_OF_MONTH);
		}

		private void setTime(long timeInMillis) {
			if (this.calendar == null)
				this.calendar = Calendar.getInstance();
			this.calendar.setTimeInMillis(timeInMillis);
			this.month = this.calendar.get(Calendar.MONTH);
			this.year = this.calendar.get(Calendar.YEAR);
			this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
		}

		public void set(CalendarDay calendarDay) {
			this.year = calendarDay.year;
			this.month = calendarDay.month;
			this.day = calendarDay.day;
		}

		public void setDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}
}
