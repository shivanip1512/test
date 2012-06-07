package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class StarsAppliances implements Iterable<StarsAppliance> {
    private Vector<StarsAppliance> starsApplianceList;

    public StarsAppliances() {
        starsApplianceList = new Vector<StarsAppliance>();
    }

    public void addStarsAppliance(StarsAppliance vStarsAppliance) {
        starsApplianceList.addElement(vStarsAppliance);
    }

    public void addStarsAppliance(int index, StarsAppliance vStarsAppliance) {
        starsApplianceList.insertElementAt(vStarsAppliance, index);
    }

    public Enumeration<StarsAppliance> enumerateStarsAppliance() {
        return starsApplianceList.elements();
    } 

    public StarsAppliance getStarsAppliance(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsApplianceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsApplianceList.elementAt(index);
    }

    public StarsAppliance[] getStarsAppliance() {
        int size = starsApplianceList.size();
        StarsAppliance[] mArray = new StarsAppliance[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsAppliance) starsApplianceList.elementAt(index);
        }
        return mArray;
    } 

    public int getStarsApplianceCount() {
        return starsApplianceList.size();
    }

    public void removeAllStarsAppliance() {
        starsApplianceList.removeAllElements();
    }

    public StarsAppliance removeStarsAppliance(int index) {
        StarsAppliance obj = starsApplianceList.elementAt(index);
        starsApplianceList.removeElementAt(index);
        return obj;
    } 

    public void setStarsAppliance(int index, StarsAppliance vStarsAppliance) {
        //-- check bounds for index
        if ((index < 0) || (index > starsApplianceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsApplianceList.setElementAt(vStarsAppliance, index);
    }

    public void setStarsAppliance(StarsAppliance[] starsApplianceArray) {
        //-- copy array
        starsApplianceList.removeAllElements();
        for (int i = 0; i < starsApplianceArray.length; i++) {
            starsApplianceList.addElement(starsApplianceArray[i]);
        }
    }

    @Override
    public Iterator<StarsAppliance> iterator() {
        return starsApplianceList.iterator();
    }
}
