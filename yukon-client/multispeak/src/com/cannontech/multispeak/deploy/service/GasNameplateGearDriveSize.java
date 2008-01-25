/**
 * GasNameplateGearDriveSize.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasNameplateGearDriveSize implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected GasNameplateGearDriveSize(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "0";
    public static final java.lang.String _value2 = "1";
    public static final java.lang.String _value3 = "2";
    public static final java.lang.String _value4 = "3";
    public static final java.lang.String _value5 = "4";
    public static final java.lang.String _value6 = "5";
    public static final java.lang.String _value7 = "6";
    public static final java.lang.String _value8 = "7";
    public static final java.lang.String _value9 = "";
    public static final GasNameplateGearDriveSize value1 = new GasNameplateGearDriveSize(_value1);
    public static final GasNameplateGearDriveSize value2 = new GasNameplateGearDriveSize(_value2);
    public static final GasNameplateGearDriveSize value3 = new GasNameplateGearDriveSize(_value3);
    public static final GasNameplateGearDriveSize value4 = new GasNameplateGearDriveSize(_value4);
    public static final GasNameplateGearDriveSize value5 = new GasNameplateGearDriveSize(_value5);
    public static final GasNameplateGearDriveSize value6 = new GasNameplateGearDriveSize(_value6);
    public static final GasNameplateGearDriveSize value7 = new GasNameplateGearDriveSize(_value7);
    public static final GasNameplateGearDriveSize value8 = new GasNameplateGearDriveSize(_value8);
    public static final GasNameplateGearDriveSize value9 = new GasNameplateGearDriveSize(_value9);
    public java.lang.String getValue() { return _value_;}
    public static GasNameplateGearDriveSize fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        GasNameplateGearDriveSize enumeration = (GasNameplateGearDriveSize)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static GasNameplateGearDriveSize fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(GasNameplateGearDriveSize.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gearDriveSize"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
