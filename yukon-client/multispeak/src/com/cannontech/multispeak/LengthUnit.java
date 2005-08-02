/**
 * LengthUnit.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LengthUnit implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected LengthUnit(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Feet";
    public static final java.lang.String _value2 = "Meters";
    public static final java.lang.String _value3 = "Thousand Feet";
    public static final java.lang.String _value4 = "Thousand Meters";
    public static final java.lang.String _value5 = "Miles";
    public static final java.lang.String _value6 = "Inches";
    public static final LengthUnit value1 = new LengthUnit(_value1);
    public static final LengthUnit value2 = new LengthUnit(_value2);
    public static final LengthUnit value3 = new LengthUnit(_value3);
    public static final LengthUnit value4 = new LengthUnit(_value4);
    public static final LengthUnit value5 = new LengthUnit(_value5);
    public static final LengthUnit value6 = new LengthUnit(_value6);
    public java.lang.String getValue() { return _value_;}
    public static LengthUnit fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        LengthUnit enumeration = (LengthUnit)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static LengthUnit fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(LengthUnit.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lengthUnit"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
