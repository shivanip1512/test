/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsEnergyCompanySettings.java,v 1.19 2004/10/06 20:59:30 zyao Exp $
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
 * @version $Revision: 1.19 $ $Date: 2004/10/06 20:59:30 $
**/
public class StarsEnergyCompanySettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _energyCompanyID;

    /**
     * keeps track of state for field: _energyCompanyID
    **/
    private boolean _has_energyCompanyID;

    private StarsEnergyCompany _starsEnergyCompany;

    private StarsWebConfig _starsWebConfig;

    private StarsEnrollmentPrograms _starsEnrollmentPrograms;

    private StarsCustomerSelectionLists _starsCustomerSelectionLists;

    private StarsCustomerFAQs _starsCustomerFAQs;

    private StarsServiceCompanies _starsServiceCompanies;

    private StarsExitInterviewQuestions _starsExitInterviewQuestions;

    private StarsDefaultThermostatSchedules _starsDefaultThermostatSchedules;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsEnergyCompanySettings() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteEnergyCompanyID()
    {
        this._has_energyCompanyID= false;
    } //-- void deleteEnergyCompanyID() 

    /**
     * Returns the value of field 'energyCompanyID'.
     * 
     * @return the value of field 'energyCompanyID'.
    **/
    public int getEnergyCompanyID()
    {
        return this._energyCompanyID;
    } //-- int getEnergyCompanyID() 

    /**
     * Returns the value of field 'starsCustomerFAQs'.
     * 
     * @return the value of field 'starsCustomerFAQs'.
    **/
    public StarsCustomerFAQs getStarsCustomerFAQs()
    {
        return this._starsCustomerFAQs;
    } //-- StarsCustomerFAQs getStarsCustomerFAQs() 

    /**
     * Returns the value of field 'starsCustomerSelectionLists'.
     * 
     * @return the value of field 'starsCustomerSelectionLists'.
    **/
    public StarsCustomerSelectionLists getStarsCustomerSelectionLists()
    {
        return this._starsCustomerSelectionLists;
    } //-- StarsCustomerSelectionLists getStarsCustomerSelectionLists() 

    /**
     * Returns the value of field
     * 'starsDefaultThermostatSchedules'.
     * 
     * @return the value of field 'starsDefaultThermostatSchedules'.
    **/
    public StarsDefaultThermostatSchedules getStarsDefaultThermostatSchedules()
    {
        return this._starsDefaultThermostatSchedules;
    } //-- StarsDefaultThermostatSchedules getStarsDefaultThermostatSchedules() 

    /**
     * Returns the value of field 'starsEnergyCompany'.
     * 
     * @return the value of field 'starsEnergyCompany'.
    **/
    public StarsEnergyCompany getStarsEnergyCompany()
    {
        return this._starsEnergyCompany;
    } //-- StarsEnergyCompany getStarsEnergyCompany() 

    /**
     * Returns the value of field 'starsEnrollmentPrograms'.
     * 
     * @return the value of field 'starsEnrollmentPrograms'.
    **/
    public StarsEnrollmentPrograms getStarsEnrollmentPrograms()
    {
        return this._starsEnrollmentPrograms;
    } //-- StarsEnrollmentPrograms getStarsEnrollmentPrograms() 

    /**
     * Returns the value of field 'starsExitInterviewQuestions'.
     * 
     * @return the value of field 'starsExitInterviewQuestions'.
    **/
    public StarsExitInterviewQuestions getStarsExitInterviewQuestions()
    {
        return this._starsExitInterviewQuestions;
    } //-- StarsExitInterviewQuestions getStarsExitInterviewQuestions() 

    /**
     * Returns the value of field 'starsServiceCompanies'.
     * 
     * @return the value of field 'starsServiceCompanies'.
    **/
    public StarsServiceCompanies getStarsServiceCompanies()
    {
        return this._starsServiceCompanies;
    } //-- StarsServiceCompanies getStarsServiceCompanies() 

    /**
     * Returns the value of field 'starsWebConfig'.
     * 
     * @return the value of field 'starsWebConfig'.
    **/
    public StarsWebConfig getStarsWebConfig()
    {
        return this._starsWebConfig;
    } //-- StarsWebConfig getStarsWebConfig() 

    /**
    **/
    public boolean hasEnergyCompanyID()
    {
        return this._has_energyCompanyID;
    } //-- boolean hasEnergyCompanyID() 

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
     * Sets the value of field 'energyCompanyID'.
     * 
     * @param energyCompanyID the value of field 'energyCompanyID'.
    **/
    public void setEnergyCompanyID(int energyCompanyID)
    {
        this._energyCompanyID = energyCompanyID;
        this._has_energyCompanyID = true;
    } //-- void setEnergyCompanyID(int) 

    /**
     * Sets the value of field 'starsCustomerFAQs'.
     * 
     * @param starsCustomerFAQs the value of field
     * 'starsCustomerFAQs'.
    **/
    public void setStarsCustomerFAQs(StarsCustomerFAQs starsCustomerFAQs)
    {
        this._starsCustomerFAQs = starsCustomerFAQs;
    } //-- void setStarsCustomerFAQs(StarsCustomerFAQs) 

    /**
     * Sets the value of field 'starsCustomerSelectionLists'.
     * 
     * @param starsCustomerSelectionLists the value of field
     * 'starsCustomerSelectionLists'.
    **/
    public void setStarsCustomerSelectionLists(StarsCustomerSelectionLists starsCustomerSelectionLists)
    {
        this._starsCustomerSelectionLists = starsCustomerSelectionLists;
    } //-- void setStarsCustomerSelectionLists(StarsCustomerSelectionLists) 

    /**
     * Sets the value of field 'starsDefaultThermostatSchedules'.
     * 
     * @param starsDefaultThermostatSchedules the value of field
     * 'starsDefaultThermostatSchedules'.
    **/
    public void setStarsDefaultThermostatSchedules(StarsDefaultThermostatSchedules starsDefaultThermostatSchedules)
    {
        this._starsDefaultThermostatSchedules = starsDefaultThermostatSchedules;
    } //-- void setStarsDefaultThermostatSchedules(StarsDefaultThermostatSchedules) 

    /**
     * Sets the value of field 'starsEnergyCompany'.
     * 
     * @param starsEnergyCompany the value of field
     * 'starsEnergyCompany'.
    **/
    public void setStarsEnergyCompany(StarsEnergyCompany starsEnergyCompany)
    {
        this._starsEnergyCompany = starsEnergyCompany;
    } //-- void setStarsEnergyCompany(StarsEnergyCompany) 

    /**
     * Sets the value of field 'starsEnrollmentPrograms'.
     * 
     * @param starsEnrollmentPrograms the value of field
     * 'starsEnrollmentPrograms'.
    **/
    public void setStarsEnrollmentPrograms(StarsEnrollmentPrograms starsEnrollmentPrograms)
    {
        this._starsEnrollmentPrograms = starsEnrollmentPrograms;
    } //-- void setStarsEnrollmentPrograms(StarsEnrollmentPrograms) 

    /**
     * Sets the value of field 'starsExitInterviewQuestions'.
     * 
     * @param starsExitInterviewQuestions the value of field
     * 'starsExitInterviewQuestions'.
    **/
    public void setStarsExitInterviewQuestions(StarsExitInterviewQuestions starsExitInterviewQuestions)
    {
        this._starsExitInterviewQuestions = starsExitInterviewQuestions;
    } //-- void setStarsExitInterviewQuestions(StarsExitInterviewQuestions) 

    /**
     * Sets the value of field 'starsServiceCompanies'.
     * 
     * @param starsServiceCompanies the value of field
     * 'starsServiceCompanies'.
    **/
    public void setStarsServiceCompanies(StarsServiceCompanies starsServiceCompanies)
    {
        this._starsServiceCompanies = starsServiceCompanies;
    } //-- void setStarsServiceCompanies(StarsServiceCompanies) 

    /**
     * Sets the value of field 'starsWebConfig'.
     * 
     * @param starsWebConfig the value of field 'starsWebConfig'.
    **/
    public void setStarsWebConfig(StarsWebConfig starsWebConfig)
    {
        this._starsWebConfig = starsWebConfig;
    } //-- void setStarsWebConfig(StarsWebConfig) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
