/**
 * WdgCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class WdgCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WdgCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Y-Y";
    public static final java.lang.String _value2 = "D-Y";
    public static final java.lang.String _value3 = "Grounded Y-D";
    public static final java.lang.String _value4 = "Ungrounded Y-D";
    public static final java.lang.String _value5 = "Open Y-D";
    public static final java.lang.String _value6 = "D-D";
    public static final java.lang.String _value7 = "Y-Y Grounded Imp";
    public static final java.lang.String _value8 = "Y-Y 3ph core type";
    public static final java.lang.String _value9 = "D-D one";
    public static final java.lang.String _value10 = "D-D open";
    public static final java.lang.String _value11 = "Y-Y-D Ground";
    public static final java.lang.String _value12 = "Y-D one";
    public static final java.lang.String _value13 = "D-Y open";
    public static final WdgCode value1 = new WdgCode(_value1);
    public static final WdgCode value2 = new WdgCode(_value2);
    public static final WdgCode value3 = new WdgCode(_value3);
    public static final WdgCode value4 = new WdgCode(_value4);
    public static final WdgCode value5 = new WdgCode(_value5);
    public static final WdgCode value6 = new WdgCode(_value6);
    public static final WdgCode value7 = new WdgCode(_value7);
    public static final WdgCode value8 = new WdgCode(_value8);
    public static final WdgCode value9 = new WdgCode(_value9);
    public static final WdgCode value10 = new WdgCode(_value10);
    public static final WdgCode value11 = new WdgCode(_value11);
    public static final WdgCode value12 = new WdgCode(_value12);
    public static final WdgCode value13 = new WdgCode(_value13);
    public java.lang.String getValue() { return _value_;}
    public static WdgCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WdgCode enumeration = (WdgCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WdgCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WdgCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
