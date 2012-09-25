package org.vandymobile.dining;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class DiningListView extends ListActivity {
    public static class ViewHolder {
        public TextView tvTitle, tvDesc, tvDist, tvStatus;
        private ImageView imgView;
        }
    public static Integer[] RestaurantMap = {24,25,26,7,27,28,29,4,30,3,31,11,12,23,32,22,5,18,33,34,17,35,36,37,38,39,40,9,8,1,10,41,42,43,44,45,46,15,16,14,47,2,6,13,19,21,20,48,49,50};
    private static DatabaseHelper myDbHelper;
    private static SQLiteDatabase diningDatabase;
    private String[] adapterInput;
    public static Cursor locCursor;
    private GeoPoint curLoc = null;
    int curHour;
    int curMin;
    int curDay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_list_view);
        Time now = new Time();
        now.setToNow();
        curHour = now.hour;
        curMin = now.minute;
        curDay = now.weekDay + 1;// to match up with the Calendar class' day numbering scheme
        
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
        
        String[] tmp2 = {"_id"};
        Cursor adapterCursor = diningDatabase.query("dining", tmp2, null, null, null, null, null);
        adapterInput = new String[adapterCursor.getCount()];
        adapterCursor.close();
        
        String[] tmp = {"lat", "long", "name"};
        locCursor = diningDatabase.query("dining", tmp, null, null, null, null, "name");
        
        LocationManager _locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location x = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (x == null){
            x = _locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (x == null){
            x = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (x != null){
            curLoc = new GeoPoint((int)(x.getLatitude()*1000000), 
                             (int)(x.getLongitude()*1000000));//current position
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get location - defaulting", Toast.LENGTH_SHORT).show();
            curLoc = new GeoPoint(36143091, -86804699); //defaults to Vanderbilt if the current position cannot be determined
        }        
        setListAdapter(new IconicAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dining_list_view, menu);
        return true;
    }
    
    public void onListItemClick(ListView parent, View v, int position, long id) {
        startRestaurantDetails(position, id);
    }
    
    public void startRestaurantDetails(int position, long id){
        Toast.makeText(getApplicationContext(), "["+position+"]:["+id+"]", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LocationDetails.class).putExtra("id", id);
        startActivity(intent);
    }

    public void homeClick(View v){
        //nothing here
    }
    public void mapsClick(View v){
        Intent _int = new Intent(getApplicationContext(), DiningMap.class);
        startActivity(_int);
    }
    public void menuClick(View v){
        //TODO implement this
    }
    public void happyClick(View v){
        //TODO implement this
    }
    
    public double getDistance(GeoPoint pointA, GeoPoint pointB){
        Location locationA = new Location("point A");
    
        locationA.setLatitude(pointA.getLatitudeE6() / 1E6);
        locationA.setLongitude(pointA.getLongitudeE6() / 1E6);
    
        Location locationB = new Location("point B");
    
        locationB.setLatitude(pointB.getLatitudeE6() / 1E6);
        locationB.setLongitude(pointB.getLongitudeE6() / 1E6);
    
        return (locationA.distanceTo(locationB)/1609.34);//return in miles, not meters
    }

    double roundDouble(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
    return Double.valueOf(twoDForm.format(d));
}
    
    private Cursor getHours(int i, SQLiteDatabase _db, int id){
        switch (i){
            case Calendar.SUNDAY:
                String[] tmp = {"sunday_hours"};
                return _db.query("dining", tmp, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.MONDAY:
                String[] tmp1 = {"monday_hours"};
                return _db.query("dining", tmp1, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.TUESDAY:
                String[] tmp2 = {"tuesday_hours"};
                return _db.query("dining", tmp2, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.WEDNESDAY:
                String[] tmp3 = {"wednesday_hours"};
                return _db.query("dining", tmp3, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.THURSDAY:
                String[] tmp4 = {"thursday_hours"};
                return _db.query("dining", tmp4, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.FRIDAY:
                String[] tmp5 = {"friday_hours"};
                return _db.query("dining", tmp5, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            case Calendar.SATURDAY:
                String[] tmp6 = {"saturday_hours"};
                return _db.query("dining", tmp6, "_id = "+DiningListView.RestaurantMap[id], null, null, null, null);
            default:
                return null;
        }
    }
    private String[] parseHours(String _in){
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
    
    private String isOpen(String[] hours){
    	//TODO implement this
    	return "Closed";
    }
    
    
    class IconicAdapter extends ArrayAdapter<String> { 
        Activity context;
        private Cursor nameCursor;
        
        IconicAdapter(Activity context) {
            super(context, R.layout.row, adapterInput);
            this.context=context; 
            String[] tmp = {"name","type"};
            nameCursor = diningDatabase.query("dining", tmp, null, null, null, null, "name");
            }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (position == 0){
                LayoutInflater inflater=context.getLayoutInflater();
                convertView=inflater.inflate(R.layout.onerow, null);
                holder = new ViewHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
                holder.tvDesc = (TextView) convertView.findViewById(R.id.tvdesc);
                holder.tvDist = (TextView) convertView.findViewById(R.id.tvdist);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.tvstatus);
                holder.imgView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
                Toast.makeText(getApplicationContext(), convertView.getId()+"", Toast.LENGTH_SHORT).show();
            } else {
                if (convertView == null) {
                    LayoutInflater inflater=context.getLayoutInflater();
                    convertView=inflater.inflate(R.layout.row, null);
                    holder = new ViewHolder();
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
                    holder.tvDesc = (TextView) convertView.findViewById(R.id.tvdesc);
                    holder.tvDist = (TextView) convertView.findViewById(R.id.tvdist);
                    holder.tvStatus = (TextView) convertView.findViewById(R.id.tvstatus);
                    holder.imgView = (ImageView) convertView.findViewById(R.id.image);
                    convertView.setTag(holder);
                    
                } else if (convertView.getId() == R.id.badid) {
                    LayoutInflater inflater=context.getLayoutInflater();
                    convertView=inflater.inflate(R.layout.row, null);
                    holder = new ViewHolder();
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
                    holder.tvDesc = (TextView) convertView.findViewById(R.id.tvdesc);
                    holder.tvDist = (TextView) convertView.findViewById(R.id.tvdist);
                    holder.tvStatus = (TextView) convertView.findViewById(R.id.tvstatus);
                    holder.imgView = (ImageView) convertView.findViewById(R.id.image);
                    convertView.setTag(holder);
                    
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
            }

            
            String[] tempHours;
            Cursor hoursCursor = getHours(curDay, diningDatabase, position);
            hoursCursor.moveToFirst();//initialize the cursor
            tempHours = parseHours(hoursCursor.getString(0));
            hoursCursor.close();
            
            String tempName = "this is a default value";
            nameCursor.moveToFirst();//initialize the cursor
            nameCursor.move(position);
            tempName = nameCursor.getString(0); //grab the name value for the current row
            String description = nameCursor.getString(1);

            if (locCursor.moveToPosition(position)){
                GeoPoint thisLocation = new GeoPoint((int)(locCursor.getFloat(0)*1000000), (int)(locCursor.getFloat(1)*1000000));
                holder.tvDist.setText(roundDouble(getDistance(thisLocation, curLoc))+" mi away");
            }
            
            holder.tvTitle.setText(tempName);
            holder.tvStatus.setText(isOpen(tempHours));
            holder.tvDesc.setText(description); 
            holder.imgView.setImageResource(R.drawable.compass); //TODO fix temp value

            return(convertView);         
        }
    }

    
    
}