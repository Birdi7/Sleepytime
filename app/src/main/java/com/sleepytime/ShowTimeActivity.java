package com.sleepytime;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
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

import java.util.ArrayList;
import java.util.Locale;

public class ShowTimeActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_HOUR = "EXTRA_HOUR";
    public static final String EXTRA_MINUTES = "EXTRA_MINUTES";

    private int[] idsOfTexts = {R.string.text_intro_fall_asleep, R.string.text_intro_wake_up, R.string.text_intro_time_go_to_bed_now};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time);

        final int id = getIntent().getIntExtra(EXTRA_ID, -1);
        ((TextView) findViewById(R.id.text_intro_show_time))
                .setText(getString(idsOfTexts[id])); //set text

        int hour = getIntent().getIntExtra(EXTRA_HOUR, -1);
        int minutes = getIntent().getIntExtra(EXTRA_MINUTES, -1);

        if (hour == -1 || minutes == -1) {
            Toast.makeText(ShowTimeActivity.this, "Bug!", Toast.LENGTH_SHORT).show();
            return;
        }
        final ArrayList<int[]> alarms = (id == 0) ? calculateTimeToFallAsleep(hour, minutes) : calculateTimeToGoToBed(hour, minutes);
        String[] buttons = makeButtons(alarms);


        ListView listView = (ListView) findViewById(R.id.list_view_with_alarms);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShowTimeActivity.this,
                android.R.layout.simple_list_item_1,
                buttons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
                int[] alarmTime = alarms.get(var3);

                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString((id == 0) ? R.string.alarm_message_go_to_bed : R.string.alarm_message_wake_up));
                intent.putExtra(AlarmClock.EXTRA_HOUR, alarmTime[0]);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, alarmTime[1]);
                startActivity(intent);
            }
        });
    }

    private ArrayList<int[]> calculateTimeToFallAsleep(int hour, int minutes) {
        ArrayList<int[]> alarms = new ArrayList<>();
        minutes -= getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREF_USER_TIME, 14);
        minutes += hour * 60;
        if (minutes - 60*9 >= 0)
            minutes -= 60 * 9;
        else {
            minutes = 24 * 60 + minutes - 60 * 9;
        }
        for (int i = 0; i < 4; i++) {
            int alarmHour = minutes / 60;
            int alarmMinutes = minutes % 60;
            alarms.add(new int[] {alarmHour, alarmMinutes});
            minutes += 1.5 * 60; //1.5 hour multiplies minutes in a hour
        }
        return alarms;
    }

    private ArrayList<int[]> calculateTimeToGoToBed(int hour, int minutes) {
        ArrayList<int[]> alarms = new ArrayList<>();
        hour += (minutes + getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREF_USER_TIME, -1)) / 60;
        minutes = (minutes + getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREF_USER_TIME, -1)) % 60;
        minutes += hour * 60;
        for (int i = 0; i < 6; i++) {
            minutes += 90;
            int newHour = (minutes / 60) % 24;
            int newMin = minutes % 60;
            alarms.add(new int[] {newHour, newMin});
        }
        return alarms;
    }

    private String[] makeButtons(ArrayList<int[]> alarms) {
        ArrayList<String> buttons = new ArrayList<>();
        for (int[] alarm : alarms) {
            int hour = alarm[0];
            int minutes = alarm[1];
            String newTime;
            if (DateFormat.is24HourFormat(ShowTimeActivity.this))
                newTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minutes);
            else {
                String ampm = (hour < 12) ? " am" : " pm";
                newTime = String.format(Locale.getDefault(), "%02d:%02d", hour % 12, minutes).concat(ampm);
            }
            buttons.add(newTime);
        }
        return buttons.toArray(new String[1]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(11)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.set_time_to_fall_asleep:
                final NumberPicker picker = new NumberPicker(this);
                picker.setMinValue(0);
                picker.setMaxValue(30);
                picker.setValue(getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(MainActivity.PREF_USER_TIME, -1));

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowTimeActivity.this);
                builder.setTitle(R.string.set_your_average_time_to_fall_asleep);
                builder.setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
                        editor.putInt(MainActivity.PREF_USER_TIME, picker.getValue());
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
        }
        return super.onOptionsItemSelected(item);
    }

}
/*
 TODO: HOW TO SET ALARM:
    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
    intent.putExtra(AlarmClock.EXTRA_HOUR, 23);
    intent.putExtra(AlarmClock.EXTRA_MINUTES, 22);
    startActivity(intent);*/
//DateFormat.is24HourFormat(this));