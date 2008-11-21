/**
 * OEStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OEStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected OEStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _VerifiedOpen = "VerifiedOpen";
    public static final java.lang.String _VerifiedBreak = "VerifiedBreak";
    public static final java.lang.String _VerifiedClosedNoPower = "VerifiedClosedNoPower";
    public static final java.lang.String _VerifiedClosedWithPower = "VerifiedClosedWithPower";
    public static final java.lang.String _TempOpen = "TempOpen";
    public static final java.lang.String _TempBreak = "TempBreak";
    public static final java.lang.String _TempClosed = "TempClosed";
    public static final java.lang.String _NormalOrRestored = "NormalOrRestored";
    public static final java.lang.String _NoChange = "NoChange";
    public static final java.lang.String _Other = "Other";
    public static final OEStatus VerifiedOpen = new OEStatus(_VerifiedOpen);
    public static final OEStatus VerifiedBreak = new OEStatus(_VerifiedBreak);
    public static final OEStatus VerifiedClosedNoPower = new OEStatus(_VerifiedClosedNoPower);
    public static final OEStatus VerifiedClosedWithPower = new OEStatus(_VerifiedClosedWithPower);
    public static final OEStatus TempOpen = new OEStatus(_TempOpen);
    public static final OEStatus TempBreak = new OEStatus(_TempBreak);
    public static final OEStatus TempClosed = new OEStatus(_TempClosed);
    public static final OEStatus NormalOrRestored = new OEStatus(_NormalOrRestored);
    public static final OEStatus NoChange = new OEStatus(_NoChange);
    public static final OEStatus Other = new OEStatus(_Other);
    public java.lang.String getValue() { return _value_;}
    public static OEStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        OEStatus enumeration = (OEStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static OEStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(OEStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "OEStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
