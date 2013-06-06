package com.fourmob.datetimepicker.date;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fourmob.datetimepicker.R;
import com.fourmob.datetimepicker.Utils;
import com.nineoldandroids.animation.ObjectAnimator;

public class DatePickerDialog extends DialogFragment implements View.OnClickListener, DatePickerController {
	private static final int VIEW_DATE_PICKER_YEAR = 1;
	private static final int VIEW_DATE_PICKER_MONTH_DAY = 0;
	private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
	private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
	private ViewAnimator mAnimator;
	private final Calendar mCalendar = Calendar.getInstance();
	private OnDateSetListener mCallBack;
	private int mCurrentView = -1;
	private TextView mDayOfWeekView;
	private String mDayPickerDescription;
	private DayPickerView mDayPickerView;
	private boolean mDelayAnimation = true;
	private Button mDoneButton;
	private long mLastVibrate;
	private HashSet<OnDateChangedListener> mListeners = new HashSet<OnDateChangedListener>();
	private int mMaxYear = 2100;
	private int mMinYear = 1970;
	private LinearLayout mMonthAndDayView;
	private String mSelectDay;
	private String mSelectYear;
	private TextView mSelectedDayTextView;
	private TextView mSelectedMonthTextView;
	private Vibrator mVibrator;
	private int mWeekStart = this.mCalendar.getFirstDayOfWeek();
	private String mYearPickerDescription;
	private YearPickerView mYearPickerView;
	private TextView mYearView;
	private DateFormatSymbols dateformartsymbols = new DateFormatSymbols();

	private void adjustDayInMonthIfNeeded(int month, int year) {
		int currentDay = this.mCalendar.get(Calendar.DAY_OF_MONTH);
		int day = Utils.getDaysInMonth(month, year);
		if (currentDay > day)
			this.mCalendar.set(Calendar.DAY_OF_MONTH, day);
	}

	public static DatePickerDialog newInstance(OnDateSetListener onDateSetListener, int year, int month, int day) {
		DatePickerDialog datePickerDialog = new DatePickerDialog();
		datePickerDialog.initialize(onDateSetListener, year, month, day);
		return datePickerDialog;
	}

	private void setCurrentView(int currentView) {
		long timeInMillis = this.mCalendar.getTimeInMillis();
		switch (currentView) {
		case VIEW_DATE_PICKER_MONTH_DAY:
			ObjectAnimator monthDayAnim = Utils.getPulseAnimator(this.mMonthAndDayView, 0.9F, 1.05F);
			if (this.mDelayAnimation) {
				monthDayAnim.setStartDelay(500L);
				this.mDelayAnimation = false;
			}
			this.mDayPickerView.onDateChanged();
			if (this.mCurrentView != currentView) {
				this.mMonthAndDayView.setSelected(true);
				this.mYearView.setSelected(false);
				this.mAnimator.setDisplayedChild(VIEW_DATE_PICKER_MONTH_DAY);
				this.mCurrentView = currentView;
			}
			monthDayAnim.start();
			String monthDayDesc = DateUtils.formatDateTime(getActivity(), timeInMillis, DateUtils.FORMAT_SHOW_DATE);
			this.mAnimator.setContentDescription(this.mDayPickerDescription + ": " + monthDayDesc);
			return;
		case VIEW_DATE_PICKER_YEAR:
			ObjectAnimator yearAnim = Utils.getPulseAnimator(this.mYearView, 0.85F, 1.1F);
			if (this.mDelayAnimation) {
				yearAnim.setStartDelay(500L);
				this.mDelayAnimation = false;
			}
			this.mYearPickerView.onDateChanged();
			if (this.mCurrentView != currentView) {
				this.mMonthAndDayView.setSelected(false);
				this.mYearView.setSelected(true);
				this.mAnimator.setDisplayedChild(VIEW_DATE_PICKER_YEAR);
				this.mCurrentView = currentView;
			}
			yearAnim.start();
			String dayDesc = YEAR_FORMAT.format(Long.valueOf(timeInMillis));
			this.mAnimator.setContentDescription(this.mYearPickerDescription + ": " + dayDesc);
		}

	}

