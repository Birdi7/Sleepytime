package com.sleepytime;

/**
 * My own class represents alarm
 * Created by Egor on 7/25/2016.
 */

import java.util.Locale;
import java.util.Objects;

class MyAlarm {
    private final int hour;
    private final int minutes;
    private final boolean is24Hour;
    private String message;

    public MyAlarm(int hour, int minutes, boolean is24Hour) {
        this.hour = hour;
        this.minutes = minutes;
        this.is24Hour = is24Hour;
        this.message = null;
    }

    public MyAlarm(int hour, int minutes, boolean is24Hour, String message) {
        this.hour = hour;
        this.minutes = minutes;
        this.is24Hour = is24Hour;
        this.message = message;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyAlarm myAlarm = (MyAlarm) o;
        return getHour() == myAlarm.getHour() &&
                getMinutes() == myAlarm.getMinutes() &&
                is24Hour == myAlarm.is24Hour &&
                Objects.equals(getMessage(), myAlarm.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHour(), getMinutes(), is24Hour, getMessage());
    }

    /**
     * @return alarm as "HOUR:MINUTE" or "HOUR:MINUTE am/pm" depending on is24Hour
     */
    @Override
    public String toString() {
        return is24Hour ?
                String.format(Locale.getDefault(), "%02d:%02d", hour, minutes) :
                String.format(Locale.getDefault(), "%02d:%02d %s", hour % 12, minutes, (hour < 12) ? " am" : " pm");
    }

}
