package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsCustomerSelectionLists {
    private Vector<StarsCustSelectionList> _starsCustSelectionListList;

    public StarsCustomerSelectionLists() {
        _starsCustSelectionListList = new Vector<StarsCustSelectionList>();
    }

    public void addStarsCustSelectionList(StarsCustSelectionList vStarsCustSelectionList) {
        _starsCustSelectionListList.addElement(vStarsCustSelectionList);
    }

    public void addStarsCustSelectionList(int index, StarsCustSelectionList vStarsCustSelectionList) {
        _starsCustSelectionListList.insertElementAt(vStarsCustSelectionList, index);
    }

    public Enumeration<StarsCustSelectionList> enumerateStarsCustSelectionList() {
        return _starsCustSelectionListList.elements();
    }

    public StarsCustSelectionList getStarsCustSelectionList(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustSelectionListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return _starsCustSelectionListList.elementAt(index);
    } 

    public StarsCustSelectionList[] getStarsCustSelectionList() {
        int size = _starsCustSelectionListList.size();
        StarsCustSelectionList[] mArray = new StarsCustSelectionList[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustSelectionList) _starsCustSelectionListList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsCustSelectionListCount() {
        return _starsCustSelectionListList.size();
    }

    public void removeAllStarsCustSelectionList() {
        _starsCustSelectionListList.removeAllElements();
    }

    public StarsCustSelectionList removeStarsCustSelectionList(int index) {
        StarsCustSelectionList obj = _starsCustSelectionListList.elementAt(index);
        _starsCustSelectionListList.removeElementAt(index);
        return obj;
    }

    public void setStarsCustSelectionList(int index, StarsCustSelectionList vStarsCustSelectionList) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustSelectionListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustSelectionListList.setElementAt(vStarsCustSelectionList, index);
    }

    public void setStarsCustSelectionList(StarsCustSelectionList[] starsCustSelectionListArray) {
        //-- copy array
        _starsCustSelectionListList.removeAllElements();
        for (int i = 0; i < starsCustSelectionListArray.length; i++) {
            _starsCustSelectionListList.addElement(starsCustSelectionListArray[i]);
        }
    }

}
