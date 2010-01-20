/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.pao.definition.model.castor;

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
 * Class PaoType.
 * 
 * @version $Revision$ $Date$
 */
public class PaoType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _enabled
     */
    private java.lang.Boolean _enabled = new java.lang.Boolean("true");

    /**
     * Field _abstract
     */
    private java.lang.Boolean _abstract = new java.lang.Boolean("false");

    /**
     * Field _inherits
     */
    private java.lang.String _inherits;

    /**
     * Field _displayName
     */
    private java.lang.String _displayName;

    /**
     * Field _displayGroup
     */
    private java.lang.String _displayGroup;

    /**
     * Field _changeGroup
     */
    private java.lang.String _changeGroup;

    /**
     * Field _points
     */
    private com.cannontech.common.pao.definition.model.castor.Points _points;

    /**
     * Field _commands
     */
    private com.cannontech.common.pao.definition.model.castor.Commands _commands;

    /**
     * Field _attributes
     */
    private com.cannontech.common.pao.definition.model.castor.Attributes _attributes;

    /**
     * Field _tags
     */
    private com.cannontech.common.pao.definition.model.castor.Tags _tags;


      //----------------/
     //- Constructors -/
    //----------------/

    public PaoType() 
     {
        super();
    } //-- com.cannontech.common.pao.definition.model.castor.PaoType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'abstract'.
     * 
     * @return Boolean
     * @return the value of field 'abstract'.
     */
    public java.lang.Boolean getAbstract()
    {
        return this._abstract;
    } //-- java.lang.Boolean getAbstract() 

    /**
     * Returns the value of field 'attributes'.
     * 
     * @return Attributes
     * @return the value of field 'attributes'.
     */
    public com.cannontech.common.pao.definition.model.castor.Attributes getAttributes()
    {
        return this._attributes;
    } //-- com.cannontech.common.pao.definition.model.castor.Attributes getAttributes() 

    /**
     * Returns the value of field 'changeGroup'.
     * 
     * @return String
     * @return the value of field 'changeGroup'.
     */
    public java.lang.String getChangeGroup()
    {
        return this._changeGroup;
    } //-- java.lang.String getChangeGroup() 

    /**
     * Returns the value of field 'commands'.
     * 
     * @return Commands
     * @return the value of field 'commands'.
     */
    public com.cannontech.common.pao.definition.model.castor.Commands getCommands()
    {
        return this._commands;
    } //-- com.cannontech.common.pao.definition.model.castor.Commands getCommands() 

    /**
     * Returns the value of field 'displayGroup'.
     * 
     * @return String
     * @return the value of field 'displayGroup'.
     */
    public java.lang.String getDisplayGroup()
    {
        return this._displayGroup;
    } //-- java.lang.String getDisplayGroup() 

    /**
     * Returns the value of field 'displayName'.
     * 
     * @return String
     * @return the value of field 'displayName'.
     */
    public java.lang.String getDisplayName()
    {
        return this._displayName;
    } //-- java.lang.String getDisplayName() 

    /**
     * Returns the value of field 'enabled'.
     * 
     * @return Boolean
     * @return the value of field 'enabled'.
     */
    public java.lang.Boolean getEnabled()
    {
        return this._enabled;
    } //-- java.lang.Boolean getEnabled() 

    /**
     * Returns the value of field 'id'.
     * 
     * @return String
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Returns the value of field 'inherits'.
     * 
     * @return String
     * @return the value of field 'inherits'.
     */
    public java.lang.String getInherits()
    {
        return this._inherits;
    } //-- java.lang.String getInherits() 

    /**
     * Returns the value of field 'points'.
     * 
     * @return Points
     * @return the value of field 'points'.
     */
    public com.cannontech.common.pao.definition.model.castor.Points getPoints()
    {
        return this._points;
    } //-- com.cannontech.common.pao.definition.model.castor.Points getPoints() 

    /**
     * Returns the value of field 'tags'.
     * 
     * @return Tags
     * @return the value of field 'tags'.
     */
    public com.cannontech.common.pao.definition.model.castor.Tags getTags()
    {
        return this._tags;
    } //-- com.cannontech.common.pao.definition.model.castor.Tags getTags() 

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
     * Sets the value of field 'abstract'.
     * 
     * @param _abstract
     * @param abstract the value of field 'abstract'.
     */
    public void setAbstract(java.lang.Boolean _abstract)
    {
        this._abstract = _abstract;
    } //-- void setAbstract(java.lang.Boolean) 

    /**
     * Sets the value of field 'attributes'.
     * 
     * @param attributes the value of field 'attributes'.
     */
    public void setAttributes(com.cannontech.common.pao.definition.model.castor.Attributes attributes)
    {
        this._attributes = attributes;
    } //-- void setAttributes(com.cannontech.common.pao.definition.model.castor.Attributes) 

    /**
     * Sets the value of field 'changeGroup'.
     * 
     * @param changeGroup the value of field 'changeGroup'.
     */
    public void setChangeGroup(java.lang.String changeGroup)
    {
        this._changeGroup = changeGroup;
    } //-- void setChangeGroup(java.lang.String) 

    /**
     * Sets the value of field 'commands'.
     * 
     * @param commands the value of field 'commands'.
     */
    public void setCommands(com.cannontech.common.pao.definition.model.castor.Commands commands)
    {
        this._commands = commands;
    } //-- void setCommands(com.cannontech.common.pao.definition.model.castor.Commands) 

    /**
     * Sets the value of field 'displayGroup'.
     * 
     * @param displayGroup the value of field 'displayGroup'.
     */
    public void setDisplayGroup(java.lang.String displayGroup)
    {
        this._displayGroup = displayGroup;
    } //-- void setDisplayGroup(java.lang.String) 

    /**
     * Sets the value of field 'displayName'.
     * 
     * @param displayName the value of field 'displayName'.
     */
    public void setDisplayName(java.lang.String displayName)
    {
        this._displayName = displayName;
    } //-- void setDisplayName(java.lang.String) 

    /**
     * Sets the value of field 'enabled'.
     * 
     * @param enabled the value of field 'enabled'.
     */
    public void setEnabled(java.lang.Boolean enabled)
    {
        this._enabled = enabled;
    } //-- void setEnabled(java.lang.Boolean) 

    /**
     * Sets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Sets the value of field 'inherits'.
     * 
     * @param inherits the value of field 'inherits'.
     */
    public void setInherits(java.lang.String inherits)
    {
        this._inherits = inherits;
    } //-- void setInherits(java.lang.String) 

    /**
     * Sets the value of field 'points'.
     * 
     * @param points the value of field 'points'.
     */
    public void setPoints(com.cannontech.common.pao.definition.model.castor.Points points)
    {
        this._points = points;
    } //-- void setPoints(com.cannontech.common.pao.definition.model.castor.Points) 

    /**
     * Sets the value of field 'tags'.
     * 
     * @param tags the value of field 'tags'.
     */
    public void setTags(com.cannontech.common.pao.definition.model.castor.Tags tags)
    {
        this._tags = tags;
    } //-- void setTags(com.cannontech.common.pao.definition.model.castor.Tags) 

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
        return (com.cannontech.common.pao.definition.model.castor.PaoType) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.PaoType.class, reader);
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
