/**
 * CallType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class CallType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CallType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Call = "Call";
    public static final java.lang.String _CustomerServiceRep = "CustomerServiceRep";
    public static final java.lang.String _InboundTelephoneDevice = "InboundTelephoneDevice";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _manual = "manual";
    public static final java.lang.String _IVR = "IVR";
    public static final java.lang.String _sensor = "sensor";
    public static final CallType Call = new CallType(_Call);
    public static final CallType CustomerServiceRep = new CallType(_CustomerServiceRep);
    public static final CallType InboundTelephoneDevice = new CallType(_InboundTelephoneDevice);
    public static final CallType Other = new CallType(_Other);
    public static final CallType manual = new CallType(_manual);
    public static final CallType IVR = new CallType(_IVR);
    public static final CallType sensor = new CallType(_sensor);
    public java.lang.String getValue() { return _value_;}
    public static CallType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        CallType enumeration = (CallType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static CallType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(CallType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
