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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsGetEnergyCompanySettingsResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsEnergyCompany _starsEnergyCompany;

    private StarsWebConfig _starsWebConfig;

    private StarsEnrollmentPrograms _starsEnrollmentPrograms;

    private StarsCustomerSelectionLists _starsCustomerSelectionLists;

    private StarsCustomerFAQs _starsCustomerFAQs;

    private StarsServiceCompanies _starsServiceCompanies;

    private StarsExitInterviewQuestions _starsExitInterviewQuestions;

    private StarsDefaultThermostatSettings _starsDefaultThermostatSettings;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetEnergyCompanySettingsResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'starsDefaultThermostatSettings'.
     * 
     * @return the value of field 'starsDefaultThermostatSettings'.
    **/
    public StarsDefaultThermostatSettings getStarsDefaultThermostatSettings()
    {
        return this._starsDefaultThermostatSettings;
    } //-- StarsDefaultThermostatSettings getStarsDefaultThermostatSettings() 

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
    public static com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
