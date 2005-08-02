/**
 * PoleUse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PoleUse implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PoleUse(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Distribution";
    public static final java.lang.String _value2 = "Transmission";
    public static final java.lang.String _value3 = "Service";
    public static final java.lang.String _value4 = "Guy Stub";
    public static final java.lang.String _value5 = "Stub";
    public static final java.lang.String _value6 = "Other";
    public static final java.lang.String _value7 = "Unknown";
    public static final PoleUse value1 = new PoleUse(_value1);
    public static final PoleUse value2 = new PoleUse(_value2);
    public static final PoleUse value3 = new PoleUse(_value3);
    public static final PoleUse value4 = new PoleUse(_value4);
    public static final PoleUse value5 = new PoleUse(_value5);
    public static final PoleUse value6 = new PoleUse(_value6);
    public static final PoleUse value7 = new PoleUse(_value7);
    public java.lang.String getValue() { return _value_;}
    public static PoleUse fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PoleUse enumeration = (PoleUse)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PoleUse fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PoleUse.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
