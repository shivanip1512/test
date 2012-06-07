package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class StarsSubstations {
    private final Vector<StarsSubstation> starsSubstationList =
        new Vector<StarsSubstation>();

    public StarsSubstations() {
    }
    
    public List<StarsSubstation> getStarsSubstationList() {
		return starsSubstationList;
	}

    public void addStarsSubstation(StarsSubstation vStarsSubstation) {
        starsSubstationList.addElement(vStarsSubstation);
    }

    public void addStarsSubstation(int index, StarsSubstation vStarsSubstation) {
        starsSubstationList.insertElementAt(vStarsSubstation, index);
    }

    public Enumeration<StarsSubstation> enumerateStarsSubstation() {
        return starsSubstationList.elements();
    } 

    public StarsSubstation getStarsSubstation(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsSubstationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsSubstationList.elementAt(index);
    } 

    public StarsSubstation[] getStarsSubstation() {
        int size = starsSubstationList.size();
        StarsSubstation[] mArray = new StarsSubstation[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = starsSubstationList.elementAt(index);
        }
        return mArray;
    } 

    public int getStarsSubstationCount() {
        return starsSubstationList.size();
    }

    public void removeAllStarsSubstation() {
        starsSubstationList.removeAllElements();
    }

    public StarsSubstation removeStarsSubstation(int index) {
        java.lang.Object obj = starsSubstationList.elementAt(index);
        starsSubstationList.removeElementAt(index);
        return (StarsSubstation) obj;
    }

    public void setStarsSubstation(int index, StarsSubstation vStarsSubstation) {
        //-- check bounds for index
        if ((index < 0) || (index > starsSubstationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsSubstationList.setElementAt(vStarsSubstation, index);
    }

    public void setStarsSubstation(StarsSubstation[] starsSubstationArray) {
        //-- copy array
        starsSubstationList.removeAllElements();
        for (int i = 0; i < starsSubstationArray.length; i++) {
            starsSubstationList.addElement(starsSubstationArray[i]);
        }
    }

}
