/**
 * Accountability.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Accountability implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Accountability(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Q1";
    public static final java.lang.String _value2 = "Q2";
    public static final java.lang.String _value3 = "Q3";
    public static final java.lang.String _value4 = "Q4";
    public static final java.lang.String _value5 = "Q1-2";
    public static final java.lang.String _value6 = "Q1-3";
    public static final java.lang.String _value7 = "Q1-4";
    public static final java.lang.String _value8 = "Q2-3";
    public static final java.lang.String _value9 = "Q2-4";
    public static final java.lang.String _value10 = "Q3-4";
    public static final java.lang.String _value11 = "Q1-2-3";
    public static final java.lang.String _value12 = "Q1-2-4";
    public static final java.lang.String _value13 = "Q1-3-4";
    public static final java.lang.String _value14 = "Q2-3-4";
    public static final java.lang.String _value15 = "Q1-2-3-4";
    public static final java.lang.String _value16 = "Q1-2-netFlow";
    public static final java.lang.String _value17 = "Q1-3-netFlow";
    public static final java.lang.String _value18 = "Q1-4-netFlow";
    public static final java.lang.String _value19 = "Q2-3-netFlow";
    public static final java.lang.String _value20 = "Q2-4-netFlow";
    public static final java.lang.String _value21 = "Q3-4-netFlow";
    public static final java.lang.String _value22 = "Q1-2-3-netFlow";
    public static final java.lang.String _value23 = "Q1-2-4-netFlow";
    public static final java.lang.String _value24 = "Q1-3-4-netFlow";
    public static final java.lang.String _value25 = "Q2-3-4-netFlow";
    public static final java.lang.String _value26 = "Q1-2-3-4-netFlow";
    public static final Accountability value1 = new Accountability(_value1);
    public static final Accountability value2 = new Accountability(_value2);
    public static final Accountability value3 = new Accountability(_value3);
    public static final Accountability value4 = new Accountability(_value4);
    public static final Accountability value5 = new Accountability(_value5);
    public static final Accountability value6 = new Accountability(_value6);
    public static final Accountability value7 = new Accountability(_value7);
    public static final Accountability value8 = new Accountability(_value8);
    public static final Accountability value9 = new Accountability(_value9);
    public static final Accountability value10 = new Accountability(_value10);
    public static final Accountability value11 = new Accountability(_value11);
    public static final Accountability value12 = new Accountability(_value12);
    public static final Accountability value13 = new Accountability(_value13);
    public static final Accountability value14 = new Accountability(_value14);
    public static final Accountability value15 = new Accountability(_value15);
    public static final Accountability value16 = new Accountability(_value16);
    public static final Accountability value17 = new Accountability(_value17);
    public static final Accountability value18 = new Accountability(_value18);
    public static final Accountability value19 = new Accountability(_value19);
    public static final Accountability value20 = new Accountability(_value20);
    public static final Accountability value21 = new Accountability(_value21);
    public static final Accountability value22 = new Accountability(_value22);
    public static final Accountability value23 = new Accountability(_value23);
    public static final Accountability value24 = new Accountability(_value24);
    public static final Accountability value25 = new Accountability(_value25);
    public static final Accountability value26 = new Accountability(_value26);
    public java.lang.String getValue() { return _value_;}
    public static Accountability fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Accountability enumeration = (Accountability)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Accountability fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Accountability.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
