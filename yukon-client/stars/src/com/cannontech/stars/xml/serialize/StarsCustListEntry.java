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
    **/
    public boolean hasEntryID()
    {
        return this._has_entryID;
    } //-- boolean hasEntryID() 

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
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
