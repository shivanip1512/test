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
public class StarsThermoScheduleDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsThermoScheduleDescriptor() {
        super();
        xmlName = "stars-ThermoSchedule";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _day
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings.class, "_day", "day", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getDay();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setDay( (com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler( new EnumFieldHandler(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings.class, handler));
        desc.setImmutable(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _day
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _mode
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings.class, "_mode", "mode", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getMode();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setMode( (com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler( new EnumFieldHandler(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings.class, handler));
        desc.setImmutable(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _mode
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _time1
        desc = new XMLFieldDescriptorImpl(org.exolab.castor.types.Time.class, "_time1", "Time1", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getTime1();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setTime1( (org.exolab.castor.types.Time) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.exolab.castor.types.Time();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _time1
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            DateTimeValidator dv = new DateTimeValidator();
            fieldValidator.setValidator(dv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _temperature1
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_temperature1", "Temperature1", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                if(!target.hasTemperature1())
                    return null;
                return new Integer(target.getTemperature1());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setTemperature1( ((Integer)value).intValue());
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
        
        //-- validation code for: _temperature1
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _time2
        desc = new XMLFieldDescriptorImpl(org.exolab.castor.types.Time.class, "_time2", "Time2", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getTime2();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setTime2( (org.exolab.castor.types.Time) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.exolab.castor.types.Time();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _time2
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            DateTimeValidator dv = new DateTimeValidator();
            fieldValidator.setValidator(dv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _temperature2
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_temperature2", "Temperature2", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                if(!target.hasTemperature2())
                    return null;
                return new Integer(target.getTemperature2());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setTemperature2( ((Integer)value).intValue());
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
        
        //-- validation code for: _temperature2
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _time3
        desc = new XMLFieldDescriptorImpl(org.exolab.castor.types.Time.class, "_time3", "Time3", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getTime3();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setTime3( (org.exolab.castor.types.Time) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.exolab.castor.types.Time();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _time3
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            DateTimeValidator dv = new DateTimeValidator();
            fieldValidator.setValidator(dv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _temperature3
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_temperature3", "Temperature3", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                if(!target.hasTemperature3())
                    return null;
                return new Integer(target.getTemperature3());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setTemperature3( ((Integer)value).intValue());
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
        
        //-- validation code for: _temperature3
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _time4
        desc = new XMLFieldDescriptorImpl(org.exolab.castor.types.Time.class, "_time4", "Time4", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                return target.getTime4();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    target.setTime4( (org.exolab.castor.types.Time) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.exolab.castor.types.Time();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _time4
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            DateTimeValidator dv = new DateTimeValidator();
            fieldValidator.setValidator(dv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _temperature4
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_temperature4", "Temperature4", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermoSchedule target = (StarsThermoSchedule) object;
                if(!target.hasTemperature4())
                    return null;
                return new Integer(target.getTemperature4());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermoSchedule target = (StarsThermoSchedule) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setTemperature4( ((Integer)value).intValue());
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
        
        //-- validation code for: _temperature4
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsThermoScheduleDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsThermoSchedule.class;
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
