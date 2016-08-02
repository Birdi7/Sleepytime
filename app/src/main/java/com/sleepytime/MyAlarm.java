package com.sleepytime;


import java.util.Locale;
import java.util.Objects;

/**
 * My own class represents alarm
 *
 * Created by Egor on 7/25/2016.
 */
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

    /**
     * This method is not depend on is24Hour
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyAlarm myAlarm = (MyAlarm) o;
        return this.getHour() == myAlarm.getHour() &&
                this.getMinutes() == myAlarm.getMinutes() &&
                Objects.equals(this.getMessage(), myAlarm.getMessage());
    }

    /**
     * This method is not depend on is24Hour
     */
    @Override
    public int hashCode() {
        return Objects.hash(getHour(), getMinutes(), getMessage());
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
