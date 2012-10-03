package org.vandymobile.dining.util;

import android.text.format.Time;

import com.google.android.maps.GeoPoint;

/**
 * @author Matthew Lavin
 */

public class Location {

    private String[] mHours = new String[7];
    public int mId;
    public String mDescription;
    public String mName;
    public GeoPoint mLocation;
    public String mPhone;
    public String mUrl;
    public boolean mMealMoney;
    public boolean mMealPlan;
    public boolean mOnCampus;
    
    public Location(int id, String name, String type, float lat, float lon, String phone, String url, String sunday, String monday,
            String tuesday, String wednesday, String thursday, String friday, String saturday, int on_campus, int meal_plan, int meal_money){
        mId = id;
        mName = name;
        mDescription = type;
        mLocation = new GeoPoint((int)(lat*1000000), (int)(lon*1000000));
        mPhone = phone;
        mUrl = url;
        mHours[0] = sunday;
        mHours[1] = monday;
        mHours[2] = tuesday;
        mHours[3] = wednesday;
        mHours[4] = thursday;
        mHours[5] = friday;
        mHours[6] = saturday;
        mOnCampus = (on_campus == 1);
        mMealPlan = (meal_plan == 1);
        mMealMoney = (meal_money == 1);
    }
    
    /**
     * Get today's hours
     * @return: A string representing today's hours
     */
    public String getHours(){
        Time now = new Time();
        now.setToNow();

        int curDay = now.weekDay;//Scheme of this should be 0 = Sunday through 6 = Saturday
        if (now.hour <= 4){ //this call assumes that if it is before 5am you want the hours for YESTERDAY
            //e.g. if it is 1:00am on a Tuesday, you want to look at Monday's hours for locations
            curDay--;
        }
        
        if (curDay == -1){
            curDay = 6;
        }
        
        return mHours[curDay];
    }
    
    /**
     * Get the hours for a specific day
     * @param day: The day the hours should be for
     * @return: The hours for the day input
     */
    public String getHours(int day){
        return mHours[day];
    }
}