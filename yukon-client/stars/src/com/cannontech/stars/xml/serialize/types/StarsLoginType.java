/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsLoginType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The OperatorLogin type
    **/
    public static final int OPERATORLOGIN_TYPE = 0;

    /**
     * The instance of the OperatorLogin type
    **/
    public static final StarsLoginType OPERATORLOGIN = new StarsLoginType(OPERATORLOGIN_TYPE, "OperatorLogin");

    /**
     * The ConsumerLogin type
    **/
    public static final int CONSUMERLOGIN_TYPE = 1;

    /**
     * The instance of the ConsumerLogin type
    **/
    public static final StarsLoginType CONSUMERLOGIN = new StarsLoginType(CONSUMERLOGIN_TYPE, "ConsumerLogin");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsLoginType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsLoginType
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsLoginType
    **/
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
    **/
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("OperatorLogin", OPERATORLOGIN);
        members.put("ConsumerLogin", CONSUMERLOGIN);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StarsLoginType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsLoginType based on the given String
     * value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsLoginType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsLoginType";
            throw new IllegalArgumentException(err);
        }
        return (StarsLoginType) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginType valueOf(java.lang.String) 

}
