/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class DeviceType.
 * 
 * @version $Revision$ $Date$
 */
public class DeviceType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type
     */
    private com.cannontech.common.device.definition.model.castor.Type _type;

    /**
     * Field _displayName
     */
    private com.cannontech.common.device.definition.model.castor.DisplayName _displayName;

    /**
     * Field _displayGroup
     */
    private com.cannontech.common.device.definition.model.castor.DisplayGroup _displayGroup;

    /**
     * Field _paoClass
     */
    private com.cannontech.common.device.definition.model.castor.PaoClass _paoClass;

    /**
     * Field _paoCategory
     */
    private com.cannontech.common.device.definition.model.castor.PaoCategory _paoCategory;

    /**
     * Field _javaClass
     */
    private com.cannontech.common.device.definition.model.castor.JavaClass _javaClass;

    /**
     * Field _points
     */
    private com.cannontech.common.device.definition.model.castor.Points _points;

    /**
     * Field _commands
     */
    private com.cannontech.common.device.definition.model.castor.Commands _commands;


      //----------------/
     //- Constructors -/
    //----------------/

    public DeviceType() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.DeviceType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'commands'.
     * 
     * @return Commands
     * @return the value of field 'commands'.
     */
    public com.cannontech.common.device.definition.model.castor.Commands getCommands()
    {
        return this._commands;
    } //-- com.cannontech.common.device.definition.model.castor.Commands getCommands() 

    /**
     * Returns the value of field 'displayGroup'.
     * 
     * @return DisplayGroup
     * @return the value of field 'displayGroup'.
     */
    public com.cannontech.common.device.definition.model.castor.DisplayGroup getDisplayGroup()
    {
        return this._displayGroup;
    } //-- com.cannontech.common.device.definition.model.castor.DisplayGroup getDisplayGroup() 

    /**
     * Returns the value of field 'displayName'.
     * 
     * @return DisplayName
     * @return the value of field 'displayName'.
     */
    public com.cannontech.common.device.definition.model.castor.DisplayName getDisplayName()
    {
        return this._displayName;
    } //-- com.cannontech.common.device.definition.model.castor.DisplayName getDisplayName() 

    /**
     * Returns the value of field 'javaClass'.
     * 
     * @return JavaClass
     * @return the value of field 'javaClass'.
     */
    public com.cannontech.common.device.definition.model.castor.JavaClass getJavaClass()
    {
        return this._javaClass;
    } //-- com.cannontech.common.device.definition.model.castor.JavaClass getJavaClass() 

    /**
     * Returns the value of field 'paoCategory'.
     * 
     * @return PaoCategory
     * @return the value of field 'paoCategory'.
     */
    public com.cannontech.common.device.definition.model.castor.PaoCategory getPaoCategory()
    {
        return this._paoCategory;
    } //-- com.cannontech.common.device.definition.model.castor.PaoCategory getPaoCategory() 

    /**
     * Returns the value of field 'paoClass'.
     * 
     * @return PaoClass
     * @return the value of field 'paoClass'.
     */
    public com.cannontech.common.device.definition.model.castor.PaoClass getPaoClass()
    {
        return this._paoClass;
    } //-- com.cannontech.common.device.definition.model.castor.PaoClass getPaoClass() 

    /**
     * Returns the value of field 'points'.
     * 
     * @return Points
     * @return the value of field 'points'.
     */
    public com.cannontech.common.device.definition.model.castor.Points getPoints()
    {
        return this._points;
    } //-- com.cannontech.common.device.definition.model.castor.Points getPoints() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return Type
     * @return the value of field 'type'.
     */
    public com.cannontech.common.device.definition.model.castor.Type getType()
    {
        return this._type;
    } //-- com.cannontech.common.device.definition.model.castor.Type getType() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'commands'.
     * 
     * @param commands the value of field 'commands'.
     */
    public void setCommands(com.cannontech.common.device.definition.model.castor.Commands commands)
    {
        this._commands = commands;
    } //-- void setCommands(com.cannontech.common.device.definition.model.castor.Commands) 

    /**
     * Sets the value of field 'displayGroup'.
     * 
     * @param displayGroup the value of field 'displayGroup'.
     */
    public void setDisplayGroup(com.cannontech.common.device.definition.model.castor.DisplayGroup displayGroup)
    {
        this._displayGroup = displayGroup;
    } //-- void setDisplayGroup(com.cannontech.common.device.definition.model.castor.DisplayGroup) 

    /**
     * Sets the value of field 'displayName'.
     * 
     * @param displayName the value of field 'displayName'.
     */
    public void setDisplayName(com.cannontech.common.device.definition.model.castor.DisplayName displayName)
    {
        this._displayName = displayName;
    } //-- void setDisplayName(com.cannontech.common.device.definition.model.castor.DisplayName) 

    /**
     * Sets the value of field 'javaClass'.
     * 
     * @param javaClass the value of field 'javaClass'.
     */
    public void setJavaClass(com.cannontech.common.device.definition.model.castor.JavaClass javaClass)
    {
        this._javaClass = javaClass;
    } //-- void setJavaClass(com.cannontech.common.device.definition.model.castor.JavaClass) 

    /**
     * Sets the value of field 'paoCategory'.
     * 
     * @param paoCategory the value of field 'paoCategory'.
     */
    public void setPaoCategory(com.cannontech.common.device.definition.model.castor.PaoCategory paoCategory)
    {
        this._paoCategory = paoCategory;
    } //-- void setPaoCategory(com.cannontech.common.device.definition.model.castor.PaoCategory) 

    /**
     * Sets the value of field 'paoClass'.
     * 
     * @param paoClass the value of field 'paoClass'.
     */
    public void setPaoClass(com.cannontech.common.device.definition.model.castor.PaoClass paoClass)
    {
        this._paoClass = paoClass;
    } //-- void setPaoClass(com.cannontech.common.device.definition.model.castor.PaoClass) 

    /**
     * Sets the value of field 'points'.
     * 
     * @param points the value of field 'points'.
     */
    public void setPoints(com.cannontech.common.device.definition.model.castor.Points points)
    {
        this._points = points;
    } //-- void setPoints(com.cannontech.common.device.definition.model.castor.Points) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(com.cannontech.common.device.definition.model.castor.Type type)
    {
        this._type = type;
    } //-- void setType(com.cannontech.common.device.definition.model.castor.Type) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.common.device.definition.model.castor.DeviceType) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.DeviceType.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
