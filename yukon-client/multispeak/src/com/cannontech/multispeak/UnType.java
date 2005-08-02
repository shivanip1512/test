/**
 * UnType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class UnType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected UnType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Anc = "Anc";
    public static final java.lang.String _Cnd = "Cnd";
    public static final java.lang.String _Guy = "Guy";
    public static final java.lang.String _OhA = "OhA";
    public static final java.lang.String _OhP = "OhP";
    public static final java.lang.String _OhS = "OhS";
    public static final java.lang.String _OhT = "OhT";
    public static final java.lang.String _OH = "OH";
    public static final java.lang.String _Pol = "Pol";
    public static final java.lang.String _Sub = "Sub";
    public static final java.lang.String _UgA = "UgA";
    public static final java.lang.String _UgP = "UgP";
    public static final java.lang.String _UgS = "UgS";
    public static final java.lang.String _UgT = "UgT";
    public static final java.lang.String _UG = "UG";
    public static final java.lang.String _Other = "Other";
    public static final UnType Anc = new UnType(_Anc);
    public static final UnType Cnd = new UnType(_Cnd);
    public static final UnType Guy = new UnType(_Guy);
    public static final UnType OhA = new UnType(_OhA);
    public static final UnType OhP = new UnType(_OhP);
    public static final UnType OhS = new UnType(_OhS);
    public static final UnType OhT = new UnType(_OhT);
    public static final UnType OH = new UnType(_OH);
    public static final UnType Pol = new UnType(_Pol);
    public static final UnType Sub = new UnType(_Sub);
    public static final UnType UgA = new UnType(_UgA);
    public static final UnType UgP = new UnType(_UgP);
    public static final UnType UgS = new UnType(_UgS);
    public static final UnType UgT = new UnType(_UgT);
    public static final UnType UG = new UnType(_UG);
    public static final UnType Other = new UnType(_Other);
    public java.lang.String getValue() { return _value_;}
    public static UnType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        UnType enumeration = (UnType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static UnType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(UnType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
