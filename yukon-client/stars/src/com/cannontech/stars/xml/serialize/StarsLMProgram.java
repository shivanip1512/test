/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsLMProgram.java,v 1.1 2002/07/16 19:50:07 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:07 $
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

    private java.lang.String _programName;

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

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
    **/
    public int getGroupID()
    {
        return this._groupID;
    } //-- int getGroupID() 

    /**
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
    **/
    public java.lang.String getProgramName()
    {
        return this._programName;
    } //-- java.lang.String getProgramName() 

    /**
    **/
    public StarsLMControlHistory getStarsLMControlHistory()
    {
        return this._starsLMControlHistory;
    } //-- StarsLMControlHistory getStarsLMControlHistory() 

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
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * 
     * @param groupID
    **/
    public void setGroupID(int groupID)
    {
        this._groupID = groupID;
        this._has_groupID = true;
    } //-- void setGroupID(int) 

    /**
     * 
     * @param programID
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

    /**
     * 
     * @param programName
    **/
    public void setProgramName(java.lang.String programName)
    {
        this._programName = programName;
    } //-- void setProgramName(java.lang.String) 

    /**
     * 
     * @param starsLMControlHistory
    **/
    public void setStarsLMControlHistory(StarsLMControlHistory starsLMControlHistory)
    {
        this._starsLMControlHistory = starsLMControlHistory;
    } //-- void setStarsLMControlHistory(StarsLMControlHistory) 

    /**
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
