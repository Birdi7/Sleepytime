package com.sleepytime;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    boolean is24hour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        is24hour = DateFormat.is24HourFormat(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            case R.id.action_settings:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickHaveToWakeUp(View view) {

    }

}
/*
 HOW TO SET ALARM:
    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
intent.putExtra(AlarmClock.EXTRA_HOUR, 23);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, 22);
        startActivity(intent);*/
