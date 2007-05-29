/**
 * AdjustmentItemBalanceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class AdjustmentItemBalanceType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AdjustmentItemBalanceType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _priorityBalance = "priorityBalance";
    public static final java.lang.String _arrearsBalance = "arrearsBalance";
    public static final java.lang.String _prepaidBalance = "prepaidBalance";
    public static final java.lang.String _totalBalance = "totalBalance";
    public static final AdjustmentItemBalanceType priorityBalance = new AdjustmentItemBalanceType(_priorityBalance);
    public static final AdjustmentItemBalanceType arrearsBalance = new AdjustmentItemBalanceType(_arrearsBalance);
    public static final AdjustmentItemBalanceType prepaidBalance = new AdjustmentItemBalanceType(_prepaidBalance);
    public static final AdjustmentItemBalanceType totalBalance = new AdjustmentItemBalanceType(_totalBalance);
    public java.lang.String getValue() { return _value_;}
    public static AdjustmentItemBalanceType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        AdjustmentItemBalanceType enumeration = (AdjustmentItemBalanceType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static AdjustmentItemBalanceType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(AdjustmentItemBalanceType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">adjustmentItem>balanceType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
