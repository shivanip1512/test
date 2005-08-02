/**
 * UnitPrefix.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class UnitPrefix implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected UnitPrefix(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Maximum = "Maximum";
    public static final java.lang.String _Minimum = "Minimum";
    public static final java.lang.String _Average = "Average";
    public static final java.lang.String _Instantaneous = "Instantaneous";
    public static final java.lang.String _Cumulative = "Cumulative";
    public static final java.lang.String _Quantity = "Quantity";
    public static final UnitPrefix Maximum = new UnitPrefix(_Maximum);
    public static final UnitPrefix Minimum = new UnitPrefix(_Minimum);
    public static final UnitPrefix Average = new UnitPrefix(_Average);
    public static final UnitPrefix Instantaneous = new UnitPrefix(_Instantaneous);
    public static final UnitPrefix Cumulative = new UnitPrefix(_Cumulative);
    public static final UnitPrefix Quantity = new UnitPrefix(_Quantity);
    public java.lang.String getValue() { return _value_;}
    public static UnitPrefix fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        UnitPrefix enumeration = (UnitPrefix)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static UnitPrefix fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(UnitPrefix.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
