/**
 * BaseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class BaseType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected BaseType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Base Type 1";
    public static final java.lang.String _value2 = "S-base";
    public static final java.lang.String _value3 = "A-Base";
    public static final java.lang.String _value4 = "B-Base";
    public static final java.lang.String _value5 = "K-Base";
    public static final java.lang.String _value6 = "P-Base";
    public static final java.lang.String _value7 = "IECBottomConnected";
    public static final java.lang.String _value8 = "switchboard";
    public static final java.lang.String _value9 = "rackmount";
    public static final BaseType value1 = new BaseType(_value1);
    public static final BaseType value2 = new BaseType(_value2);
    public static final BaseType value3 = new BaseType(_value3);
    public static final BaseType value4 = new BaseType(_value4);
    public static final BaseType value5 = new BaseType(_value5);
    public static final BaseType value6 = new BaseType(_value6);
    public static final BaseType value7 = new BaseType(_value7);
    public static final BaseType value8 = new BaseType(_value8);
    public static final BaseType value9 = new BaseType(_value9);
    public java.lang.String getValue() { return _value_;}
    public static BaseType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        BaseType enumeration = (BaseType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static BaseType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(BaseType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "baseType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
