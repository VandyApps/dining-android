package org.vandymobile.dining;

import java.io.IOException;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DiningListView extends ListActivity {
    private static String[] _temp = {"Campus Store","Commons","C.T. West","Last Drop","Quiznos","Rand","Rotiki","Varsity Marketplace"};
	public static class ViewHolder {
		public TextView tvTitle, tvDesc, tvDist;
		private ImageView imgView;
		}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_list_view);
        
        
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
        
        setListAdapter(new IconicAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dining_list_view, menu);
        return true;
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
    
    
    class IconicAdapter extends ArrayAdapter<String> { 
        Activity context;

        IconicAdapter(Activity context) { 
            super(context, R.layout.row, _temp);
            this.context=context; 
            }

        public View getView(int position, View convertView, ViewGroup parent) { 
            ViewHolder holder;
            if(convertView==null){
                LayoutInflater inflater=context.getLayoutInflater();
                convertView=inflater.inflate(R.layout.row, null);
                holder = new ViewHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
                holder.tvDesc = (TextView) convertView.findViewById(R.id.tvdesc);
                holder.tvDist = (TextView) convertView.findViewById(R.id.tvdist);
                holder.imgView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
               }
            else {
                  holder = (ViewHolder) convertView.getTag();
              }

            /*TextView label=(TextView)row.findViewById(R.id.label);
            label.setText(items[position]);
            if (items[position].length()>4) { 
                ImageView icon=(ImageView)row.findViewById(R.id.icon);
                icon.setImageResource(R.drawable.ic_launcher); 
            }*/
            holder.tvTitle.setText(_temp[position]);
            holder.tvDesc.setText("Open for 15 more minutes!");
            holder.tvDist.setText("0.2mi away");
            holder.imgView.setImageResource(R.drawable.compass);

            return(convertView);         
        }
    }

    
    
}