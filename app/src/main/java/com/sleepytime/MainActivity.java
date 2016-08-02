package com.sleepytime;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends MenuActivity
        implements TimePickerDialog.OnTimeSetListener {

/*
    public static final String PREFERENCES_USER_TIME = "PREFERENCES_USER_TIME";
    public static final String PREFERENCES_EXTRA_ALARM = "PREFERENCES_EXTRA_ALARM";
    public static final String PREFERENCES_SHOW_ADD_EXTRA_ALARM_DESCRIPTION = "PREFERENCES_SHOW_ADD_EXTRA_ALARM_DESCRIPTION";
*/

    private int onClickButtonId;
    public static int numberOfClickedOnAddExtraAlarmButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.numberOfClickedOnAddExtraAlarmButton = 0;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }


    public void onClickSetTimeWakeUp(View view) {
        if (view.getId() == R.id.calculate_time_to_wake_up_button)
            onClickButtonId = 0;
        else if (view.getId() == R.id.calculate_time_plan_to_fall_asleep_button)
            onClickButtonId = 1;

        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                MainActivity.this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));
        dialog.show();
    }

    public void onTimeSet(TimePicker picker, int hour, int minutes) {
        showAlarms(hour, minutes, onClickButtonId);
    }

    private void showAlarms(int hour, int minutes, int id) {
        Intent intent = new Intent(MainActivity.this, ShowTimeActivity.class);
        intent.putExtra(ShowTimeActivity.EXTRA_HOUR, hour);
        intent.putExtra(ShowTimeActivity.EXTRA_MINUTES, minutes);
        intent.putExtra(ShowTimeActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    public void onClickGoToBedNowButton(View view) {
        Calendar calendar = Calendar.getInstance();
        showAlarms(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 2);
    }
}
