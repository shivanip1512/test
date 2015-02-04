/**
 * GetReadingsByDateFormattedBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetReadingsByDateFormattedBlock  implements java.io.Serializable {
    private java.util.Calendar billingDate;

    private int kWhLookBack;

    private int kWLookBack;

    private int kWLookForward;

    private java.lang.String lastReceived;

    private java.lang.String formattedBlockTemplateName;

    private java.lang.String[] fieldName;

    public GetReadingsByDateFormattedBlock() {
    }

    public GetReadingsByDateFormattedBlock(
           java.util.Calendar billingDate,
           int kWhLookBack,
           int kWLookBack,
           int kWLookForward,
           java.lang.String lastReceived,
           java.lang.String formattedBlockTemplateName,
           java.lang.String[] fieldName) {
           this.billingDate = billingDate;
           this.kWhLookBack = kWhLookBack;
           this.kWLookBack = kWLookBack;
           this.kWLookForward = kWLookForward;
           this.lastReceived = lastReceived;
           this.formattedBlockTemplateName = formattedBlockTemplateName;
           this.fieldName = fieldName;
    }


    /**
     * Gets the billingDate value for this GetReadingsByDateFormattedBlock.
     * 
     * @return billingDate
     */
    public java.util.Calendar getBillingDate() {
        return billingDate;
    }


    /**
     * Sets the billingDate value for this GetReadingsByDateFormattedBlock.
     * 
     * @param billingDate
     */
    public void setBillingDate(java.util.Calendar billingDate) {
        this.billingDate = billingDate;
    }


    /**
     * Gets the kWhLookBack value for this GetReadingsByDateFormattedBlock.
     * 
     * @return kWhLookBack
     */
    public int getKWhLookBack() {
        return kWhLookBack;
    }


    /**
     * Sets the kWhLookBack value for this GetReadingsByDateFormattedBlock.
     * 
     * @param kWhLookBack
     */
    public void setKWhLookBack(int kWhLookBack) {
        this.kWhLookBack = kWhLookBack;
    }


    /**
     * Gets the kWLookBack value for this GetReadingsByDateFormattedBlock.
     * 
     * @return kWLookBack
     */
    public int getKWLookBack() {
        return kWLookBack;
    }


    /**
     * Sets the kWLookBack value for this GetReadingsByDateFormattedBlock.
     * 
     * @param kWLookBack
     */
    public void setKWLookBack(int kWLookBack) {
        this.kWLookBack = kWLookBack;
    }


    /**
     * Gets the kWLookForward value for this GetReadingsByDateFormattedBlock.
     * 
     * @return kWLookForward
     */
    public int getKWLookForward() {
        return kWLookForward;
    }


    /**
     * Sets the kWLookForward value for this GetReadingsByDateFormattedBlock.
     * 
     * @param kWLookForward
     */
    public void setKWLookForward(int kWLookForward) {
        this.kWLookForward = kWLookForward;
    }


    /**
     * Gets the lastReceived value for this GetReadingsByDateFormattedBlock.
     * 
     * @return lastReceived
     */
    public java.lang.String getLastReceived() {
        return lastReceived;
    }


    /**
     * Sets the lastReceived value for this GetReadingsByDateFormattedBlock.
     * 
     * @param lastReceived
     */
    public void setLastReceived(java.lang.String lastReceived) {
        this.lastReceived = lastReceived;
    }


    /**
     * Gets the formattedBlockTemplateName value for this GetReadingsByDateFormattedBlock.
     * 
     * @return formattedBlockTemplateName
     */
    public java.lang.String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }


    /**
     * Sets the formattedBlockTemplateName value for this GetReadingsByDateFormattedBlock.
     * 
     * @param formattedBlockTemplateName
     */
    public void setFormattedBlockTemplateName(java.lang.String formattedBlockTemplateName) {
        this.formattedBlockTemplateName = formattedBlockTemplateName;
    }


    /**
     * Gets the fieldName value for this GetReadingsByDateFormattedBlock.
     * 
     * @return fieldName
     */
    public java.lang.String[] getFieldName() {
        return fieldName;
    }


    /**
     * Sets the fieldName value for this GetReadingsByDateFormattedBlock.
     * 
     * @param fieldName
     */
    public void setFieldName(java.lang.String[] fieldName) {
        this.fieldName = fieldName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetReadingsByDateFormattedBlock)) return false;
        GetReadingsByDateFormattedBlock other = (GetReadingsByDateFormattedBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.billingDate==null && other.getBillingDate()==null) || 
             (this.billingDate!=null &&
              this.billingDate.equals(other.getBillingDate()))) &&
            this.kWhLookBack == other.getKWhLookBack() &&
            this.kWLookBack == other.getKWLookBack() &&
            this.kWLookForward == other.getKWLookForward() &&
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
        if (getBillingDate() != null) {
            _hashCode += getBillingDate().hashCode();
        }
        _hashCode += getKWhLookBack();
        _hashCode += getKWLookBack();
        _hashCode += getKWLookForward();
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
        new org.apache.axis.description.TypeDesc(GetReadingsByDateFormattedBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetReadingsByDateFormattedBlock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWhLookBack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWhLookBack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWLookBack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookBack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWLookForward");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWLookForward"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
