/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsLoginStatus.java,v 1.5 2004/05/10 22:13:38 zyao Exp $
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
 * @version $Revision: 1.5 $ $Date: 2004/05/10 22:13:38 $
**/
public class StarsLoginStatus implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Enabled type
    **/
    public static final int ENABLED_TYPE = 0;

    /**
     * The instance of the Enabled type
    **/
    public static final StarsLoginStatus ENABLED = new StarsLoginStatus(ENABLED_TYPE, "Enabled");

    /**
     * The Disabled type
    **/
    public static final int DISABLED_TYPE = 1;

    /**
     * The instance of the Disabled type
    **/
    public static final StarsLoginStatus DISABLED = new StarsLoginStatus(DISABLED_TYPE, "Disabled");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsLoginStatus(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginStatus(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsLoginStatus
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsLoginStatus
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
        members.put("Enabled", ENABLED);
        members.put("Disabled", DISABLED);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StarsLoginStatus
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsLoginStatus based on the given String
     * value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsLoginStatus valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsLoginStatus";
            throw new IllegalArgumentException(err);
        }
        return (StarsLoginStatus) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsLoginStatus valueOf(java.lang.String) 

}
