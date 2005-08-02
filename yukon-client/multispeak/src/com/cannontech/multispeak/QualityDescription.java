/**
 * QualityDescription.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class QualityDescription implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected QualityDescription(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Measured = "Measured";
    public static final java.lang.String _Default = "Default";
    public static final java.lang.String _Estimated = "Estimated";
    public static final java.lang.String _Calculated = "Calculated";
    public static final java.lang.String _Initial = "Initial";
    public static final java.lang.String _Last = "Last";
    public static final java.lang.String _Failed = "Failed";
    public static final QualityDescription Measured = new QualityDescription(_Measured);
    public static final QualityDescription Default = new QualityDescription(_Default);
    public static final QualityDescription Estimated = new QualityDescription(_Estimated);
    public static final QualityDescription Calculated = new QualityDescription(_Calculated);
    public static final QualityDescription Initial = new QualityDescription(_Initial);
    public static final QualityDescription Last = new QualityDescription(_Last);
    public static final QualityDescription Failed = new QualityDescription(_Failed);
    public java.lang.String getValue() { return _value_;}
    public static QualityDescription fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        QualityDescription enumeration = (QualityDescription)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static QualityDescription fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(QualityDescription.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
