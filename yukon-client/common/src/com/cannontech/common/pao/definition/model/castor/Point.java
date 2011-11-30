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
 * Class Point.
 * 
 * @version $Revision$ $Date$
 */
public class Point implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type
     */
    private java.lang.String _type;

    /**
     * Field _offset
     */
    private java.lang.Integer _offset;

    /**
     * Field _enabled
     */
    private java.lang.Boolean _enabled = new java.lang.Boolean("true");

    /**
     * Field _init
     */
    private java.lang.Boolean _init = new java.lang.Boolean("false");

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _controltype
     */
    private java.lang.String _controltype = "NONE";

    /**
     * Field _archive
     */
    private com.cannontech.common.pao.definition.model.castor.Archive _archive;

    /**
     * Field _pointChoice
     */
    private com.cannontech.common.pao.definition.model.castor.PointChoice _pointChoice;

    /**
     * Field _calculation
     */
    private com.cannontech.common.pao.definition.model.castor.Calculation _calculation;


      //----------------/
     //- Constructors -/
    //----------------/

    public Point() 
     {
        super();
        setControltype("NONE");
    } //-- com.cannontech.common.pao.definition.model.castor.Point()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'archive'.
     * 
     * @return Archive
     * @return the value of field 'archive'.
     */
    public com.cannontech.common.pao.definition.model.castor.Archive getArchive()
    {
        return this._archive;
    } //-- com.cannontech.common.pao.definition.model.castor.Archive getArchive() 

    /**
     * Returns the value of field 'calculation'.
     * 
     * @return Calculation
     * @return the value of field 'calculation'.
     */
    public com.cannontech.common.pao.definition.model.castor.Calculation getCalculation()
    {
        return this._calculation;
    } //-- com.cannontech.common.pao.definition.model.castor.Calculation getCalculation() 

    /**
     * Returns the value of field 'controltype'.
     * 
     * @return String
     * @return the value of field 'controltype'.
     */
    public java.lang.String getControltype()
    {
        return this._controltype;
    } //-- java.lang.String getControltype() 

    /**
     * Returns the value of field 'description'.
     * 
     * @return String
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

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
     * Returns the value of field 'init'.
     * 
     * @return Boolean
     * @return the value of field 'init'.
     */
    public java.lang.Boolean getInit()
    {
        return this._init;
    } //-- java.lang.Boolean getInit() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'offset'.
     * 
     * @return Integer
     * @return the value of field 'offset'.
     */
    public java.lang.Integer getOffset()
    {
        return this._offset;
    } //-- java.lang.Integer getOffset() 

    /**
     * Returns the value of field 'pointChoice'.
     * 
     * @return PointChoice
     * @return the value of field 'pointChoice'.
     */
    public com.cannontech.common.pao.definition.model.castor.PointChoice getPointChoice()
    {
        return this._pointChoice;
    } //-- com.cannontech.common.pao.definition.model.castor.PointChoice getPointChoice() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return String
     * @return the value of field 'type'.
     */
    public java.lang.String getType()
    {
        return this._type;
    } //-- java.lang.String getType() 

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
     * Sets the value of field 'archive'.
     * 
     * @param archive the value of field 'archive'.
     */
    public void setArchive(com.cannontech.common.pao.definition.model.castor.Archive archive)
    {
        this._archive = archive;
    } //-- void setArchive(com.cannontech.common.pao.definition.model.castor.Archive) 

    /**
     * Sets the value of field 'calculation'.
     * 
     * @param calculation the value of field 'calculation'.
     */
    public void setCalculation(com.cannontech.common.pao.definition.model.castor.Calculation calculation)
    {
        this._calculation = calculation;
    } //-- void setCalculation(com.cannontech.common.pao.definition.model.castor.Calculation) 

    /**
     * Sets the value of field 'controltype'.
     * 
     * @param controltype the value of field 'controltype'.
     */
    public void setControltype(java.lang.String controltype)
    {
        this._controltype = controltype;
    } //-- void setControltype(java.lang.String) 

    /**
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

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
     * Sets the value of field 'init'.
     * 
     * @param init the value of field 'init'.
     */
    public void setInit(java.lang.Boolean init)
    {
        this._init = init;
    } //-- void setInit(java.lang.Boolean) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'offset'.
     * 
     * @param offset the value of field 'offset'.
     */
    public void setOffset(java.lang.Integer offset)
    {
        this._offset = offset;
    } //-- void setOffset(java.lang.Integer) 

    /**
     * Sets the value of field 'pointChoice'.
     * 
     * @param pointChoice the value of field 'pointChoice'.
     */
    public void setPointChoice(com.cannontech.common.pao.definition.model.castor.PointChoice pointChoice)
    {
        this._pointChoice = pointChoice;
    } //-- void setPointChoice(com.cannontech.common.pao.definition.model.castor.PointChoice) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(java.lang.String type)
    {
        this._type = type;
    } //-- void setType(java.lang.String) 

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
        return (com.cannontech.common.pao.definition.model.castor.Point) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.Point.class, reader);
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
