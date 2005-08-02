/**
 * PmEventCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PmEventCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PmEventCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _LowBattery = "LowBattery";
    public static final java.lang.String _Event1 = "Event1";
    public static final java.lang.String _Event2 = "Event2";
    public static final java.lang.String _Event3 = "Event3";
    public static final java.lang.String _LockOut = "LockOut";
    public static final java.lang.String _PowerRestored = "PowerRestored";
    public static final java.lang.String _UndeterminedOn = "UndeterminedOn";
    public static final java.lang.String _UndeterminedOff = "UndeterminedOff";
    public static final java.lang.String _BrownOut = "BrownOut";
    public static final java.lang.String _HighVoltage = "HighVoltage";
    public static final java.lang.String _NormalVoltage = "NormalVoltage";
    public static final PmEventCode LowBattery = new PmEventCode(_LowBattery);
    public static final PmEventCode Event1 = new PmEventCode(_Event1);
    public static final PmEventCode Event2 = new PmEventCode(_Event2);
    public static final PmEventCode Event3 = new PmEventCode(_Event3);
    public static final PmEventCode LockOut = new PmEventCode(_LockOut);
    public static final PmEventCode PowerRestored = new PmEventCode(_PowerRestored);
    public static final PmEventCode UndeterminedOn = new PmEventCode(_UndeterminedOn);
    public static final PmEventCode UndeterminedOff = new PmEventCode(_UndeterminedOff);
    public static final PmEventCode BrownOut = new PmEventCode(_BrownOut);
    public static final PmEventCode HighVoltage = new PmEventCode(_HighVoltage);
    public static final PmEventCode NormalVoltage = new PmEventCode(_NormalVoltage);
    public java.lang.String getValue() { return _value_;}
    public static PmEventCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PmEventCode enumeration = (PmEventCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PmEventCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PmEventCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pmEventCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
