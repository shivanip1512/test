/**
 * NumberType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class NumberType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected NumberType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Work order";
    public static final java.lang.String _value2 = "Service order";
    public static final java.lang.String _value3 = "Maintenance order";
    public static final java.lang.String _value4 = "Work task";
    public static final java.lang.String _value5 = "Outage ticket";
    public static final java.lang.String _value6 = "Other";
    public static final java.lang.String _value7 = "Unknown";
    public static final NumberType value1 = new NumberType(_value1);
    public static final NumberType value2 = new NumberType(_value2);
    public static final NumberType value3 = new NumberType(_value3);
    public static final NumberType value4 = new NumberType(_value4);
    public static final NumberType value5 = new NumberType(_value5);
    public static final NumberType value6 = new NumberType(_value6);
    public static final NumberType value7 = new NumberType(_value7);
    public java.lang.String getValue() { return _value_;}
    public static NumberType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        NumberType enumeration = (NumberType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static NumberType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(NumberType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
