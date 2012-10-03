package org.vandymobile.dining;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.vandymobile.dining.util.Locations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Matthew Lavin
 */

public class LocationDetails extends Activity {
    
    private TextView mDay;
    private TextView mRange;
    private static int today = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
    private int curHoursDisplay;
    private static DatabaseHelper myDbHelper;
    private static SQLiteDatabase diningDatabase;
    private static Long id;
    private Cursor mHours; 
    private static Locations loc;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_details);
        
        myDbHelper = new DatabaseHelper(this);
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
        diningDatabase = myDbHelper.getReadableDatabase();
        id = this.getIntent().getLongExtra("id", -1); //get the location ID from the intent
        loadLocation(id); //set the name and other (non-changing) things for the location
        mHours = getHours(today, diningDatabase);//during init, load the current hours as today's hours
        curHoursDisplay = today; //this keeps track of what day is currently displayed in the hours box
        
        loc = Locations.getInstance(getApplicationContext());
        
        mDay = (TextView) findViewById(R.restaurantDetails.hoursDay);
        mRange = (TextView) findViewById(R.restaurantDetails.hoursRangeDisplay);
        if (mHours.moveToFirst()){
            updateRangeText(today, parseHours(mHours.getString(0)), 1);
        } else {
            //something broke.
        }
        Toast.makeText(getApplicationContext(), "TODAY IS ["+getCurrentDay(today)+"]", Toast.LENGTH_SHORT).show();
        ((ImageView) findViewById(R.restaurantDetails.rightArrow)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                curHoursDisplay = (curHoursDisplay+1);
                if (curHoursDisplay > 7){
                    curHoursDisplay = curHoursDisplay - 7;
                }
                mHours = getHours(curHoursDisplay, diningDatabase);
                String[] newhours = null;
                if (mHours.moveToFirst()){
                    newhours = parseHours(mHours.getString(0));
                } else {
                    //something broke. 
                }
                if (newhours != null){
                    updateRangeText(curHoursDisplay, newhours, 1);//so far only supporting the first set of hours each day
                }
                
                
            }
        });
        ((ImageView) findViewById(R.restaurantDetails.leftArrow)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                curHoursDisplay = (curHoursDisplay-1);
                if (curHoursDisplay < 1){
                    curHoursDisplay = curHoursDisplay + 7;
                }
                mHours = getHours(curHoursDisplay, diningDatabase);
                String[] newhours = null;
                if (mHours.moveToFirst()){
                    newhours = parseHours(mHours.getString(0));
                } else {
                    //something broke. 
                }
                if (newhours != null){
                    updateRangeText(curHoursDisplay, newhours, 1);//so far only supporting the first set of hours each day
                }
            }
        });

        MenuPagerAdapter adapter = new MenuPagerAdapter();
        ViewPager myPager = (ViewPager) findViewById(R.id.menu_pager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(2);
    }

    private void updateRangeText(int _day, String[] newhours, int pos) {
        mDay.setText(getCurrentDay(_day));
        if (newhours[0] == null){
            mRange.setText("closed");
        } else if (newhours[0].equals(newhours[1])){ //if open and close times are the same
            mRange.setText("Open 24/7!");
        } else {
            if (pos == 1){
                mRange.setText(normalizeHours(newhours[0])+" - "+normalizeHours(newhours[1]));
            } else if (pos == 2) {
                mRange.setText(normalizeHours(newhours[2])+" - "+normalizeHours(newhours[3]));
            }
        }
    }

    private CharSequence getCurrentDay(int _day) {
        switch (_day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "";
        } 
    }

    private Cursor getHours(int i, SQLiteDatabase _db){
        switch (i){
            case Calendar.SUNDAY:
                String[] tmp = {"sunday_hours"};
                return _db.query("dining", tmp, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.MONDAY:
                String[] tmp1 = {"monday_hours"};
                return _db.query("dining", tmp1, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.TUESDAY:
                String[] tmp2 = {"tuesday_hours"};
                return _db.query("dining", tmp2, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.WEDNESDAY:
                String[] tmp3 = {"wednesday_hours"};
                return _db.query("dining", tmp3, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.THURSDAY:
                String[] tmp4 = {"thursday_hours"};
                return _db.query("dining", tmp4, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.FRIDAY:
                String[] tmp5 = {"friday_hours"};
                return _db.query("dining", tmp5, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.SATURDAY:
                String[] tmp6 = {"saturday_hours"};
                return _db.query("dining", tmp6, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            default:
                return null;
        }
    }
    public String[] parseHours(String _in){
        String[] ret = {null,null,null,null};
        if (_in == "null"){
            return ret;
        }
        for (int i = 0; i < _in.length(); i++){
            if (_in.charAt(i)== ','){
                ret[0]= _in.substring(0, i);//once you reach the first comma, break off the first start hour into ret[0]
                for (int x = i; x < _in.length();x++){
                    if (_in.charAt(x) == ';'){//if you hit a semicolon, there are two start and end times today. 
                                              //Don't worry, we'll use recursion!
                        ret[1] = _in.substring(i+1,x);
                        String[] tmp2 = parseHours(_in.substring(x+1));
                        ret[2] = tmp2[0]; ret[3] = tmp2[1];
                        break;
                    } else if (x == _in.length() -1){//if you hit the end of the string and haven't found a semicolon, don't worry! 
                                                     //there are only one set of hours for today.

                        ret[1] = _in.substring(i+1);//grab the second hour string into ret[2] - 
                                                                    //don't worry about ret[3] and ret[4], we'll check for null later
                    }
                }
                break;
            }
        }
        
        return ret;
    }
    
    private String normalizeHours(String hours){
        String res = "Invalid time input";
        int first;
        if (hours.contains(":")){
            first = Integer.parseInt(hours.substring(0, hours.indexOf(":")));
            
            if ((first > 0) && (first < 12) ){
                res = hours + " am";
            } else if ((first > 12) && (first < 24)){
                res = (first - 12) + hours.substring(2) + " pm";
            } else if (first == 0){
                res = 12 + hours.substring(1) + " am";
            } else if (first == 12){
                res = hours + " pm";
            }
        }
        
        return res;
    }
    
    private void loadLocation(Long id){
        Cursor locationCursor = diningDatabase.query("dining", null, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
        String name = "default name";
        if (locationCursor.moveToFirst()){
            name = locationCursor.getString(locationCursor.getColumnIndex("name"));
        }
        
        Toast.makeText(getApplicationContext(), "id is: ["+id+"]", Toast.LENGTH_SHORT).show();
        TextView nametv = (TextView) findViewById(R.restaurantDetails.name);
        nametv.setText(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_details, menu);
        return true;
    }
    
    /**
     * PagerAdapter which allows you to page horizontally between menus for breakfast, lunch, and dinner. 
     * @author Matthew Lavin
     */
    private class MenuPagerAdapter extends PagerAdapter {
        
        public int getCount() {
            return 3;
        }
        
        public Object instantiateItem(View collection, int position) {
            LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int page = R.layout.menu_page;
            String meal = "";
            switch (position) {
                case 0:
                    meal = "Breakfast!";
                    break;
                case 1:
                    meal = "Lunch!";
                    break;
                case 2:
                    meal = "Dinner!";
                    break;
            }
            
            View view = inflater.inflate(page, null);
            TextView mealTitle = (TextView) view.findViewById(R.id.meal_title);
            mealTitle.setText(meal);
            
            ((ViewPager) collection).addView(view, 0);
            final ViewPager pager = (ViewPager)collection;
            
            View menupage = view.findViewById(R.id.menu_page_ll);
            menupage.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    int page = ((ViewPager) pager).getCurrentItem();
                    Intent i = new Intent(getApplicationContext(), Menus.class);
                    i.putExtra("page", page);
                    startActivity(i);
                }
            });
            
            return view;
        }
        
        @Override
        public void destroyItem(View view, int x, Object obj) {
            ((ViewPager) view).removeView((View) obj);
        }
        
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }
        
        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
