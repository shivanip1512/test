/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: VersaCom.java,v 1.2 2004/07/26 21:33:23 yao Exp $
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
 * @version $Revision: 1.2 $ $Date: 2004/07/26 21:33:23 $
**/
public class VersaCom implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _utility;

    /**
     * keeps track of state for field: _utility
    **/
    private boolean _has_utility;

    private int _section;

    /**
     * keeps track of state for field: _section
    **/
    private boolean _has_section;

    private int _classAddress;

    /**
     * keeps track of state for field: _classAddress
    **/
    private boolean _has_classAddress;

    private int _division;

    /**
     * keeps track of state for field: _division
    **/
    private boolean _has_division;


      //----------------/
     //- Constructors -/
    //----------------/

    public VersaCom() {
        super();
    } //-- com.cannontech.stars.xml.serialize.VersaCom()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'classAddress'.
     * 
     * @return the value of field 'classAddress'.
    **/
    public int getClassAddress()
    {
        return this._classAddress;
    } //-- int getClassAddress() 

    /**
     * Returns the value of field 'division'.
     * 
     * @return the value of field 'division'.
    **/
    public int getDivision()
    {
        return this._division;
    } //-- int getDivision() 

    /**
     * Returns the value of field 'section'.
     * 
     * @return the value of field 'section'.
    **/
    public int getSection()
    {
        return this._section;
    } //-- int getSection() 

    /**
     * Returns the value of field 'utility'.
     * 
     * @return the value of field 'utility'.
    **/
    public int getUtility()
    {
        return this._utility;
    } //-- int getUtility() 

    /**
    **/
    public boolean hasClassAddress()
    {
        return this._has_classAddress;
    } //-- boolean hasClassAddress() 

    /**
    **/
    public boolean hasDivision()
    {
        return this._has_division;
    } //-- boolean hasDivision() 

    /**
    **/
    public boolean hasSection()
    {
        return this._has_section;
    } //-- boolean hasSection() 

    /**
    **/
    public boolean hasUtility()
    {
        return this._has_utility;
    } //-- boolean hasUtility() 

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
     * Sets the value of field 'classAddress'.
     * 
     * @param classAddress the value of field 'classAddress'.
    **/
    public void setClassAddress(int classAddress)
    {
        this._classAddress = classAddress;
        this._has_classAddress = true;
    } //-- void setClassAddress(int) 

    /**
     * Sets the value of field 'division'.
     * 
     * @param division the value of field 'division'.
    **/
    public void setDivision(int division)
    {
        this._division = division;
        this._has_division = true;
    } //-- void setDivision(int) 

    /**
     * Sets the value of field 'section'.
     * 
     * @param section the value of field 'section'.
    **/
    public void setSection(int section)
    {
        this._section = section;
        this._has_section = true;
    } //-- void setSection(int) 

    /**
     * Sets the value of field 'utility'.
     * 
     * @param utility the value of field 'utility'.
    **/
    public void setUtility(int utility)
    {
        this._utility = utility;
        this._has_utility = true;
    } //-- void setUtility(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.VersaCom unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.VersaCom) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.VersaCom.class, reader);
    } //-- com.cannontech.stars.xml.serialize.VersaCom unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
