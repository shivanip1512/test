/**
 * CircuitElementElementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CircuitElementElementType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CircuitElementElementType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _ohPrimaryLine = "ohPrimaryLine";
    public static final java.lang.String _ohSecondaryLine = "ohSecondaryLine";
    public static final java.lang.String _ugPrimaryLine = "ugPrimaryLine";
    public static final java.lang.String _ugSecondaryLine = "ugSecondaryLine";
    public static final java.lang.String _fakeNodeSection = "fakeNodeSection";
    public static final java.lang.String _capacitorBank = "capacitorBank";
    public static final java.lang.String _overcurrentDeviceBank = "overcurrentDeviceBank";
    public static final java.lang.String _switchDeviceBank = "switchDeviceBank";
    public static final java.lang.String _regulatorBank = "regulatorBank";
    public static final java.lang.String _transformerBank = "transformerBank";
    public static final java.lang.String _serviceLocation = "serviceLocation";
    public static final java.lang.String _substation = "substation";
    public static final java.lang.String _generator = "generator";
    public static final java.lang.String _motor = "motor";
    public static final java.lang.String _root = "root";
    public static final CircuitElementElementType ohPrimaryLine = new CircuitElementElementType(_ohPrimaryLine);
    public static final CircuitElementElementType ohSecondaryLine = new CircuitElementElementType(_ohSecondaryLine);
    public static final CircuitElementElementType ugPrimaryLine = new CircuitElementElementType(_ugPrimaryLine);
    public static final CircuitElementElementType ugSecondaryLine = new CircuitElementElementType(_ugSecondaryLine);
    public static final CircuitElementElementType fakeNodeSection = new CircuitElementElementType(_fakeNodeSection);
    public static final CircuitElementElementType capacitorBank = new CircuitElementElementType(_capacitorBank);
    public static final CircuitElementElementType overcurrentDeviceBank = new CircuitElementElementType(_overcurrentDeviceBank);
    public static final CircuitElementElementType switchDeviceBank = new CircuitElementElementType(_switchDeviceBank);
    public static final CircuitElementElementType regulatorBank = new CircuitElementElementType(_regulatorBank);
    public static final CircuitElementElementType transformerBank = new CircuitElementElementType(_transformerBank);
    public static final CircuitElementElementType serviceLocation = new CircuitElementElementType(_serviceLocation);
    public static final CircuitElementElementType substation = new CircuitElementElementType(_substation);
    public static final CircuitElementElementType generator = new CircuitElementElementType(_generator);
    public static final CircuitElementElementType motor = new CircuitElementElementType(_motor);
    public static final CircuitElementElementType root = new CircuitElementElementType(_root);
    public java.lang.String getValue() { return _value_;}
    public static CircuitElementElementType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        CircuitElementElementType enumeration = (CircuitElementElementType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static CircuitElementElementType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(CircuitElementElementType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementElementType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
