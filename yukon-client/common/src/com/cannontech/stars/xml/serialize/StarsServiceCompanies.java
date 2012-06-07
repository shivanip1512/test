package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsServiceCompanies {
    private final Vector<StarsServiceCompany> starsServiceCompanyList =
        new Vector<StarsServiceCompany>();

    public StarsServiceCompanies() {
    }
    
    public void addStarsServiceCompany(StarsServiceCompany vStarsServiceCompany) {
        starsServiceCompanyList.addElement(vStarsServiceCompany);
    }

    public void addStarsServiceCompany(int index, StarsServiceCompany vStarsServiceCompany) {
        starsServiceCompanyList.insertElementAt(vStarsServiceCompany, index);
    }

    public Enumeration<StarsServiceCompany> enumerateStarsServiceCompany() {
        return starsServiceCompanyList.elements();
    }

    public StarsServiceCompany getStarsServiceCompany(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsServiceCompanyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsServiceCompanyList.elementAt(index);
    } 

    public StarsServiceCompany[] getStarsServiceCompany() {
        int size = starsServiceCompanyList.size();
        StarsServiceCompany[] mArray = new StarsServiceCompany[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = starsServiceCompanyList.elementAt(index);
        }
        return mArray;
    } 

    public int getStarsServiceCompanyCount() {
        return starsServiceCompanyList.size();
    }

    public void removeAllStarsServiceCompany() {
        starsServiceCompanyList.removeAllElements();
    }

    public StarsServiceCompany removeStarsServiceCompany(int index) {
        StarsServiceCompany obj = starsServiceCompanyList.elementAt(index);
        starsServiceCompanyList.removeElementAt(index);
        return obj;
    } 

    public void setStarsServiceCompany(int index, StarsServiceCompany vStarsServiceCompany) {
        //-- check bounds for index
        if ((index < 0) || (index > starsServiceCompanyList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsServiceCompanyList.setElementAt(vStarsServiceCompany, index);
    }

    public void setStarsServiceCompany(StarsServiceCompany[] starsServiceCompanyArray) {
        //-- copy array
        starsServiceCompanyList.removeAllElements();
        for (int i = 0; i < starsServiceCompanyArray.length; i++) {
            starsServiceCompanyList.addElement(starsServiceCompanyArray[i]);
        }
    }

}
