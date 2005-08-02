/**
 * OutageStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OutageStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected OutageStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Assumed = "Assumed";
    public static final java.lang.String _Confirmed = "Confirmed";
    public static final java.lang.String _Restored = "Restored";
    public static final java.lang.String _Unknown = "Unknown";
    public static final java.lang.String _Scheduled = "Scheduled";
    public static final java.lang.String _StillOut = "StillOut";
    public static final java.lang.String _NonPay = "NonPay";
    public static final OutageStatus Assumed = new OutageStatus(_Assumed);
    public static final OutageStatus Confirmed = new OutageStatus(_Confirmed);
    public static final OutageStatus Restored = new OutageStatus(_Restored);
    public static final OutageStatus Unknown = new OutageStatus(_Unknown);
    public static final OutageStatus Scheduled = new OutageStatus(_Scheduled);
    public static final OutageStatus StillOut = new OutageStatus(_StillOut);
    public static final OutageStatus NonPay = new OutageStatus(_NonPay);
    public java.lang.String getValue() { return _value_;}
    public static OutageStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        OutageStatus enumeration = (OutageStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static OutageStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(OutageStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
