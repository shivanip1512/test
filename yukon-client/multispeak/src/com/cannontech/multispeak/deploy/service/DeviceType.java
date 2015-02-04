/**
 * DeviceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class DeviceType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected DeviceType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Overhead = "Overhead";
    public static final java.lang.String _Underground = "Underground";
    public static final java.lang.String _ZsmConductor = "ZsmConductor";
    public static final java.lang.String _ZabcConductor = "ZabcConductor";
    public static final java.lang.String _Transformer = "Transformer";
    public static final java.lang.String _Regulator = "Regulator";
    public static final java.lang.String _Device = "Device";
    public static final java.lang.String _Other = "Other";
    public static final DeviceType Overhead = new DeviceType(_Overhead);
    public static final DeviceType Underground = new DeviceType(_Underground);
    public static final DeviceType ZsmConductor = new DeviceType(_ZsmConductor);
    public static final DeviceType ZabcConductor = new DeviceType(_ZabcConductor);
    public static final DeviceType Transformer = new DeviceType(_Transformer);
    public static final DeviceType Regulator = new DeviceType(_Regulator);
    public static final DeviceType Device = new DeviceType(_Device);
    public static final DeviceType Other = new DeviceType(_Other);
    public java.lang.String getValue() { return _value_;}
    public static DeviceType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        DeviceType enumeration = (DeviceType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static DeviceType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(DeviceType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
