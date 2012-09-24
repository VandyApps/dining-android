package org.vandymobile.dining;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationDetails extends Activity {
    
    private TextView day;
    private TextView range;
    private static int today = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
    private int curHoursDisplay;
    private static DatabaseHelper myDbHelper;
    private static SQLiteDatabase diningDatabase;
    private static Long id;
    private Cursor Hours; 
    
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
        Hours = getHours(today);//during init, load the current hours as today's hours
        curHoursDisplay = today; //this keeps track of what day is currently displayed in the hours box
        
        day = (TextView) findViewById(R.restaurantDetails.hoursDay);
        range = (TextView) findViewById(R.restaurantDetails.hoursRangeDisplay);
        if (Hours.moveToFirst()){
            updateRangeText(today, parseHours(Hours.getString(0)), 1);
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
                Hours = getHours(curHoursDisplay);
                String[] newhours = null;
                if (Hours.moveToFirst()){
                    newhours = parseHours(Hours.getString(0));
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
                Hours = getHours(curHoursDisplay);
                String[] newhours = null;
                if (Hours.moveToFirst()){
                    newhours = parseHours(Hours.getString(0));
                } else {
                    //something broke. 
                }
                if (newhours != null){
                    updateRangeText(curHoursDisplay, newhours, 1);//so far only supporting the first set of hours each day
                }
            }
        });

        
    }

    private void updateRangeText(int _day, String[] newhours, int pos) {
        day.setText(getCurrentDay(_day));
        if (newhours[1] == null){
            range.setText("closed");
        } else {
            if (pos == 1){
                range.setText(newhours[1]+" - "+newhours[2]);
            } else if (pos == 2) {
                range.setText(newhours[3]+" - "+newhours[4]);
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

    private Cursor getHours(int i){
        switch (i){
            case Calendar.SUNDAY:
                String[] tmp = {"sunday_hours"};
                return diningDatabase.query("dining", tmp, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.MONDAY:
                String[] tmp1 = {"monday_hours"};
                return diningDatabase.query("dining", tmp1, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.TUESDAY:
                String[] tmp2 = {"tuesday_hours"};
                return diningDatabase.query("dining", tmp2, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.WEDNESDAY:
                String[] tmp3 = {"wednesday_hours"};
                return diningDatabase.query("dining", tmp3, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.THURSDAY:
                String[] tmp4 = {"thursday_hours"};
                return diningDatabase.query("dining", tmp4, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.FRIDAY:
                String[] tmp5 = {"friday_hours"};
                return diningDatabase.query("dining", tmp5, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
            case Calendar.SATURDAY:
                String[] tmp6 = {"saturday_hours"};
                return diningDatabase.query("dining", tmp6, "_id = "+DiningListView.RestaurantMap[id.intValue()], null, null, null, null);
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
                ret[1]=_in.substring(0, i);//once you reach the first comma, break off the first start hour into ret[1]
                for (int x = i; x < _in.length();x++){
                    if (_in.charAt(x) == ';'){//if you hit a semicolon, there are two start and end times today. 
                                              //Don't worry, we'll use recursion!
                        ret[2] = _in.substring(i+1,x);
                        String[] tmp2 = parseHours(_in.substring(x+1));
                        ret[3] = tmp2[1]; ret[4] = tmp2[2];
                        break;
                    } else if (x == _in.length() -1){//if you hit the end of the string and haven't found a semicolon, don't worry! 
                                                     //there are only one set of hours for today.
                        
                        ret[2] = _in.substring(i+1);//grab the second hour string into ret[2] - 
                                            //don't worry about ret[3] and ret[4], we'll check for null later
                    }
                }
                break;
            }
        }
        
        return ret;
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
}
