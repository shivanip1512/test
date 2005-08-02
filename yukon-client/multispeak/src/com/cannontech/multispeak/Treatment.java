/**
 * Treatment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Treatment implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Treatment(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Butt = "Butt";
    public static final java.lang.String _Natural = "Natural";
    public static final java.lang.String _Penta = "Penta";
    public static final java.lang.String _Creosote = "Creosote";
    public static final java.lang.String _CCA = "CCA";
    public static final java.lang.String _Chemonite = "Chemonite";
    public static final java.lang.String _Napthena = "Napthena";
    public static final java.lang.String _Cellon = "Cellon";
    public static final java.lang.String _Other = "Other";
    public static final java.lang.String _Unknown = "Unknown";
    public static final Treatment Butt = new Treatment(_Butt);
    public static final Treatment Natural = new Treatment(_Natural);
    public static final Treatment Penta = new Treatment(_Penta);
    public static final Treatment Creosote = new Treatment(_Creosote);
    public static final Treatment CCA = new Treatment(_CCA);
    public static final Treatment Chemonite = new Treatment(_Chemonite);
    public static final Treatment Napthena = new Treatment(_Napthena);
    public static final Treatment Cellon = new Treatment(_Cellon);
    public static final Treatment Other = new Treatment(_Other);
    public static final Treatment Unknown = new Treatment(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static Treatment fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Treatment enumeration = (Treatment)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Treatment fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(Treatment.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "treatment"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
