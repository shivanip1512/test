/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsGetLMControlHistory.java,v 1.62 2004/01/21 17:52:18 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Get LM control history of a LM program
 * 
 * @version $Revision: 1.62 $ $Date: 2004/01/21 17:52:18 $
**/
public class StarsGetLMControlHistory implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _groupID;

    /**
     * keeps track of state for field: _groupID
    **/
    private boolean _has_groupID;

    private com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod _period;

    private boolean _getSummary;

    /**
     * keeps track of state for field: _getSummary
    **/
    private boolean _has_getSummary;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetLMControlHistory() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetLMControlHistory()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteGetSummary()
    {
        this._has_getSummary= false;
    } //-- void deleteGetSummary() 

    /**
     * Returns the value of field 'getSummary'.
     * 
     * @return the value of field 'getSummary'.
    **/
    public boolean getGetSummary()
    {
        return this._getSummary;
    } //-- boolean getGetSummary() 

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
     * Returns the value of field 'period'.
     * 
     * @return the value of field 'period'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod getPeriod()
    {
        return this._period;
    } //-- com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod getPeriod() 

    /**
    **/
    public boolean hasGetSummary()
    {
        return this._has_getSummary;
    } //-- boolean hasGetSummary() 

    /**
    **/
    public boolean hasGroupID()
    {
        return this._has_groupID;
    } //-- boolean hasGroupID() 

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
     * Sets the value of field 'getSummary'.
     * 
     * @param getSummary the value of field 'getSummary'.
    **/
    public void setGetSummary(boolean getSummary)
    {
        this._getSummary = getSummary;
        this._has_getSummary = true;
    } //-- void setGetSummary(boolean) 

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
     * Sets the value of field 'period'.
     * 
     * @param period the value of field 'period'.
    **/
    public void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod period)
    {
        this._period = period;
    } //-- void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsGetLMControlHistory unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetLMControlHistory) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetLMControlHistory.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetLMControlHistory unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
