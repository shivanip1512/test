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
public abstract class StarsSrvReq implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _orderID;

    /**
     * keeps track of state for field: _orderID
    **/
    private boolean _has_orderID;

    private java.lang.String _orderNumber;

    private ServiceType _serviceType;

    private java.util.Date _dateReported;

    private ServiceCompany _serviceCompany;

    private java.lang.String _orderedBy;

    private java.lang.String _description;

    private CurrentState _currentState;

    private java.util.Date _dateScheduled;

    private java.util.Date _dateCompleted;

    private java.lang.String _actionTaken;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSrvReq() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSrvReq()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteOrderID()
    {
        this._has_orderID= false;
    } //-- void deleteOrderID() 

    /**
     * Returns the value of field 'actionTaken'.
     * 
     * @return the value of field 'actionTaken'.
    **/
    public java.lang.String getActionTaken()
    {
        return this._actionTaken;
    } //-- java.lang.String getActionTaken() 

    /**
     * Returns the value of field 'currentState'.
     * 
     * @return the value of field 'currentState'.
    **/
    public CurrentState getCurrentState()
    {
        return this._currentState;
    } //-- CurrentState getCurrentState() 

    /**
     * Returns the value of field 'dateCompleted'.
     * 
     * @return the value of field 'dateCompleted'.
    **/
    public java.util.Date getDateCompleted()
    {
        return this._dateCompleted;
    } //-- java.util.Date getDateCompleted() 

    /**
     * Returns the value of field 'dateReported'.
     * 
     * @return the value of field 'dateReported'.
    **/
    public java.util.Date getDateReported()
    {
        return this._dateReported;
    } //-- java.util.Date getDateReported() 

    /**
     * Returns the value of field 'dateScheduled'.
     * 
     * @return the value of field 'dateScheduled'.
    **/
    public java.util.Date getDateScheduled()
    {
        return this._dateScheduled;
    } //-- java.util.Date getDateScheduled() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Returns the value of field 'orderID'.
     * 
     * @return the value of field 'orderID'.
    **/
    public int getOrderID()
    {
        return this._orderID;
    } //-- int getOrderID() 

    /**
     * Returns the value of field 'orderNumber'.
     * 
     * @return the value of field 'orderNumber'.
    **/
    public java.lang.String getOrderNumber()
    {
        return this._orderNumber;
    } //-- java.lang.String getOrderNumber() 

    /**
     * Returns the value of field 'orderedBy'.
     * 
     * @return the value of field 'orderedBy'.
    **/
    public java.lang.String getOrderedBy()
    {
        return this._orderedBy;
    } //-- java.lang.String getOrderedBy() 

    /**
     * Returns the value of field 'serviceCompany'.
     * 
     * @return the value of field 'serviceCompany'.
    **/
    public ServiceCompany getServiceCompany()
    {
        return this._serviceCompany;
    } //-- ServiceCompany getServiceCompany() 

    /**
     * Returns the value of field 'serviceType'.
     * 
     * @return the value of field 'serviceType'.
    **/
    public ServiceType getServiceType()
    {
        return this._serviceType;
    } //-- ServiceType getServiceType() 

    /**
    **/
    public boolean hasOrderID()
    {
        return this._has_orderID;
    } //-- boolean hasOrderID() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'actionTaken'.
     * 
     * @param actionTaken the value of field 'actionTaken'.
    **/
    public void setActionTaken(java.lang.String actionTaken)
    {
        this._actionTaken = actionTaken;
    } //-- void setActionTaken(java.lang.String) 

    /**
     * Sets the value of field 'currentState'.
     * 
     * @param currentState the value of field 'currentState'.
    **/
    public void setCurrentState(CurrentState currentState)
    {
        this._currentState = currentState;
    } //-- void setCurrentState(CurrentState) 

    /**
     * Sets the value of field 'dateCompleted'.
     * 
     * @param dateCompleted the value of field 'dateCompleted'.
    **/
    public void setDateCompleted(java.util.Date dateCompleted)
    {
        this._dateCompleted = dateCompleted;
    } //-- void setDateCompleted(java.util.Date) 

    /**
     * Sets the value of field 'dateReported'.
     * 
     * @param dateReported the value of field 'dateReported'.
    **/
    public void setDateReported(java.util.Date dateReported)
    {
        this._dateReported = dateReported;
    } //-- void setDateReported(java.util.Date) 

    /**
     * Sets the value of field 'dateScheduled'.
     * 
     * @param dateScheduled the value of field 'dateScheduled'.
    **/
    public void setDateScheduled(java.util.Date dateScheduled)
    {
        this._dateScheduled = dateScheduled;
    } //-- void setDateScheduled(java.util.Date) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'orderID'.
     * 
     * @param orderID the value of field 'orderID'.
    **/
    public void setOrderID(int orderID)
    {
        this._orderID = orderID;
        this._has_orderID = true;
    } //-- void setOrderID(int) 

    /**
     * Sets the value of field 'orderNumber'.
     * 
     * @param orderNumber the value of field 'orderNumber'.
    **/
    public void setOrderNumber(java.lang.String orderNumber)
    {
        this._orderNumber = orderNumber;
    } //-- void setOrderNumber(java.lang.String) 

    /**
     * Sets the value of field 'orderedBy'.
     * 
     * @param orderedBy the value of field 'orderedBy'.
    **/
    public void setOrderedBy(java.lang.String orderedBy)
    {
        this._orderedBy = orderedBy;
    } //-- void setOrderedBy(java.lang.String) 

    /**
     * Sets the value of field 'serviceCompany'.
     * 
     * @param serviceCompany the value of field 'serviceCompany'.
    **/
    public void setServiceCompany(ServiceCompany serviceCompany)
    {
        this._serviceCompany = serviceCompany;
    } //-- void setServiceCompany(ServiceCompany) 

    /**
     * Sets the value of field 'serviceType'.
     * 
     * @param serviceType the value of field 'serviceType'.
    **/
    public void setServiceType(ServiceType serviceType)
    {
        this._serviceType = serviceType;
    } //-- void setServiceType(ServiceType) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
