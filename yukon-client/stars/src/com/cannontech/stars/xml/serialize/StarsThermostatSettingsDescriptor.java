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
public class StarsThermostatSettingsDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public StarsThermostatSettingsDescriptor() {
        super();
        xmlName = "stars-ThermostatSettings";
        XMLFieldDescriptorImpl  desc           = null;
        XMLFieldHandler         handler        = null;
        FieldValidator          fieldValidator = null;
        
        //-- set grouping compositor
        setCompositorAsSequence();
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _starsThermostatProgram
        desc = new XMLFieldDescriptorImpl(StarsThermostatProgram.class, "_starsThermostatProgram", "stars-ThermostatProgram", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSettings target = (StarsThermostatSettings) object;
                return target.getStarsThermostatProgram();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSettings target = (StarsThermostatSettings) object;
                    target.setStarsThermostatProgram( (StarsThermostatProgram) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsThermostatProgram();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsThermostatProgram
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(1);
        desc.setValidator(fieldValidator);
        
        //-- _starsThermostatManualEventList
        desc = new XMLFieldDescriptorImpl(StarsThermostatManualEvent.class, "_starsThermostatManualEventList", "stars-ThermostatManualEvent", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSettings target = (StarsThermostatSettings) object;
                return target.getStarsThermostatManualEvent();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSettings target = (StarsThermostatSettings) object;
                    target.addStarsThermostatManualEvent( (StarsThermostatManualEvent) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsThermostatManualEvent();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsThermostatManualEventList
        fieldValidator = new FieldValidator();
        fieldValidator.setMinOccurs(0);
        desc.setValidator(fieldValidator);
        
        //-- _starsThermostatDynamicData
        desc = new XMLFieldDescriptorImpl(StarsThermostatDynamicData.class, "_starsThermostatDynamicData", "stars-ThermostatDynamicData", NodeType.Element);
        handler = (new XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                StarsThermostatSettings target = (StarsThermostatSettings) object;
                return target.getStarsThermostatDynamicData();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    StarsThermostatSettings target = (StarsThermostatSettings) object;
                    target.setStarsThermostatDynamicData( (StarsThermostatDynamicData) value);
                }
                catch (Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new StarsThermostatDynamicData();
            }
        } );
        desc.setHandler(handler);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _starsThermostatDynamicData
        fieldValidator = new FieldValidator();
        desc.setValidator(fieldValidator);
        
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSettingsDescriptor()


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
        return com.cannontech.stars.xml.serialize.StarsThermostatSettings.class;
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
