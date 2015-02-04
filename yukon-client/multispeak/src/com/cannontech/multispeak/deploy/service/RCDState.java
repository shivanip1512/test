/**
 * RCDState.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RCDState implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected RCDState(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Opened = "Opened";
    public static final java.lang.String _Closed = "Closed";
    public static final java.lang.String _Armed = "Armed";
    public static final java.lang.String _Unknown = "Unknown";
    public static final java.lang.String _Enabled = "Enabled";
    public static final java.lang.String _Disabled = "Disabled";
    public static final RCDState Opened = new RCDState(_Opened);
    public static final RCDState Closed = new RCDState(_Closed);
    public static final RCDState Armed = new RCDState(_Armed);
    public static final RCDState Unknown = new RCDState(_Unknown);
    public static final RCDState Enabled = new RCDState(_Enabled);
    public static final RCDState Disabled = new RCDState(_Disabled);
    public java.lang.String getValue() { return _value_;}
    public static RCDState fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        RCDState enumeration = (RCDState)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static RCDState fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(RCDState.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "RCDState"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
