package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsCallReportHistory {
    private Vector<StarsCallReport> starsCallReportList;

    public StarsCallReportHistory() {
        
        starsCallReportList = new Vector<StarsCallReport>();
    }

    public void addStarsCallReport(StarsCallReport vStarsCallReport) {
        starsCallReportList.addElement(vStarsCallReport);
    }

    public void addStarsCallReport(int index, StarsCallReport vStarsCallReport) {
        starsCallReportList.insertElementAt(vStarsCallReport, index);
    }

    public Enumeration<StarsCallReport> enumerateStarsCallReport() {
        return starsCallReportList.elements();
    }

    public StarsCallReport getStarsCallReport(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsCallReportList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsCallReportList.elementAt(index);
    }

    public StarsCallReport[] getStarsCallReport() {
        int size = starsCallReportList.size();
        StarsCallReport[] mArray = new StarsCallReport[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = starsCallReportList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsCallReportCount() {
        return starsCallReportList.size();
    }

    public void removeAllStarsCallReport() {
        starsCallReportList.removeAllElements();
    }

    public StarsCallReport removeStarsCallReport(int index) {
        StarsCallReport obj = starsCallReportList.elementAt(index);
        starsCallReportList.removeElementAt(index);
        return obj;
    } 

    public void setStarsCallReport(int index, StarsCallReport vStarsCallReport) {
        //-- check bounds for index
        if ((index < 0) || (index > starsCallReportList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsCallReportList.setElementAt(vStarsCallReport, index);
    }

    public void setStarsCallReport(StarsCallReport[] starsCallReportArray) {
        //-- copy array
        starsCallReportList.removeAllElements();
        for (int i = 0; i < starsCallReportArray.length; i++) {
            starsCallReportList.addElement(starsCallReportArray[i]);
        }
    }

}
