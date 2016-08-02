package com.sleepytime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class ShowTimeActivity extends MenuActivity {
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_HOUR = "EXTRA_HOUR";
    public static final String EXTRA_MINUTES = "EXTRA_MINUTES";

    private final int minutesInHour = 60;
    private final int delayForExtraAlarmAfterPrimaryAlarm = 35;


    private boolean isExtraAlarmSet;
    private MyAlarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time);

        MainActivity.numberOfClickedOnAddExtraAlarmButton = 0;
        setUpAlarmsUI();
    }

    private void setUpAlarmsUI() {
        final int id = getIntent().getIntExtra(EXTRA_ID, -1);
        int hour = getIntent().getIntExtra(EXTRA_HOUR, -1);
        int minutes = getIntent().getIntExtra(EXTRA_MINUTES, -1);
        checkNonNegative(id, hour, minutes); //handle bug.

        int introStringId;
        switch (id) {
            case 1:
                introStringId = R.string.text_intro_fall_asleep;
                break;
            case 2:
                introStringId = R.string.text_intro_time_go_to_bed_now;
                break;
            default:
                introStringId = R.string.text_intro_wake_up;
        }
        ((TextView) findViewById(R.id.text_intro_show_time))
                .setText(getString(introStringId)); //set intro text

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
        isExtraAlarmSet = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BuildConfig.PREF_EXTRA_ALARM, false);
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
        int newHour = myAlarm.getHour() + (myAlarm.getMinutes() + delayForExtraAlarmAfterPrimaryAlarm) / 60;
        int newMinutes = (myAlarm.getMinutes() + delayForExtraAlarmAfterPrimaryAlarm) % 60;

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
        int userTimeToFallAsleep = PreferenceManager.getDefaultSharedPreferences(this).getInt(BuildConfig.PREF_USER_TIME, 14);

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
        int userTimeToFallAsleep = PreferenceManager.getDefaultSharedPreferences(this).getInt(BuildConfig.PREF_USER_TIME, 14);

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

    /**
     * Handling tap on timepicker's positive button
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface, i);
        setUpAlarmsUI();
    }
}