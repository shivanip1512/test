/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsSearchByType.java,v 1.90 2004/12/09 16:25:49 zyao Exp $
 */

package com.cannontech.stars.xml.serialize.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.*;

/**
 * 
 * 
 * @version $Revision: 1.90 $ $Date: 2004/12/09 16:25:49 $
**/
public class StarsSearchByType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The AccountNumber type
    **/
    public static final int ACCOUNTNUMBER_TYPE = 0;

    /**
     * The instance of the AccountNumber type
    **/
    public static final StarsSearchByType ACCOUNTNUMBER = new StarsSearchByType(ACCOUNTNUMBER_TYPE, "AccountNumber");

    /**
     * The PhoneNumber type
    **/
    public static final int PHONENUMBER_TYPE = 1;

    /**
     * The instance of the PhoneNumber type
    **/
    public static final StarsSearchByType PHONENUMBER = new StarsSearchByType(PHONENUMBER_TYPE, "PhoneNumber");

    /**
     * The Name type
    **/
    public static final int NAME_TYPE = 2;

    /**
     * The instance of the Name type
    **/
    public static final StarsSearchByType NAME = new StarsSearchByType(NAME_TYPE, "Name");

    /**
     * The Address type
    **/
    public static final int ADDRESS_TYPE = 3;

    /**
     * The instance of the Address type
    **/
    public static final StarsSearchByType ADDRESS = new StarsSearchByType(ADDRESS_TYPE, "Address");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsSearchByType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsSearchByType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsSearchByType
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsSearchByType
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
        members.put("AccountNumber", ACCOUNTNUMBER);
        members.put("PhoneNumber", PHONENUMBER);
        members.put("Name", NAME);
        members.put("Address", ADDRESS);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StarsSearchByType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsSearchByType based on the given String
     * value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsSearchByType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsSearchByType";
            throw new IllegalArgumentException(err);
        }
        return (StarsSearchByType) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsSearchByType valueOf(java.lang.String) 

}
