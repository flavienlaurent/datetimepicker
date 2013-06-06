package com.fourmob.datetimepicker.date;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import com.fourmob.datetimepicker.R;
import com.fourmob.datetimepicker.Utils;

public class SimpleMonthView extends View {
	protected static int DAY_SELECTED_CIRCLE_SIZE;
	protected static int DAY_SEPARATOR_WIDTH = 1;
	protected static int DEFAULT_HEIGHT = 32;
	protected static int MINI_DAY_NUMBER_TEXT_SIZE;
	protected static int MIN_HEIGHT = 10;
	protected static int MONTH_DAY_LABEL_TEXT_SIZE;
	protected static int MONTH_HEADER_SIZE;
	protected static int MONTH_LABEL_TEXT_SIZE;
	protected static float mScale = 0.0F;
	private final Calendar mCalendar;
	private final Calendar mDayLabelCalendar;
	private int mDayOfWeekStart = 0;
	private String mDayOfWeekTypeface;
	protected int mDayTextColor;
	protected int mFirstJulianDay = -1;
	protected int mFirstMonth = -1;
	private final Formatter mFormatter;
	protected boolean mHasToday = false;
	protected int mLastMonth = -1;
	protected int mMonth;
	protected Paint mMonthDayLabelPaint;
	protected Paint mMonthNumPaint;
	protected int mMonthTitleBGColor;
	protected Paint mMonthTitleBGPaint;
	protected int mMonthTitleColor;
	protected Paint mMonthTitlePaint;
	private String mMonthTitleTypeface;
	protected int mNumCells = this.mNumDays;
	protected int mNumDays = 7;
	private int mNumRows = 6;
	private OnDayClickListener mOnDayClickListener;
	protected int mPadding = 0;
	protected int mRowHeight = DEFAULT_HEIGHT;
	protected Paint mSelectedCirclePaint;
	protected int mSelectedDay = -1;
	protected int mSelectedLeft = -1;
	protected int mSelectedRight = -1;
	private final StringBuilder mStringBuilder;
	protected int mToday = -1;
	protected int mTodayNumberColor;
	protected int mWeekStart = 1;
	protected int mWidth;
	protected int mYear;
	private DateFormatSymbols dateformartsymbols = new DateFormatSymbols();

