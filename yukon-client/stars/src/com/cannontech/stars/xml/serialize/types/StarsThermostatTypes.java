/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsThermostatTypes.java,v 1.12 2004/05/24 21:11:04 zyao Exp $
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
 * @version $Revision: 1.12 $ $Date: 2004/05/24 21:11:04 $
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
