package com.cannontech.stars.util;


public class FilterWrapper 
{
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

