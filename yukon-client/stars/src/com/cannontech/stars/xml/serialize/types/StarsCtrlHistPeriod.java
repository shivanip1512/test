/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCtrlHistPeriod.java,v 1.74 2004/05/18 17:48:51 zyao Exp $
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
 * @version $Revision: 1.74 $ $Date: 2004/05/18 17:48:51 $
**/
public class StarsCtrlHistPeriod implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The PastDay type
    **/
    public static final int PASTDAY_TYPE = 0;

    /**
     * The instance of the PastDay type
    **/
    public static final StarsCtrlHistPeriod PASTDAY = new StarsCtrlHistPeriod(PASTDAY_TYPE, "PastDay");

    /**
     * The PastWeek type
    **/
    public static final int PASTWEEK_TYPE = 1;

    /**
     * The instance of the PastWeek type
    **/
    public static final StarsCtrlHistPeriod PASTWEEK = new StarsCtrlHistPeriod(PASTWEEK_TYPE, "PastWeek");

    /**
     * The PastMonth type
    **/
    public static final int PASTMONTH_TYPE = 2;

    /**
     * The instance of the PastMonth type
    **/
    public static final StarsCtrlHistPeriod PASTMONTH = new StarsCtrlHistPeriod(PASTMONTH_TYPE, "PastMonth");

    /**
     * The PastSeason type
    **/
    public static final int PASTSEASON_TYPE = 3;

    /**
     * The instance of the PastSeason type
    **/
    public static final StarsCtrlHistPeriod PASTSEASON = new StarsCtrlHistPeriod(PASTSEASON_TYPE, "PastSeason");

    /**
     * The PastYear type
    **/
    public static final int PASTYEAR_TYPE = 4;

    /**
     * The instance of the PastYear type
    **/
    public static final StarsCtrlHistPeriod PASTYEAR = new StarsCtrlHistPeriod(PASTYEAR_TYPE, "PastYear");

    /**
     * The All type
    **/
    public static final int ALL_TYPE = 5;

    /**
     * The instance of the All type
    **/
    public static final StarsCtrlHistPeriod ALL = new StarsCtrlHistPeriod(ALL_TYPE, "All");

    /**
     * The None type
    **/
    public static final int NONE_TYPE = 6;

    /**
     * The instance of the None type
    **/
    public static final StarsCtrlHistPeriod NONE = new StarsCtrlHistPeriod(NONE_TYPE, "None");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StarsCtrlHistPeriod(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StarsCtrlHistPeriod
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StarsCtrlHistPeriod
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
        members.put("PastDay", PASTDAY);
        members.put("PastWeek", PASTWEEK);
        members.put("PastMonth", PASTMONTH);
        members.put("PastSeason", PASTSEASON);
        members.put("PastYear", PASTYEAR);
        members.put("All", ALL);
        members.put("None", NONE);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StarsCtrlHistPeriod
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StarsCtrlHistPeriod based on the given String
     * value.
     * 
     * @param string
    **/
    public static com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StarsCtrlHistPeriod";
            throw new IllegalArgumentException(err);
        }
        return (StarsCtrlHistPeriod) obj;
    } //-- com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod valueOf(java.lang.String) 

}
