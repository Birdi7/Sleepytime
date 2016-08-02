package com.sleepytime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Locale;

public abstract class MenuActivity extends AppCompatActivity
    implements DialogInterface.OnClickListener {

    private NumberPicker picker = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem extraAlarmView = menu.findItem(R.id.add_extra_alarm);
        boolean isChecked = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BuildConfig.PREF_EXTRA_ALARM, false);
        extraAlarmView.setChecked(isChecked);
        extraAlarmView.setIcon(isChecked ? R.drawable.ic_alarm_off_white : R.drawable.ic_alarm_add_white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        switch (id) {
            case R.id.set_time_to_fall_asleep:
                picker = new NumberPicker(this);
                picker.setMinValue(0);
                picker.setMaxValue(30);
                picker.setValue(sharedPreferences.getInt(BuildConfig.PREF_USER_TIME, 14));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.set_your_average_time_to_fall_asleep);
                builder.setPositiveButton(R.string.set_time, this); //look at OnClick method

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                FrameLayout parent = new FrameLayout(this);
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
                    editor.putBoolean(BuildConfig.PREF_SHOW_ADD_EXTRA_ALARM_DESCRIPTION, false);
                    editor.apply();
                }

                boolean showDescription = sharedPreferences.getBoolean(BuildConfig.PREF_SHOW_ADD_EXTRA_ALARM_DESCRIPTION, true);

                if (!item.isChecked() && showDescription) {
                    String description = getString(R.string.description_add_extra_alarm);
                    Toast.makeText(this, String.format(Locale.getDefault(), description, 7 - MainActivity.numberOfClickedOnAddExtraAlarmButton), Toast.LENGTH_SHORT).show();
                }


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(BuildConfig.PREF_EXTRA_ALARM, !item.isChecked());
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
        boolean isChecked = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BuildConfig.PREF_EXTRA_ALARM, false);
        item.setIcon(isChecked ? R.drawable.ic_alarm_on_white_36dp : R.drawable.ic_alarm_off_white);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handling tap on timepicker's positive button
     *
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (picker != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(BuildConfig.PREF_USER_TIME, picker.getValue());
            editor.commit();
        }
    }

}
