/**
 * StatusIdentifier.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class StatusIdentifier implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected StatusIdentifier(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Open = "Open";
    public static final java.lang.String _Closed = "Closed";
    public static final java.lang.String _Travel = "Travel";
    public static final java.lang.String _OutofService = "OutofService";
    public static final java.lang.String _Unknown = "Unknown";
    public static final StatusIdentifier Open = new StatusIdentifier(_Open);
    public static final StatusIdentifier Closed = new StatusIdentifier(_Closed);
    public static final StatusIdentifier Travel = new StatusIdentifier(_Travel);
    public static final StatusIdentifier OutofService = new StatusIdentifier(_OutofService);
    public static final StatusIdentifier Unknown = new StatusIdentifier(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static StatusIdentifier fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        StatusIdentifier enumeration = (StatusIdentifier)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static StatusIdentifier fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(StatusIdentifier.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusIdentifier"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
