/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/



/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class DataSetALLItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private CustomerContact _customerContact;

    private CustomerInformation _customerInformation;

    private LMAppliance _LMAppliance;

    private LMHardware _LMHardware;


      //----------------/
     //- Constructors -/
    //----------------/

    public DataSetALLItem() {
        super();
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALLItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'customerContact'.
     * 
     * @return the value of field 'customerContact'.
    **/
    public CustomerContact getCustomerContact()
    {
        return this._customerContact;
    } //-- CustomerContact getCustomerContact() 

    /**
     * Returns the value of field 'customerInformation'.
     * 
     * @return the value of field 'customerInformation'.
    **/
    public CustomerInformation getCustomerInformation()
    {
        return this._customerInformation;
    } //-- CustomerInformation getCustomerInformation() 

    /**
     * Returns the value of field 'LMAppliance'.
     * 
     * @return the value of field 'LMAppliance'.
    **/
    public LMAppliance getLMAppliance()
    {
        return this._LMAppliance;
    } //-- LMAppliance getLMAppliance() 

    /**
     * Returns the value of field 'LMHardware'.
     * 
     * @return the value of field 'LMHardware'.
    **/
    public LMHardware getLMHardware()
    {
        return this._LMHardware;
    } //-- LMHardware getLMHardware() 

    /**
     * Sets the value of field 'customerContact'.
     * 
     * @param customerContact the value of field 'customerContact'.
    **/
    public void setCustomerContact(CustomerContact customerContact)
    {
        this._customerContact = customerContact;
    } //-- void setCustomerContact(CustomerContact) 

    /**
     * Sets the value of field 'customerInformation'.
     * 
     * @param customerInformation the value of field
     * 'customerInformation'.
    **/
    public void setCustomerInformation(CustomerInformation customerInformation)
    {
        this._customerInformation = customerInformation;
    } //-- void setCustomerInformation(CustomerInformation) 

    /**
     * Sets the value of field 'LMAppliance'.
     * 
     * @param LMAppliance the value of field 'LMAppliance'.
    **/
    public void setLMAppliance(LMAppliance LMAppliance)
    {
        this._LMAppliance = LMAppliance;
    } //-- void setLMAppliance(LMAppliance) 

    /**
     * Sets the value of field 'LMHardware'.
     * 
     * @param LMHardware the value of field 'LMHardware'.
    **/
    public void setLMHardware(LMHardware LMHardware)
    {
        this._LMHardware = LMHardware;
    } //-- void setLMHardware(LMHardware) 

}
