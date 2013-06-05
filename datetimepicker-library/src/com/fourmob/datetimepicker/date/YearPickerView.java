package com.fourmob.datetimepicker.date;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fourmob.datetimepicker.R;

public class YearPickerView extends ListView implements AdapterView.OnItemClickListener, DatePickerDialog.OnDateChangedListener {
	private YearAdapter mAdapter;
	private int mChildSize;
	private final DatePickerController mController;
	private TextViewWithCircularIndicator mSelectedView;
	private int mViewSize;

	public YearPickerView(Context context, DatePickerController datePickerController) {
		super(context);
		this.mController = datePickerController;
		this.mController.registerOnDateChangedListener(this);
		setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
		Resources resources = context.getResources();
		this.mViewSize = resources.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height);
		this.mChildSize = resources.getDimensionPixelOffset(R.dimen.year_label_height);
		setVerticalFadingEdgeEnabled(true);
		setFadingEdgeLength(this.mChildSize / 3);
		init(context);
		setOnItemClickListener(this);
		setSelector(new StateListDrawable());
		setDividerHeight(0);
		onDateChanged();
	}

	private int getYearFromTextView(TextView textView) {
		return Integer.valueOf(textView.getText().toString()).intValue();
	}

	private void init(Context context) {
		ArrayList<String> years = new ArrayList<String>();
		for (int year = this.mController.getMinYear(); year <= this.mController.getMaxYear(); year++) {
			years.add(String.format("%d", year));
		}
		this.mAdapter = new YearAdapter(context, R.layout.year_label_text_view, years);
		setAdapter(this.mAdapter);
	}

	public int getFirstPositionOffset() {
		View view = getChildAt(0);
		if (view == null)
			return 0;
		return view.getTop();
	}

	public void onDateChanged() {
		this.mAdapter.notifyDataSetChanged();
		postSetSelectionCentered(this.mController.getSelectedDay().year - this.mController.getMinYear());
	}


	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.mController.tryVibrate();
		TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) view;
		if (textViewWithCircularIndicator != null) {
			if (textViewWithCircularIndicator != this.mSelectedView) {
				if (this.mSelectedView != null) {
					this.mSelectedView.drawIndicator(false);
					this.mSelectedView.requestLayout();
				}
				textViewWithCircularIndicator.drawIndicator(true);
				textViewWithCircularIndicator.requestLayout();
				this.mSelectedView = textViewWithCircularIndicator;
			}
			this.mController.onYearSelected(getYearFromTextView(textViewWithCircularIndicator));
			this.mAdapter.notifyDataSetChanged();
		}
	}

	public void postSetSelectionCentered(int position) {
		postSetSelectionFromTop(position, this.mViewSize / 2 - this.mChildSize / 2);
	}

	public void postSetSelectionFromTop(final int position, final int y) {
		post(new Runnable() {
			public void run() {
				YearPickerView.this.setSelectionFromTop(position, y);
				YearPickerView.this.requestLayout();
			}
		});
	}

	private class YearAdapter extends ArrayAdapter<String> {
		public YearAdapter(Context context, int resourceId, List<String> years) {
			super(context, resourceId, years);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) super.getView(position, convertView, parent);
			textViewWithCircularIndicator.requestLayout();

			int year = getYearFromTextView(textViewWithCircularIndicator);
			textViewWithCircularIndicator.drawIndicator(YearPickerView.this.mController.getSelectedDay().year == year);

			return textViewWithCircularIndicator;
		}
	}
}