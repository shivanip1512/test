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

import org.exolab.castor.xml.FieldValidator;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.XMLFieldHandler;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class DataSetALLItemDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public DataSetALLItemDescriptor() {
        super();
        nsURI = "http://www.tempuri.org/DataSetALL.xsd";
        xmlName = "DataSetALL";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsChoice();
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _customerContact
        desc = new XMLFieldDescriptorImpl(CustomerContact.class, "_customerContact", "CustomerContact", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                DataSetALLItem target = (DataSetALLItem) object;
                return target.getCustomerContact();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    DataSetALLItem target = (DataSetALLItem) object;
                    target.setCustomerContact( (CustomerContact) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new CustomerContact();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _customerContact
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _customerInformation
        desc = new XMLFieldDescriptorImpl(CustomerInformation.class, "_customerInformation", "CustomerInformation", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                DataSetALLItem target = (DataSetALLItem) object;
                return target.getCustomerInformation();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    DataSetALLItem target = (DataSetALLItem) object;
                    target.setCustomerInformation( (CustomerInformation) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new CustomerInformation();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _customerInformation
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _LMAppliance
        desc = new XMLFieldDescriptorImpl(LMAppliance.class, "_LMAppliance", "LMAppliance", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                DataSetALLItem target = (DataSetALLItem) object;
                return target.getLMAppliance();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    DataSetALLItem target = (DataSetALLItem) object;
                    target.setLMAppliance( (LMAppliance) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new LMAppliance();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _LMAppliance
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _LMHardware
        desc = new XMLFieldDescriptorImpl(LMHardware.class, "_LMHardware", "LMHardware", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                DataSetALLItem target = (DataSetALLItem) object;
                return target.getLMHardware();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    DataSetALLItem target = (DataSetALLItem) object;
                    target.setLMHardware( (LMHardware) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new LMHardware();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _LMHardware
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.honeywell.serialize.DataSetALLItemDescriptor()


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
        return com.cannontech.stars.honeywell.serialize.DataSetALLItem.class;
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
