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
public class StarsThermoModeSettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Heat type
    **/
    public static final int HEAT_TYPE = 0;

    /**
     * The instance of the Heat type
    **/
    public static final StarsThermoModeSettings HEAT = new StarsThermoModeSettings(HEAT_TYPE, "Heat");

    /**
     * The Cool type
    **/
    public static final int COOL_TYPE = 1;

    /**
     * The instance of the Cool type
    **/
    public static final StarsThermoModeSettings COOL = new StarsThermoModeSettings(COOL_TYPE, "Cool");

    /**
     * The Off type
    **/
    public static final int OFF_TYPE = 2;

    /**
     * The instance of the Off type
    **/
    public static final StarsThermoModeSettings OFF = new StarsThermoModeSettings(OFF_TYPE, "Off");

    /**
     * The EmgHeat type
    **/
    public static final int EMHEAT_TYPE = 3;

    /**
     * The instance of the EmgHeat type
    **/
    public static final StarsThermoModeSettings EMHEAT = new StarsThermoModeSettings(EMHEAT_TYPE, "EmHeat");

    private static Hashtable<String, StarsThermoModeSettings> _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsThermoModeSettings(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsThermoModeSettings
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsThermoModeSettings
    **/
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
    **/
    private static Hashtable<String, StarsThermoModeSettings> init()
    {
        Hashtable<String, StarsThermoModeSettings> members = new Hashtable<String, StarsThermoModeSettings>();
        members.put("Heat", HEAT);
        members.put("Cool", COOL);
        members.put("Off", OFF);
        members.put("EmHeat", EMHEAT);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this
     * StarsThermoModeSettings
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsThermoModeSettings based on the given
     * String value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsThermoModeSettings";
            throw new IllegalArgumentException(err);
        }
        return (StarsThermoModeSettings) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings valueOf(java.lang.String) 

}
