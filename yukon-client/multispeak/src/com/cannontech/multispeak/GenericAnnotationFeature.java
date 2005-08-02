/**
 * GenericAnnotationFeature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GenericAnnotationFeature  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.String featureType;
    private java.lang.String featureSubtype;
    private java.lang.Float scale;
    private java.lang.String annotation;

    public GenericAnnotationFeature() {
    }

    public GenericAnnotationFeature(
           java.lang.String featureType,
           java.lang.String featureSubtype,
           java.lang.Float scale,
           java.lang.String annotation) {
           this.featureType = featureType;
           this.featureSubtype = featureSubtype;
           this.scale = scale;
           this.annotation = annotation;
    }


    /**
     * Gets the featureType value for this GenericAnnotationFeature.
     * 
     * @return featureType
     */
    public java.lang.String getFeatureType() {
        return featureType;
    }


    /**
     * Sets the featureType value for this GenericAnnotationFeature.
     * 
     * @param featureType
     */
    public void setFeatureType(java.lang.String featureType) {
        this.featureType = featureType;
    }


    /**
     * Gets the featureSubtype value for this GenericAnnotationFeature.
     * 
     * @return featureSubtype
     */
    public java.lang.String getFeatureSubtype() {
        return featureSubtype;
    }


    /**
     * Sets the featureSubtype value for this GenericAnnotationFeature.
     * 
     * @param featureSubtype
     */
    public void setFeatureSubtype(java.lang.String featureSubtype) {
        this.featureSubtype = featureSubtype;
    }


    /**
     * Gets the scale value for this GenericAnnotationFeature.
     * 
     * @return scale
     */
    public java.lang.Float getScale() {
        return scale;
    }


    /**
     * Sets the scale value for this GenericAnnotationFeature.
     * 
     * @param scale
     */
    public void setScale(java.lang.Float scale) {
        this.scale = scale;
    }


    /**
     * Gets the annotation value for this GenericAnnotationFeature.
     * 
     * @return annotation
     */
    public java.lang.String getAnnotation() {
        return annotation;
    }


    /**
     * Sets the annotation value for this GenericAnnotationFeature.
     * 
     * @param annotation
     */
    public void setAnnotation(java.lang.String annotation) {
        this.annotation = annotation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GenericAnnotationFeature)) return false;
        GenericAnnotationFeature other = (GenericAnnotationFeature) obj;
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
              this.featureSubtype.equals(other.getFeatureSubtype()))) &&
            ((this.scale==null && other.getScale()==null) || 
             (this.scale!=null &&
              this.scale.equals(other.getScale()))) &&
            ((this.annotation==null && other.getAnnotation()==null) || 
             (this.annotation!=null &&
              this.annotation.equals(other.getAnnotation())));
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
        if (getScale() != null) {
            _hashCode += getScale().hashCode();
        }
        if (getAnnotation() != null) {
            _hashCode += getAnnotation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GenericAnnotationFeature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scale");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("annotation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "annotation"));
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