	private void updateDisplay() {  
		if (this.mDayOfWeekView != null)
			this.mDayOfWeekView.setText(dateformartsymbols.getWeekdays()[Calendar.DAY_OF_WEEK].toUpperCase(Locale.getDefault()));
		
		this.mSelectedMonthTextView.setText(dateformartsymbols.getMonths()[this.mCalendar.getTime().getMonth()].toUpperCase(Locale.getDefault()));
		this.mSelectedDayTextView.setText(DAY_FORMAT.format(this.mCalendar.getTime()));
		this.mYearView.setText(YEAR_FORMAT.format(this.mCalendar.getTime()));
		long timeInMillis = this.mCalendar.getTimeInMillis();
		String desc = DateUtils.formatDateTime(getActivity(), timeInMillis, 24);
		this.mMonthAndDayView.setContentDescription(desc);
	}

	private void updatePickers() {
		Iterator<OnDateChangedListener> it = this.mListeners.iterator();
		while (it.hasNext())
			it.next().onDateChanged();
	}

	public int getFirstDayOfWeek() {
		return this.mWeekStart;
	}

	public int getMaxYear() {
		return this.mMaxYear;
	}

	public int getMinYear() {
		return this.mMinYear;
	}

	public SimpleMonthAdapter.CalendarDay getSelectedDay() {
		return new SimpleMonthAdapter.CalendarDay(this.mCalendar);
	}

	public void initialize(OnDateSetListener onDateSetListener, int year, int month, int day) {
		this.mCallBack = onDateSetListener;
		this.mCalendar.set(Calendar.YEAR, year);
		this.mCalendar.set(Calendar.MONTH, month);
		this.mCalendar.set(Calendar.DAY_OF_MONTH, day);
	}

	public void onClick(View view) {
		tryVibrate();
		if (view.getId() == R.id.date_picker_year)
			setCurrentView(VIEW_DATE_PICKER_YEAR);
		else if (view.getId() == R.id.date_picker_month_and_day)
			setCurrentView(VIEW_DATE_PICKER_MONTH_DAY);
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Activity activity = getActivity();
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.mVibrator = ((Vibrator) activity.getSystemService("vibrator"));
		if (bundle != null) {
			this.mCalendar.set(Calendar.YEAR, bundle.getInt("year"));
			this.mCalendar.set(Calendar.MONTH, bundle.getInt("month"));
			this.mCalendar.set(Calendar.DAY_OF_MONTH, bundle.getInt("day"));
		}
	}

