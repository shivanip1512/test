/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLMProgram.java,v 1.5 2002/09/06 22:37:25 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision: 1.5 $ $Date: 2002/09/06 22:37:25 $
**/
public class StarsLMProgram implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private java.lang.String _programName;

    private java.lang.String _status;

    private StarsLMControlHistory _starsLMControlHistory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMProgram() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgram()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteGroupID()
    {
        this._has_groupID= false;
    } //-- void deleteGroupID() 

    /**
     * Returns the value of field 'groupID'.
     * 
     * @return the value of field 'groupID'.
    **/
    public int getGroupID()
    {
        return this._groupID;
    } //-- int getGroupID() 

    /**
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
     * Returns the value of field 'programName'.
     * 
     * @return the value of field 'programName'.
    **/
    public java.lang.String getProgramName()
    {
        return this._programName;
    } //-- java.lang.String getProgramName() 

    /**
     * Returns the value of field 'starsLMControlHistory'.
     * 
     * @return the value of field 'starsLMControlHistory'.
    **/
    public StarsLMControlHistory getStarsLMControlHistory()
    {
        return this._starsLMControlHistory;
    } //-- StarsLMControlHistory getStarsLMControlHistory() 

    /**
     * Returns the value of field 'status'.
     * 
     * @return the value of field 'status'.
    **/
    public java.lang.String getStatus()
    {
        return this._status;
    } //-- java.lang.String getStatus() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

    /**
    **/
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

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
     * Sets the value of field 'groupID'.
     * 
     * @param groupID the value of field 'groupID'.
    **/
    public void setGroupID(int groupID)
    {
        this._groupID = groupID;
        this._has_groupID = true;
    } //-- void setGroupID(int) 

    /**
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

    /**
     * Sets the value of field 'programName'.
     * 
     * @param programName the value of field 'programName'.
    **/
    public void setProgramName(java.lang.String programName)
    {
        this._programName = programName;
    } //-- void setProgramName(java.lang.String) 

    /**
     * Sets the value of field 'starsLMControlHistory'.
     * 
     * @param starsLMControlHistory the value of field
     * 'starsLMControlHistory'.
    **/
    public void setStarsLMControlHistory(StarsLMControlHistory starsLMControlHistory)
    {
        this._starsLMControlHistory = starsLMControlHistory;
    } //-- void setStarsLMControlHistory(StarsLMControlHistory) 

    /**
     * Sets the value of field 'status'.
     * 
     * @param status the value of field 'status'.
    **/
    public void setStatus(java.lang.String status)
    {
        this._status = status;
    } //-- void setStatus(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsLMProgram unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsLMProgram) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsLMProgram.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsLMProgram unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
