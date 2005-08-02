/**
 * PhaseCd.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PhaseCd implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PhaseCd(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _A = "A";
    public static final java.lang.String _B = "B";
    public static final java.lang.String _C = "C";
    public static final java.lang.String _AB = "AB";
    public static final java.lang.String _AC = "AC";
    public static final java.lang.String _BC = "BC";
    public static final java.lang.String _ABC = "ABC";
    public static final java.lang.String _Unknown = "Unknown";
    public static final PhaseCd A = new PhaseCd(_A);
    public static final PhaseCd B = new PhaseCd(_B);
    public static final PhaseCd C = new PhaseCd(_C);
    public static final PhaseCd AB = new PhaseCd(_AB);
    public static final PhaseCd AC = new PhaseCd(_AC);
    public static final PhaseCd BC = new PhaseCd(_BC);
    public static final PhaseCd ABC = new PhaseCd(_ABC);
    public static final PhaseCd Unknown = new PhaseCd(_Unknown);
    public java.lang.String getValue() { return _value_;}
    public static PhaseCd fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PhaseCd enumeration = (PhaseCd)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PhaseCd fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PhaseCd.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
