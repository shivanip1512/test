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
public class StarsThermoFanSettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Auto type
    **/
    public static final int AUTO_TYPE = 0;

    /**
     * The instance of the Auto type
    **/
    public static final StarsThermoFanSettings AUTO = new StarsThermoFanSettings(AUTO_TYPE, "Auto");

    /**
     * The On type
    **/
    public static final int ON_TYPE = 1;

    /**
     * The instance of the On type
    **/
    public static final StarsThermoFanSettings ON = new StarsThermoFanSettings(ON_TYPE, "On");

    /**
     * The Circulate type
    **/
    public static final int CIRCULATE_TYPE = 2;

    /**
     * The instance of the Circulate type
    **/
    public static final StarsThermoFanSettings CIRCULATE = new StarsThermoFanSettings(CIRCULATE_TYPE, "Circulate");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsThermoFanSettings(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsThermoFanSettings
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsThermoFanSettings
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
        members.put("Auto", AUTO);
        members.put("Circulate", CIRCULATE);
        members.put("On", ON);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this
     * StarsThermoFanSettings
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsThermoFanSettings based on the given
     * String value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsThermoFanSettings";
            throw new IllegalArgumentException(err);
        }
        return (StarsThermoFanSettings) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings valueOf(java.lang.String) 

}
