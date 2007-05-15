/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsThermostatTypes.java,v 1.33 2007/05/15 16:33:16 jdayton Exp $
 */

package com.cannontech.stars.xml.serialize.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * 
 * 
 * @version $Revision: 1.33 $ $Date: 2007/05/15 16:33:16 $
**/
public class StarsThermostatTypes implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The ExpressStat type
    **/
    public static final int EXPRESSSTAT_TYPE = 0;

    /**
     * The instance of the ExpressStat type
    **/
    public static final StarsThermostatTypes EXPRESSSTAT = new StarsThermostatTypes(EXPRESSSTAT_TYPE, "ExpressStat");

    /**
     * The EnergyPro type
    **/
    public static final int ENERGYPRO_TYPE = 1;

    /**
     * The instance of the EnergyPro type
    **/
    public static final StarsThermostatTypes ENERGYPRO = new StarsThermostatTypes(ENERGYPRO_TYPE, "EnergyPro");

    /**
     * The Commercial type
    **/
    public static final int COMMERCIAL_TYPE = 2;

    /**
     * The instance of the Commercial type
    **/
    public static final StarsThermostatTypes COMMERCIAL = new StarsThermostatTypes(COMMERCIAL_TYPE, "Commercial");

    /**
     * The Commercial type
    **/
    public static final int EXPRESSSTAT_HEATPUMP_TYPE = 3;

    /**
     * The instance of the Commercial type
    **/
    public static final StarsThermostatTypes EXPRESSSTAT_HEATPUMP = new StarsThermostatTypes(EXPRESSSTAT_HEATPUMP_TYPE, "ExpressStat Heat Pump");

    /**
     * The total number of thermostat types in the system
     */
    public static final int NUMBER_OF_THERMO_TYPES = 4;
    
    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsThermostatTypes(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermostatTypes(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsThermostatTypes
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsThermostatTypes
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
        members.put("ExpressStat", EXPRESSSTAT);
        members.put("EnergyPro", ENERGYPRO);
        members.put("Commercial", COMMERCIAL);
        members.put("ExpressStat Heat Pump", EXPRESSSTAT_HEATPUMP);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StarsThermostatType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsThermostatTypes based on the given String
     * value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsThermostatTypes valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsThermostatTypes";
            throw new IllegalArgumentException(err);
        }
        return (StarsThermostatTypes) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermostatTypes valueOf(java.lang.String) 

}
