/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSaveThermostatSchedule.java,v 1.21 2008/06/23 20:01:33 nmeverden Exp $
 */

package com.cannontech.stars.xml.serialize;

/**
 * 
 * 
 * @version $Revision: 1.21 $ $Date: 2008/06/23 20:01:33 $
**/
public class StarsSaveThermostatSchedule {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private java.lang.String _scheduleName;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSaveThermostatSchedule() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSaveThermostatSchedule()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'inventoryID'.
     * 
     * @return the value of field 'inventoryID'.
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
     * Returns the value of field 'scheduleName'.
     * 
     * @return the value of field 'scheduleName'.
    **/
    public java.lang.String getScheduleName()
    {
        return this._scheduleName;
    } //-- java.lang.String getScheduleName() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
     * Sets the value of field 'inventoryID'.
     * 
     * @param inventoryID the value of field 'inventoryID'.
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * Sets the value of field 'scheduleName'.
     * 
     * @param scheduleName the value of field 'scheduleName'.
    **/
    public void setScheduleName(java.lang.String scheduleName)
    {
        this._scheduleName = scheduleName;
    } //-- void setScheduleName(java.lang.String) 

}
