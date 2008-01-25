/**
 * WaterNameplateInstallType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WaterNameplateInstallType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WaterNameplateInstallType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "0";
    public static final java.lang.String _value2 = "1";
    public static final java.lang.String _value3 = "2";
    public static final java.lang.String _value4 = "3";
    public static final java.lang.String _value5 = "";
    public static final WaterNameplateInstallType value1 = new WaterNameplateInstallType(_value1);
    public static final WaterNameplateInstallType value2 = new WaterNameplateInstallType(_value2);
    public static final WaterNameplateInstallType value3 = new WaterNameplateInstallType(_value3);
    public static final WaterNameplateInstallType value4 = new WaterNameplateInstallType(_value4);
    public static final WaterNameplateInstallType value5 = new WaterNameplateInstallType(_value5);
    public java.lang.String getValue() { return _value_;}
    public static WaterNameplateInstallType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WaterNameplateInstallType enumeration = (WaterNameplateInstallType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WaterNameplateInstallType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WaterNameplateInstallType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>installType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
