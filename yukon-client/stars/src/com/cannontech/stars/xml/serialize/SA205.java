/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: SA205.java,v 1.7 2004/10/26 21:15:41 zyao Exp $
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
 * @version $Revision: 1.7 $ $Date: 2004/10/26 21:15:41 $
**/
public class SA205 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _slot1;

    /**
     * keeps track of state for field: _slot1
    **/
    private boolean _has_slot1;

    private int _slot2;

    /**
     * keeps track of state for field: _slot2
    **/
    private boolean _has_slot2;

    private int _slot3;

    /**
     * keeps track of state for field: _slot3
    **/
    private boolean _has_slot3;

    private int _slot4;

    /**
     * keeps track of state for field: _slot4
    **/
    private boolean _has_slot4;

    private int _slot5;

    /**
     * keeps track of state for field: _slot5
    **/
    private boolean _has_slot5;

    private int _slot6;

    /**
     * keeps track of state for field: _slot6
    **/
    private boolean _has_slot6;


      //----------------/
     //- Constructors -/
    //----------------/

    public SA205() {
        super();
    } //-- com.cannontech.stars.xml.serialize.SA205()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'slot1'.
     * 
     * @return the value of field 'slot1'.
    **/
    public int getSlot1()
    {
        return this._slot1;
    } //-- int getSlot1() 

    /**
     * Returns the value of field 'slot2'.
     * 
     * @return the value of field 'slot2'.
    **/
    public int getSlot2()
    {
        return this._slot2;
    } //-- int getSlot2() 

    /**
     * Returns the value of field 'slot3'.
     * 
     * @return the value of field 'slot3'.
    **/
    public int getSlot3()
    {
        return this._slot3;
    } //-- int getSlot3() 

    /**
     * Returns the value of field 'slot4'.
     * 
     * @return the value of field 'slot4'.
    **/
    public int getSlot4()
    {
        return this._slot4;
    } //-- int getSlot4() 

    /**
     * Returns the value of field 'slot5'.
     * 
     * @return the value of field 'slot5'.
    **/
    public int getSlot5()
    {
        return this._slot5;
    } //-- int getSlot5() 

    /**
     * Returns the value of field 'slot6'.
     * 
     * @return the value of field 'slot6'.
    **/
    public int getSlot6()
    {
        return this._slot6;
    } //-- int getSlot6() 

    /**
    **/
    public boolean hasSlot1()
    {
        return this._has_slot1;
    } //-- boolean hasSlot1() 

    /**
    **/
    public boolean hasSlot2()
    {
        return this._has_slot2;
    } //-- boolean hasSlot2() 

    /**
    **/
    public boolean hasSlot3()
    {
        return this._has_slot3;
    } //-- boolean hasSlot3() 

    /**
    **/
    public boolean hasSlot4()
    {
        return this._has_slot4;
    } //-- boolean hasSlot4() 

    /**
    **/
    public boolean hasSlot5()
    {
        return this._has_slot5;
    } //-- boolean hasSlot5() 

    /**
    **/
    public boolean hasSlot6()
    {
        return this._has_slot6;
    } //-- boolean hasSlot6() 

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
     * Sets the value of field 'slot1'.
     * 
     * @param slot1 the value of field 'slot1'.
    **/
    public void setSlot1(int slot1)
    {
        this._slot1 = slot1;
        this._has_slot1 = true;
    } //-- void setSlot1(int) 

    /**
     * Sets the value of field 'slot2'.
     * 
     * @param slot2 the value of field 'slot2'.
    **/
    public void setSlot2(int slot2)
    {
        this._slot2 = slot2;
        this._has_slot2 = true;
    } //-- void setSlot2(int) 

    /**
     * Sets the value of field 'slot3'.
     * 
     * @param slot3 the value of field 'slot3'.
    **/
    public void setSlot3(int slot3)
    {
        this._slot3 = slot3;
        this._has_slot3 = true;
    } //-- void setSlot3(int) 

    /**
     * Sets the value of field 'slot4'.
     * 
     * @param slot4 the value of field 'slot4'.
    **/
    public void setSlot4(int slot4)
    {
        this._slot4 = slot4;
        this._has_slot4 = true;
    } //-- void setSlot4(int) 

    /**
     * Sets the value of field 'slot5'.
     * 
     * @param slot5 the value of field 'slot5'.
    **/
    public void setSlot5(int slot5)
    {
        this._slot5 = slot5;
        this._has_slot5 = true;
    } //-- void setSlot5(int) 

    /**
     * Sets the value of field 'slot6'.
     * 
     * @param slot6 the value of field 'slot6'.
    **/
    public void setSlot6(int slot6)
    {
        this._slot6 = slot6;
        this._has_slot6 = true;
    } //-- void setSlot6(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.SA205 unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.SA205) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.SA205.class, reader);
    } //-- com.cannontech.stars.xml.serialize.SA205 unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
