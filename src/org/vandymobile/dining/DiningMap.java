package org.vandymobile.dining;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DiningMap extends MapActivity {

    AllOverlays diningOverlay;
    private MapController _mapViewController;
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    GeoPoint p = null;
    private static DatabaseHelper myDbHelper;
    private static SQLiteDatabase diningDatabase;
    MyLocationOverlay myLocationOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dining_map_large);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        MapView myMap = (MapView) findViewById(R.id.mapview);
        myMap.setBuiltInZoomControls(true);
                
        GeoPoint _geoPoint = new GeoPoint(36143091, -86804699); //This is roughly the center of Vanderbilt
        _mapViewController = myMap.getController();
        _mapViewController.animateTo(_geoPoint);
        _mapViewController.setZoom(17); //center map on this point, zoomed to fit
        
        _locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        _locationListener = new MyLocationListener();
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);//get current GPS location into a listener
        
        Location x = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (x == null){
            x = _locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (x == null){
            x = _locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (x != null){
            p = new GeoPoint((int)(x.getLatitude()*1000000), 
                             (int)(x.getLongitude()*1000000));//current position
            myLocationOverlay = new MyLocationOverlay();
            myMap.getOverlays().add(myLocationOverlay); //this is an overlay which contains an image for our current location
            //This overlay is only drawn if we successfully retrieved the location of the user. 
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get location - defaulting", Toast.LENGTH_SHORT).show();
            p = new GeoPoint(36143091, -86804699); //defaults to Vanderbilt if the current position cannot be determined
        }
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
        
        // creates the overlay containing markers for all dining locations
        // uses the database
        diningOverlay = new AllOverlays(this, myMap);
        myMap.getOverlays().add(diningOverlay);
    }
    
    public void homeClick(View v){
        Intent _int = new Intent(getApplicationContext(), DiningListView.class);
        startActivity(_int);
    }
    public void mapsClick(View v){
        // Already at map - do nothing
    }
    public void menuClick(View v){
        //TODO implement this
    }
    public void happyClick(View v){
        //TODO implement this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dining_map, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    protected class MyLocationOverlay extends com.google.android.maps.Overlay {

        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean Shadow, long When) {
            super.draw(canvas, mapView, Shadow);
            Point cur;
            Paint my_paint = new Paint();

            cur = mapView.getProjection().toPixels(p, null); //use this to get the location to draw to in usable format

            my_paint.setStyle(Paint.Style.STROKE);//this is used to draw to the canvas
            my_paint.setARGB(255, 0, 0, 0);
            my_paint.setStrokeWidth(1);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.location); //temporary drawable for our current location
            canvas.drawBitmap(bmp, cur.x, cur.y, my_paint);
            
            return true;
        }
    }

    private class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            GeoPoint locPoint = new GeoPoint((int)(loc.getLatitude()*1000000),(int)(loc.getLongitude()*1000000));
            _mapViewController.animateTo(locPoint); //follow the user? Not sure if we want this to happen or not...
        }
        
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider,
            int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    
    public class AllOverlays extends ItemizedOverlay<OverlayItem> implements View.OnClickListener {
        
        private static final int NUM_FILTERS = 3;
        public static final int FILTER_CLOSED = 0;
        public static final int FILTER_PLAN = 1;
        public static final int FILTER_MONEY = 2;
        
        private int clickedPosition = -1;
        private DiningMap map;
        private MapView mapView;
        private RelativeLayout popup;
        //private ImageView icon;
        private TextView popupText;
        private TextView specialText;
        
        private ArrayList<OverlayItem> locationOverlay = new ArrayList<OverlayItem>();
        private boolean [][] show; // will show an item only if every entry in the column is true

        public AllOverlays(DiningMap map, MapView mapview) {

            super(boundCenterBottom(map.getResources().getDrawable(R.drawable.pushpin)));
            this.map = map;
            this.mapView = mapview;
            popup = (RelativeLayout)mapview.findViewById(R.map.popup);
            //icon = (ImageView)popup.findViewById(R.map.icon);
            popupText = (TextView)popup.findViewById(R.map.title);
            specialText = (TextView)popup.findViewById(R.map.specialText);
            
            popup.setOnClickListener(this);
            
            String[] tmp = {"lat", "long", "name"};
            Cursor locName = diningDatabase.query("dining", tmp, null, null, null, null, "name");
            locName.moveToFirst(); //move the cursor from row -1 to the first row (row 0)
            
            //ArrayList<Long> IDs = Restaurant.getIDs();
            show = new boolean [NUM_FILTERS][locName.getCount()]; // only 1 possible criteria for showing now

            for (int i = 0; i < locName.getCount(); i++) {
                OverlayItem overlayItem = new OverlayItem(new GeoPoint((int)(locName.getFloat(0)*1000000),
                        (int)(locName.getFloat(1)*1000000)), locName.getString(2), "sample hours text");
                /*if (Restaurant.offCampus(IDs.get(i)))
                    overlayItem.setMarker(boundCenterBottom(map.getResources().getDrawable(R.drawable.map_marker_n)));
                        // TODO get a better custom marker for off campus restaurants and/or make more custom markers for different 
                        // types or individual restaurants
                else overlayItem.setMarker(boundCenterBottom(map.getResources().getDrawable(R.drawable.map_marker_v)));*/
                overlayItem.setMarker(boundCenterBottom(map.getResources().getDrawable(R.drawable.pushpin)));
                locationOverlay.add(overlayItem);
                locName.move(1);//move the cursor forward one position
                for (int j = 0; j < NUM_FILTERS; j++)
                    show[j][i] = true;
            } 
            populate();
        }

        @Override
        protected boolean onTap(int index) {
            if (clickedPosition == index) {
                clickedPosition = -1;
                popup.setVisibility(View.GONE);
                return true; //super.onTap(index);
            }
            clickedPosition = index;

            //icon.setImageResource(Restaurant.getIcon(Restaurant.getIDs().get(index)));
            popupText.setText(getItem(index).getTitle());
            specialText.setText(getItem(index).getSnippet());
            
            popup.setLayoutParams(new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, 
                    getItem(index).getPoint(), 0, -getItem(index).getMarker(0).getIntrinsicHeight(), MapView.LayoutParams.BOTTOM_CENTER));
            popup.setVisibility(View.VISIBLE);
            
            mapView.getController().animateTo(getItem(index).getPoint());
            return true; //super.onTap(index);
        }
        
        private int lastI;
        private int lastIndex;
        @Override
        protected OverlayItem createItem(int i) {
            if (lastI == i - 1) {
                for (int j = lastIndex + 1; j < show[0].length; j++)
                    if (getShowItem(j)) {
                        lastI = i;
                        lastIndex = j;
                        return locationOverlay.get(j);
                    }
            } else {
                int num = -1;
                for (int j = 0; j < show[0].length; j++) {
                    if (getShowItem(j))
                        num++;
                    if (num == i) {
                        lastI = i;
                        lastIndex = j;
                        return locationOverlay.get(j);
                    }
                }
            }
            throw new RuntimeException("createItem error");
        }

        @Override
        public int size() {
            int size = 0;
            for (int i = 0; i < show[0].length; i++) 
                if (getShowItem(i))
                    size++;
            return size;
        }


        public void onClick(View v) {
            Intent toDetails = new Intent(map, LocationDetails.class);
            long id = (long)clickedPosition;
            toDetails.putExtra("id", id);//testing - might open the correct location?
            map.startActivity(toDetails);
        }
        
        public ArrayList<OverlayItem> getLocationOverlay() {
            return locationOverlay;
        }
        
        public void setHideForFilter(boolean hide, int filter) {
            if (!hide) 
                for (int i = 0; i < show[0].length; i++)
                    setShowItem(i, filter, true);
            else {
                switch (filter) {
                case FILTER_CLOSED:
                    for (int i = 0; i < show[0].length; i++)
                        if (/*!Restaurant.getHours(Restaurant.getIDs().get(i)).isOpen()*/false)
                            setShowItem(i, filter, false);
                    break;
                case FILTER_PLAN:
                    for (int i = 0; i < show[0].length; i++)
                        if (/*!Restaurant.mealPlanAccepted(Restaurant.getIDs().get(i))*/false)
                            setShowItem(i, filter, false);
                    break;
                case FILTER_MONEY:
                    for (int i = 0; i < show[0].length; i++)
                        if (/*!Restaurant.mealMoneyAccepted(Restaurant.getIDs().get(i))*/false)
                            setShowItem(i, filter, false);
                    break;
                }
            }
        }
        
        public void setShowItem(int i, int filter, boolean display) {
            show[filter][i] = display;
        }
        
        public boolean getShowItem(int i) {
            for (int j = 0; j<NUM_FILTERS; j++)
                if (!show[j][i])
                    return false;
            return true;
        }
        
        public void notifyDataSetChanged() {
            populate();
        }

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}