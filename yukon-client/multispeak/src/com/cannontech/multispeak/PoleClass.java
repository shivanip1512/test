/**
 * PoleClass.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PoleClass implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PoleClass(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Class 1";
    public static final java.lang.String _value2 = "Class 2";
    public static final java.lang.String _value3 = "Class 3";
    public static final java.lang.String _value4 = "Class 4";
    public static final java.lang.String _value5 = "Class 5";
    public static final java.lang.String _value6 = "Class 6";
    public static final java.lang.String _value7 = "Class 7";
    public static final java.lang.String _value8 = "Class 8";
    public static final java.lang.String _value9 = "Class 9";
    public static final java.lang.String _value10 = "Class 10";
    public static final java.lang.String _value11 = "H1";
    public static final java.lang.String _value12 = "H2";
    public static final java.lang.String _value13 = "H3";
    public static final java.lang.String _value14 = "H4";
    public static final java.lang.String _value15 = "H5";
    public static final java.lang.String _value16 = "H6";
    public static final java.lang.String _value17 = "H7";
    public static final java.lang.String _value18 = "Other";
    public static final java.lang.String _value19 = "Unknown";
    public static final PoleClass value1 = new PoleClass(_value1);
    public static final PoleClass value2 = new PoleClass(_value2);
    public static final PoleClass value3 = new PoleClass(_value3);
    public static final PoleClass value4 = new PoleClass(_value4);
    public static final PoleClass value5 = new PoleClass(_value5);
    public static final PoleClass value6 = new PoleClass(_value6);
    public static final PoleClass value7 = new PoleClass(_value7);
    public static final PoleClass value8 = new PoleClass(_value8);
    public static final PoleClass value9 = new PoleClass(_value9);
    public static final PoleClass value10 = new PoleClass(_value10);
    public static final PoleClass value11 = new PoleClass(_value11);
    public static final PoleClass value12 = new PoleClass(_value12);
    public static final PoleClass value13 = new PoleClass(_value13);
    public static final PoleClass value14 = new PoleClass(_value14);
    public static final PoleClass value15 = new PoleClass(_value15);
    public static final PoleClass value16 = new PoleClass(_value16);
    public static final PoleClass value17 = new PoleClass(_value17);
    public static final PoleClass value18 = new PoleClass(_value18);
    public static final PoleClass value19 = new PoleClass(_value19);
    public java.lang.String getValue() { return _value_;}
    public static PoleClass fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PoleClass enumeration = (PoleClass)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PoleClass fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PoleClass.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
