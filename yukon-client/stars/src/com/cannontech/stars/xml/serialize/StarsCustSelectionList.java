package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsCustSelectionList {
    private int _listID;
    private boolean _has_listID;
    private String _listName;
    private Vector<StarsSelectionListEntry> _starsSelectionListEntryList;

    public StarsCustSelectionList() {
        _starsSelectionListEntryList = new Vector<StarsSelectionListEntry>();
    }


    public void addStarsSelectionListEntry(StarsSelectionListEntry vStarsSelectionListEntry)
    {
        _starsSelectionListEntryList.addElement(vStarsSelectionListEntry);
    }

    public void addStarsSelectionListEntry(int index, StarsSelectionListEntry vStarsSelectionListEntry)
    {
        _starsSelectionListEntryList.insertElementAt(vStarsSelectionListEntry, index);
    }

    public void deleteListID()
    {
        this._has_listID= false;
    }

    public Enumeration<StarsSelectionListEntry> enumerateStarsSelectionListEntry()
    {
        return _starsSelectionListEntryList.elements();
    } 

    public int getListID()
    {
        return this._listID;
    } 

    public String getListName()
    {
        return this._listName;
    } 

    public StarsSelectionListEntry getStarsSelectionListEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSelectionListEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return _starsSelectionListEntryList.elementAt(index);
    }
    
    public StarsSelectionListEntry[] getStarsSelectionListEntry()
    {
        int size = _starsSelectionListEntryList.size();
        StarsSelectionListEntry[] mArray = new StarsSelectionListEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = _starsSelectionListEntryList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsSelectionListEntryCount()
    {
        return _starsSelectionListEntryList.size();
    }  

    public boolean hasListID()
    {
        return this._has_listID;
    }
    
    public void removeAllStarsSelectionListEntry()
    {
        _starsSelectionListEntryList.removeAllElements();
    }

    public StarsSelectionListEntry removeStarsSelectionListEntry(int index)
    {
        StarsSelectionListEntry obj = _starsSelectionListEntryList.elementAt(index);
        _starsSelectionListEntryList.removeElementAt(index);
        return obj;
    } 

    public void setListID(int listID)
    {
        this._listID = listID;
        this._has_listID = true;
    } 

    public void setListName(String listName)
    {
        this._listName = listName;
    } 

    public void setStarsSelectionListEntry(int index, StarsSelectionListEntry vStarsSelectionListEntry)
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsSelectionListEntryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsSelectionListEntryList.setElementAt(vStarsSelectionListEntry, index);
    } 

    public void setStarsSelectionListEntry(StarsSelectionListEntry[] starsSelectionListEntryArray)
    {
        //-- copy array
        _starsSelectionListEntryList.removeAllElements();
        for (int i = 0; i < starsSelectionListEntryArray.length; i++) {
            _starsSelectionListEntryList.addElement(starsSelectionListEntryArray[i]);
        }
    } 

}
