/**
 * NumberOfElement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class NumberOfElement implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected NumberOfElement(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "numberOfElements 1";
    public static final java.lang.String _value2 = "numberOfElements 1.5";
    public static final java.lang.String _value3 = "numberOfElements 2";
    public static final java.lang.String _value4 = "numberOfElements 2.5";
    public static final java.lang.String _value5 = "numberOfElements 3";
    public static final java.lang.String _value6 = "numberOfElements 6";
    public static final NumberOfElement value1 = new NumberOfElement(_value1);
    public static final NumberOfElement value2 = new NumberOfElement(_value2);
    public static final NumberOfElement value3 = new NumberOfElement(_value3);
    public static final NumberOfElement value4 = new NumberOfElement(_value4);
    public static final NumberOfElement value5 = new NumberOfElement(_value5);
    public static final NumberOfElement value6 = new NumberOfElement(_value6);
    public java.lang.String getValue() { return _value_;}
    public static NumberOfElement fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        NumberOfElement enumeration = (NumberOfElement)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static NumberOfElement fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(NumberOfElement.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfElement"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
