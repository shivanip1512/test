/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsEnergyCompanySettings.java,v 1.3 2004/05/04 16:10:39 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2004/05/04 16:10:39 $
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

    private java.util.Vector _starsDefaultThermostatSettingsList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsEnergyCompanySettings() {
        super();
        _starsDefaultThermostatSettingsList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsDefaultThermostatSettings
    **/
    public void addStarsDefaultThermostatSettings(StarsDefaultThermostatSettings vStarsDefaultThermostatSettings)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsDefaultThermostatSettingsList.size() < 3)) {
            throw new IndexOutOfBoundsException();
        }
        _starsDefaultThermostatSettingsList.addElement(vStarsDefaultThermostatSettings);
    } //-- void addStarsDefaultThermostatSettings(StarsDefaultThermostatSettings) 

    /**
     * 
     * 
     * @param index
     * @param vStarsDefaultThermostatSettings
    **/
    public void addStarsDefaultThermostatSettings(int index, StarsDefaultThermostatSettings vStarsDefaultThermostatSettings)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsDefaultThermostatSettingsList.size() < 3)) {
            throw new IndexOutOfBoundsException();
        }
        _starsDefaultThermostatSettingsList.insertElementAt(vStarsDefaultThermostatSettings, index);
    } //-- void addStarsDefaultThermostatSettings(int, StarsDefaultThermostatSettings) 

    /**
    **/
    public void deleteEnergyCompanyID()
    {
        this._has_energyCompanyID= false;
    } //-- void deleteEnergyCompanyID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsDefaultThermostatSettings()
    {
        return _starsDefaultThermostatSettingsList.elements();
    } //-- java.util.Enumeration enumerateStarsDefaultThermostatSettings() 

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
     * 
     * 
     * @param index
    **/
    public StarsDefaultThermostatSettings getStarsDefaultThermostatSettings(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsDefaultThermostatSettingsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsDefaultThermostatSettings) _starsDefaultThermostatSettingsList.elementAt(index);
    } //-- StarsDefaultThermostatSettings getStarsDefaultThermostatSettings(int) 

    /**
    **/
    public StarsDefaultThermostatSettings[] getStarsDefaultThermostatSettings()
    {
        int size = _starsDefaultThermostatSettingsList.size();
        StarsDefaultThermostatSettings[] mArray = new StarsDefaultThermostatSettings[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsDefaultThermostatSettings) _starsDefaultThermostatSettingsList.elementAt(index);
        }
        return mArray;
    } //-- StarsDefaultThermostatSettings[] getStarsDefaultThermostatSettings() 

    /**
    **/
    public int getStarsDefaultThermostatSettingsCount()
    {
        return _starsDefaultThermostatSettingsList.size();
    } //-- int getStarsDefaultThermostatSettingsCount() 

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
    **/
    public void removeAllStarsDefaultThermostatSettings()
    {
        _starsDefaultThermostatSettingsList.removeAllElements();
    } //-- void removeAllStarsDefaultThermostatSettings() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsDefaultThermostatSettings removeStarsDefaultThermostatSettings(int index)
    {
        java.lang.Object obj = _starsDefaultThermostatSettingsList.elementAt(index);
        _starsDefaultThermostatSettingsList.removeElementAt(index);
        return (StarsDefaultThermostatSettings) obj;
    } //-- StarsDefaultThermostatSettings removeStarsDefaultThermostatSettings(int) 

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
     * 
     * 
     * @param index
     * @param vStarsDefaultThermostatSettings
    **/
    public void setStarsDefaultThermostatSettings(int index, StarsDefaultThermostatSettings vStarsDefaultThermostatSettings)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsDefaultThermostatSettingsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 3)) {
            throw new IndexOutOfBoundsException();
        }
        _starsDefaultThermostatSettingsList.setElementAt(vStarsDefaultThermostatSettings, index);
    } //-- void setStarsDefaultThermostatSettings(int, StarsDefaultThermostatSettings) 

    /**
     * 
     * 
     * @param starsDefaultThermostatSettingsArray
    **/
    public void setStarsDefaultThermostatSettings(StarsDefaultThermostatSettings[] starsDefaultThermostatSettingsArray)
    {
        //-- copy array
        _starsDefaultThermostatSettingsList.removeAllElements();
        for (int i = 0; i < starsDefaultThermostatSettingsArray.length; i++) {
            _starsDefaultThermostatSettingsList.addElement(starsDefaultThermostatSettingsArray[i]);
        }
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
