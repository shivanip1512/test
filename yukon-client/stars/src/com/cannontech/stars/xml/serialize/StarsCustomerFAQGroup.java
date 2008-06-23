package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsCustomerFAQGroup {
    private int _subjectID;
    private boolean _has_subjectID;
    private String _subject;
    private Vector<StarsCustomerFAQ> _starsCustomerFAQList;

    public StarsCustomerFAQGroup() {
        _starsCustomerFAQList = new Vector<StarsCustomerFAQ>();
    }

    public void addStarsCustomerFAQ(StarsCustomerFAQ vStarsCustomerFAQ) {
        _starsCustomerFAQList.addElement(vStarsCustomerFAQ);
    }

    public void addStarsCustomerFAQ(int index, StarsCustomerFAQ vStarsCustomerFAQ) {
        _starsCustomerFAQList.insertElementAt(vStarsCustomerFAQ, index);
    }

    public void deleteSubjectID() {
        this._has_subjectID = false;
    }

    public Enumeration<StarsCustomerFAQ> enumerateStarsCustomerFAQ() {
        return _starsCustomerFAQList.elements();
    }

    public StarsCustomerFAQ getStarsCustomerFAQ(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQList.size())) {
            throw new IndexOutOfBoundsException();
        }
        return _starsCustomerFAQList.elementAt(index);
    }

    public StarsCustomerFAQ[] getStarsCustomerFAQ() {
        int size = _starsCustomerFAQList.size();
        StarsCustomerFAQ[] mArray = new StarsCustomerFAQ[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = _starsCustomerFAQList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsCustomerFAQCount() {
        return _starsCustomerFAQList.size();
    }

    public String getSubject() {
        return this._subject;
    } 

    public int getSubjectID() {
        return this._subjectID;
    } 

    public boolean hasSubjectID() {
        return this._has_subjectID;
    }

    public void removeAllStarsCustomerFAQ() {
        _starsCustomerFAQList.removeAllElements();
    }

    public StarsCustomerFAQ removeStarsCustomerFAQ(int index) {
        StarsCustomerFAQ obj = _starsCustomerFAQList.elementAt(index);
        _starsCustomerFAQList.removeElementAt(index);
        return obj;
    } 

    public void setStarsCustomerFAQ(int index, StarsCustomerFAQ vStarsCustomerFAQ) {
        //-- check bounds for index
        if ((index < 0) || (index > _starsCustomerFAQList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsCustomerFAQList.setElementAt(vStarsCustomerFAQ, index);
    }

    public void setStarsCustomerFAQ(StarsCustomerFAQ[] starsCustomerFAQArray) {
        //-- copy array
        _starsCustomerFAQList.removeAllElements();
        for (int i = 0; i < starsCustomerFAQArray.length; i++) {
            _starsCustomerFAQList.addElement(starsCustomerFAQArray[i]);
        }
    }

    public void setSubject(String subject) {
        this._subject = subject;
    }

    public void setSubjectID(int subjectID) {
        this._subjectID = subjectID;
        this._has_subjectID = true;
    }

}
