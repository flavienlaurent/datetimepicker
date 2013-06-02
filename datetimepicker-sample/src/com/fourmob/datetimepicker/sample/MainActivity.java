package com.fourmob.datetimepicker.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final DatePickerDialog datePickerDialog = new DatePickerDialog();
		datePickerDialog.setOnDateSetListener(new OnDateSetListener() {

			@Override
			public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
				Toast.makeText(MainActivity.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
			}
		});

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				datePickerDialog.show(getSupportFragmentManager(), "datepicker");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
