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
public class StarsServiceRequestDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsServiceRequestDescriptor() {
        super();
        xmlName = "stars-ServiceRequest";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _orderNumber
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_orderNumber", "orderNumber", NodeType.Attribute);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getOrderNumber();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setOrderNumber( (java.lang.String) value);
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
        
        //-- validation code for: _orderNumber
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _serviceType
        desc = new XMLFieldDescriptorImpl(ServiceType.class, "_serviceType", "ServiceType", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getServiceType();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setServiceType( (ServiceType) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new ServiceType();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _serviceType
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _currentState
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_currentState", "CurrentState", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getCurrentState();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setCurrentState( (java.lang.String) value);
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _currentState
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _serviceCompany
        desc = new XMLFieldDescriptorImpl(ServiceCompany.class, "_serviceCompany", "ServiceCompany", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getServiceCompany();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setServiceCompany( (ServiceCompany) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new ServiceCompany();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _serviceCompany
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _dateReported
        desc = new XMLFieldDescriptorImpl(java.util.Date.class, "_dateReported", "DateReported", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getDateReported();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setDateReported( (java.util.Date) value);
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
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _dateReported
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _description
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_description", "Description", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getDescription();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setDescription( (java.lang.String) value);
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
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _description
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _dateScheduled
        desc = new XMLFieldDescriptorImpl(java.util.Date.class, "_dateScheduled", "DateScheduled", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getDateScheduled();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setDateScheduled( (java.util.Date) value);
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _dateScheduled
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _dateCompleted
        desc = new XMLFieldDescriptorImpl(java.util.Date.class, "_dateCompleted", "DateCompleted", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getDateCompleted();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setDateCompleted( (java.util.Date) value);
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _dateCompleted
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _actionTaken
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_actionTaken", "ActionTaken", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsServiceRequest target = (StarsServiceRequest) object;
                return target.getActionTaken();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsServiceRequest target = (StarsServiceRequest) object;
                    target.setActionTaken( (java.lang.String) value);
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _actionTaken
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsServiceRequestDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsServiceRequest.class;
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
