/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLMConfiguration.java,v 1.9 2004/12/09 16:25:42 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.9 $ $Date: 2004/12/09 16:25:42 $
**/
public class StarsLMConfiguration implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _coldLoadPickup;

    private java.lang.String _tamperDetect;

    private SA205 _SA205;

    private SA305 _SA305;

    private VersaCom _versaCom;

    private ExpressCom _expressCom;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMConfiguration() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMConfiguration()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'coldLoadPickup'.
     * 
     * @return the value of field 'coldLoadPickup'.
    **/
    public java.lang.String getColdLoadPickup()
    {
        return this._coldLoadPickup;
    } //-- java.lang.String getColdLoadPickup() 

    /**
     * Returns the value of field 'expressCom'.
     * 
     * @return the value of field 'expressCom'.
    **/
    public ExpressCom getExpressCom()
    {
        return this._expressCom;
    } //-- ExpressCom getExpressCom() 

    /**
     * Returns the value of field 'SA205'.
     * 
     * @return the value of field 'SA205'.
    **/
    public SA205 getSA205()
    {
        return this._SA205;
    } //-- SA205 getSA205() 

    /**
     * Returns the value of field 'SA305'.
     * 
     * @return the value of field 'SA305'.
    **/
    public SA305 getSA305()
    {
        return this._SA305;
    } //-- SA305 getSA305() 

    /**
     * Returns the value of field 'tamperDetect'.
     * 
     * @return the value of field 'tamperDetect'.
    **/
    public java.lang.String getTamperDetect()
    {
        return this._tamperDetect;
    } //-- java.lang.String getTamperDetect() 

    /**
     * Returns the value of field 'versaCom'.
     * 
     * @return the value of field 'versaCom'.
    **/
    public VersaCom getVersaCom()
    {
        return this._versaCom;
    } //-- VersaCom getVersaCom() 

    /**
    **/
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
     * 
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'coldLoadPickup'.
     * 
     * @param coldLoadPickup the value of field 'coldLoadPickup'.
    **/
    public void setColdLoadPickup(java.lang.String coldLoadPickup)
    {
        this._coldLoadPickup = coldLoadPickup;
    } //-- void setColdLoadPickup(java.lang.String) 

    /**
     * Sets the value of field 'expressCom'.
     * 
     * @param expressCom the value of field 'expressCom'.
    **/
    public void setExpressCom(ExpressCom expressCom)
    {
        this._expressCom = expressCom;
    } //-- void setExpressCom(ExpressCom) 

    /**
     * Sets the value of field 'SA205'.
     * 
     * @param SA205 the value of field 'SA205'.
    **/
    public void setSA205(SA205 SA205)
    {
        this._SA205 = SA205;
    } //-- void setSA205(SA205) 

    /**
     * Sets the value of field 'SA305'.
     * 
     * @param SA305 the value of field 'SA305'.
    **/
    public void setSA305(SA305 SA305)
    {
        this._SA305 = SA305;
    } //-- void setSA305(SA305) 

    /**
     * Sets the value of field 'tamperDetect'.
     * 
     * @param tamperDetect the value of field 'tamperDetect'.
    **/
    public void setTamperDetect(java.lang.String tamperDetect)
    {
        this._tamperDetect = tamperDetect;
    } //-- void setTamperDetect(java.lang.String) 

    /**
     * Sets the value of field 'versaCom'.
     * 
     * @param versaCom the value of field 'versaCom'.
    **/
    public void setVersaCom(VersaCom versaCom)
    {
        this._versaCom = versaCom;
    } //-- void setVersaCom(VersaCom) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMConfiguration unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMConfiguration) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMConfiguration.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMConfiguration unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
