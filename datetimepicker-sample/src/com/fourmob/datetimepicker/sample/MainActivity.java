package com.fourmob.datetimepicker.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {

					@Override
					public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
						Toast.makeText(MainActivity.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
					}
				}, 2007, 10, 1, ((CheckBox)findViewById(R.id.checkBoxVibrate)).isChecked());
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.show(getSupportFragmentManager(), "datepicker");
			}
		});
	}
}
