/**
 * ElementsVoltage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ElementsVoltage implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ElementsVoltage(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "ElementVoltage 69.3";
    public static final java.lang.String _value2 = "ElementVoltage 72";
    public static final java.lang.String _value3 = "ElementVoltage 120";
    public static final java.lang.String _value4 = "ElementVoltage 208";
    public static final java.lang.String _value5 = "ElementVoltage 240";
    public static final java.lang.String _value6 = "ElementVoltage 277";
    public static final java.lang.String _value7 = "ElementVoltage 347";
    public static final java.lang.String _value8 = "ElementVoltage 480";
    public static final java.lang.String _value9 = "ElementVoltage 600";
    public static final java.lang.String _value10 = "ElementVoltge120-277";
    public static final java.lang.String _value11 = "ElementVoltage120-240";
    public static final ElementsVoltage value1 = new ElementsVoltage(_value1);
    public static final ElementsVoltage value2 = new ElementsVoltage(_value2);
    public static final ElementsVoltage value3 = new ElementsVoltage(_value3);
    public static final ElementsVoltage value4 = new ElementsVoltage(_value4);
    public static final ElementsVoltage value5 = new ElementsVoltage(_value5);
    public static final ElementsVoltage value6 = new ElementsVoltage(_value6);
    public static final ElementsVoltage value7 = new ElementsVoltage(_value7);
    public static final ElementsVoltage value8 = new ElementsVoltage(_value8);
    public static final ElementsVoltage value9 = new ElementsVoltage(_value9);
    public static final ElementsVoltage value10 = new ElementsVoltage(_value10);
    public static final ElementsVoltage value11 = new ElementsVoltage(_value11);
    public java.lang.String getValue() { return _value_;}
    public static ElementsVoltage fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ElementsVoltage enumeration = (ElementsVoltage)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ElementsVoltage fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ElementsVoltage.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementsVoltage"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
