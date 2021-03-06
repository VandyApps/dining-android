package org.vandymobile.dining;

import java.util.Iterator;
import java.util.List;

import org.vandymobile.dining.util.Locations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author Matthew Lavin
 */

public class Menus extends Activity {

    private static Locations loc;
    private int id;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        
        loc = Locations.getInstance(getApplicationContext());
        
        Intent i = getIntent();
        String[] click = {"Breakfast","Lunch","Dinner","[ERROR: bad meal input]"};
        int page = i.getIntExtra("meal", 3);
        id = i.getIntExtra("restaurant", -10);
        if (id == -10){
            ((TextView)findViewById(R.id.menu_tv)).setText("This would show the general menu page for all locations.");
        } else {
            ((TextView)findViewById(R.id.menu_tv)).setText("This would show the menus for "+click[page]+ " at "+loc.findRestaurantById(id).mName);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menus, menu);
        return true;
    }
    
    public void menusList(List<MenuListItem> menus, ScrollView mainScrollView)
    {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        
        
        Iterator<MenuListItem> _it = menus.iterator();
        
        while (_it.hasNext()){
            MenuListItem x =_it.next();
            RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.menulistitem, null);
            
            ((TextView)item.findViewById(R.id.menulistitemtitle)).setText(x.getTitle());
            
            
            List<String> listMenu = x.getMenus();
            Iterator<String> menuListIterator = listMenu.iterator();
            
            TextView cur;
            while(menuListIterator.hasNext()){
                String y = menuListIterator.next();
                cur = new TextView(getApplicationContext());
                cur.setText(y);
                item.addView(cur);
            }
            mainScrollView.addView(item);
            
        }
    }
}
