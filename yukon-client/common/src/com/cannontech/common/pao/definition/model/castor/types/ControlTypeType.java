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
 * Class ControlTypeType.
 * 
 * @version $Revision$ $Date$
 */
public class ControlTypeType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The NONE type
     */
    public static final int NONE_TYPE = 0;

    /**
     * The instance of the NONE type
     */
    public static final ControlTypeType NONE = new ControlTypeType(NONE_TYPE, "NONE");

    /**
     * The NORMAL type
     */
    public static final int NORMAL_TYPE = 1;

    /**
     * The instance of the NORMAL type
     */
    public static final ControlTypeType NORMAL = new ControlTypeType(NORMAL_TYPE, "NORMAL");

    /**
     * The LATCH type
     */
    public static final int LATCH_TYPE = 2;

    /**
     * The instance of the LATCH type
     */
    public static final ControlTypeType LATCH = new ControlTypeType(LATCH_TYPE, "LATCH");

    /**
     * The PSEUDO type
     */
    public static final int PSEUDO_TYPE = 3;

    /**
     * The instance of the PSEUDO type
     */
    public static final ControlTypeType PSEUDO = new ControlTypeType(PSEUDO_TYPE, "PSEUDO");

    /**
     * The SBOLATCH type
     */
    public static final int SBOLATCH_TYPE = 4;

    /**
     * The instance of the SBOLATCH type
     */
    public static final ControlTypeType SBOLATCH = new ControlTypeType(SBOLATCH_TYPE, "SBOLATCH");

    /**
     * The SBOPULSE type
     */
    public static final int SBOPULSE_TYPE = 5;

    /**
     * The instance of the SBOPULSE type
     */
    public static final ControlTypeType SBOPULSE = new ControlTypeType(SBOPULSE_TYPE, "SBOPULSE");

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

    private ControlTypeType(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.cannontech.common.pao.definition.model.castor.types.ControlTypeType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * ControlTypeType
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
     * Returns the type of this ControlTypeType
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
        members.put("NONE", NONE);
        members.put("NORMAL", NORMAL);
        members.put("LATCH", LATCH);
        members.put("PSEUDO", PSEUDO);
        members.put("SBOLATCH", SBOLATCH);
        members.put("SBOPULSE", SBOPULSE);
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
     * Returns the String representation of this ControlTypeType
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
     * Returns a new ControlTypeType based on the given String
     * value.
     * 
     * @param string
     * @return ControlTypeType
     */
    public static com.cannontech.common.pao.definition.model.castor.types.ControlTypeType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid ControlTypeType";
            throw new IllegalArgumentException(err);
        }
        return (ControlTypeType) obj;
    } //-- com.cannontech.common.pao.definition.model.castor.types.ControlTypeType valueOf(java.lang.String) 

}
