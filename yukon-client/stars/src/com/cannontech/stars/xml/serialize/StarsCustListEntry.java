/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
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
 * @version $Revision$ $Date$
**/
public abstract class StarsCustListEntry implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
    **/
    private java.lang.String _content = "";

    private int _entryID;

    /**
     * keeps track of state for field: _entryID
    **/
    private boolean _has_entryID;

    private int _yukonDefID;

    /**
     * keeps track of state for field: _yukonDefID
    **/
    private boolean _has_yukonDefID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustListEntry() {
        super();
        setContent("");
    } //-- com.cannontech.stars.xml.serialize.StarsCustListEntry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteEntryID()
    {
        this._has_entryID= false;
    } //-- void deleteEntryID() 

    /**
    **/
    public void deleteYukonDefID()
    {
        this._has_yukonDefID= false;
    } //-- void deleteYukonDefID() 

    /**
     * Returns the value of field 'content'. The field 'content'
     * has the following description: internal content storage
     * 
     * @return the value of field 'content'.
    **/
    public java.lang.String getContent()
    {
        return this._content;
    } //-- java.lang.String getContent() 

    /**
     * Returns the value of field 'entryID'.
     * 
     * @return the value of field 'entryID'.
    **/
    public int getEntryID()
    {
        return this._entryID;
    } //-- int getEntryID() 

    /**
     * Returns the value of field 'yukonDefID'.
     * 
     * @return the value of field 'yukonDefID'.
    **/
    public int getYukonDefID()
    {
        return this._yukonDefID;
    } //-- int getYukonDefID() 

    /**
    **/
    public boolean hasEntryID()
    {
        return this._has_entryID;
    } //-- boolean hasEntryID() 

    /**
    **/
    public boolean hasYukonDefID()
    {
        return this._has_yukonDefID;
    } //-- boolean hasYukonDefID() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'content'. The field 'content' has
     * the following description: internal content storage
     * 
     * @param content the value of field 'content'.
    **/
    public void setContent(java.lang.String content)
    {
        this._content = content;
    } //-- void setContent(java.lang.String) 

    /**
     * Sets the value of field 'entryID'.
     * 
     * @param entryID the value of field 'entryID'.
    **/
    public void setEntryID(int entryID)
    {
        this._entryID = entryID;
        this._has_entryID = true;
    } //-- void setEntryID(int) 

    /**
     * Sets the value of field 'yukonDefID'.
     * 
     * @param yukonDefID the value of field 'yukonDefID'.
    **/
    public void setYukonDefID(int yukonDefID)
    {
        this._yukonDefID = yukonDefID;
        this._has_yukonDefID = true;
    } //-- void setYukonDefID(int) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
