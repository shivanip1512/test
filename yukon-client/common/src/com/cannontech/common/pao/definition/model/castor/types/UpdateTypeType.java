/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.cannontech.common.pao.definition.model.castor.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class UpdateTypeType.
 * 
 * @version $Revision$ $Date$
 */
public class UpdateTypeType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The On First Change type
     */
    public static final int ON_FIRST_CHANGE_TYPE = 0;

    /**
     * The instance of the On First Change type
     */
    public static final UpdateTypeType ON_FIRST_CHANGE = new UpdateTypeType(ON_FIRST_CHANGE_TYPE, "On First Change");

    /**
     * The On All Change type
     */
    public static final int ON_ALL_CHANGE_TYPE = 1;

    /**
     * The instance of the On All Change type
     */
    public static final UpdateTypeType ON_ALL_CHANGE = new UpdateTypeType(ON_ALL_CHANGE_TYPE, "On All Change");

    /**
     * The On Timer type
     */
    public static final int ON_TIMER_TYPE = 2;

    /**
     * The instance of the On Timer type
     */
    public static final UpdateTypeType ON_TIMER = new UpdateTypeType(ON_TIMER_TYPE, "On Timer");

    /**
     * The On Timer+Change type
     */
    public static final int ON_TIMER_CHANGE_TYPE = 3;

    /**
     * The instance of the On Timer+Change type
     */
    public static final UpdateTypeType ON_TIMER_CHANGE = new UpdateTypeType(ON_TIMER_CHANGE_TYPE, "On Timer+Change");

    /**
     * The Constant type
     */
    public static final int CONSTANT_TYPE = 4;

    /**
     * The instance of the Constant type
     */
    public static final UpdateTypeType CONSTANT = new UpdateTypeType(CONSTANT_TYPE, "Constant");

    /**
     * The Historical type
     */
    public static final int HISTORICAL_TYPE = 5;

    /**
     * The instance of the Historical type
     */
    public static final UpdateTypeType HISTORICAL = new UpdateTypeType(HISTORICAL_TYPE, "Historical");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private UpdateTypeType(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * UpdateTypeType
     * 
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getType
     * 
     * Returns the type of this UpdateTypeType
     * 
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     * 
     * 
     * 
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("On First Change", ON_FIRST_CHANGE);
        members.put("On All Change", ON_ALL_CHANGE);
        members.put("On Timer", ON_TIMER);
        members.put("On Timer+Change", ON_TIMER_CHANGE);
        members.put("Constant", CONSTANT);
        members.put("Historical", HISTORICAL);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method readResolve
     * 
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     * 
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve() 

    /**
     * Method toString
     * 
     * Returns the String representation of this UpdateTypeType
     * 
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOf
     * 
     * Returns a new UpdateTypeType based on the given String
     * value.
     * 
     * @param string
     * @return UpdateTypeType
     */
    public static com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid UpdateTypeType";
            throw new IllegalArgumentException(err);
        }
        return (UpdateTypeType) obj;
    } //-- com.cannontech.common.pao.definition.model.castor.types.UpdateTypeType valueOf(java.lang.String) 

}
