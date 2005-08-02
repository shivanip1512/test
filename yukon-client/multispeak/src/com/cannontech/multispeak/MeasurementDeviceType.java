/**
 * MeasurementDeviceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeasurementDeviceType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected MeasurementDeviceType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Emeter = "Emeter";
    public static final java.lang.String _Gmeter = "Gmeter";
    public static final java.lang.String _Wmeter = "Wmeter";
    public static final java.lang.String _Demand = "Demand";
    public static final java.lang.String _Harmonic = "Harmonic";
    public static final java.lang.String _Phaseangle = "Phaseangle";
    public static final java.lang.String _BusV = "BusV";
    public static final java.lang.String _BusI = "BusI";
    public static final java.lang.String _BkrV = "BkrV";
    public static final java.lang.String _BkrI = "BkrI";
    public static final java.lang.String _LineV = "LineV";
    public static final java.lang.String _LineI = "LineI";
    public static final java.lang.String _GPSClock = "GPSClock";
    public static final java.lang.String _Temp = "Temp";
    public static final java.lang.String _Other = "Other";
    public static final MeasurementDeviceType Emeter = new MeasurementDeviceType(_Emeter);
    public static final MeasurementDeviceType Gmeter = new MeasurementDeviceType(_Gmeter);
    public static final MeasurementDeviceType Wmeter = new MeasurementDeviceType(_Wmeter);
    public static final MeasurementDeviceType Demand = new MeasurementDeviceType(_Demand);
    public static final MeasurementDeviceType Harmonic = new MeasurementDeviceType(_Harmonic);
    public static final MeasurementDeviceType Phaseangle = new MeasurementDeviceType(_Phaseangle);
    public static final MeasurementDeviceType BusV = new MeasurementDeviceType(_BusV);
    public static final MeasurementDeviceType BusI = new MeasurementDeviceType(_BusI);
    public static final MeasurementDeviceType BkrV = new MeasurementDeviceType(_BkrV);
    public static final MeasurementDeviceType BkrI = new MeasurementDeviceType(_BkrI);
    public static final MeasurementDeviceType LineV = new MeasurementDeviceType(_LineV);
    public static final MeasurementDeviceType LineI = new MeasurementDeviceType(_LineI);
    public static final MeasurementDeviceType GPSClock = new MeasurementDeviceType(_GPSClock);
    public static final MeasurementDeviceType Temp = new MeasurementDeviceType(_Temp);
    public static final MeasurementDeviceType Other = new MeasurementDeviceType(_Other);
    public java.lang.String getValue() { return _value_;}
    public static MeasurementDeviceType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        MeasurementDeviceType enumeration = (MeasurementDeviceType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static MeasurementDeviceType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(MeasurementDeviceType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
