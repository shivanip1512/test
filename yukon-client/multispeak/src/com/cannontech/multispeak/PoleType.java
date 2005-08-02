/**
 * PoleType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PoleType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PoleType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Aluminum = "Aluminum";
    public static final java.lang.String _Concrete = "Concrete";
    public static final java.lang.String _Fiberglass = "Fiberglass";
    public static final java.lang.String _Galvanized = "Galvanized";
    public static final java.lang.String _Steel = "Steel";
    public static final java.lang.String _Wood = "Wood";
    public static final java.lang.String _WoodTreated = "WoodTreated";
    public static final java.lang.String _WoodUntreated = "WoodUntreated";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final PoleType Aluminum = new PoleType(_Aluminum);
    public static final PoleType Concrete = new PoleType(_Concrete);
    public static final PoleType Fiberglass = new PoleType(_Fiberglass);
    public static final PoleType Galvanized = new PoleType(_Galvanized);
    public static final PoleType Steel = new PoleType(_Steel);
    public static final PoleType Wood = new PoleType(_Wood);
    public static final PoleType WoodTreated = new PoleType(_WoodTreated);
    public static final PoleType WoodUntreated = new PoleType(_WoodUntreated);
    public static final PoleType Other = new PoleType(_Other);
    public static final PoleType Unknown = new PoleType(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static PoleType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PoleType enumeration = (PoleType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PoleType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PoleType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
