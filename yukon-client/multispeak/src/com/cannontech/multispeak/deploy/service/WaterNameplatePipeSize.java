/**
 * WaterNameplatePipeSize.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WaterNameplatePipeSize implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected WaterNameplatePipeSize(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "0";
    public static final java.lang.String _value2 = "1";
    public static final java.lang.String _value3 = "2";
    public static final java.lang.String _value4 = "3";
    public static final java.lang.String _value5 = "4";
    public static final java.lang.String _value6 = "5";
    public static final java.lang.String _value7 = "6";
    public static final java.lang.String _value8 = "7";
    public static final java.lang.String _value9 = "8";
    public static final java.lang.String _value10 = "9";
    public static final java.lang.String _value11 = "10";
    public static final java.lang.String _value12 = "11";
    public static final java.lang.String _value13 = "12";
    public static final java.lang.String _value14 = "13";
    public static final java.lang.String _value15 = "14";
    public static final java.lang.String _value16 = "15";
    public static final java.lang.String _value17 = "16";
    public static final java.lang.String _value18 = "17";
    public static final java.lang.String _value19 = "18";
    public static final java.lang.String _value20 = "19";
    public static final java.lang.String _value21 = "20";
    public static final java.lang.String _value22 = "21";
    public static final java.lang.String _value23 = "22";
    public static final java.lang.String _value24 = "23";
    public static final java.lang.String _value25 = "24";
    public static final java.lang.String _value26 = "25";
    public static final java.lang.String _value27 = "";
    public static final WaterNameplatePipeSize value1 = new WaterNameplatePipeSize(_value1);
    public static final WaterNameplatePipeSize value2 = new WaterNameplatePipeSize(_value2);
    public static final WaterNameplatePipeSize value3 = new WaterNameplatePipeSize(_value3);
    public static final WaterNameplatePipeSize value4 = new WaterNameplatePipeSize(_value4);
    public static final WaterNameplatePipeSize value5 = new WaterNameplatePipeSize(_value5);
    public static final WaterNameplatePipeSize value6 = new WaterNameplatePipeSize(_value6);
    public static final WaterNameplatePipeSize value7 = new WaterNameplatePipeSize(_value7);
    public static final WaterNameplatePipeSize value8 = new WaterNameplatePipeSize(_value8);
    public static final WaterNameplatePipeSize value9 = new WaterNameplatePipeSize(_value9);
    public static final WaterNameplatePipeSize value10 = new WaterNameplatePipeSize(_value10);
    public static final WaterNameplatePipeSize value11 = new WaterNameplatePipeSize(_value11);
    public static final WaterNameplatePipeSize value12 = new WaterNameplatePipeSize(_value12);
    public static final WaterNameplatePipeSize value13 = new WaterNameplatePipeSize(_value13);
    public static final WaterNameplatePipeSize value14 = new WaterNameplatePipeSize(_value14);
    public static final WaterNameplatePipeSize value15 = new WaterNameplatePipeSize(_value15);
    public static final WaterNameplatePipeSize value16 = new WaterNameplatePipeSize(_value16);
    public static final WaterNameplatePipeSize value17 = new WaterNameplatePipeSize(_value17);
    public static final WaterNameplatePipeSize value18 = new WaterNameplatePipeSize(_value18);
    public static final WaterNameplatePipeSize value19 = new WaterNameplatePipeSize(_value19);
    public static final WaterNameplatePipeSize value20 = new WaterNameplatePipeSize(_value20);
    public static final WaterNameplatePipeSize value21 = new WaterNameplatePipeSize(_value21);
    public static final WaterNameplatePipeSize value22 = new WaterNameplatePipeSize(_value22);
    public static final WaterNameplatePipeSize value23 = new WaterNameplatePipeSize(_value23);
    public static final WaterNameplatePipeSize value24 = new WaterNameplatePipeSize(_value24);
    public static final WaterNameplatePipeSize value25 = new WaterNameplatePipeSize(_value25);
    public static final WaterNameplatePipeSize value26 = new WaterNameplatePipeSize(_value26);
    public static final WaterNameplatePipeSize value27 = new WaterNameplatePipeSize(_value27);
    public java.lang.String getValue() { return _value_;}
    public static WaterNameplatePipeSize fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        WaterNameplatePipeSize enumeration = (WaterNameplatePipeSize)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static WaterNameplatePipeSize fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(WaterNameplatePipeSize.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>pipeSize"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
