package com.huddle.handle.client.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONObject;

import android.text.format.DateFormat;

public class Date {

    private String minute;
    private String hour;
    private String day;
    private String month;
    private String year;
    private String formattedDate;
    
    public Date(java.util.Date d) {
    	Calendar cal = new GregorianCalendar();
    	cal.setTime(d);
    	day = cal.get(Calendar.DATE) + "";
    	hour = cal.get(Calendar.HOUR) + "";
    	month = cal.get(Calendar.MONTH) + "";
    	year = cal.get(Calendar.YEAR) + "";
    	minute = cal.get(Calendar.MINUTE) + "";
		formattedDate = (String) DateFormat.format("DD/MM", d);
    }

    public Date(JSONObject json) {
        if(json == null) return;
        minute = json.optString("Minute");
        hour = json.optString("Hour");
        day = json.optString("Day");
        month = json.optString("Month");
        year = json.optString("Year");
        formattedDate = json.optString("FormattedDate");

        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
    }

    public String getMinute() {
        return minute;
    }

    public String getHour() {
        return hour;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getShortTimeOrDate() {
        if (day == null) return "";
        Calendar now = Calendar.getInstance();
        now.setTime(new java.util.Date());
        if (now.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(day)) {
            return day + "/" + month;
        } else {
            return hour + ":" + minute;
        }
    }

}
