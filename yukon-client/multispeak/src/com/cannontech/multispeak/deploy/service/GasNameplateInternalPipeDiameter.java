/**
 * GasNameplateInternalPipeDiameter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasNameplateInternalPipeDiameter implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected GasNameplateInternalPipeDiameter(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "0";
    public static final java.lang.String _value2 = "1";
    public static final java.lang.String _value3 = "2";
    public static final java.lang.String _value4 = "3";
    public static final java.lang.String _value5 = "4";
    public static final java.lang.String _value6 = "5";
    public static final java.lang.String _value7 = "6";
    public static final java.lang.String _value8 = "7";
    public static final java.lang.String _value9 = "8";
    public static final java.lang.String _value10 = "9";
    public static final java.lang.String _value11 = "10";
    public static final java.lang.String _value12 = "11";
    public static final java.lang.String _value13 = "12";
    public static final java.lang.String _value14 = "13";
    public static final java.lang.String _value15 = "14";
    public static final java.lang.String _value16 = "15";
    public static final java.lang.String _value17 = "16";
    public static final java.lang.String _value18 = "17";
    public static final java.lang.String _value19 = "18";
    public static final java.lang.String _value20 = "19";
    public static final java.lang.String _value21 = "20";
    public static final java.lang.String _value22 = "21";
    public static final java.lang.String _value23 = "22";
    public static final java.lang.String _value24 = "23";
    public static final java.lang.String _value25 = "";
    public static final GasNameplateInternalPipeDiameter value1 = new GasNameplateInternalPipeDiameter(_value1);
    public static final GasNameplateInternalPipeDiameter value2 = new GasNameplateInternalPipeDiameter(_value2);
    public static final GasNameplateInternalPipeDiameter value3 = new GasNameplateInternalPipeDiameter(_value3);
    public static final GasNameplateInternalPipeDiameter value4 = new GasNameplateInternalPipeDiameter(_value4);
    public static final GasNameplateInternalPipeDiameter value5 = new GasNameplateInternalPipeDiameter(_value5);
    public static final GasNameplateInternalPipeDiameter value6 = new GasNameplateInternalPipeDiameter(_value6);
    public static final GasNameplateInternalPipeDiameter value7 = new GasNameplateInternalPipeDiameter(_value7);
    public static final GasNameplateInternalPipeDiameter value8 = new GasNameplateInternalPipeDiameter(_value8);
    public static final GasNameplateInternalPipeDiameter value9 = new GasNameplateInternalPipeDiameter(_value9);
    public static final GasNameplateInternalPipeDiameter value10 = new GasNameplateInternalPipeDiameter(_value10);
    public static final GasNameplateInternalPipeDiameter value11 = new GasNameplateInternalPipeDiameter(_value11);
    public static final GasNameplateInternalPipeDiameter value12 = new GasNameplateInternalPipeDiameter(_value12);
    public static final GasNameplateInternalPipeDiameter value13 = new GasNameplateInternalPipeDiameter(_value13);
    public static final GasNameplateInternalPipeDiameter value14 = new GasNameplateInternalPipeDiameter(_value14);
    public static final GasNameplateInternalPipeDiameter value15 = new GasNameplateInternalPipeDiameter(_value15);
    public static final GasNameplateInternalPipeDiameter value16 = new GasNameplateInternalPipeDiameter(_value16);
    public static final GasNameplateInternalPipeDiameter value17 = new GasNameplateInternalPipeDiameter(_value17);
    public static final GasNameplateInternalPipeDiameter value18 = new GasNameplateInternalPipeDiameter(_value18);
    public static final GasNameplateInternalPipeDiameter value19 = new GasNameplateInternalPipeDiameter(_value19);
    public static final GasNameplateInternalPipeDiameter value20 = new GasNameplateInternalPipeDiameter(_value20);
    public static final GasNameplateInternalPipeDiameter value21 = new GasNameplateInternalPipeDiameter(_value21);
    public static final GasNameplateInternalPipeDiameter value22 = new GasNameplateInternalPipeDiameter(_value22);
    public static final GasNameplateInternalPipeDiameter value23 = new GasNameplateInternalPipeDiameter(_value23);
    public static final GasNameplateInternalPipeDiameter value24 = new GasNameplateInternalPipeDiameter(_value24);
    public static final GasNameplateInternalPipeDiameter value25 = new GasNameplateInternalPipeDiameter(_value25);
    public java.lang.String getValue() { return _value_;}
    public static GasNameplateInternalPipeDiameter fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        GasNameplateInternalPipeDiameter enumeration = (GasNameplateInternalPipeDiameter)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static GasNameplateInternalPipeDiameter fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(GasNameplateInternalPipeDiameter.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>internalPipeDiameter"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
