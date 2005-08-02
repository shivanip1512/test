/**
 * CumDemand.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CumDemand  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private double _value;
    private org.apache.axis.types.UnsignedInt sourceID;  // attribute
    private com.cannontech.multispeak.CumDemandType cumDemandType;  // attribute

    public CumDemand() {
    }

    // Simple Types must have a String constructor
    public CumDemand(double _value) {
        this._value = _value;
    }
    public CumDemand(java.lang.String _value) {
        this._value = new Double(_value).doubleValue();
    }

    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return new Double(_value).toString();
    }


    /**
     * Gets the _value value for this CumDemand.
     * 
     * @return _value
     */
    public double get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this CumDemand.
     * 
     * @param _value
     */
    public void set_value(double _value) {
        this._value = _value;
    }


    /**
     * Gets the sourceID value for this CumDemand.
     * 
     * @return sourceID
     */
    public org.apache.axis.types.UnsignedInt getSourceID() {
        return sourceID;
    }


    /**
     * Sets the sourceID value for this CumDemand.
     * 
     * @param sourceID
     */
    public void setSourceID(org.apache.axis.types.UnsignedInt sourceID) {
        this.sourceID = sourceID;
    }


    /**
     * Gets the cumDemandType value for this CumDemand.
     * 
     * @return cumDemandType
     */
    public com.cannontech.multispeak.CumDemandType getCumDemandType() {
        return cumDemandType;
    }


    /**
     * Sets the cumDemandType value for this CumDemand.
     * 
     * @param cumDemandType
     */
    public void setCumDemandType(com.cannontech.multispeak.CumDemandType cumDemandType) {
        this.cumDemandType = cumDemandType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CumDemand)) return false;
        CumDemand other = (CumDemand) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this._value == other.get_value() &&
            ((this.sourceID==null && other.getSourceID()==null) || 
             (this.sourceID!=null &&
              this.sourceID.equals(other.getSourceID()))) &&
            ((this.cumDemandType==null && other.getCumDemandType()==null) || 
             (this.cumDemandType!=null &&
              this.cumDemandType.equals(other.getCumDemandType())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Double(get_value()).hashCode();
        if (getSourceID() != null) {
            _hashCode += getSourceID().hashCode();
        }
        if (getCumDemandType() != null) {
            _hashCode += getCumDemandType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CumDemand.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemand"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("sourceID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "sourceID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("cumDemandType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "cumDemandType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cumDemandType"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.SimpleSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
