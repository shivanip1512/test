/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsLMCustomerEvent {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _yukonDefID;

    /**
     * keeps track of state for field: _yukonDefID
    **/
    private boolean _has_yukonDefID;

    private java.lang.String _eventAction;

    private java.util.Date _eventDateTime;

    private java.lang.String _notes;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMCustomerEvent() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMCustomerEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteYukonDefID()
    {
        this._has_yukonDefID= false;
    } //-- void deleteYukonDefID() 

    /**
     * Returns the value of field 'eventAction'.
     * 
     * @return the value of field 'eventAction'.
    **/
    public java.lang.String getEventAction()
    {
        return this._eventAction;
    } //-- java.lang.String getEventAction() 

    /**
     * Returns the value of field 'eventDateTime'.
     * 
     * @return the value of field 'eventDateTime'.
    **/
    public java.util.Date getEventDateTime()
    {
        return this._eventDateTime;
    } //-- java.util.Date getEventDateTime() 

    /**
     * Returns the value of field 'notes'.
     * 
     * @return the value of field 'notes'.
    **/
    public java.lang.String getNotes()
    {
        return this._notes;
    } //-- java.lang.String getNotes() 

    /**
     * Returns the value of field 'yukonDefID'.
     * 
     * @return the value of field 'yukonDefID'.
    **/
    public int getYukonDefID()
    {
        return this._yukonDefID;
    } //-- int getYukonDefID() 

    /**
    **/
    public boolean hasYukonDefID()
    {
        return this._has_yukonDefID;
    } //-- boolean hasYukonDefID() 

    /**
     * Sets the value of field 'eventAction'.
     * 
     * @param eventAction the value of field 'eventAction'.
    **/
    public void setEventAction(java.lang.String eventAction)
    {
        this._eventAction = eventAction;
    } //-- void setEventAction(java.lang.String) 

    /**
     * Sets the value of field 'eventDateTime'.
     * 
     * @param eventDateTime the value of field 'eventDateTime'.
    **/
    public void setEventDateTime(java.util.Date eventDateTime)
    {
        this._eventDateTime = eventDateTime;
    } //-- void setEventDateTime(java.util.Date) 

    /**
     * Sets the value of field 'notes'.
     * 
     * @param notes the value of field 'notes'.
    **/
    public void setNotes(java.lang.String notes)
    {
        this._notes = notes;
    } //-- void setNotes(java.lang.String) 

    /**
     * Sets the value of field 'yukonDefID'.
     * 
     * @param yukonDefID the value of field 'yukonDefID'.
    **/
    public void setYukonDefID(int yukonDefID)
    {
        this._yukonDefID = yukonDefID;
        this._has_yukonDefID = true;
    } //-- void setYukonDefID(int) 

}
