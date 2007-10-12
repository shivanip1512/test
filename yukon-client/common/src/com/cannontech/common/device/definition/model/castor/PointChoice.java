/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.device.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class PointChoice.
 * 
 * @version $Revision$ $Date$
 */
public class PointChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _pointChoiceSequence
     */
    private com.cannontech.common.device.definition.model.castor.PointChoiceSequence _pointChoiceSequence;

    /**
     * Field _stategroup
     */
    private com.cannontech.common.device.definition.model.castor.Stategroup _stategroup;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointChoice() 
     {
        super();
    } //-- com.cannontech.common.device.definition.model.castor.PointChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'pointChoiceSequence'.
     * 
     * @return PointChoiceSequence
     * @return the value of field 'pointChoiceSequence'.
     */
    public com.cannontech.common.device.definition.model.castor.PointChoiceSequence getPointChoiceSequence()
    {
        return this._pointChoiceSequence;
    } //-- com.cannontech.common.device.definition.model.castor.PointChoiceSequence getPointChoiceSequence() 

    /**
     * Returns the value of field 'stategroup'.
     * 
     * @return Stategroup
     * @return the value of field 'stategroup'.
     */
    public com.cannontech.common.device.definition.model.castor.Stategroup getStategroup()
    {
        return this._stategroup;
    } //-- com.cannontech.common.device.definition.model.castor.Stategroup getStategroup() 

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
     * Sets the value of field 'pointChoiceSequence'.
     * 
     * @param pointChoiceSequence the value of field
     * 'pointChoiceSequence'.
     */
    public void setPointChoiceSequence(com.cannontech.common.device.definition.model.castor.PointChoiceSequence pointChoiceSequence)
    {
        this._pointChoiceSequence = pointChoiceSequence;
    } //-- void setPointChoiceSequence(com.cannontech.common.device.definition.model.castor.PointChoiceSequence) 

    /**
     * Sets the value of field 'stategroup'.
     * 
     * @param stategroup the value of field 'stategroup'.
     */
    public void setStategroup(com.cannontech.common.device.definition.model.castor.Stategroup stategroup)
    {
        this._stategroup = stategroup;
    } //-- void setStategroup(com.cannontech.common.device.definition.model.castor.Stategroup) 

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
        return (com.cannontech.common.device.definition.model.castor.PointChoice) Unmarshaller.unmarshal(com.cannontech.common.device.definition.model.castor.PointChoice.class, reader);
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
