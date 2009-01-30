/**
 * ServiceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ServiceType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ServiceType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Electric = "Electric";
    public static final java.lang.String _Gas = "Gas";
    public static final java.lang.String _Water = "Water";
    public static final java.lang.String _Propane = "Propane";
    public static final java.lang.String _Refuse = "Refuse";
    public static final java.lang.String _Sewer = "Sewer";
    public static final java.lang.String _Telecom = "Telecom";
    public static final java.lang.String _Cable = "Cable";
    public static final java.lang.String _Heating = "Heating";
    public static final java.lang.String _Steam = "Steam";
    public static final java.lang.String _Transportation = "Transportation";
    public static final java.lang.String _All = "All";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final ServiceType Electric = new ServiceType(_Electric);
    public static final ServiceType Gas = new ServiceType(_Gas);
    public static final ServiceType Water = new ServiceType(_Water);
    public static final ServiceType Propane = new ServiceType(_Propane);
    public static final ServiceType Refuse = new ServiceType(_Refuse);
    public static final ServiceType Sewer = new ServiceType(_Sewer);
    public static final ServiceType Telecom = new ServiceType(_Telecom);
    public static final ServiceType Cable = new ServiceType(_Cable);
    public static final ServiceType Heating = new ServiceType(_Heating);
    public static final ServiceType Steam = new ServiceType(_Steam);
    public static final ServiceType Transportation = new ServiceType(_Transportation);
    public static final ServiceType All = new ServiceType(_All);
    public static final ServiceType Other = new ServiceType(_Other);
    public static final ServiceType Unknown = new ServiceType(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static ServiceType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ServiceType enumeration = (ServiceType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ServiceType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ServiceType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
