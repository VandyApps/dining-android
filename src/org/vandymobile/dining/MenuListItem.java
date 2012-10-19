package org.vandymobile.dining;

import java.util.List;

public class MenuListItem {
    private final int count;
    private final List<String> menus;
    
    MenuListItem(List<String> x){
        menus = x;
        count = x.size();
    }
    
    public int getCount(){
        return count;
    }
    
    public List<String> getMenus(){
        return menus;
    }

}
