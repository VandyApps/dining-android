package org.vandymobile.dining;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class LocationDetails extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_details);
        Long id = this.getIntent().getLongExtra("id", -1);
        loadLocation(id);
    }
    
    private void loadLocation(Long id){
        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        
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
