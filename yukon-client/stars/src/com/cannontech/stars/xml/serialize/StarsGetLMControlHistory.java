/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsGetLMControlHistory.java,v 1.1 2002/07/16 19:50:05 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * Get LM control history of a LM program
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:05 $
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
    **/
    public boolean getGetSummary()
    {
        return this._getSummary;
    } //-- boolean getGetSummary() 

    /**
    **/
    public int getGroupID()
    {
        return this._groupID;
    } //-- int getGroupID() 

    /**
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
     * @param getSummary
    **/
    public void setGetSummary(boolean getSummary)
    {
        this._getSummary = getSummary;
        this._has_getSummary = true;
    } //-- void setGetSummary(boolean) 

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
     * @param period
    **/
    public void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod period)
    {
        this._period = period;
    } //-- void setPeriod(com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod) 

    /**
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
