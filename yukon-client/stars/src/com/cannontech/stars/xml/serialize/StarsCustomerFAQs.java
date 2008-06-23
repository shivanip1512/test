package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsCustomerFAQs {
    private Vector<StarsCustomerFAQGroup> _starsCustomerFAQGroupList;

    public StarsCustomerFAQs() {
        _starsCustomerFAQGroupList = new Vector<StarsCustomerFAQGroup>();
    }

    public void addStarsCustomerFAQGroup(StarsCustomerFAQGroup vStarsCustomerFAQGroup) {
        _starsCustomerFAQGroupList.addElement(vStarsCustomerFAQGroup);
    }

    public void addStarsCustomerFAQGroup(int index, StarsCustomerFAQGroup vStarsCustomerFAQGroup) {
        _starsCustomerFAQGroupList.insertElementAt(vStarsCustomerFAQGroup, index);
    }

    public Enumeration<StarsCustomerFAQGroup> enumerateStarsCustomerFAQGroup() {
        return _starsCustomerFAQGroupList.elements();
    }

    public StarsCustomerFAQGroup getStarsCustomerFAQGroup(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsCustomerFAQGroup) _starsCustomerFAQGroupList.elementAt(index);
    }

    public StarsCustomerFAQGroup[] getStarsCustomerFAQGroup() {
        int size = _starsCustomerFAQGroupList.size();
        StarsCustomerFAQGroup[] mArray = new StarsCustomerFAQGroup[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsCustomerFAQGroup) _starsCustomerFAQGroupList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsCustomerFAQGroupCount() {
        return _starsCustomerFAQGroupList.size();
    }

    public void removeAllStarsCustomerFAQGroup() {
        _starsCustomerFAQGroupList.removeAllElements();
    }

    public StarsCustomerFAQGroup removeStarsCustomerFAQGroup(int index) {
        java.lang.Object obj = _starsCustomerFAQGroupList.elementAt(index);
        _starsCustomerFAQGroupList.removeElementAt(index);
        return (StarsCustomerFAQGroup) obj;
    }

    public void setStarsCustomerFAQGroup(int index, StarsCustomerFAQGroup vStarsCustomerFAQGroup) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustomerFAQGroupList.setElementAt(vStarsCustomerFAQGroup, index);
    }

    public void setStarsCustomerFAQGroup(StarsCustomerFAQGroup[] starsCustomerFAQGroupArray) {
        //-- copy array
        _starsCustomerFAQGroupList.removeAllElements();
        for (int i = 0; i < starsCustomerFAQGroupArray.length; i++) {
            _starsCustomerFAQGroupList.addElement(starsCustomerFAQGroupArray[i]);
        }
    }

}
