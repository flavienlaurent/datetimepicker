package com.fourmob.datetimepicker.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MinMaxDateActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_min_max_date);

    if (savedInstanceState == null) {
      showMinMaxFragment();
    }
  }

  private void showMinMaxFragment() {
    MinMaxDateFragment minMaxDateFragment = MinMaxDateFragment.newInstance();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.content, minMaxDateFragment);
    fragmentTransaction.commit();
  }
}