	public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle bundle) {
		Log.d("DatePickerDialog", "onCreateView: ");
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = layoutInflater.inflate(R.layout.date_picker_dialog, null);
		this.mDayOfWeekView = ((TextView) view.findViewById(R.id.date_picker_header));
		this.mMonthAndDayView = ((LinearLayout) view.findViewById(R.id.date_picker_month_and_day));
		this.mMonthAndDayView.setOnClickListener(this);
		this.mSelectedMonthTextView = ((TextView) view.findViewById(R.id.date_picker_month));
		this.mSelectedDayTextView = ((TextView) view.findViewById(R.id.date_picker_day));
		this.mYearView = ((TextView) view.findViewById(R.id.date_picker_year));
		this.mYearView.setOnClickListener(this);
		int listPosition = -1;
		int currentView = 0;
		int listPositionOffset = 0;
		if (bundle != null) {
			this.mWeekStart = bundle.getInt("week_start");
			this.mMinYear = bundle.getInt("year_start");
			this.mMaxYear = bundle.getInt("year_end");
			currentView = bundle.getInt("current_view");
			listPosition = bundle.getInt("list_position");
			listPositionOffset = bundle.getInt("list_position_offset");
		}
		Activity activity = getActivity();
		this.mDayPickerView = new DayPickerView(activity, this);
		this.mYearPickerView = new YearPickerView(activity, this);
		Resources resources = getResources();
		this.mDayPickerDescription = resources.getString(R.string.day_picker_description);
		this.mSelectDay = resources.getString(R.string.select_day);
		this.mYearPickerDescription = resources.getString(R.string.year_picker_description);
		this.mSelectYear = resources.getString(R.string.select_year);
		this.mAnimator = ((ViewAnimator) view.findViewById(R.id.animator));
		this.mAnimator.addView(this.mDayPickerView);
		this.mAnimator.addView(this.mYearPickerView);
		// this.mAnimator.setDateMillis(this.mCalendar.getTimeInMillis());
		AlphaAnimation inAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
		inAlphaAnimation.setDuration(300L);
		this.mAnimator.setInAnimation(inAlphaAnimation);
		AlphaAnimation outAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
		outAlphaAnimation.setDuration(300L);
		this.mAnimator.setOutAnimation(outAlphaAnimation);
		this.mDoneButton = ((Button) view.findViewById(R.id.done));
		this.mDoneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				DatePickerDialog.this.tryVibrate();
				if (DatePickerDialog.this.mCallBack != null)
					DatePickerDialog.this.mCallBack.onDateSet(DatePickerDialog.this, DatePickerDialog.this.mCalendar.get(Calendar.YEAR), DatePickerDialog.this.mCalendar.get(Calendar.MONTH), DatePickerDialog.this.mCalendar.get(Calendar.DAY_OF_MONTH));
				DatePickerDialog.this.dismiss();
			}
		});
		updateDisplay();
		setCurrentView(currentView);

		if (listPosition != -1) {
			if (currentView == VIEW_DATE_PICKER_MONTH_DAY) {
				this.mDayPickerView.postSetSelection(listPosition);
			}
			if (currentView == VIEW_DATE_PICKER_YEAR) {
				this.mYearPickerView.postSetSelectionFromTop(listPosition, listPositionOffset);
			}
		}
		return view;
	}

	public void onDayOfMonthSelected(int year, int month, int day) {
		this.mCalendar.set(Calendar.YEAR, year);
		this.mCalendar.set(Calendar.MONTH, month);
		this.mCalendar.set(Calendar.DAY_OF_MONTH, day);
		updatePickers();
		updateDisplay();
	}

	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt("year", this.mCalendar.get(Calendar.YEAR));
		bundle.putInt("month", this.mCalendar.get(Calendar.MONTH));
		bundle.putInt("day", this.mCalendar.get(Calendar.DAY_OF_MONTH));
		bundle.putInt("week_start", this.mWeekStart);
		bundle.putInt("year_start", this.mMinYear);
		bundle.putInt("year_end", this.mMaxYear);
		bundle.putInt("current_view", this.mCurrentView);
		int mostVisiblePosition = -1;
		if (this.mCurrentView == 0)
			mostVisiblePosition = this.mDayPickerView.getMostVisiblePosition();
		bundle.putInt("list_position", mostVisiblePosition);
		if (this.mCurrentView == 1) {
			mostVisiblePosition = this.mYearPickerView.getFirstVisiblePosition();
			bundle.putInt("list_position_offset", this.mYearPickerView.getFirstPositionOffset());
		}
	}

	public void onYearSelected(int year) {
		adjustDayInMonthIfNeeded(this.mCalendar.get(Calendar.MONTH), year);
		this.mCalendar.set(Calendar.YEAR, year);
		updatePickers();
		setCurrentView(0);
		updateDisplay();
	}

	public void registerOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
		this.mListeners.add(onDateChangedListener);
	}

	public void setFirstDayOfWeek(int weekStart) {
		if ((weekStart < 1) || (weekStart > 7))
			throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and Calendar.SATURDAY");
		this.mWeekStart = weekStart;
		if (this.mDayPickerView != null)
			this.mDayPickerView.onChange();
	}

	public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
		this.mCallBack = onDateSetListener;
	}

	public void setYearRange(int minYear, int maxYear) {
		if (maxYear <= minYear)
			throw new IllegalArgumentException("Year end must be larger than year start");
		this.mMinYear = minYear;
		this.mMaxYear = maxYear;
		if (this.mDayPickerView != null)
			this.mDayPickerView.onChange();
	}

	public void tryVibrate() {
		if (this.mVibrator != null) {
			long timeInMillis = SystemClock.uptimeMillis();
			if (timeInMillis - this.mLastVibrate >= 125L) {
				this.mVibrator.vibrate(5L);
				this.mLastVibrate = timeInMillis;
			}
		}
	}

	static abstract interface OnDateChangedListener {
		public abstract void onDateChanged();
	}

	public static abstract interface OnDateSetListener {
		public abstract void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day);
	}
}