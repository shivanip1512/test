/**
 * GetLatestReadingByType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetLatestReadingByType  implements java.io.Serializable {
    private java.lang.String readingType;

    private java.lang.String lastReceived;

    private java.lang.String formattedBlockTemplateName;

    private java.lang.String[] fieldName;

    public GetLatestReadingByType() {
    }

    public GetLatestReadingByType(
           java.lang.String readingType,
           java.lang.String lastReceived,
           java.lang.String formattedBlockTemplateName,
           java.lang.String[] fieldName) {
           this.readingType = readingType;
           this.lastReceived = lastReceived;
           this.formattedBlockTemplateName = formattedBlockTemplateName;
           this.fieldName = fieldName;
    }


    /**
     * Gets the readingType value for this GetLatestReadingByType.
     * 
     * @return readingType
     */
    public java.lang.String getReadingType() {
        return readingType;
    }


    /**
     * Sets the readingType value for this GetLatestReadingByType.
     * 
     * @param readingType
     */
    public void setReadingType(java.lang.String readingType) {
        this.readingType = readingType;
    }


    /**
     * Gets the lastReceived value for this GetLatestReadingByType.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetLatestReadingByType.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }


    /**
     * Gets the formattedBlockTemplateName value for this GetLatestReadingByType.
     * 
     * @return formattedBlockTemplateName
     */
    public java.lang.String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }


    /**
     * Sets the formattedBlockTemplateName value for this GetLatestReadingByType.
     * 
     * @param formattedBlockTemplateName
     */
    public void setFormattedBlockTemplateName(java.lang.String formattedBlockTemplateName) {
        this.formattedBlockTemplateName = formattedBlockTemplateName;
    }


    /**
     * Gets the fieldName value for this GetLatestReadingByType.
     * 
     * @return fieldName
     */
    public java.lang.String[] getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this GetLatestReadingByType.
     * 
     * @param fieldName
     */
    public void setFieldName(java.lang.String[] fieldName) {
        this.fieldName = fieldName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLatestReadingByType)) return false;
        GetLatestReadingByType other = (GetLatestReadingByType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.readingType==null && other.getReadingType()==null) || 
             (this.readingType!=null &&
              this.readingType.equals(other.getReadingType()))) &&
            ((this.lastReceived==null && other.getLastReceived()==null) || 
             (this.lastReceived!=null &&
              this.lastReceived.equals(other.getLastReceived()))) &&
            ((this.formattedBlockTemplateName==null && other.getFormattedBlockTemplateName()==null) || 
             (this.formattedBlockTemplateName!=null &&
              this.formattedBlockTemplateName.equals(other.getFormattedBlockTemplateName()))) &&
            ((this.fieldName==null && other.getFieldName()==null) || 
             (this.fieldName!=null &&
              java.util.Arrays.equals(this.fieldName, other.getFieldName())));
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
        if (getReadingType() != null) {
            _hashCode += getReadingType().hashCode();
        }
        if (getLastReceived() != null) {
            _hashCode += getLastReceived().hashCode();
        }
        if (getFormattedBlockTemplateName() != null) {
            _hashCode += getFormattedBlockTemplateName().hashCode();
        }
        if (getFieldName() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFieldName());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFieldName(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetLatestReadingByType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLatestReadingByType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastReceived");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastReceived"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formattedBlockTemplateName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplateName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fieldName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
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
