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
public abstract class StarsServiceRequest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _orderNumber;

    private ServiceType _serviceType;

    private CurrentState _currentState;

    private ServiceCompany _serviceCompany;

    private java.util.Date _dateAssigned;

    private java.lang.String _description;

    private java.util.Date _dateCompleted;

    private java.lang.String _actionTaken;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsServiceRequest() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsServiceRequest()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'dateAssigned'.
     * 
     * @return the value of field 'dateAssigned'.
    **/
    public java.util.Date getDateAssigned()
    {
        return this._dateAssigned;
    } //-- java.util.Date getDateAssigned() 

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
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'description'.
    **/
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

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
     * Sets the value of field 'dateAssigned'.
     * 
     * @param dateAssigned the value of field 'dateAssigned'.
    **/
    public void setDateAssigned(java.util.Date dateAssigned)
    {
        this._dateAssigned = dateAssigned;
    } //-- void setDateAssigned(java.util.Date) 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
    **/
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

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
