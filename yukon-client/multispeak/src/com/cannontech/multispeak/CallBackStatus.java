/**
 * CallBackStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CallBackStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CallBackStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _On = "On";
    public static final java.lang.String _Off = "Off";
    public static final java.lang.String _StillOff = "StillOff";
    public static final java.lang.String _Undialable = "Undialable";
    public static final java.lang.String _MaxDial = "MaxDial";
    public static final java.lang.String _NoResponse = "NoResponse";
    public static final CallBackStatus On = new CallBackStatus(_On);
    public static final CallBackStatus Off = new CallBackStatus(_Off);
    public static final CallBackStatus StillOff = new CallBackStatus(_StillOff);
    public static final CallBackStatus Undialable = new CallBackStatus(_Undialable);
    public static final CallBackStatus MaxDial = new CallBackStatus(_MaxDial);
    public static final CallBackStatus NoResponse = new CallBackStatus(_NoResponse);
    public java.lang.String getValue() { return _value_;}
    public static CallBackStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        CallBackStatus enumeration = (CallBackStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static CallBackStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(CallBackStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
