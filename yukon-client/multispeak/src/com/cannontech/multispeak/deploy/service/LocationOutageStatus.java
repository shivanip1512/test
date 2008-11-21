/**
 * LocationOutageStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LocationOutageStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected LocationOutageStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Assumed = "Assumed";
    public static final java.lang.String _Confirmed = "Confirmed";
    public static final java.lang.String _Restored = "Restored";
    public static final java.lang.String _Scheduled = "Scheduled";
    public static final java.lang.String _StillOut = "StillOut";
    public static final java.lang.String _NonPay = "NonPay";
    public static final java.lang.String _NoOutage = "NoOutage";
    public static final java.lang.String _Unknown = "Unknown";
    public static final java.lang.String _Other = "Other";
    public static final LocationOutageStatus Assumed = new LocationOutageStatus(_Assumed);
    public static final LocationOutageStatus Confirmed = new LocationOutageStatus(_Confirmed);
    public static final LocationOutageStatus Restored = new LocationOutageStatus(_Restored);
    public static final LocationOutageStatus Scheduled = new LocationOutageStatus(_Scheduled);
    public static final LocationOutageStatus StillOut = new LocationOutageStatus(_StillOut);
    public static final LocationOutageStatus NonPay = new LocationOutageStatus(_NonPay);
    public static final LocationOutageStatus NoOutage = new LocationOutageStatus(_NoOutage);
    public static final LocationOutageStatus Unknown = new LocationOutageStatus(_Unknown);
    public static final LocationOutageStatus Other = new LocationOutageStatus(_Other);
    public java.lang.String getValue() { return _value_;}
    public static LocationOutageStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        LocationOutageStatus enumeration = (LocationOutageStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static LocationOutageStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(LocationOutageStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "locationOutageStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
