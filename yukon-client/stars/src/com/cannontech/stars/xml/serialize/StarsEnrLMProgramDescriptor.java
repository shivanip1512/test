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
public class StarsEnrLMProgramDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsEnrLMProgramDescriptor() {
        super();
        xmlName = "stars-EnrLMProgram";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _programID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_programID", "programID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                if(!target.hasProgramID())
                    return null;
                return new Integer(target.getProgramID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteProgramID();
                        return;
                    }
                    target.setProgramID( ((Integer)value).intValue());
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
        
        //-- validation code for: _programID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _deviceID
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_deviceID", "deviceID", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                if(!target.hasDeviceID())
                    return null;
                return new Integer(target.getDeviceID());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteDeviceID();
                        return;
                    }
                    target.setDeviceID( ((Integer)value).intValue());
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
        
        //-- validation code for: _deviceID
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _yukonName
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_yukonName", "YukonName", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                return target.getYukonName();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                    target.setYukonName( (java.lang.String) value);
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
        
        //-- validation code for: _yukonName
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _starsWebConfig
        desc = new XMLFieldDescriptorImpl(StarsWebConfig.class, "_starsWebConfig", "stars-WebConfig", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                return target.getStarsWebConfig();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
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
        
        //-- _addressingGroupList
        desc = new XMLFieldDescriptorImpl(AddressingGroup.class, "_addressingGroupList", "AddressingGroup", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                return target.getAddressingGroup();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                    target.addAddressingGroup( (AddressingGroup) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new AddressingGroup();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _addressingGroupList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        desc.setValidator(fieldValidator);
        
        //-- _chanceOfControl
        desc = new XMLFieldDescriptorImpl(ChanceOfControl.class, "_chanceOfControl", "ChanceOfControl", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                return target.getChanceOfControl();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsEnrLMProgram target = (StarsEnrLMProgram) object;
                    target.setChanceOfControl( (ChanceOfControl) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new ChanceOfControl();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _chanceOfControl
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsEnrLMProgramDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsEnrLMProgram.class;
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
