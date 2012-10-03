package org.vandymobile.dining.util;

import com.google.android.maps.GeoPoint;

/**
 * @author Matthew Lavin
 */

public class Location {

    public String[] mHours = new String[7];
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
}