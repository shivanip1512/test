/**
 * PhoneNumberPhoneType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PhoneNumberPhoneType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PhoneNumberPhoneType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Home";
    public static final java.lang.String _value2 = "Business";
    public static final java.lang.String _value3 = "Mobile";
    public static final java.lang.String _value4 = "Business mobile";
    public static final java.lang.String _value5 = "Pager";
    public static final java.lang.String _value6 = "Fax";
    public static final java.lang.String _value7 = "Other";
    public static final PhoneNumberPhoneType value1 = new PhoneNumberPhoneType(_value1);
    public static final PhoneNumberPhoneType value2 = new PhoneNumberPhoneType(_value2);
    public static final PhoneNumberPhoneType value3 = new PhoneNumberPhoneType(_value3);
    public static final PhoneNumberPhoneType value4 = new PhoneNumberPhoneType(_value4);
    public static final PhoneNumberPhoneType value5 = new PhoneNumberPhoneType(_value5);
    public static final PhoneNumberPhoneType value6 = new PhoneNumberPhoneType(_value6);
    public static final PhoneNumberPhoneType value7 = new PhoneNumberPhoneType(_value7);
    public java.lang.String getValue() { return _value_;}
    public static PhoneNumberPhoneType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PhoneNumberPhoneType enumeration = (PhoneNumberPhoneType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PhoneNumberPhoneType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PhoneNumberPhoneType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">phoneNumber>phoneType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
