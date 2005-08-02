/**
 * MaterialUnits.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MaterialUnits implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected MaterialUnits(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Feet = "Feet";
    public static final java.lang.String _Meters = "Meters";
    public static final java.lang.String _Inches = "Inches";
    public static final java.lang.String _Pounds = "Pounds";
    public static final java.lang.String _Kilograms = "Kilograms";
    public static final java.lang.String _Miles = "Miles";
    public static final java.lang.String _ThousandFeet = "ThousandFeet";
    public static final java.lang.String _Pair = "Pair";
    public static final java.lang.String _Each = "Each";
    public static final java.lang.String _Hour = "Hour";
    public static final java.lang.String _Other = "Other";
    public static final MaterialUnits Feet = new MaterialUnits(_Feet);
    public static final MaterialUnits Meters = new MaterialUnits(_Meters);
    public static final MaterialUnits Inches = new MaterialUnits(_Inches);
    public static final MaterialUnits Pounds = new MaterialUnits(_Pounds);
    public static final MaterialUnits Kilograms = new MaterialUnits(_Kilograms);
    public static final MaterialUnits Miles = new MaterialUnits(_Miles);
    public static final MaterialUnits ThousandFeet = new MaterialUnits(_ThousandFeet);
    public static final MaterialUnits Pair = new MaterialUnits(_Pair);
    public static final MaterialUnits Each = new MaterialUnits(_Each);
    public static final MaterialUnits Hour = new MaterialUnits(_Hour);
    public static final MaterialUnits Other = new MaterialUnits(_Other);
    public java.lang.String getValue() { return _value_;}
    public static MaterialUnits fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        MaterialUnits enumeration = (MaterialUnits)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static MaterialUnits fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(MaterialUnits.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "materialUnits"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
