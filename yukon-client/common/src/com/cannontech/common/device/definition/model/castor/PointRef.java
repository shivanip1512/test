/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: PointRef.java,v 1.1 2007/05/10 19:52:43 jkoponen Exp $
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
 * Class PointRef.
 * 
 * @version $Revision: 1.1 $ $Date: 2007/05/10 19:52:43 $
 */
public class PointRef implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _expectedMsgs
     */
    private int _expectedMsgs;

    /**
     * keeps track of state for field: _expectedMsgs
     */
    private boolean _has_expectedMsgs;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointRef() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.PointRef()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteExpectedMsgs
     * 
     */
    public void deleteExpectedMsgs()
    {
        this._has_expectedMsgs= false;
    } //-- void deleteExpectedMsgs() 

    /**
     * Returns the value of field 'expectedMsgs'.
     * 
     * @return int
     * @return the value of field 'expectedMsgs'.
     */
    public int getExpectedMsgs()
    {
        return this._expectedMsgs;
    } //-- int getExpectedMsgs() 

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
     * Method hasExpectedMsgs
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasExpectedMsgs()
    {
        return this._has_expectedMsgs;
    } //-- boolean hasExpectedMsgs() 

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
     * Sets the value of field 'expectedMsgs'.
     * 
     * @param expectedMsgs the value of field 'expectedMsgs'.
     */
    public void setExpectedMsgs(int expectedMsgs)
    {
        this._expectedMsgs = expectedMsgs;
        this._has_expectedMsgs = true;
    } //-- void setExpectedMsgs(int) 

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
        return (com.cannontech.common.device.definition.model.castor.PointRef) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.PointRef.class, reader);
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
