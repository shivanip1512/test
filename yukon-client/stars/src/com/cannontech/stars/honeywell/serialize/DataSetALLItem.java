/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.honeywell.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import org.exolab.castor.xml.*;

/**
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
    **/
    public CustomerContact getCustomerContact()
    {
        return this._customerContact;
    } //-- CustomerContact getCustomerContact() 

    /**
    **/
    public CustomerInformation getCustomerInformation()
    {
        return this._customerInformation;
    } //-- CustomerInformation getCustomerInformation() 

    /**
    **/
    public LMAppliance getLMAppliance()
    {
        return this._LMAppliance;
    } //-- LMAppliance getLMAppliance() 

    /**
    **/
    public LMHardware getLMHardware()
    {
        return this._LMHardware;
    } //-- LMHardware getLMHardware() 

    /**
     * 
     * @param customerContact
    **/
    public void setCustomerContact(CustomerContact customerContact)
    {
        this._customerContact = customerContact;
    } //-- void setCustomerContact(CustomerContact) 

    /**
     * 
     * @param customerInformation
    **/
    public void setCustomerInformation(CustomerInformation customerInformation)
    {
        this._customerInformation = customerInformation;
    } //-- void setCustomerInformation(CustomerInformation) 

    /**
     * 
     * @param LMAppliance
    **/
    public void setLMAppliance(LMAppliance LMAppliance)
    {
        this._LMAppliance = LMAppliance;
    } //-- void setLMAppliance(LMAppliance) 

    /**
     * 
     * @param LMHardware
    **/
    public void setLMHardware(LMHardware LMHardware)
    {
        this._LMHardware = LMHardware;
    } //-- void setLMHardware(LMHardware) 

}
