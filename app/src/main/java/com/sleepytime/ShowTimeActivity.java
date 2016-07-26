package com.sleepytime;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ShowTimeActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_HOUR = "EXTRA_HOUR";
    public static final String EXTRA_MINUTES = "EXTRA_MINUTES";

    private final int minutesInHour = 60;

    private int[] introTexts = {R.string.text_intro_fall_asleep, R.string.text_intro_wake_up, R.string.text_intro_time_go_to_bed_now};

    private boolean isExtraAlarmSet;
    private MyAlarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time);

        MainActivity.numberOfClickedOnAddExtraAlarmButton = 0;

        final int id = getIntent().getIntExtra(EXTRA_ID, -1);
        int hour = getIntent().getIntExtra(EXTRA_HOUR, -1);
        int minutes = getIntent().getIntExtra(EXTRA_MINUTES, -1);
        checkNonNegative(id, hour, minutes); //handle bug.

        ((TextView) findViewById(R.id.text_intro_show_time))
                .setText(getString(introTexts[id])); //set intro text

        final List<MyAlarm> myAlarms = (id == 0) ? calculateTimeToFallAsleep(hour, minutes) : calculateTimeToGoToBed(hour, minutes); // list of myAlarms


        ListView listView = (ListView) findViewById(R.id.list_view_with_alarms);
        ArrayAdapter<MyAlarm> adapter = new ArrayAdapter<>(ShowTimeActivity.this,
                android.R.layout.simple_selectable_list_item,
                myAlarms);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
                MyAlarm myAlarm = myAlarms.get((int) var4);
                myAlarm.setMessage(getString((id == 0) ? R.string.alarm_message_go_to_bed : R.string.alarm_message_wake_up));
                setAlarm(myAlarm);
            }
        });
    }


    /**
     * Start AlarmClock.ACTION_SET_ALARM intent
     *
     * @param myAlarm alarm represents as object of com.sleepytime.MyAlarm
     */
    private void setAlarm(MyAlarm myAlarm) {
        isExtraAlarmSet = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, false);
        alarm = myAlarm;

        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, myAlarm.getMessage());
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, myAlarm.getHour());
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, myAlarm.getMinutes());
        startActivity(alarmIntent);
    }

    /**
     * @param myAlarm alarm represents as object of com.sleepytime.MyAlarm
     */
    private void setExtraAlarm(MyAlarm myAlarm) {
        int newHour = myAlarm.getHour() + (myAlarm.getMinutes() + 30) / 60;
        int newMinutes = (myAlarm.getMinutes() + 30) % 60;

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, myAlarm.getMessage());
        intent.putExtra(AlarmClock.EXTRA_HOUR, newHour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, newMinutes);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isExtraAlarmSet) {
            setExtraAlarm(alarm);
        }
        isExtraAlarmSet = false;
        alarm = null;
    }

    /**
     * Check if all numbers are non negative
     * @param numbers
     * @throws IllegalStateException if at least one number is negative
     *
     */
    private void checkNonNegative(int... numbers) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] < 0) {
                String message = "%s is negative!";
                switch (i) {
                    case 0:
                        message = String.format(message, "Id");
                        break;
                    case 1:
                        message = String.format(message, "Hour");
                        break;
                    case 2:
                        message = String.format(message, "Minutes");
                        break;
                    default:
                        message = String.format(message, String.valueOf(i));
                }
                throw new IllegalStateException(message);
            }
        }
    }

    @NonNull
    private List<MyAlarm> calculateTimeToFallAsleep(int hour, int minutes) {
        List<MyAlarm> myAlarms = new LinkedList<>();
        int userTimeToFallAsleep = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREFERENCES_USER_TIME, 14);

        if (minutes - userTimeToFallAsleep >= 0) {
            minutes -= userTimeToFallAsleep;
        }
        else {
            minutes -= userTimeToFallAsleep;
            minutes = minutesInHour - Math.abs(minutes);
        }

        minutes += hour * minutesInHour;

        if (minutes - minutesInHour *9 >= 0)
            minutes -= minutesInHour * 9;
        else {
            minutes = 24 * minutesInHour + minutes - minutesInHour * 9;
        }

        for (int i = 0; i < 4; i++) {
            int alarmHour = minutes / minutesInHour;
            int alarmMinutes = minutes % minutesInHour;
            boolean is24Hour = DateFormat.is24HourFormat(ShowTimeActivity.this);
            myAlarms.add(new MyAlarm(alarmHour, alarmMinutes, is24Hour));
            minutes += 90; // + 1.5 hour
        }
        return myAlarms;
    }


    @NonNull
    private List<MyAlarm> calculateTimeToGoToBed(int hour, int minutes) {
        List<MyAlarm> myAlarms = new LinkedList<>();
        int userTimeToFallAsleep = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREFERENCES_USER_TIME, 14);

        hour += (minutes + userTimeToFallAsleep) / minutesInHour;
        minutes = (minutes + userTimeToFallAsleep) % minutesInHour;
        minutes += hour * minutesInHour;
        for (int i = 0; i < 6; i++) {
            minutes += 90; // + 1.5 hour
            int alarmHour = (minutes / minutesInHour) % 24;
            int alarmMin = minutes % minutesInHour;
            boolean is24Hour = DateFormat.is24HourFormat(ShowTimeActivity.this);
            myAlarms.add(new MyAlarm(alarmHour, alarmMin, is24Hour));
        }
        return myAlarms;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.add_extra_alarm);
        boolean isChecked = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, false);
        item.setChecked(isChecked);
        item.setIcon(isChecked ? R.drawable.ic_alarm_off_white : R.drawable.ic_alarm_add_white);

        return true;
    }


    @TargetApi(11)
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowTimeActivity.this);
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

                FrameLayout parent = new FrameLayout(ShowTimeActivity.this);
                parent.addView(picker, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                ));
                builder.setView(parent);
                builder.create().show();
                return true;
            case R.id.add_extra_alarm:
                if (++MainActivity.numberOfClickedOnAddExtraAlarmButton == 7) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(MainActivity.PREFERENCES_SHOW_ADD_EXTRA_ALARM_DESCRIPTION, false);
                    editor.apply();
                }

                boolean showDescription = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_SHOW_ADD_EXTRA_ALARM_DESCRIPTION, true);

                if (!item.isChecked() && showDescription) {
                    String description = getString(R.string.description_add_extra_alarm);
                    Toast.makeText(ShowTimeActivity.this, String.format(Locale.getDefault(), description, 7 - MainActivity.numberOfClickedOnAddExtraAlarmButton), Toast.LENGTH_SHORT).show();
                }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.add_extra_alarm);
        boolean isChecked = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.PREFERENCES_EXTRA_ALARM, false);
        item.setIcon(isChecked ? R.drawable.ic_alarm_off_white : R.drawable.ic_alarm_add_white);
        return super.onPrepareOptionsMenu(menu);
    }
}