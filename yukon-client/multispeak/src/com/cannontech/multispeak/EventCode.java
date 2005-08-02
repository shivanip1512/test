/**
 * EventCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class EventCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EventCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "Primary power down";
    public static final java.lang.String _value2 = "Primary power up";
    public static final java.lang.String _value3 = "Time changed, old time";
    public static final java.lang.String _value4 = "Time changed, new time";
    public static final java.lang.String _value5 = "Time changed, old time as argument";
    public static final java.lang.String _value6 = "Time changed, new time as argument";
    public static final java.lang.String _value7 = "Meter accessed for read";
    public static final java.lang.String _value8 = "Meter accessed for write";
    public static final java.lang.String _value9 = "Procedure invoked";
    public static final java.lang.String _value10 = "Written to";
    public static final java.lang.String _value11 = "Programmed";
    public static final java.lang.String _value12 = "Communication terminated normally";
    public static final java.lang.String _value13 = "Communication terminated abnormally";
    public static final java.lang.String _value14 = "List cleared";
    public static final java.lang.String _value15 = "Last read entry updated";
    public static final java.lang.String _value16 = "History Log cleared";
    public static final java.lang.String _value17 = "History Log last read entry updated";
    public static final java.lang.String _value18 = "Event Log cleared";
    public static final java.lang.String _value19 = "Event Log last read entry updated";
    public static final java.lang.String _value20 = "Demand reset occurred";
    public static final java.lang.String _value21 = "Self read occurred";
    public static final java.lang.String _value22 = "Season change";
    public static final java.lang.String _value23 = "Rate change";
    public static final java.lang.String _value24 = "Special Schedule activation";
    public static final java.lang.String _value25 = "Tier changed";
    public static final java.lang.String _value26 = "Pending data stucture activation";
    public static final java.lang.String _value27 = "Pending data stucture clear";
    public static final java.lang.String _value28 = "Metering mode started";
    public static final java.lang.String _value29 = "Metering mode stopped";
    public static final java.lang.String _value30 = "Test mode started";
    public static final java.lang.String _value31 = "Test mode stopped";
    public static final java.lang.String _value32 = "Meter shop mode started";
    public static final java.lang.String _value33 = "Meter shop mode stopped";
    public static final java.lang.String _value34 = "Meter reprogrammed";
    public static final java.lang.String _value35 = "Configuration error detected";
    public static final java.lang.String _value36 = "Self check error detected";
    public static final java.lang.String _value37 = "RAM failure detected";
    public static final java.lang.String _value38 = "ROM failure detected";
    public static final java.lang.String _value39 = "Nonvolatile memory failure detected";
    public static final java.lang.String _value40 = "Clock error detected";
    public static final java.lang.String _value41 = "Measurement error detected";
    public static final java.lang.String _value42 = "Low battery detected";
    public static final java.lang.String _value43 = "Low loss potential detected";
    public static final java.lang.String _value44 = "Demand overload detected";
    public static final java.lang.String _value45 = "Tamper attempt detected";
    public static final java.lang.String _value46 = "Reverse flow detected";
    public static final java.lang.String _value47 = "Momentary interruption";
    public static final java.lang.String _value48 = "Sustained outage";
    public static final EventCode value1 = new EventCode(_value1);
    public static final EventCode value2 = new EventCode(_value2);
    public static final EventCode value3 = new EventCode(_value3);
    public static final EventCode value4 = new EventCode(_value4);
    public static final EventCode value5 = new EventCode(_value5);
    public static final EventCode value6 = new EventCode(_value6);
    public static final EventCode value7 = new EventCode(_value7);
    public static final EventCode value8 = new EventCode(_value8);
    public static final EventCode value9 = new EventCode(_value9);
    public static final EventCode value10 = new EventCode(_value10);
    public static final EventCode value11 = new EventCode(_value11);
    public static final EventCode value12 = new EventCode(_value12);
    public static final EventCode value13 = new EventCode(_value13);
    public static final EventCode value14 = new EventCode(_value14);
    public static final EventCode value15 = new EventCode(_value15);
    public static final EventCode value16 = new EventCode(_value16);
    public static final EventCode value17 = new EventCode(_value17);
    public static final EventCode value18 = new EventCode(_value18);
    public static final EventCode value19 = new EventCode(_value19);
    public static final EventCode value20 = new EventCode(_value20);
    public static final EventCode value21 = new EventCode(_value21);
    public static final EventCode value22 = new EventCode(_value22);
    public static final EventCode value23 = new EventCode(_value23);
    public static final EventCode value24 = new EventCode(_value24);
    public static final EventCode value25 = new EventCode(_value25);
    public static final EventCode value26 = new EventCode(_value26);
    public static final EventCode value27 = new EventCode(_value27);
    public static final EventCode value28 = new EventCode(_value28);
    public static final EventCode value29 = new EventCode(_value29);
    public static final EventCode value30 = new EventCode(_value30);
    public static final EventCode value31 = new EventCode(_value31);
    public static final EventCode value32 = new EventCode(_value32);
    public static final EventCode value33 = new EventCode(_value33);
    public static final EventCode value34 = new EventCode(_value34);
    public static final EventCode value35 = new EventCode(_value35);
    public static final EventCode value36 = new EventCode(_value36);
    public static final EventCode value37 = new EventCode(_value37);
    public static final EventCode value38 = new EventCode(_value38);
    public static final EventCode value39 = new EventCode(_value39);
    public static final EventCode value40 = new EventCode(_value40);
    public static final EventCode value41 = new EventCode(_value41);
    public static final EventCode value42 = new EventCode(_value42);
    public static final EventCode value43 = new EventCode(_value43);
    public static final EventCode value44 = new EventCode(_value44);
    public static final EventCode value45 = new EventCode(_value45);
    public static final EventCode value46 = new EventCode(_value46);
    public static final EventCode value47 = new EventCode(_value47);
    public static final EventCode value48 = new EventCode(_value48);
    public java.lang.String getValue() { return _value_;}
    public static EventCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EventCode enumeration = (EventCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EventCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EventCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
