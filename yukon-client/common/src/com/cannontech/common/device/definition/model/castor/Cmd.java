/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id: Cmd.java,v 1.3 2007/10/12 14:44:59 stacey Exp $
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Cmd.
 * 
 * @version $Revision: 1.3 $ $Date: 2007/10/12 14:44:59 $
 */
public class Cmd implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _text
     */
    private java.lang.String _text;


      //----------------/
     //- Constructors -/
    //----------------/

    public Cmd() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.Cmd()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'text'.
     * 
     * @return String
     * @return the value of field 'text'.
     */
    public java.lang.String getText()
    {
        return this._text;
    } //-- java.lang.String getText() 

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
     * Sets the value of field 'text'.
     * 
     * @param text the value of field 'text'.
     */
    public void setText(java.lang.String text)
    {
        this._text = text;
    } //-- void setText(java.lang.String) 

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
        return (com.cannontech.common.device.definition.model.castor.Cmd) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.Cmd.class, reader);
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
