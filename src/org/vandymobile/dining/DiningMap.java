package org.vandymobile.dining;

import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DiningMap extends MapActivity {

    private MapController _mapViewController;
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    GeoPoint p = null;
    
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
        
        List<Overlay> overlayList = myMap.getOverlays();
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
        overlayList.add(myLocationOverlay);

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
        } else {
        	Toast.makeText(getApplicationContext(), "Couldn't get location - defaulting", Toast.LENGTH_SHORT).show();
            p = new GeoPoint(36143091, -86804699); //defaults to Vanderbilt if the current position cannot be determined
        }
    }
    
    public void homeClick(View v){
        Intent _int = new Intent(getApplicationContext(), DiningListView.class);
        startActivity(_int);
    }
    public void mapsClick(View v){
        //nothing here
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

            /*if (cur == null){
                Toast.makeText(getApplicationContext(), "point is null", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "point is not null", Toast.LENGTH_SHORT).show();
            }*/ //commented - this happens a lot

            my_paint.setStyle(Paint.Style.STROKE);//this is used to draw to the canvas
            my_paint.setARGB(255, 0, 0, 0);
            my_paint.setStrokeWidth(1);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.location); //temporary drawable for our current location
            canvas.drawBitmap(bmp, cur.x, cur.y, my_paint);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pushpin);
            canvas.drawBitmap(bmp, cur.x+50, cur.y+50, my_paint);
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
}