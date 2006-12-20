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
     * Field _clazz
     */
    private com.cannontech.common.device.definition.model.castor.Class _clazz;

    /**
     * Field _category
     */
    private com.cannontech.common.device.definition.model.castor.Category _category;

    /**
     * Field _javaClass
     */
    private com.cannontech.common.device.definition.model.castor.JavaClass _javaClass;

    /**
     * Field _points
     */
    private com.cannontech.common.device.definition.model.castor.Points _points;


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
     * Returns the value of field 'category'.
     * 
     * @return Category
     * @return the value of field 'category'.
     */
    public com.cannontech.common.device.definition.model.castor.Category getCategory()
    {
        return this._category;
    } //-- com.cannontech.common.device.definition.model.castor.Category getCategory() 

    /**
     * Returns the value of field 'clazz'.
     * 
     * @return Class
     * @return the value of field 'clazz'.
     */
    public com.cannontech.common.device.definition.model.castor.Class getClazz()
    {
        return this._clazz;
    } //-- com.cannontech.common.device.definition.model.castor.Class getClazz() 

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
     * Sets the value of field 'category'.
     * 
     * @param category the value of field 'category'.
     */
    public void setCategory(com.cannontech.common.device.definition.model.castor.Category category)
    {
        this._category = category;
    } //-- void setCategory(com.cannontech.common.device.definition.model.castor.Category) 

    /**
     * Sets the value of field 'clazz'.
     * 
     * @param clazz the value of field 'clazz'.
     */
    public void setClazz(com.cannontech.common.device.definition.model.castor.Class clazz)
    {
        this._clazz = clazz;
    } //-- void setClazz(com.cannontech.common.device.definition.model.castor.Class) 

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
