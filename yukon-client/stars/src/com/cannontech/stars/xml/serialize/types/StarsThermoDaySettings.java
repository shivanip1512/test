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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.*;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsThermoDaySettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Weekday type
    **/
    public static final int WEEKDAY_TYPE = 0;

    /**
     * The instance of the Weekday type
    **/
    public static final StarsThermoDaySettings WEEKDAY = new StarsThermoDaySettings(WEEKDAY_TYPE, "Weekday");

    /**
     * The Weekend type
    **/
    public static final int WEEKEND_TYPE = 1;

    /**
     * The instance of the Weekend type
    **/
    public static final StarsThermoDaySettings WEEKEND = new StarsThermoDaySettings(WEEKEND_TYPE, "Weekend");

    /**
     * The Saturday type
    **/
    public static final int SATURDAY_TYPE = 2;

    /**
     * The instance of the Saturday type
    **/
    public static final StarsThermoDaySettings SATURDAY = new StarsThermoDaySettings(SATURDAY_TYPE, "Saturday");

    /**
     * The Sunday type
    **/
    public static final int SUNDAY_TYPE = 3;

    /**
     * The instance of the Sunday type
    **/
    public static final StarsThermoDaySettings SUNDAY = new StarsThermoDaySettings(SUNDAY_TYPE, "Sunday");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsThermoDaySettings(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsThermoDaySettings
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsThermoDaySettings
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
        members.put("Weekday", WEEKDAY);
        members.put("Weekend", WEEKEND);
        members.put("Saturday", SATURDAY);
        members.put("Sunday", SUNDAY);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this
     * StarsThermoDaySettings
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsThermoDaySettings based on the given
     * String value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsThermoDaySettings";
            throw new IllegalArgumentException(err);
        }
        return (StarsThermoDaySettings) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings valueOf(java.lang.String) 

}
