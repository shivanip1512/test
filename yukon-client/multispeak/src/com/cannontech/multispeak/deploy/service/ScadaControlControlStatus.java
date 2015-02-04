/**
 * ScadaControlControlStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ScadaControlControlStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ScadaControlControlStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Control accepted";
    public static final java.lang.String _value2 = "Select timeout";
    public static final java.lang.String _value3 = "Point not selected";
    public static final java.lang.String _value4 = "Formatting errors in control request";
    public static final java.lang.String _value5 = "Control not supported";
    public static final java.lang.String _value6 = "Control queue full";
    public static final java.lang.String _value7 = "Hardware failure";
    public static final java.lang.String _value8 = "Point already selected";
    public static final ScadaControlControlStatus value1 = new ScadaControlControlStatus(_value1);
    public static final ScadaControlControlStatus value2 = new ScadaControlControlStatus(_value2);
    public static final ScadaControlControlStatus value3 = new ScadaControlControlStatus(_value3);
    public static final ScadaControlControlStatus value4 = new ScadaControlControlStatus(_value4);
    public static final ScadaControlControlStatus value5 = new ScadaControlControlStatus(_value5);
    public static final ScadaControlControlStatus value6 = new ScadaControlControlStatus(_value6);
    public static final ScadaControlControlStatus value7 = new ScadaControlControlStatus(_value7);
    public static final ScadaControlControlStatus value8 = new ScadaControlControlStatus(_value8);
    public java.lang.String getValue() { return _value_;}
    public static ScadaControlControlStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ScadaControlControlStatus enumeration = (ScadaControlControlStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ScadaControlControlStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ScadaControlControlStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">scadaControl>controlStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
