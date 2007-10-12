/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: Mapping.java,v 1.1 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Mapping.
 * 
 * @version $Revision: 1.1 $ $Date: 2007/10/12 14:44:59 $
 */
public class Mapping implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _default
     */
    private boolean _default = false;

    /**
     * keeps track of state for field: _default
     */
    private boolean _has_default;

    /**
     * Field _type
     */
    private java.lang.String _type;

    /**
     * Field _point
     */
    private java.lang.String _point;


      //----------------/
     //- Constructors -/
    //----------------/

    public Mapping() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.Mapping()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteDefault
     * 
     */
    public void deleteDefault()
    {
        this._has_default= false;
    } //-- void deleteDefault() 

    /**
     * Returns the value of field 'default'.
     * 
     * @return boolean
     * @return the value of field 'default'.
     */
    public boolean getDefault()
    {
        return this._default;
    } //-- boolean getDefault() 

    /**
     * Returns the value of field 'point'.
     * 
     * @return String
     * @return the value of field 'point'.
     */
    public java.lang.String getPoint()
    {
        return this._point;
    } //-- java.lang.String getPoint() 

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
     * Method hasDefault
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasDefault()
    {
        return this._has_default;
    } //-- boolean hasDefault() 

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
     * Sets the value of field 'default'.
     * 
     * @param _default
     * @param default the value of field 'default'.
     */
    public void setDefault(boolean _default)
    {
        this._default = _default;
        this._has_default = true;
    } //-- void setDefault(boolean) 

    /**
     * Sets the value of field 'point'.
     * 
     * @param point the value of field 'point'.
     */
    public void setPoint(java.lang.String point)
    {
        this._point = point;
    } //-- void setPoint(java.lang.String) 

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
        return (com.cannontech.common.device.definition.model.castor.Mapping) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Mapping.class, reader);
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
