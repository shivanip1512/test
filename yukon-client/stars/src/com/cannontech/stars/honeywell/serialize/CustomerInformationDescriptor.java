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
 * @version $Revision$ $Date$
**/
public class CustomerInformationDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public CustomerInformationDescriptor() {
        super();
        nsURI = "http://www.tempuri.org/DataSetALL.xsd";
        xmlName = "CustomerInformation";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _id
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_id", "id", NodeType.Attribute);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getId();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setId( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        addFieldDescriptor(desc);
        
        //-- validation code for: _id
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _rowData
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_rowData", "rowData", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                if(!target.hasRowData())
                    return null;
                return new Integer(target.getRowData());
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteRowData();
                        return;
                    }
                    target.setRowData( ((Integer)value).intValue());
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        addFieldDescriptor(desc);
        
        //-- validation code for: _rowData
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _ACCOUNTID
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_ACCOUNTID", "ACCOUNTID", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getACCOUNTID();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setACCOUNTID( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ACCOUNTID
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _CUSTOMERNUMBER
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_CUSTOMERNUMBER", "CUSTOMERNUMBER", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getCUSTOMERNUMBER();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setCUSTOMERNUMBER( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _CUSTOMERNUMBER
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _ACCOUNTNUMBER
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_ACCOUNTNUMBER", "ACCOUNTNUMBER", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getACCOUNTNUMBER();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setACCOUNTNUMBER( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ACCOUNTNUMBER
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _ACCOUNTNOTES
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_ACCOUNTNOTES", "ACCOUNTNOTES", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getACCOUNTNOTES();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setACCOUNTNOTES( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ACCOUNTNOTES
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _PROPERTYNUMBER
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_PROPERTYNUMBER", "PROPERTYNUMBER", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getPROPERTYNUMBER();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setPROPERTYNUMBER( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _PROPERTYNUMBER
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _PROPERTYNOTES
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_PROPERTYNOTES", "PROPERTYNOTES", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getPROPERTYNOTES();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setPROPERTYNOTES( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _PROPERTYNOTES
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _STREETADDRESS1
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_STREETADDRESS1", "STREETADDRESS1", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSTREETADDRESS1();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSTREETADDRESS1( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STREETADDRESS1
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _STREETADDRESS2
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_STREETADDRESS2", "STREETADDRESS2", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSTREETADDRESS2();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSTREETADDRESS2( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STREETADDRESS2
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _STREETADDRESSCITY
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_STREETADDRESSCITY", "STREETADDRESSCITY", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSTREETADDRESSCITY();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSTREETADDRESSCITY( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STREETADDRESSCITY
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _STREETADDRESSSTATECODE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_STREETADDRESSSTATECODE", "STREETADDRESSSTATECODE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSTREETADDRESSSTATECODE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSTREETADDRESSSTATECODE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STREETADDRESSSTATECODE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _STREETADDRESSZIPCODE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_STREETADDRESSZIPCODE", "STREETADDRESSZIPCODE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSTREETADDRESSZIPCODE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSTREETADDRESSZIPCODE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STREETADDRESSZIPCODE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _SUBSTATIONNAME
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_SUBSTATIONNAME", "SUBSTATIONNAME", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSUBSTATIONNAME();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSUBSTATIONNAME( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _SUBSTATIONNAME
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _FEEDER
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_FEEDER", "FEEDER", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getFEEDER();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setFEEDER( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _FEEDER
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _POLE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_POLE", "POLE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getPOLE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setPOLE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _POLE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _TRANSFORMERSIZE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_TRANSFORMERSIZE", "TRANSFORMERSIZE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getTRANSFORMERSIZE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setTRANSFORMERSIZE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _TRANSFORMERSIZE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _SERVICEVOLTAGE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_SERVICEVOLTAGE", "SERVICEVOLTAGE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getSERVICEVOLTAGE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setSERVICEVOLTAGE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _SERVICEVOLTAGE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _BILLINGADDRESS1
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_BILLINGADDRESS1", "BILLINGADDRESS1", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getBILLINGADDRESS1();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setBILLINGADDRESS1( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILLINGADDRESS1
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _BILLINGADDRESS2
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_BILLINGADDRESS2", "BILLINGADDRESS2", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getBILLINGADDRESS2();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setBILLINGADDRESS2( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILLINGADDRESS2
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _BILLINGADDRESSCITY
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_BILLINGADDRESSCITY", "BILLINGADDRESSCITY", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getBILLINGADDRESSCITY();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setBILLINGADDRESSCITY( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILLINGADDRESSCITY
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _BILLINGADDRESSSTATECODE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_BILLINGADDRESSSTATECODE", "BILLINGADDRESSSTATECODE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getBILLINGADDRESSSTATECODE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setBILLINGADDRESSSTATECODE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILLINGADDRESSSTATECODE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _BILLINGADDRESSZIPCODE
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_BILLINGADDRESSZIPCODE", "BILLINGADDRESSZIPCODE", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getBILLINGADDRESSZIPCODE();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setBILLINGADDRESSZIPCODE( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILLINGADDRESSZIPCODE
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _ACCOUNT_ID
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_ACCOUNT_ID", "ACCOUNT_ID", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getACCOUNT_ID();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setACCOUNT_ID( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ACCOUNT_ID
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _ADDRESS_ID
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_ADDRESS_ID", "ADDRESS_ID", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public Object getValue( Object object ) 
                throws IllegalStateException
            {
                CustomerInformation target = (CustomerInformation) object;
                return target.getADDRESS_ID();
            }
            public void setValue( Object object, Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CustomerInformation target = (CustomerInformation) object;
                    target.setADDRESS_ID( (java.lang.String) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public Object newInstance( Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.tempuri.org/DataSetALL.xsd");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ADDRESS_ID
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.honeywell.serialize.CustomerInformationDescriptor()


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
        return com.cannontech.stars.honeywell.serialize.CustomerInformation.class;
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
