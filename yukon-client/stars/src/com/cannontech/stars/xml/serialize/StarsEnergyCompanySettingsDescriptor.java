/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsEnergyCompanySettingsDescriptor.java,v 1.14 2004/06/25 21:37:07 zyao Exp $
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
 * @version $Revision: 1.14 $ $Date: 2004/06/25 21:37:07 $
**/
public class StarsEnergyCompanySettingsDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsEnergyCompanySettingsDescriptor() {
        super();
        xmlName = "stars-EnergyCompanySettings";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _energyCompanyID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_energyCompanyID", "energyCompanyID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                if(!target.hasEnergyCompanyID())
                    return null;
                return new Integer(target.getEnergyCompanyID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteEnergyCompanyID();
                        return;
                    }
                    target.setEnergyCompanyID( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _energyCompanyID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _starsEnergyCompany
        desc = new XMLFieldDescriptorImpl(StarsEnergyCompany.class, "_starsEnergyCompany", "stars-EnergyCompany", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsEnergyCompany();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsWebConfig();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsEnrollmentPrograms();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsCustomerSelectionLists();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsCustomerFAQs();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsServiceCompanies();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsExitInterviewQuestions();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
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
        
        //-- _starsDefaultThermostatSchedules
        desc = new XMLFieldDescriptorImpl(StarsDefaultThermostatSchedules.class, "_starsDefaultThermostatSchedules", "stars-DefaultThermostatSchedules", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                return target.getStarsDefaultThermostatSchedules();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnergyCompanySettings target = (StarsEnergyCompanySettings) object;
                    target.setStarsDefaultThermostatSchedules( (StarsDefaultThermostatSchedules) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsDefaultThermostatSchedules();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsDefaultThermostatSchedules
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsEnergyCompanySettingsDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings.class;
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
