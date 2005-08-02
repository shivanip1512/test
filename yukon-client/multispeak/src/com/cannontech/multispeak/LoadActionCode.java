/**
 * LoadActionCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LoadActionCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected LoadActionCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Connect = "Connect";
    public static final java.lang.String _Disconnect = "Disconnect";
    public static final java.lang.String _InitiatePowerLimitation = "InitiatePowerLimitation";
    public static final java.lang.String _Open = "Open";
    public static final java.lang.String _Closed = "Closed";
    public static final java.lang.String _Armed = "Armed";
    public static final java.lang.String _Unknown = "Unknown";
    public static final LoadActionCode Connect = new LoadActionCode(_Connect);
    public static final LoadActionCode Disconnect = new LoadActionCode(_Disconnect);
    public static final LoadActionCode InitiatePowerLimitation = new LoadActionCode(_InitiatePowerLimitation);
    public static final LoadActionCode Open = new LoadActionCode(_Open);
    public static final LoadActionCode Closed = new LoadActionCode(_Closed);
    public static final LoadActionCode Armed = new LoadActionCode(_Armed);
    public static final LoadActionCode Unknown = new LoadActionCode(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static LoadActionCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        LoadActionCode enumeration = (LoadActionCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static LoadActionCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(LoadActionCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
