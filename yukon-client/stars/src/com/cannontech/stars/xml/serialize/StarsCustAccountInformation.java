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

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsCustAccountInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustomerAccount _starsCustomerAccount;

    private StarsResidenceInformation _starsResidenceInformation;

    private StarsLMPrograms _starsLMPrograms;

    private StarsAppliances _starsAppliances;

    private StarsInventories _starsInventories;

    private StarsCallReportHistory _starsCallReportHistory;

    private StarsServiceRequestHistory _starsServiceRequestHistory;

    private StarsThermostatSettings _starsThermostatSettings;

    private StarsDefaultThermostatSettings _starsDefaultThermostatSettings;

    private StarsUser _starsUser;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustAccountInformation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountInformation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsAppliances'.
     * 
     * @return the value of field 'starsAppliances'.
    **/
    public StarsAppliances getStarsAppliances()
    {
        return this._starsAppliances;
    } //-- StarsAppliances getStarsAppliances() 

    /**
     * Returns the value of field 'starsCallReportHistory'.
     * 
     * @return the value of field 'starsCallReportHistory'.
    **/
    public StarsCallReportHistory getStarsCallReportHistory()
    {
        return this._starsCallReportHistory;
    } //-- StarsCallReportHistory getStarsCallReportHistory() 

    /**
     * Returns the value of field 'starsCustomerAccount'.
     * 
     * @return the value of field 'starsCustomerAccount'.
    **/
    public StarsCustomerAccount getStarsCustomerAccount()
    {
        return this._starsCustomerAccount;
    } //-- StarsCustomerAccount getStarsCustomerAccount() 

    /**
     * Returns the value of field 'starsDefaultThermostatSettings'.
     * 
     * @return the value of field 'starsDefaultThermostatSettings'.
    **/
    public StarsDefaultThermostatSettings getStarsDefaultThermostatSettings()
    {
        return this._starsDefaultThermostatSettings;
    } //-- StarsDefaultThermostatSettings getStarsDefaultThermostatSettings() 

    /**
     * Returns the value of field 'starsInventories'.
     * 
     * @return the value of field 'starsInventories'.
    **/
    public StarsInventories getStarsInventories()
    {
        return this._starsInventories;
    } //-- StarsInventories getStarsInventories() 

    /**
     * Returns the value of field 'starsLMPrograms'.
     * 
     * @return the value of field 'starsLMPrograms'.
    **/
    public StarsLMPrograms getStarsLMPrograms()
    {
        return this._starsLMPrograms;
    } //-- StarsLMPrograms getStarsLMPrograms() 

    /**
     * Returns the value of field 'starsResidenceInformation'.
     * 
     * @return the value of field 'starsResidenceInformation'.
    **/
    public StarsResidenceInformation getStarsResidenceInformation()
    {
        return this._starsResidenceInformation;
    } //-- StarsResidenceInformation getStarsResidenceInformation() 

    /**
     * Returns the value of field 'starsServiceRequestHistory'.
     * 
     * @return the value of field 'starsServiceRequestHistory'.
    **/
    public StarsServiceRequestHistory getStarsServiceRequestHistory()
    {
        return this._starsServiceRequestHistory;
    } //-- StarsServiceRequestHistory getStarsServiceRequestHistory() 

    /**
     * Returns the value of field 'starsThermostatSettings'.
     * 
     * @return the value of field 'starsThermostatSettings'.
    **/
    public StarsThermostatSettings getStarsThermostatSettings()
    {
        return this._starsThermostatSettings;
    } //-- StarsThermostatSettings getStarsThermostatSettings() 

    /**
     * Returns the value of field 'starsUser'.
     * 
     * @return the value of field 'starsUser'.
    **/
    public StarsUser getStarsUser()
    {
        return this._starsUser;
    } //-- StarsUser getStarsUser() 

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'starsAppliances'.
     * 
     * @param starsAppliances the value of field 'starsAppliances'.
    **/
    public void setStarsAppliances(StarsAppliances starsAppliances)
    {
        this._starsAppliances = starsAppliances;
    } //-- void setStarsAppliances(StarsAppliances) 

    /**
     * Sets the value of field 'starsCallReportHistory'.
     * 
     * @param starsCallReportHistory the value of field
     * 'starsCallReportHistory'.
    **/
    public void setStarsCallReportHistory(StarsCallReportHistory starsCallReportHistory)
    {
        this._starsCallReportHistory = starsCallReportHistory;
    } //-- void setStarsCallReportHistory(StarsCallReportHistory) 

    /**
     * Sets the value of field 'starsCustomerAccount'.
     * 
     * @param starsCustomerAccount the value of field
     * 'starsCustomerAccount'.
    **/
    public void setStarsCustomerAccount(StarsCustomerAccount starsCustomerAccount)
    {
        this._starsCustomerAccount = starsCustomerAccount;
    } //-- void setStarsCustomerAccount(StarsCustomerAccount) 

    /**
     * Sets the value of field 'starsDefaultThermostatSettings'.
     * 
     * @param starsDefaultThermostatSettings the value of field
     * 'starsDefaultThermostatSettings'.
    **/
    public void setStarsDefaultThermostatSettings(StarsDefaultThermostatSettings starsDefaultThermostatSettings)
    {
        this._starsDefaultThermostatSettings = starsDefaultThermostatSettings;
    } //-- void setStarsDefaultThermostatSettings(StarsDefaultThermostatSettings) 

    /**
     * Sets the value of field 'starsInventories'.
     * 
     * @param starsInventories the value of field 'starsInventories'
    **/
    public void setStarsInventories(StarsInventories starsInventories)
    {
        this._starsInventories = starsInventories;
    } //-- void setStarsInventories(StarsInventories) 

    /**
     * Sets the value of field 'starsLMPrograms'.
     * 
     * @param starsLMPrograms the value of field 'starsLMPrograms'.
    **/
    public void setStarsLMPrograms(StarsLMPrograms starsLMPrograms)
    {
        this._starsLMPrograms = starsLMPrograms;
    } //-- void setStarsLMPrograms(StarsLMPrograms) 

    /**
     * Sets the value of field 'starsResidenceInformation'.
     * 
     * @param starsResidenceInformation the value of field
     * 'starsResidenceInformation'.
    **/
    public void setStarsResidenceInformation(StarsResidenceInformation starsResidenceInformation)
    {
        this._starsResidenceInformation = starsResidenceInformation;
    } //-- void setStarsResidenceInformation(StarsResidenceInformation) 

    /**
     * Sets the value of field 'starsServiceRequestHistory'.
     * 
     * @param starsServiceRequestHistory the value of field
     * 'starsServiceRequestHistory'.
    **/
    public void setStarsServiceRequestHistory(StarsServiceRequestHistory starsServiceRequestHistory)
    {
        this._starsServiceRequestHistory = starsServiceRequestHistory;
    } //-- void setStarsServiceRequestHistory(StarsServiceRequestHistory) 

    /**
     * Sets the value of field 'starsThermostatSettings'.
     * 
     * @param starsThermostatSettings the value of field
     * 'starsThermostatSettings'.
    **/
    public void setStarsThermostatSettings(StarsThermostatSettings starsThermostatSettings)
    {
        this._starsThermostatSettings = starsThermostatSettings;
    } //-- void setStarsThermostatSettings(StarsThermostatSettings) 

    /**
     * Sets the value of field 'starsUser'.
     * 
     * @param starsUser the value of field 'starsUser'.
    **/
    public void setStarsUser(StarsUser starsUser)
    {
        this._starsUser = starsUser;
    } //-- void setStarsUser(StarsUser) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustAccountInformation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustAccountInformation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustAccountInformation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountInformation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
