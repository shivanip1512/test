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
public class StarsThermostatDynamicDataDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsThermostatDynamicDataDescriptor() {
        super();
        xmlName = "stars-ThermostatDynamicData";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _lastUpdateTime
        desc = new XMLFieldDescriptorImpl(java.util.Date.class, "_lastUpdateTime", "LastUpdateTime", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                return target.getLastUpdateTime();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    target.setLastUpdateTime( (java.util.Date) value);
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
        
        //-- validation code for: _lastUpdateTime
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _displayedTemperature
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_displayedTemperature", "DisplayedTemperature", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasDisplayedTemperature())
                    return null;
                return new Integer(target.getDisplayedTemperature());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteDisplayedTemperature();
                        return;
                    }
                    target.setDisplayedTemperature( ((Integer)value).intValue());
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
        
        //-- validation code for: _displayedTemperature
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _displayedTempUnit
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_displayedTempUnit", "DisplayedTempUnit", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                return target.getDisplayedTempUnit();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    target.setDisplayedTempUnit( (java.lang.String) value);
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
        
        //-- validation code for: _displayedTempUnit
        fieldValidator = new FieldValidator();
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _fan
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings.class, "_fan", "Fan", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                return target.getFan();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    target.setFan( (com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler( new EnumFieldHandler(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings.class, handler));
        desc.setImmutable(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _fan
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _mode
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings.class, "_mode", "Mode", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                return target.getMode();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _mode
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
        //-- _coolSetpoint
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_coolSetpoint", "CoolSetpoint", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasCoolSetpoint())
                    return null;
                return new Integer(target.getCoolSetpoint());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteCoolSetpoint();
                        return;
                    }
                    target.setCoolSetpoint( ((Integer)value).intValue());
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
        
        //-- validation code for: _coolSetpoint
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _heatSetpoint
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_heatSetpoint", "HeatSetpoint", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasHeatSetpoint())
                    return null;
                return new Integer(target.getHeatSetpoint());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteHeatSetpoint();
                        return;
                    }
                    target.setHeatSetpoint( ((Integer)value).intValue());
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
        
        //-- validation code for: _heatSetpoint
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _setpointHold
        desc = new XMLFieldDescriptorImpl(java.lang.Boolean.TYPE, "_setpointHold", "SetpointHold", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasSetpointHold())
                    return null;
                return new Boolean(target.getSetpointHold());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteSetpointHold();
                        return;
                    }
                    target.setSetpointHold( ((Boolean)value).booleanValue());
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
        
        //-- validation code for: _setpointHold
        fieldValidator = new FieldValidator();
        { //-- local scope
            BooleanValidator bv = new BooleanValidator();
            fieldValidator.setValidator(bv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _lowerCoolSetpointLimit
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_lowerCoolSetpointLimit", "LowerCoolSetpointLimit", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasLowerCoolSetpointLimit())
                    return null;
                return new Integer(target.getLowerCoolSetpointLimit());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteLowerCoolSetpointLimit();
                        return;
                    }
                    target.setLowerCoolSetpointLimit( ((Integer)value).intValue());
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
        
        //-- validation code for: _lowerCoolSetpointLimit
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _upperHeatSetpointLimit
        desc = new XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_upperHeatSetpointLimit", "UpperHeatSetpointLimit", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                if(!target.hasUpperHeatSetpointLimit())
                    return null;
                return new Integer(target.getUpperHeatSetpointLimit());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    // if null, use delete method for optional primitives 
                    if (value == null) {
                        target.deleteUpperHeatSetpointLimit();
                        return;
                    }
                    target.setUpperHeatSetpointLimit( ((Integer)value).intValue());
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
        
        //-- validation code for: _upperHeatSetpointLimit
        fieldValidator = new FieldValidator();
        { //-- local scope
            IntegerValidator iv = new IntegerValidator();
            fieldValidator.setValidator(iv);
        }
        desc.setValidator(fieldValidator);
        
        //-- _infoStringList
        desc = new XMLFieldDescriptorImpl(java.lang.String.class, "_infoStringList", "InfoString", NodeType.Element);
        desc.setImmutable(true);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                return target.getInfoString();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatDynamicData target = (StarsThermostatDynamicData) object;
                    target.addInfoString( (java.lang.String) value);
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
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _infoStringList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
            StringValidator sv = new StringValidator();
            sv.setWhiteSpace("preserve");
            fieldValidator.setValidator(sv);
        }
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatDynamicDataDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsThermostatDynamicData.class;
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
