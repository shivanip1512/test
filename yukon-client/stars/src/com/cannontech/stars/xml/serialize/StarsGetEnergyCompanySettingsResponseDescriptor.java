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

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.handlers.*;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.exolab.castor.xml.validators.*;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsGetEnergyCompanySettingsResponseDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String nsPrefix;

    private java.lang.String nsURI;

    private java.lang.String xmlName;

    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetEnergyCompanySettingsResponseDescriptor() {
        super();
        xmlName = "stars-GetEnergyCompanySettingsResponse";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _starsEnergyCompany
        desc = new XMLFieldDescriptorImpl(StarsEnergyCompany.class, "_starsEnergyCompany", "stars-EnergyCompany", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsEnergyCompany();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsEnergyCompany( (StarsEnergyCompany) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsEnergyCompany();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsEnergyCompany
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _starsWebConfig
        desc = new XMLFieldDescriptorImpl(StarsWebConfig.class, "_starsWebConfig", "stars-WebConfig", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsWebConfig();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsWebConfig( (StarsWebConfig) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsWebConfig();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsWebConfig
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsEnrollmentPrograms
        desc = new XMLFieldDescriptorImpl(StarsEnrollmentPrograms.class, "_starsEnrollmentPrograms", "stars-EnrollmentPrograms", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsEnrollmentPrograms();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsEnrollmentPrograms( (StarsEnrollmentPrograms) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsEnrollmentPrograms();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsEnrollmentPrograms
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCustomerSelectionLists
        desc = new XMLFieldDescriptorImpl(StarsCustomerSelectionLists.class, "_starsCustomerSelectionLists", "stars-CustomerSelectionLists", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsCustomerSelectionLists();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsCustomerSelectionLists( (StarsCustomerSelectionLists) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCustomerSelectionLists();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCustomerSelectionLists
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCustomerFAQs
        desc = new XMLFieldDescriptorImpl(StarsCustomerFAQs.class, "_starsCustomerFAQs", "stars-CustomerFAQs", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsCustomerFAQs();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsCustomerFAQs( (StarsCustomerFAQs) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCustomerFAQs();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCustomerFAQs
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsServiceCompanies
        desc = new XMLFieldDescriptorImpl(StarsServiceCompanies.class, "_starsServiceCompanies", "stars-ServiceCompanies", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsServiceCompanies();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsServiceCompanies( (StarsServiceCompanies) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsServiceCompanies();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsServiceCompanies
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsExitInterviewQuestions
        desc = new XMLFieldDescriptorImpl(StarsExitInterviewQuestions.class, "_starsExitInterviewQuestions", "stars-ExitInterviewQuestions", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsExitInterviewQuestions();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.setStarsExitInterviewQuestions( (StarsExitInterviewQuestions) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsExitInterviewQuestions();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsExitInterviewQuestions
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsDefaultThermostatSettingsList
        desc = new XMLFieldDescriptorImpl(StarsDefaultThermostatSettings.class, "_starsDefaultThermostatSettingsList", "stars-DefaultThermostatSettings", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                return target.getStarsDefaultThermostatSettings();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsGetEnergyCompanySettingsResponse target = (StarsGetEnergyCompanySettingsResponse) object;
                    target.addStarsDefaultThermostatSettings( (StarsDefaultThermostatSettings) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsDefaultThermostatSettings();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsDefaultThermostatSettingsList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        fieldValidator.setMaxOccurs(3);
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponseDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
    **/
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
    **/
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
    **/
    public java.lang.Class getJavaClass()
    {
        return com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse.class;
    } //-- java.lang.Class getJavaClass() 

    /**
    **/
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
    **/
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
    **/
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
    **/
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
