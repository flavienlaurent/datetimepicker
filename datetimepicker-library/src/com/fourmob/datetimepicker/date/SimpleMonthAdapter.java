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

public class SimpleMonthAdapter extends BaseAdapter implements SimpleMonthView.OnDayClickListener {

    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;

	private final Context mContext;
	private final DatePickerController mController;

	private CalendarDay mSelectedDay;
  private Calendar minCalendar;
  private Calendar maxCalendar;
  private int monthsCount;
  private ArrayList<Integer> arrayListMonth;
  private ArrayList<Integer> arrayListYears;
  private int minDay;
  private int maxDay;

  public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
    mContext = context;
		mController = datePickerController;
    init();
    setSelectedDay(mController.getSelectedDay());
  }

	private boolean isSelectedDayInMonth(int year, int month) {
		return (mSelectedDay.year == year) && (mSelectedDay.month == month);
	}

	public int getCount() {
    return monthsCount;
  }

  public Object getItem(int position) {
    return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SimpleMonthView v;
        HashMap<String, Integer> drawingParams = null;
		if (convertView != null) {
			v = (SimpleMonthView) convertView;
            drawingParams = (HashMap<String, Integer>) v.getTag();
        } else {
			v = new SimpleMonthView(mContext);
			v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			v.setClickable(true);
			v.setOnDayClickListener(this);
		}
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Integer>();
        }
        drawingParams.clear();

    final int month = arrayListMonth.get(position);
    final int year = arrayListYears.get(position);

    int selectedDay = -1;
    if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }

		v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());

    if (position == 0 && minDay != 0) {
      drawingParams.put(SimpleMonthView.VIEW_PARAMS_LAST_INACTIVE_DAY, minDay);
    }

    if (position == getCount() - 1 && maxDay != 0) {
      drawingParams.put(SimpleMonthView.VIEW_PARAMS_FIRST_INACTIVE_DAY, maxDay);
    }


    v.setMonthParams(drawingParams);
    v.invalidate();

		return v;
	}

	protected void init() {
		mSelectedDay = new CalendarDay(System.currentTimeMillis());
    minCalendar = mController.getMinDate();
    maxCalendar = mController.getMaxDate();

    initMonthCount();
    initMinMaxDays();
    prepareMonthsAndYearsList();
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

  private void initMonthCount() {
    monthsCount = ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;

    if (minCalendar != null) {
      monthsCount -= minCalendar.get(Calendar.MONTH);
    }

    if (maxCalendar != null) {
      monthsCount -= (MONTHS_IN_YEAR - maxCalendar.get(Calendar.MONTH) - 1);
    }
  }

  private void prepareMonthsAndYearsList() {
    arrayListMonth = new ArrayList<Integer>(getCount());
    arrayListYears = new ArrayList<Integer>(getCount());

    int month = 0;
    int year = mController.getMinYear();

    if (minCalendar != null) {
      month = minCalendar.get(Calendar.MONTH);
    }

    for (int i = 0; i < getCount(); i++) {
      if (month == 12) {
        month = 0;
        year++;
      }

      arrayListMonth.add(month);
      arrayListYears.add(year);

      month++;
    }
  }

  private void initMinMaxDays() {
    if (minCalendar != null) {
      minDay = minCalendar.get(Calendar.DAY_OF_MONTH);
    }

    if (maxCalendar != null) {
      maxDay = maxCalendar.get(Calendar.DAY_OF_MONTH);
    }
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
      this.calendar = calendar;

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
      if (calendar == null) {
        calendar = Calendar.getInstance();
      }

      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, month);
      calendar.set(Calendar.DAY_OF_MONTH, day);

      this.year = year;
      this.month = month;
			this.day = day;
		}

    public Calendar getCalendar() {
      return calendar;
    }
  }
}