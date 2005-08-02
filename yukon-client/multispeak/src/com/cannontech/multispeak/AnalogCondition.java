/**
 * AnalogCondition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class AnalogCondition implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AnalogCondition(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _L4 = "L4";
    public static final java.lang.String _L3 = "L3";
    public static final java.lang.String _L2 = "L2";
    public static final java.lang.String _L1 = "L1";
    public static final java.lang.String _Normal = "Normal";
    public static final java.lang.String _H1 = "H1";
    public static final java.lang.String _H2 = "H2";
    public static final java.lang.String _H3 = "H3";
    public static final java.lang.String _H4 = "H4";
    public static final AnalogCondition L4 = new AnalogCondition(_L4);
    public static final AnalogCondition L3 = new AnalogCondition(_L3);
    public static final AnalogCondition L2 = new AnalogCondition(_L2);
    public static final AnalogCondition L1 = new AnalogCondition(_L1);
    public static final AnalogCondition Normal = new AnalogCondition(_Normal);
    public static final AnalogCondition H1 = new AnalogCondition(_H1);
    public static final AnalogCondition H2 = new AnalogCondition(_H2);
    public static final AnalogCondition H3 = new AnalogCondition(_H3);
    public static final AnalogCondition H4 = new AnalogCondition(_H4);
    public java.lang.String getValue() { return _value_;}
    public static AnalogCondition fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        AnalogCondition enumeration = (AnalogCondition)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static AnalogCondition fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(AnalogCondition.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "analogCondition"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
