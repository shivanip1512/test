/**
 * GuyGuyAgainst.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GuyGuyAgainst implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected GuyGuyAgainst(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Dead-end";
    public static final java.lang.String _value2 = "Angle";
    public static final java.lang.String _value3 = "Storm-right";
    public static final java.lang.String _value4 = "Storm-left";
    public static final java.lang.String _value5 = "Unknown";
    public static final java.lang.String _value6 = "Other";
    public static final GuyGuyAgainst value1 = new GuyGuyAgainst(_value1);
    public static final GuyGuyAgainst value2 = new GuyGuyAgainst(_value2);
    public static final GuyGuyAgainst value3 = new GuyGuyAgainst(_value3);
    public static final GuyGuyAgainst value4 = new GuyGuyAgainst(_value4);
    public static final GuyGuyAgainst value5 = new GuyGuyAgainst(_value5);
    public static final GuyGuyAgainst value6 = new GuyGuyAgainst(_value6);
    public java.lang.String getValue() { return _value_;}
    public static GuyGuyAgainst fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        GuyGuyAgainst enumeration = (GuyGuyAgainst)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static GuyGuyAgainst fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(GuyGuyAgainst.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyAgainst"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
