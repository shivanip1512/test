/**
 * SupplyVoltage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class SupplyVoltage implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SupplyVoltage(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "SupplyVoltage 69.3";
    public static final java.lang.String _value2 = "SupplyVoltage 72";
    public static final java.lang.String _value3 = "SupplyVoltage 120";
    public static final java.lang.String _value4 = "SupplyVoltage 208";
    public static final java.lang.String _value5 = "SupplyVoltage 240";
    public static final java.lang.String _value6 = "SupplyVoltage 277";
    public static final java.lang.String _value7 = "SupplyVoltage 347";
    public static final java.lang.String _value8 = "SupplyVoltage 480";
    public static final java.lang.String _value9 = "SupplyVoltage 600";
    public static final java.lang.String _value10 = "SupplyVoltage120 through 277";
    public static final java.lang.String _value11 = "SupplyVoltage120 through 480";
    public static final java.lang.String _value12 = "SupplyVoltage48 DC";
    public static final java.lang.String _value13 = "SupplyVoltage125 DC";
    public static final java.lang.String _value14 = "SupplyVoltage250 DC";
    public static final SupplyVoltage value1 = new SupplyVoltage(_value1);
    public static final SupplyVoltage value2 = new SupplyVoltage(_value2);
    public static final SupplyVoltage value3 = new SupplyVoltage(_value3);
    public static final SupplyVoltage value4 = new SupplyVoltage(_value4);
    public static final SupplyVoltage value5 = new SupplyVoltage(_value5);
    public static final SupplyVoltage value6 = new SupplyVoltage(_value6);
    public static final SupplyVoltage value7 = new SupplyVoltage(_value7);
    public static final SupplyVoltage value8 = new SupplyVoltage(_value8);
    public static final SupplyVoltage value9 = new SupplyVoltage(_value9);
    public static final SupplyVoltage value10 = new SupplyVoltage(_value10);
    public static final SupplyVoltage value11 = new SupplyVoltage(_value11);
    public static final SupplyVoltage value12 = new SupplyVoltage(_value12);
    public static final SupplyVoltage value13 = new SupplyVoltage(_value13);
    public static final SupplyVoltage value14 = new SupplyVoltage(_value14);
    public java.lang.String getValue() { return _value_;}
    public static SupplyVoltage fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SupplyVoltage enumeration = (SupplyVoltage)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SupplyVoltage fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(SupplyVoltage.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supplyVoltage"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
