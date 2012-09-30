package org.vandymobile.dining.util;

import com.google.android.maps.GeoPoint;

/**
 * @author Matthew Lavin
 */

public class Location {

    public String[] mHours = new String[7];
    public int mId;
    public String description;
    public String mName;
    public GeoPoint mLocation;
    public String mPhone;
    public String mUrl;
    public boolean mMealMoney;
    public boolean mMealPlan;
    public boolean mOnCampus;
    
    public Location(){
        
    }
}