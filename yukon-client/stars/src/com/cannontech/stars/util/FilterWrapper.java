package com.cannontech.stars.util;

import java.util.Comparator;

import com.cannontech.database.data.lite.LiteYukonPAObject;


public class FilterWrapper 
{
    public static Comparator<FilterWrapper> filterWrapperComparator = new Comparator<FilterWrapper>() {
        public int compare(FilterWrapper wrap1, FilterWrapper wrap2) {
            int result = wrap1.getFilterTypeID().compareToIgnoreCase(wrap2.getFilterTypeID());
            if(result == 0)
                return wrap1.getFilterID().compareToIgnoreCase(wrap2.getFilterID());
            return result;
        }
        
        public boolean equals(Object obj) {
            return false;
        }
    };
    
    private String filterTypeID;
    private String filterID;
    private String filterText;
    
    public FilterWrapper(String typeID, String newText, String selectID)
    {
        filterTypeID = typeID;
        filterText = newText;
        filterID = selectID;
    }

    public String getFilterID() 
    {
        return filterID;
    }

    public void setFilterID(String filterID) 
    {
        this.filterID = filterID;
    }

    public String getFilterText() 
    {
        return filterText;
    }

    public void setFilterText(String filterText) 
    {
        this.filterText = filterText;
    }

    public String getFilterTypeID() 
    {
        return filterTypeID;
    }

    public void setFilterTypeID(String filterTypeID) 
    {
        this.filterTypeID = filterTypeID;
        
    }
      
    
}

