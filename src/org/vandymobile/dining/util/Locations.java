package org.vandymobile.dining.util;

import java.io.IOException;

import org.vandymobile.dining.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Matthew Lavin
 */

public class Locations {

    private static Locations instance = null;
    public Restaurant[] mLocations;
    public int mCount;
    private int[] mIds;
    
    /**
     * Grabs all of the data and stores it in an array. This ensures we don't have to ever deal with cursors or any slow DB operations
     * in our activities. It is implemented as a singleton so all the activities can reference the same object, this should 
     * minimize memory usage. 
     * @param context: the application context (for getting the database)
     */
    protected Locations(Context context) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
         
        try {
            myDbHelper.openDataBase();
        } catch(SQLException sqle) {
            throw sqle;
        }
        
        SQLiteDatabase diningDatabase = myDbHelper.getReadableDatabase();
        
        String[] where = {"_id","name","type","lat","long","phone","url","sunday_hours","monday_hours","tuesday_hours","wednesday_hours"
                ,"thursday_hours","friday_hours","saturday_hours","on_campus","meal_plan","meal_money"};
        Cursor locationsCursor = diningDatabase.query("dining", where, null, null, null, null, "name");
        
        locationsCursor.moveToFirst();
        mCount = locationsCursor.getCount();
        mLocations = new Restaurant[mCount];
        mIds = new int[mCount];
        
        for (int i = 0;i < mCount; i++) {
            mLocations[i] = new Restaurant(locationsCursor.getInt(0), locationsCursor.getString(1), locationsCursor.getString(2),
                    locationsCursor.getFloat(3),locationsCursor.getFloat(4), locationsCursor.getString(5),locationsCursor.getString(6),
                    locationsCursor.getString(7),locationsCursor.getString(8),locationsCursor.getString(9),locationsCursor.getString(10),
                    locationsCursor.getString(11),locationsCursor.getString(12),locationsCursor.getString(13),locationsCursor.getInt(14),
                    locationsCursor.getInt(15),locationsCursor.getInt(16));
            mIds[mLocations[i].mId-1] = i;
            locationsCursor.move(1);
        }
        
        locationsCursor.close();
        diningDatabase.close();
        myDbHelper.close();
    }
    
    /**
     * Makes the instance and returns it if necessary, else just returns the already-created instance
     * @param context: the application context (for getting the database)
     * @return: an instance of this class
     */
    public static Locations getInstance(Context context) {
        if (instance == null) {
            instance = new Locations(context);
            return instance;
        } else {
            return instance;
        }
    }
    
    /**
     * Returns the restaurant object based on its ID
     * @param id: The id of the restaurant
     * @return: the actual restaurant object
     */
    public Restaurant findRestaurantById(int id){
        return mLocations[mIds[id-1]];
    }
}