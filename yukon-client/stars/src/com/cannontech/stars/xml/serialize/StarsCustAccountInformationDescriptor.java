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
public class StarsCustAccountInformationDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsCustAccountInformationDescriptor() {
        super();
        xmlName = "stars-CustAccountInformation";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _lastActiveTime
        desc = new XMLFieldDescriptorImpl(java.util.Date.class, "_lastActiveTime", "lastActiveTime", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getLastActiveTime();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setLastActiveTime( (java.util.Date) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new java.util.Date();
            }
        } );
        desc.setHandler( new DateFieldHandler(handler));
        desc.setImmutable(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _lastActiveTime
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _starsCustomerAccount
        desc = new XMLFieldDescriptorImpl(StarsCustomerAccount.class, "_starsCustomerAccount", "stars-CustomerAccount", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsCustomerAccount();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsCustomerAccount( (StarsCustomerAccount) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCustomerAccount();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCustomerAccount
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _starsResidenceInformation
        desc = new XMLFieldDescriptorImpl(StarsResidenceInformation.class, "_starsResidenceInformation", "stars-ResidenceInformation", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsResidenceInformation();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsResidenceInformation( (StarsResidenceInformation) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsResidenceInformation();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsResidenceInformation
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsLMPrograms
        desc = new XMLFieldDescriptorImpl(StarsLMPrograms.class, "_starsLMPrograms", "stars-LMPrograms", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsLMPrograms();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsLMPrograms( (StarsLMPrograms) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsLMPrograms();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsLMPrograms
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _starsAppliances
        desc = new XMLFieldDescriptorImpl(StarsAppliances.class, "_starsAppliances", "stars-Appliances", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsAppliances();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsAppliances( (StarsAppliances) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsAppliances();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsAppliances
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsInventories
        desc = new XMLFieldDescriptorImpl(StarsInventories.class, "_starsInventories", "stars-Inventories", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsInventories();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsInventories( (StarsInventories) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsInventories();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsInventories
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsCallReportHistory
        desc = new XMLFieldDescriptorImpl(StarsCallReportHistory.class, "_starsCallReportHistory", "stars-CallReportHistory", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsCallReportHistory();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsCallReportHistory( (StarsCallReportHistory) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsCallReportHistory();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsCallReportHistory
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsServiceRequestHistory
        desc = new XMLFieldDescriptorImpl(StarsServiceRequestHistory.class, "_starsServiceRequestHistory", "stars-ServiceRequestHistory", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsServiceRequestHistory();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsServiceRequestHistory( (StarsServiceRequestHistory) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsServiceRequestHistory();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsServiceRequestHistory
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsUser
        desc = new XMLFieldDescriptorImpl(StarsUser.class, "_starsUser", "stars-User", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsUser();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsUser( (StarsUser) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsUser();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsUser
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _starsSavedThermostatSchedules
        desc = new XMLFieldDescriptorImpl(StarsSavedThermostatSchedules.class, "_starsSavedThermostatSchedules", "stars-SavedThermostatSchedules", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                return target.getStarsSavedThermostatSchedules();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsCustAccountInformation target = (StarsCustAccountInformation) object;
                    target.setStarsSavedThermostatSchedules( (StarsSavedThermostatSchedules) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsSavedThermostatSchedules();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsSavedThermostatSchedules
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountInformationDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsCustAccountInformation.class;
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
