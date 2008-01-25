/**
 * ReadingValueReadingValueType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingValueReadingValueType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ReadingValueReadingValueType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Energy";
    public static final java.lang.String _value2 = "Negative Energy";
    public static final java.lang.String _value3 = "Current demand";
    public static final java.lang.String _value4 = "Max demand";
    public static final java.lang.String _value5 = "Min demand";
    public static final java.lang.String _value6 = "Current Voltage";
    public static final java.lang.String _value7 = "Max Voltage";
    public static final java.lang.String _value8 = "Min Voltage";
    public static final java.lang.String _value9 = "Current Voltage Phase A";
    public static final java.lang.String _value10 = "Current Voltage Phase B";
    public static final java.lang.String _value11 = "Current Voltage Phase C";
    public static final java.lang.String _value12 = "Max Voltage Phase A";
    public static final java.lang.String _value13 = "Max Voltage Phase B";
    public static final java.lang.String _value14 = "Max Voltage Phase C";
    public static final java.lang.String _value15 = "Min Voltage Phase A";
    public static final java.lang.String _value16 = "Min Voltage Phase B";
    public static final java.lang.String _value17 = "Min Voltage Phase C";
    public static final java.lang.String _value18 = "Current kVAr";
    public static final java.lang.String _value19 = "Max kVar";
    public static final java.lang.String _value20 = "Min kVar";
    public static final java.lang.String _value21 = "kVar at demand peak";
    public static final java.lang.String _value22 = "Current power factor";
    public static final java.lang.String _value23 = "Max power factor";
    public static final java.lang.String _value24 = "Min power factor";
    public static final java.lang.String _value25 = "Current frequency";
    public static final java.lang.String _value26 = "Max frequency";
    public static final java.lang.String _value27 = "Min frequency";
    public static final java.lang.String _value28 = "Momentary interruptions count";
    public static final java.lang.String _value29 = "Sustained interruptions count";
    public static final java.lang.String _value30 = "Sustained interruptions duration";
    public static final java.lang.String _value31 = "Other";
    public static final ReadingValueReadingValueType value1 = new ReadingValueReadingValueType(_value1);
    public static final ReadingValueReadingValueType value2 = new ReadingValueReadingValueType(_value2);
    public static final ReadingValueReadingValueType value3 = new ReadingValueReadingValueType(_value3);
    public static final ReadingValueReadingValueType value4 = new ReadingValueReadingValueType(_value4);
    public static final ReadingValueReadingValueType value5 = new ReadingValueReadingValueType(_value5);
    public static final ReadingValueReadingValueType value6 = new ReadingValueReadingValueType(_value6);
    public static final ReadingValueReadingValueType value7 = new ReadingValueReadingValueType(_value7);
    public static final ReadingValueReadingValueType value8 = new ReadingValueReadingValueType(_value8);
    public static final ReadingValueReadingValueType value9 = new ReadingValueReadingValueType(_value9);
    public static final ReadingValueReadingValueType value10 = new ReadingValueReadingValueType(_value10);
    public static final ReadingValueReadingValueType value11 = new ReadingValueReadingValueType(_value11);
    public static final ReadingValueReadingValueType value12 = new ReadingValueReadingValueType(_value12);
    public static final ReadingValueReadingValueType value13 = new ReadingValueReadingValueType(_value13);
    public static final ReadingValueReadingValueType value14 = new ReadingValueReadingValueType(_value14);
    public static final ReadingValueReadingValueType value15 = new ReadingValueReadingValueType(_value15);
    public static final ReadingValueReadingValueType value16 = new ReadingValueReadingValueType(_value16);
    public static final ReadingValueReadingValueType value17 = new ReadingValueReadingValueType(_value17);
    public static final ReadingValueReadingValueType value18 = new ReadingValueReadingValueType(_value18);
    public static final ReadingValueReadingValueType value19 = new ReadingValueReadingValueType(_value19);
    public static final ReadingValueReadingValueType value20 = new ReadingValueReadingValueType(_value20);
    public static final ReadingValueReadingValueType value21 = new ReadingValueReadingValueType(_value21);
    public static final ReadingValueReadingValueType value22 = new ReadingValueReadingValueType(_value22);
    public static final ReadingValueReadingValueType value23 = new ReadingValueReadingValueType(_value23);
    public static final ReadingValueReadingValueType value24 = new ReadingValueReadingValueType(_value24);
    public static final ReadingValueReadingValueType value25 = new ReadingValueReadingValueType(_value25);
    public static final ReadingValueReadingValueType value26 = new ReadingValueReadingValueType(_value26);
    public static final ReadingValueReadingValueType value27 = new ReadingValueReadingValueType(_value27);
    public static final ReadingValueReadingValueType value28 = new ReadingValueReadingValueType(_value28);
    public static final ReadingValueReadingValueType value29 = new ReadingValueReadingValueType(_value29);
    public static final ReadingValueReadingValueType value30 = new ReadingValueReadingValueType(_value30);
    public static final ReadingValueReadingValueType value31 = new ReadingValueReadingValueType(_value31);
    public java.lang.String getValue() { return _value_;}
    public static ReadingValueReadingValueType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ReadingValueReadingValueType enumeration = (ReadingValueReadingValueType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ReadingValueReadingValueType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ReadingValueReadingValueType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingValue>readingValueType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
