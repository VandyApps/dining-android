package org.vandymobile.dining;

import java.io.IOException;
import java.text.DecimalFormat;

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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_list_view);
        
        
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
    
    
    class IconicAdapter extends ArrayAdapter<String> { 
        Activity context;
        
        IconicAdapter(Activity context) {
            super(context, R.layout.row, adapterInput);
            this.context=context; 
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
            String[] tmp = {"name","type"};
            Cursor _cur = diningDatabase.query("dining", tmp, null, null, null, null, "name");
            
            String tempName = "this is a default value";
            
            _cur.moveToFirst();//initialize the cursor
            _cur.move(position);
            tempName = _cur.getString(0); //grab the name value for the current row
            String description = _cur.getString(1);
            _cur.close();
            if (locCursor.moveToPosition(position)){
                GeoPoint thisLocation = new GeoPoint((int)(locCursor.getFloat(0)*1000000), (int)(locCursor.getFloat(1)*1000000));
                holder.tvDist.setText(roundDouble(getDistance(thisLocation, curLoc))+" mi away");
            }
            
            holder.tvTitle.setText(tempName);
            holder.tvStatus.setText("Open for 15 minutes!");//TODO fix temp value
            holder.tvDesc.setText(description); //TODO fix temp value
            holder.imgView.setImageResource(R.drawable.compass); //TODO fix temp value

            return(convertView);         
        }
    }

    
    
}