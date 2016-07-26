package com.sleepytime;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener {

    public static final String PREFERENCES_NAME = "sleepytime_preferences";
    public static final String PREFERENCES_USER_TIME = "PREFERENCES_USER_TIME";
    public static final String PREFERENCES_EXTRA_ALARM = "PREFERENCES_EXTRA_ALARM";

    private int onClickButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem extraAlarmView = menu.findItem(R.id.add_extra_alarm);
        boolean isChecked = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, false);
        extraAlarmView.setChecked(isChecked);
        extraAlarmView.setIcon(isChecked ? R.drawable.ic_alarm_off_white : R.drawable.ic_alarm_add_white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        final SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);

        switch (id) {
            case R.id.set_time_to_fall_asleep:
                final NumberPicker picker = new NumberPicker(this);
                picker.setMinValue(0);
                picker.setMaxValue(30);
                picker.setValue(sharedPreferences.getInt(MainActivity.PREFERENCES_USER_TIME, 14));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.set_your_average_time_to_fall_asleep);
                builder.setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(MainActivity.PREFERENCES_USER_TIME, picker.getValue());
                        editor.commit();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                FrameLayout parent = new FrameLayout(MainActivity.this);
                parent.addView(picker, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                ));
                builder.setView(parent);
                builder.create().show();
                return true;
            case R.id.add_extra_alarm:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, !item.isChecked());
                editor.commit();

                item.setChecked(!item.isChecked());
                invalidateOptionsMenu();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.add_extra_alarm);
        boolean isChecked = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, false);
        item.setIcon(isChecked ? R.drawable.ic_alarm_off_white : R.drawable.ic_alarm_add_white);
        return super.onPrepareOptionsMenu(menu);
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
