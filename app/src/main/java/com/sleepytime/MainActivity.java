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


    public static final String USER_TIME_TO_FALL_ASLEEP_EXTRA = "USER_TIME_TO_FALL_ASLEEP_EXTRA";
    private int onClickButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        UserData.setUserTimeToFallAsleep(sharedPreferences.getInt(USER_TIME_TO_FALL_ASLEEP_EXTRA, 14));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.set_time_to_fall_asleep:
                final NumberPicker picker = new NumberPicker(this);
                picker.setMinValue(0);
                picker.setMaxValue(30);
                picker.setValue(UserData.getUserTimeToFallAsleep());

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.set_your_average_time_to_fall_asleep);
                builder.setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserData.setUserTimeToFallAsleep(picker.getValue());
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_TIME_TO_FALL_ASLEEP_EXTRA, UserData.getUserTimeToFallAsleep());
        editor.apply();

    }

    public void onClickSetTimeWakeUp(View view) {
        if (view == findViewById(R.id.calculate_time_wake_up_button))
            onClickButtonId = 0;
        else if (view == findViewById(R.id.calculate_time_plan_to_fall_asleep_button))
            onClickButtonId = 1;

        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                MainActivity.this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));
        dialog.show();
        view.getId();
    }

    public void onTimeSet(TimePicker picker, int hour, int minutes) {
        setAlarm(hour, minutes, onClickButtonId);
    }

    private void setAlarm(int hour, int minutes, int id) {
        Intent intent = new Intent(MainActivity.this, ShowTimeActivity.class);
        intent.putExtra(ShowTimeActivity.EXTRA_HOUR, hour);
        intent.putExtra(ShowTimeActivity.EXTRA_MINUTES, minutes);
        intent.putExtra(ShowTimeActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    public void onClickGoToBedNowButton(View view) {
        Calendar calendar = Calendar.getInstance();
        setAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 2);
    }
}
