/**
 * GenericLineFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GenericLineFeature  extends com.cannontech.multispeak.MspLineObject  implements java.io.Serializable {
    private java.lang.String featureType;
    private java.lang.String featureSubtype;

    public GenericLineFeature() {
    }

    public GenericLineFeature(
           java.lang.String featureType,
           java.lang.String featureSubtype) {
           this.featureType = featureType;
           this.featureSubtype = featureSubtype;
    }


    /**
     * Gets the featureType value for this GenericLineFeature.
     * 
     * @return featureType
     */
    public java.lang.String getFeatureType() {
        return featureType;
    }


    /**
     * Sets the featureType value for this GenericLineFeature.
     * 
     * @param featureType
     */
    public void setFeatureType(java.lang.String featureType) {
        this.featureType = featureType;
    }


    /**
     * Gets the featureSubtype value for this GenericLineFeature.
     * 
     * @return featureSubtype
     */
    public java.lang.String getFeatureSubtype() {
        return featureSubtype;
    }


    /**
     * Sets the featureSubtype value for this GenericLineFeature.
     * 
     * @param featureSubtype
     */
    public void setFeatureSubtype(java.lang.String featureSubtype) {
        this.featureSubtype = featureSubtype;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GenericLineFeature)) return false;
        GenericLineFeature other = (GenericLineFeature) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.featureType==null && other.getFeatureType()==null) || 
             (this.featureType!=null &&
              this.featureType.equals(other.getFeatureType()))) &&
            ((this.featureSubtype==null && other.getFeatureSubtype()==null) || 
             (this.featureSubtype!=null &&
              this.featureSubtype.equals(other.getFeatureSubtype())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getFeatureType() != null) {
            _hashCode += getFeatureType().hashCode();
        }
        if (getFeatureSubtype() != null) {
            _hashCode += getFeatureSubtype().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GenericLineFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "featureType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureSubtype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "featureSubtype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
          new  org.apache.axis.encoding.ser.BeanSerializer(
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
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
