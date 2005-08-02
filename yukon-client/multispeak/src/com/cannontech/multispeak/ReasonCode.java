/**
 * ReasonCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ReasonCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ReasonCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _NonPayment = "NonPayment";
    public static final java.lang.String _ServiceInactive = "ServiceInactive";
    public static final java.lang.String _PaymentAgreement = "PaymentAgreement";
    public static final java.lang.String _PaymentReceived = "PaymentReceived";
    public static final java.lang.String _NewCustomer = "NewCustomer";
    public static final ReasonCode NonPayment = new ReasonCode(_NonPayment);
    public static final ReasonCode ServiceInactive = new ReasonCode(_ServiceInactive);
    public static final ReasonCode PaymentAgreement = new ReasonCode(_PaymentAgreement);
    public static final ReasonCode PaymentReceived = new ReasonCode(_PaymentReceived);
    public static final ReasonCode NewCustomer = new ReasonCode(_NewCustomer);
    public java.lang.String getValue() { return _value_;}
    public static ReasonCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ReasonCode enumeration = (ReasonCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ReasonCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ReasonCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
