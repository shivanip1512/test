/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.pao.definition.model.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class PointChoiceSequence2.
 * 
 * @version $Revision$ $Date$
 */
public class PointChoiceSequence2 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _controlType
     */
    private com.cannontech.common.pao.definition.model.castor.ControlType _controlType;

    /**
     * Field _controlOffset
     */
    private com.cannontech.common.pao.definition.model.castor.ControlOffset _controlOffset;

    /**
     * Field _stateZeroControl
     */
    private com.cannontech.common.pao.definition.model.castor.StateZeroControl _stateZeroControl;

    /**
     * Field _stateOneControl
     */
    private com.cannontech.common.pao.definition.model.castor.StateOneControl _stateOneControl;

    /**
     * Field _stategroup
     */
    private com.cannontech.common.pao.definition.model.castor.Stategroup _stategroup;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointChoiceSequence2() 
     {
        super();
    } //-- com.cannontech.common.pao.definition.model.castor.PointChoiceSequence2()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'controlOffset'.
     * 
     * @return ControlOffset
     * @return the value of field 'controlOffset'.
     */
    public com.cannontech.common.pao.definition.model.castor.ControlOffset getControlOffset()
    {
        return this._controlOffset;
    } //-- com.cannontech.common.pao.definition.model.castor.ControlOffset getControlOffset() 

    /**
     * Returns the value of field 'controlType'.
     * 
     * @return ControlType
     * @return the value of field 'controlType'.
     */
    public com.cannontech.common.pao.definition.model.castor.ControlType getControlType()
    {
        return this._controlType;
    } //-- com.cannontech.common.pao.definition.model.castor.ControlType getControlType() 

    /**
     * Returns the value of field 'stateOneControl'.
     * 
     * @return StateOneControl
     * @return the value of field 'stateOneControl'.
     */
    public com.cannontech.common.pao.definition.model.castor.StateOneControl getStateOneControl()
    {
        return this._stateOneControl;
    } //-- com.cannontech.common.pao.definition.model.castor.StateOneControl getStateOneControl() 

    /**
     * Returns the value of field 'stateZeroControl'.
     * 
     * @return StateZeroControl
     * @return the value of field 'stateZeroControl'.
     */
    public com.cannontech.common.pao.definition.model.castor.StateZeroControl getStateZeroControl()
    {
        return this._stateZeroControl;
    } //-- com.cannontech.common.pao.definition.model.castor.StateZeroControl getStateZeroControl() 

    /**
     * Returns the value of field 'stategroup'.
     * 
     * @return Stategroup
     * @return the value of field 'stategroup'.
     */
    public com.cannontech.common.pao.definition.model.castor.Stategroup getStategroup()
    {
        return this._stategroup;
    } //-- com.cannontech.common.pao.definition.model.castor.Stategroup getStategroup() 

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
     * Sets the value of field 'controlOffset'.
     * 
     * @param controlOffset the value of field 'controlOffset'.
     */
    public void setControlOffset(com.cannontech.common.pao.definition.model.castor.ControlOffset controlOffset)
    {
        this._controlOffset = controlOffset;
    } //-- void setControlOffset(com.cannontech.common.pao.definition.model.castor.ControlOffset) 

    /**
     * Sets the value of field 'controlType'.
     * 
     * @param controlType the value of field 'controlType'.
     */
    public void setControlType(com.cannontech.common.pao.definition.model.castor.ControlType controlType)
    {
        this._controlType = controlType;
    } //-- void setControlType(com.cannontech.common.pao.definition.model.castor.ControlType) 

    /**
     * Sets the value of field 'stateOneControl'.
     * 
     * @param stateOneControl the value of field 'stateOneControl'.
     */
    public void setStateOneControl(com.cannontech.common.pao.definition.model.castor.StateOneControl stateOneControl)
    {
        this._stateOneControl = stateOneControl;
    } //-- void setStateOneControl(com.cannontech.common.pao.definition.model.castor.StateOneControl) 

    /**
     * Sets the value of field 'stateZeroControl'.
     * 
     * @param stateZeroControl the value of field 'stateZeroControl'
     */
    public void setStateZeroControl(com.cannontech.common.pao.definition.model.castor.StateZeroControl stateZeroControl)
    {
        this._stateZeroControl = stateZeroControl;
    } //-- void setStateZeroControl(com.cannontech.common.pao.definition.model.castor.StateZeroControl) 

    /**
     * Sets the value of field 'stategroup'.
     * 
     * @param stategroup the value of field 'stategroup'.
     */
    public void setStategroup(com.cannontech.common.pao.definition.model.castor.Stategroup stategroup)
    {
        this._stategroup = stategroup;
    } //-- void setStategroup(com.cannontech.common.pao.definition.model.castor.Stategroup) 

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
        return (com.cannontech.common.pao.definition.model.castor.PointChoiceSequence2) Unmarshaller.unmarshal(com.cannontech.common.pao.definition.model.castor.PointChoiceSequence2.class, reader);
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
