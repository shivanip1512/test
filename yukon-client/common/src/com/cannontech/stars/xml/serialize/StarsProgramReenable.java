/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsProgramReenable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private boolean _cancelScheduledOptOut;

    /**
     * keeps track of state for field: _cancelScheduledOptOut
    **/
    private boolean _has_cancelScheduledOptOut;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramReenable() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramReenable()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteCancelScheduledOptOut()
    {
        this._has_cancelScheduledOptOut= false;
    } //-- void deleteCancelScheduledOptOut() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'cancelScheduledOptOut'.
     * 
     * @return the value of field 'cancelScheduledOptOut'.
    **/
    public boolean getCancelScheduledOptOut()
    {
        return this._cancelScheduledOptOut;
    } //-- boolean getCancelScheduledOptOut() 

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
    **/
    public boolean hasCancelScheduledOptOut()
    {
        return this._has_cancelScheduledOptOut;
    } //-- boolean hasCancelScheduledOptOut() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
     * Sets the value of field 'cancelScheduledOptOut'.
     * 
     * @param cancelScheduledOptOut the value of field
     * 'cancelScheduledOptOut'.
    **/
    public void setCancelScheduledOptOut(boolean cancelScheduledOptOut)
    {
        this._cancelScheduledOptOut = cancelScheduledOptOut;
        this._has_cancelScheduledOptOut = true;
    } //-- void setCancelScheduledOptOut(boolean) 

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

}
