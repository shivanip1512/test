
package com.cannontech.stars.xml.serialize;

import java.util.Vector;

public class StarsEnrollmentPrograms {
    private java.util.Vector _starsApplianceCategoryList;

    public StarsEnrollmentPrograms() {
        _starsApplianceCategoryList = new Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsApplianceCategory
    **/
    public void addStarsApplianceCategory(StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceCategoryList.addElement(vStarsApplianceCategory);
    } //-- void addStarsApplianceCategory(StarsApplianceCategory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsApplianceCategory
    **/
    public void addStarsApplianceCategory(int index, StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsApplianceCategoryList.insertElementAt(vStarsApplianceCategory, index);
    } //-- void addStarsApplianceCategory(int, StarsApplianceCategory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsApplianceCategory()
    {
        return _starsApplianceCategoryList.elements();
    } //-- java.util.Enumeration enumerateStarsApplianceCategory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsApplianceCategory getStarsApplianceCategory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceCategoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsApplianceCategory) _starsApplianceCategoryList.elementAt(index);
    } //-- StarsApplianceCategory getStarsApplianceCategory(int) 

    /**
    **/
    public StarsApplianceCategory[] getStarsApplianceCategory()
    {
        int size = _starsApplianceCategoryList.size();
        StarsApplianceCategory[] mArray = new StarsApplianceCategory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsApplianceCategory) _starsApplianceCategoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsApplianceCategory[] getStarsApplianceCategory() 

    /**
    **/
    public int getStarsApplianceCategoryCount()
    {
        return _starsApplianceCategoryList.size();
    } //-- int getStarsApplianceCategoryCount() 

    /**
    **/
    public void removeAllStarsApplianceCategory()
    {
        _starsApplianceCategoryList.removeAllElements();
    } //-- void removeAllStarsApplianceCategory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsApplianceCategory removeStarsApplianceCategory(int index)
    {
        java.lang.Object obj = _starsApplianceCategoryList.elementAt(index);
        _starsApplianceCategoryList.removeElementAt(index);
        return (StarsApplianceCategory) obj;
    } //-- StarsApplianceCategory removeStarsApplianceCategory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsApplianceCategory
    **/
    public void setStarsApplianceCategory(int index, StarsApplianceCategory vStarsApplianceCategory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsApplianceCategoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsApplianceCategoryList.setElementAt(vStarsApplianceCategory, index);
    } //-- void setStarsApplianceCategory(int, StarsApplianceCategory) 

    /**
     * 
     * 
     * @param starsApplianceCategoryArray
    **/
    public void setStarsApplianceCategory(StarsApplianceCategory[] starsApplianceCategoryArray)
    {
        //-- copy array
        _starsApplianceCategoryList.removeAllElements();
        for (int i = 0; i < starsApplianceCategoryArray.length; i++) {
            _starsApplianceCategoryList.addElement(starsApplianceCategoryArray[i]);
        }
    } //-- void setStarsApplianceCategory(StarsApplianceCategory) 

}
