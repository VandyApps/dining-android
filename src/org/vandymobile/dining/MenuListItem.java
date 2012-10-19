package org.vandymobile.dining;

import java.util.List;

public class MenuListItem {
    private final int count;
    private final List<String> menus;
    private final String mTitle;
    
    MenuListItem(List<String> x, String title){
        menus = x;
        count = x.size();
        mTitle = title;
    }
    
    public int getCount(){
        return count;
    }
    
    public List<String> getMenus(){
        return menus;
    }
    
    public String getTitle(){
    	return mTitle;
    }
}