	public SimpleMonthView(Context context) {
		super(context);
		Resources resources = context.getResources();
		this.mDayLabelCalendar = Calendar.getInstance();
		this.mCalendar = Calendar.getInstance();
		this.mDayOfWeekTypeface = resources.getString(R.string.day_of_week_label_typeface);
		this.mMonthTitleTypeface = resources.getString(R.string.sans_serif);
		this.mDayTextColor = resources.getColor(R.color.date_picker_text_normal);
		this.mTodayNumberColor = resources.getColor(R.color.blue);
		this.mMonthTitleColor = resources.getColor(R.color.white);
		this.mMonthTitleBGColor = resources.getColor(R.color.circle_background);
		this.mStringBuilder = new StringBuilder(50);
		this.mFormatter = new Formatter(this.mStringBuilder, Locale.getDefault());
		MINI_DAY_NUMBER_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.day_number_size);
		MONTH_LABEL_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_label_size);
		MONTH_DAY_LABEL_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_day_label_text_size);
		MONTH_HEADER_SIZE = resources.getDimensionPixelOffset(R.dimen.month_list_item_header_height);
		DAY_SELECTED_CIRCLE_SIZE = resources.getDimensionPixelSize(R.dimen.day_number_select_circle_radius);
		this.mRowHeight = ((resources.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height) - MONTH_HEADER_SIZE) / 6);
		initView();
	}

	private int calculateNumRows() {
		int dayOffset = findDayOffset();
		int nbRows = (dayOffset + this.mNumCells) / this.mNumDays;
		int plusOne = 0;
		if ((dayOffset + this.mNumCells) % this.mNumDays > 0) {
			plusOne = 1;
		}
		return plusOne + nbRows;
	}

	private void drawMonthDayLabels(Canvas canvas) {
		int y = MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE / 2;
		int space = (this.mWidth - 2 * this.mPadding) / (2 * this.mNumDays);
		for (int day = 0; day < this.mNumDays; day++) {
			int dayOfWeek = (day + this.mWeekStart) % this.mNumDays;
			int x = space * (1 + day * 2) + this.mPadding;
			this.mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			canvas.drawText(dateformartsymbols.getShortWeekdays()[dayOfWeek+1].toUpperCase(Locale.getDefault()), x, y, this.mMonthDayLabelPaint);
		}
	}

	private void drawMonthTitle(Canvas canvas) {
		int x = (this.mWidth + 2 * this.mPadding) / 2;
		int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + MONTH_LABEL_TEXT_SIZE / 3;
		canvas.drawText(getMonthAndYearString(), x, y, this.mMonthTitlePaint);
	}

	private int findDayOffset() {
		int off;
		if (this.mDayOfWeekStart < this.mWeekStart) {
			off = this.mDayOfWeekStart + this.mNumDays;
		} else {
			off = this.mDayOfWeekStart;
		}
		return off - this.mWeekStart;
	}

	private String getMonthAndYearString() {
		this.mStringBuilder.setLength(0);
		long dateInMillis = this.mCalendar.getTimeInMillis();
		return DateUtils.formatDateRange(getContext(), dateInMillis, dateInMillis, 52).toString();
	}

	private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
		if (this.mOnDayClickListener != null)
			this.mOnDayClickListener.onDayClick(this, calendarDay);
	}

	private boolean sameDay(int monthDay, Time time) {
		return (this.mYear == time.year) && (this.mMonth == time.month) && (monthDay == time.monthDay);
	}

	protected void drawMonthNums(Canvas canvas) {
		int y = (this.mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
		int paddingDay = (this.mWidth - 2 * this.mPadding) / (2 * this.mNumDays);
		int dayOffset = findDayOffset();
		int day = 1;

		while (day <= this.mNumCells) {
			int x = paddingDay * (1 + dayOffset * 2) + this.mPadding;
			if (this.mSelectedDay == day)
				canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, this.mSelectedCirclePaint);
			if ((this.mHasToday) && (this.mToday == day))
				this.mMonthNumPaint.setColor(this.mTodayNumberColor);
			else
				this.mMonthNumPaint.setColor(this.mDayTextColor);
			canvas.drawText(String.format("%d", day), x, y, this.mMonthNumPaint);
			dayOffset++;
			if (dayOffset == this.mNumDays) {
				dayOffset = 0;
				y += this.mRowHeight;
			}
			day++;
		}
	}

	public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
		int padding = this.mPadding;
		if ((x < padding) || (x > this.mWidth - this.mPadding)) {
			return null;
		}
		int day;
		do {
			int yDay = (int) (y - MONTH_HEADER_SIZE) / this.mRowHeight;
			day = 1 + ((int) ((x - padding) * this.mNumDays / (this.mWidth - padding - this.mPadding)) - findDayOffset()) + yDay * this.mNumDays;
		} while ((day < 1) || (day > this.mNumCells));
		return new SimpleMonthAdapter.CalendarDay(this.mYear, this.mMonth, day);
	}

	protected void initView() {
		this.mMonthTitlePaint = new Paint();
		this.mMonthTitlePaint.setFakeBoldText(true);
		this.mMonthTitlePaint.setAntiAlias(true);
		this.mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
		this.mMonthTitlePaint.setTypeface(Typeface.create(this.mMonthTitleTypeface, 1));
		this.mMonthTitlePaint.setColor(this.mDayTextColor);
		this.mMonthTitlePaint.setTextAlign(Paint.Align.CENTER);
		this.mMonthTitlePaint.setStyle(Paint.Style.FILL);
		this.mMonthTitleBGPaint = new Paint();
		this.mMonthTitleBGPaint.setFakeBoldText(true);
		this.mMonthTitleBGPaint.setAntiAlias(true);
		this.mMonthTitleBGPaint.setColor(this.mMonthTitleBGColor);
		this.mMonthTitleBGPaint.setTextAlign(Paint.Align.CENTER);
		this.mMonthTitleBGPaint.setStyle(Paint.Style.FILL);
		this.mSelectedCirclePaint = new Paint();
		this.mSelectedCirclePaint.setFakeBoldText(true);
		this.mSelectedCirclePaint.setAntiAlias(true);
		this.mSelectedCirclePaint.setColor(this.mTodayNumberColor);
		this.mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
		this.mSelectedCirclePaint.setStyle(Paint.Style.FILL);
		this.mSelectedCirclePaint.setAlpha(60);
		this.mMonthDayLabelPaint = new Paint();
		this.mMonthDayLabelPaint.setAntiAlias(true);
		this.mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
		this.mMonthDayLabelPaint.setColor(this.mDayTextColor);
		this.mMonthDayLabelPaint.setTypeface(Typeface.create(this.mDayOfWeekTypeface, 0));
		this.mMonthDayLabelPaint.setStyle(Paint.Style.FILL);
		this.mMonthDayLabelPaint.setTextAlign(Paint.Align.CENTER);
		this.mMonthDayLabelPaint.setFakeBoldText(true);
		this.mMonthNumPaint = new Paint();
		this.mMonthNumPaint.setAntiAlias(true);
		this.mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
		this.mMonthNumPaint.setStyle(Paint.Style.FILL);
		this.mMonthNumPaint.setTextAlign(Paint.Align.CENTER);
		this.mMonthNumPaint.setFakeBoldText(false);
	}

	protected void onDraw(Canvas canvas) {
		drawMonthTitle(canvas);
		drawMonthDayLabels(canvas);
		drawMonthNums(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.mRowHeight * this.mNumRows + MONTH_HEADER_SIZE);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.mWidth = w;
	}

	public boolean onTouchEvent(MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(motionEvent.getX(), motionEvent.getY());
			if (calendarDay != null)
				onDayClick(calendarDay);
		}
		return true;
	}

	public void reuse() {
		this.mNumRows = 6;
		requestLayout();
	}

	public void setMonthParams(HashMap<String, Integer> monthParams) {
		if ((!monthParams.containsKey("month")) && (!monthParams.containsKey("year")))
			throw new InvalidParameterException("You must specify the month and year for this view");
		setTag(monthParams);
		if (monthParams.containsKey("height")) {
			this.mRowHeight = ((Integer) monthParams.get("height")).intValue();
			if (this.mRowHeight < MIN_HEIGHT)
				this.mRowHeight = MIN_HEIGHT;
		}
		if (monthParams.containsKey("selected_day"))
			this.mSelectedDay = ((Integer) monthParams.get("selected_day")).intValue();
		this.mMonth = ((Integer) monthParams.get("month")).intValue();
		this.mYear = ((Integer) monthParams.get("year")).intValue();
		Time time = new Time(Time.getCurrentTimezone());
		time.setToNow();
		this.mHasToday = false;
		this.mToday = -1;
		this.mCalendar.set(Calendar.MONTH, this.mMonth);
		this.mCalendar.set(Calendar.YEAR, this.mYear);
		this.mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		this.mDayOfWeekStart = this.mCalendar.get(Calendar.DAY_OF_WEEK);
		if (monthParams.containsKey("week_start")) {
			this.mWeekStart = ((Integer) monthParams.get("week_start")).intValue();
		} else {
			this.mWeekStart = this.mCalendar.getFirstDayOfWeek();
		}
		this.mNumCells = Utils.getDaysInMonth(this.mMonth, this.mYear);
		for (int day = 0; day < this.mNumCells; day++) {
			int monthDay = day + 1;
			if (sameDay(monthDay, time)) {
				this.mHasToday = true;
				this.mToday = monthDay;
			}
		}
		this.mNumRows = calculateNumRows();
	}

	public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
		this.mOnDayClickListener = onDayClickListener;
	}

	public static abstract interface OnDayClickListener {
		public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
	}
}