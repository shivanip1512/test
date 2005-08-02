/**
 * UgSecondaryLineUGSecType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class UgSecondaryLineUGSecType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected UgSecondaryLineUGSecType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Secondary = "Secondary";
    public static final java.lang.String _Service = "Service";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final UgSecondaryLineUGSecType Secondary = new UgSecondaryLineUGSecType(_Secondary);
    public static final UgSecondaryLineUGSecType Service = new UgSecondaryLineUGSecType(_Service);
    public static final UgSecondaryLineUGSecType Other = new UgSecondaryLineUGSecType(_Other);
    public static final UgSecondaryLineUGSecType Unknown = new UgSecondaryLineUGSecType(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static UgSecondaryLineUGSecType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        UgSecondaryLineUGSecType enumeration = (UgSecondaryLineUGSecType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static UgSecondaryLineUGSecType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(UgSecondaryLineUGSecType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugSecondaryLineUGSecType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
