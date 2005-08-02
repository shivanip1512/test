/**
 * ReadingStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ReadingStatus implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ReadingStatus(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _suspiciousRead = "suspiciousRead";
    public static final java.lang.String _DSTinEffect = "DSTinEffect";
    public static final java.lang.String _manuallyUpdated = "manuallyUpdated";
    public static final java.lang.String _manuallyEntered = "manuallyEntered";
    public static final java.lang.String _reset = "reset";
    public static final java.lang.String _seasonChange = "seasonChange";
    public static final java.lang.String _firstRead = "firstRead";
    public static final java.lang.String _billingRead = "billingRead";
    public static final java.lang.String _afterCorrection = "afterCorrection";
    public static final java.lang.String _beforeCorrection = "beforeCorrection";
    public static final java.lang.String _finalRead = "finalRead";
    public static final ReadingStatus suspiciousRead = new ReadingStatus(_suspiciousRead);
    public static final ReadingStatus DSTinEffect = new ReadingStatus(_DSTinEffect);
    public static final ReadingStatus manuallyUpdated = new ReadingStatus(_manuallyUpdated);
    public static final ReadingStatus manuallyEntered = new ReadingStatus(_manuallyEntered);
    public static final ReadingStatus reset = new ReadingStatus(_reset);
    public static final ReadingStatus seasonChange = new ReadingStatus(_seasonChange);
    public static final ReadingStatus firstRead = new ReadingStatus(_firstRead);
    public static final ReadingStatus billingRead = new ReadingStatus(_billingRead);
    public static final ReadingStatus afterCorrection = new ReadingStatus(_afterCorrection);
    public static final ReadingStatus beforeCorrection = new ReadingStatus(_beforeCorrection);
    public static final ReadingStatus finalRead = new ReadingStatus(_finalRead);
    public java.lang.String getValue() { return _value_;}
    public static ReadingStatus fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ReadingStatus enumeration = (ReadingStatus)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ReadingStatus fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(ReadingStatus.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingStatus"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
