package org.vandymobile.dining.util;

import java.text.DecimalFormat;

import org.vandymobile.dining.DiningListView;

import android.location.Location;
import android.text.format.Time;

import com.google.android.maps.GeoPoint;

/**
 * @author Matthew Lavin
 */

public class Restaurant {

    private final String[] mHours = new String[7];
    public final int mId;
    public final String mDescription;
    public final String mName;
    public final GeoPoint mLocation;
    public final String mPhone;
    public final String mUrl;
    public final boolean mMealMoney;
    public final boolean mMealPlan;
    public final boolean mOnCampus;
    private Time now;
    
    public Restaurant(int id, String name, String type, float lat, float lon, String phone, String url, String sunday, String monday,
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
        now = null;
    }
    
    /**
     * Get today's hours
     * @return: A string representing today's hours
     */
    public String getHours(){
        if (now == null){
            now = new Time();
        }
        now.setToNow();

        int curDay = getCurDay();
        
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
    
    /**
     * Finds the current day, on a scale from 0 to 6. Make sure the now variable is initialized - this doesn't check
     * @return: the current day as an integer
     */
    private int getCurDay(){
        int curDay = now.weekDay;//Scheme of this should be 0 = Sunday through 6 = Saturday
        if (now.hour <= 4){ //this call assumes that if it is before 5am you want the hours for YESTERDAY
            //e.g. if it is 1:00am on a Tuesday, you want to look at Monday's hours for locations
            curDay--;
        }
        
        if (curDay == -1){
            curDay = 6;
        }
        return curDay;
    }
    
    /**
     * Whether the restaurant is open right now or not
     * @return: status of the restaurant
     */
    public boolean isOpen(){
        if (now == null){
            now = new Time();
        }
        now.setToNow();
        
        int curDay = getCurDay();
        
        String hours = getHours(curDay);
        
        String status = DiningListView.isOpen(DiningListView.parseHours(hours), now);
        
        if (status.startsWith("Open 2") || status.startsWith("Closi") || status.startsWith("Open!")){
        	return true;
        } else {
        	return false;
        }
    }
    
    /**
     * getDistance: determines the distance, in miles, between this Restaurant and the GeoPoint param
     * @param pointA: the point from which the distance to the restaurant is being measured
     * @return: the distance, in miles, between the two points. Is a float. 
     */
    public double getDistance(GeoPoint pointA){
        Location locationA = new Location("point A");
    
        locationA.setLatitude(pointA.getLatitudeE6() / 1E6);
        locationA.setLongitude(pointA.getLongitudeE6() / 1E6);
    
        Location locationB = new Location("point B");
    
        locationB.setLatitude(mLocation.getLatitudeE6() / 1E6);
        locationB.setLongitude(mLocation.getLongitudeE6() / 1E6);
    
        return roundDouble(locationA.distanceTo(locationB)/1609.34);//return in miles, not meters
    }
    
    /**
     * roundDouble: Takes in a Double value and rounds it to a single decimal place
     * @param d: the Double value
     * @return: The double value, rounded to have one place after the decimal
     */
    double roundDouble(double d) {
        DecimalFormat twoDForm = new DecimalFormat("##.#");
    return Double.valueOf(twoDForm.format(d));
    }
}