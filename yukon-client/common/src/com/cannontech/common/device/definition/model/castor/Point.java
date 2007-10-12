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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

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
     * Field _init
     */
    private boolean _init = false;

    /**
     * keeps track of state for field: _init
     */
    private boolean _has_init;

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _offset
     */
    private com.cannontech.common.device.definition.model.castor.Offset _offset;

    /**
     * Field _pointChoice
     */
    private com.cannontech.common.device.definition.model.castor.PointChoice _pointChoice;


      //----------------/
     //- Constructors -/
    //----------------/

    public Point() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.Point()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteInit
     * 
     */
    public void deleteInit()
    {
        this._has_init= false;
    } //-- void deleteInit() 

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
     * Returns the value of field 'init'.
     * 
     * @return boolean
     * @return the value of field 'init'.
     */
    public boolean getInit()
    {
        return this._init;
    } //-- boolean getInit() 

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
     * @return Offset
     * @return the value of field 'offset'.
     */
    public com.cannontech.common.device.definition.model.castor.Offset getOffset()
    {
        return this._offset;
    } //-- com.cannontech.common.device.definition.model.castor.Offset getOffset() 

    /**
     * Returns the value of field 'pointChoice'.
     * 
     * @return PointChoice
     * @return the value of field 'pointChoice'.
     */
    public com.cannontech.common.device.definition.model.castor.PointChoice getPointChoice()
    {
        return this._pointChoice;
    } //-- com.cannontech.common.device.definition.model.castor.PointChoice getPointChoice() 

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
     * Method hasInit
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasInit()
    {
        return this._has_init;
    } //-- boolean hasInit() 

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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Sets the value of field 'init'.
     * 
     * @param init the value of field 'init'.
     */
    public void setInit(boolean init)
    {
        this._init = init;
        this._has_init = true;
    } //-- void setInit(boolean) 

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
    public void setOffset(com.cannontech.common.device.definition.model.castor.Offset offset)
    {
        this._offset = offset;
    } //-- void setOffset(com.cannontech.common.device.definition.model.castor.Offset) 

    /**
     * Sets the value of field 'pointChoice'.
     * 
     * @param pointChoice the value of field 'pointChoice'.
     */
    public void setPointChoice(com.cannontech.common.device.definition.model.castor.PointChoice pointChoice)
    {
        this._pointChoice = pointChoice;
    } //-- void setPointChoice(com.cannontech.common.device.definition.model.castor.PointChoice) 

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
        return (com.cannontech.common.device.definition.model.castor.Point) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Point.class, reader);
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
