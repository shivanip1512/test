/**
 * CircuitElementState.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CircuitElementState implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CircuitElementState(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final java.lang.String _UplineTempOpen = "UplineTempOpen";
    public static final java.lang.String _UplineTempBreak = "UplineTempBreak";
    public static final java.lang.String _UplineTempClosed = "UplineTempClosed";
    public static final java.lang.String _UplineVerifiedClosedNoPower = "UplineVerifiedClosedNoPower";
    public static final java.lang.String _PredictedOpen = "PredictedOpen";
    public static final java.lang.String _PredictedBreak = "PredictedBreak";
    public static final java.lang.String _PredictedClosed = "PredictedClosed";
    public static final java.lang.String _PossibleOpen = "PossibleOpen";
    public static final java.lang.String _PossibleBreak = "PossibleBreak";
    public static final java.lang.String _PossibleClosed = "PossibleClosed";
    public static final java.lang.String _VerifiedOpen = "VerifiedOpen";
    public static final java.lang.String _VerifiedBreak = "VerifiedBreak";
    public static final java.lang.String _VerifiedClosedNoPower = "VerifiedClosedNoPower";
    public static final java.lang.String _VerifiedClosedWithPower = "VerifiedClosedWithPower";
    public static final java.lang.String _TempOpen = "TempOpen";
    public static final java.lang.String _TempBreak = "TempBreak";
    public static final java.lang.String _TempClosed = "TempClosed";
    public static final java.lang.String _NormalOrRestored = "NormalOrRestored";
    public static final java.lang.String _NoChange = "NoChange";
    public static final CircuitElementState Other = new CircuitElementState(_Other);
    public static final CircuitElementState Unknown = new CircuitElementState(_Unknown);
    public static final CircuitElementState UplineTempOpen = new CircuitElementState(_UplineTempOpen);
    public static final CircuitElementState UplineTempBreak = new CircuitElementState(_UplineTempBreak);
    public static final CircuitElementState UplineTempClosed = new CircuitElementState(_UplineTempClosed);
    public static final CircuitElementState UplineVerifiedClosedNoPower = new CircuitElementState(_UplineVerifiedClosedNoPower);
    public static final CircuitElementState PredictedOpen = new CircuitElementState(_PredictedOpen);
    public static final CircuitElementState PredictedBreak = new CircuitElementState(_PredictedBreak);
    public static final CircuitElementState PredictedClosed = new CircuitElementState(_PredictedClosed);
    public static final CircuitElementState PossibleOpen = new CircuitElementState(_PossibleOpen);
    public static final CircuitElementState PossibleBreak = new CircuitElementState(_PossibleBreak);
    public static final CircuitElementState PossibleClosed = new CircuitElementState(_PossibleClosed);
    public static final CircuitElementState VerifiedOpen = new CircuitElementState(_VerifiedOpen);
    public static final CircuitElementState VerifiedBreak = new CircuitElementState(_VerifiedBreak);
    public static final CircuitElementState VerifiedClosedNoPower = new CircuitElementState(_VerifiedClosedNoPower);
    public static final CircuitElementState VerifiedClosedWithPower = new CircuitElementState(_VerifiedClosedWithPower);
    public static final CircuitElementState TempOpen = new CircuitElementState(_TempOpen);
    public static final CircuitElementState TempBreak = new CircuitElementState(_TempBreak);
    public static final CircuitElementState TempClosed = new CircuitElementState(_TempClosed);
    public static final CircuitElementState NormalOrRestored = new CircuitElementState(_NormalOrRestored);
    public static final CircuitElementState NoChange = new CircuitElementState(_NoChange);
    public java.lang.String getValue() { return _value_;}
    public static CircuitElementState fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        CircuitElementState enumeration = (CircuitElementState)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static CircuitElementState fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(CircuitElementState.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementState"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
