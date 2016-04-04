package com.fourmob.datetimepicker.date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SimpleMonthAdapter extends BaseAdapter implements SimpleMonthView.OnDayClickListener {

    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;

	private final Context mContext;
	private final DatePickerController mController;

	private CalendarDay mSelectedDay;
	private List<CalendarDay> mHighlightedDays;

	public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
		mContext = context;
		mController = datePickerController;
		init();
		setSelectedDay(mController.getSelectedDay());
        setHighlightedDays(mController.getHighlightedDays());
	}

	private boolean isSelectedDayInMonth(int year, int month) {
		return (mSelectedDay.year == year) && (mSelectedDay.month == month);
	}

	public int getCount() {
        return ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SimpleMonthView v;
        HashMap<String, Object> drawingParams = null;
		if (convertView != null) {
			v = (SimpleMonthView) convertView;
            drawingParams = (HashMap<String, Object>) v.getTag();
        } else {
			v = new SimpleMonthView(mContext);
			v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			v.setClickable(true);
			v.setOnDayClickListener(this);
		}
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Object>();
        }
        drawingParams.clear();

        final int month = position % MONTHS_IN_YEAR;
        final int year = position / MONTHS_IN_YEAR + mController.getMinYear();

        int selectedDay = -1;
        if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }
		int[] highlightedDays = daysInThisMonth(mHighlightedDays, year, month);

		v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
		drawingParams.put(SimpleMonthView.VIEW_PARAMS_HIGHLIGHTED_DAYS, highlightedDays);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());
		v.setMonthParams(drawingParams);
		v.invalidate();

		return v;
	}

	/**
	 * Filter out any days not in the specified month and year
	 * and return an integer array of days in this month.
     */
	private static int[] daysInThisMonth(List<CalendarDay> highlightedDays, int year, int month) {
		List<Integer> daysInMonth = new ArrayList<Integer>();
		for (CalendarDay highlightedDay : highlightedDays) {
			if (highlightedDay.month == month && highlightedDay.year == year) {
				daysInMonth.add(highlightedDay.day);
			}
		}
		int[] daysInMonthArr = new int[daysInMonth.size()];
		for (int i = 0; i < daysInMonth.size(); i++) {
			daysInMonthArr[i] = daysInMonth.get(i);
		}

		return daysInMonthArr;
	}

	protected void init() {
		mSelectedDay = new CalendarDay(System.currentTimeMillis());
	}

	public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
		if (calendarDay != null) {
			onDayTapped(calendarDay);
        }
	}

	protected void onDayTapped(CalendarDay calendarDay) {
		mController.tryVibrate();
		mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
		setSelectedDay(calendarDay);
	}

	public void setSelectedDay(CalendarDay calendarDay) {
		mSelectedDay = calendarDay;
		notifyDataSetChanged();
	}

    public void setHighlightedDays(List<CalendarDay> calendarDays) {
        mHighlightedDays = calendarDays;
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
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
		}

		private void setTime(long timeInMillis) {
			if (calendar == null) {
				calendar = Calendar.getInstance();
            }
			calendar.setTimeInMillis(timeInMillis);
			month = this.calendar.get(Calendar.MONTH);
			year = this.calendar.get(Calendar.YEAR);
			day = this.calendar.get(Calendar.DAY_OF_MONTH);
		}

		public void set(CalendarDay calendarDay) {
		    year = calendarDay.year;
			month = calendarDay.month;
			day = calendarDay.day;
		}

		public void setDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			CalendarDay that = (CalendarDay) o;

			if (day != that.day)
				return false;
			if (month != that.month)
				return false;
			return year == that.year;

		}

		@Override public int hashCode() {
			int result = day;
			result = 31 * result + month;
			result = 31 * result + year;
			return result;
		}

		@Override public String toString() {
			return "CalendarDay{" +
					"day=" + day +
					", month=" + month +
					", year=" + year +
					'}';
		}
	}
}