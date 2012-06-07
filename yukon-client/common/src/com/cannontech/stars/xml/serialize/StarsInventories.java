/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsInventories.java,v 1.96 2008/06/23 20:01:33 nmeverden Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

/**
 * 
 * 
 * @version $Revision: 1.96 $ $Date: 2008/06/23 20:01:33 $
**/
public class StarsInventories {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Vector _starsInventoryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsInventories() {
        super();
        _starsInventoryList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsInventories()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsInventory
    **/
    public void addStarsInventory(StarsInventory vStarsInventory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsInventoryList.addElement(vStarsInventory);
    } //-- void addStarsInventory(StarsInventory) 

    /**
     * 
     * 
     * @param index
     * @param vStarsInventory
    **/
    public void addStarsInventory(int index, StarsInventory vStarsInventory)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsInventoryList.insertElementAt(vStarsInventory, index);
    } //-- void addStarsInventory(int, StarsInventory) 

    /**
    **/
    public java.util.Enumeration enumerateStarsInventory()
    {
        return _starsInventoryList.elements();
    } //-- java.util.Enumeration enumerateStarsInventory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsInventory getStarsInventory(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsInventoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsInventory) _starsInventoryList.elementAt(index);
    } //-- StarsInventory getStarsInventory(int) 

    /**
    **/
    public StarsInventory[] getStarsInventory()
    {
        int size = _starsInventoryList.size();
        StarsInventory[] mArray = new StarsInventory[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsInventory) _starsInventoryList.elementAt(index);
        }
        return mArray;
    } //-- StarsInventory[] getStarsInventory() 

    /**
    **/
    public int getStarsInventoryCount()
    {
        return _starsInventoryList.size();
    } //-- int getStarsInventoryCount() 

    /**
    **/
    public void removeAllStarsInventory()
    {
        _starsInventoryList.removeAllElements();
    } //-- void removeAllStarsInventory() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsInventory removeStarsInventory(int index)
    {
        java.lang.Object obj = _starsInventoryList.elementAt(index);
        _starsInventoryList.removeElementAt(index);
        return (StarsInventory) obj;
    } //-- StarsInventory removeStarsInventory(int) 

    /**
     * 
     * 
     * @param index
     * @param vStarsInventory
    **/
    public void setStarsInventory(int index, StarsInventory vStarsInventory)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsInventoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsInventoryList.setElementAt(vStarsInventory, index);
    } //-- void setStarsInventory(int, StarsInventory) 

    /**
     * 
     * 
     * @param starsInventoryArray
    **/
    public void setStarsInventory(StarsInventory[] starsInventoryArray)
    {
        //-- copy array
        _starsInventoryList.removeAllElements();
        for (int i = 0; i < starsInventoryArray.length; i++) {
            _starsInventoryList.addElement(starsInventoryArray[i]);
        }
    } //-- void setStarsInventory(StarsInventory) 

}
