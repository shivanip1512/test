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
public class StarsThermostatSeasonDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsThermostatSeasonDescriptor() {
        super();
        xmlName = "stars-ThermostatSeason";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- _mode
        desc = new XMLFieldDescriptorImpl(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings.class, "_mode", "mode", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSeason target = (StarsThermostatSeason) object;
                return target.getMode();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSeason target = (StarsThermostatSeason) object;
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
        
        //-- _startDate
        desc = new XMLFieldDescriptorImpl(org.exolab.castor.types.Date.class, "_startDate", "startDate", NodeType.Attribute);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSeason target = (StarsThermostatSeason) object;
                return target.getStartDate();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSeason target = (StarsThermostatSeason) object;
                    target.setStartDate( (org.exolab.castor.types.Date.parseDate((String) value)));
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.exolab.castor.types.Date();
            }
        } );
        desc.setHandler(handler);
        addFieldDescriptor(desc);
        
        //-- validation code for: _startDate
        fieldValidator = new FieldValidator();
        { //-- local scope
            DateTimeValidator dv = new DateTimeValidator();
            fieldValidator.setValidator(dv);
        }
        desc.setValidator(fieldValidator);
        
        //-- initialize element descriptors
        
        //-- _starsThermostatScheduleList
        desc = new XMLFieldDescriptorImpl(StarsThermostatSchedule.class, "_starsThermostatScheduleList", "stars-ThermostatSchedule", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSeason target = (StarsThermostatSeason) object;
                return target.getStarsThermostatSchedule();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSeason target = (StarsThermostatSeason) object;
                    target.addStarsThermostatSchedule( (StarsThermostatSchedule) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsThermostatSchedule();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsThermostatScheduleList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        fieldValidator.setMaxOccurs(7);
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSeasonDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsThermostatSeason.class;
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
