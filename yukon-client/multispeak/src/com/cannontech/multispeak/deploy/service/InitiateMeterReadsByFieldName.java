/**
 * InitiateMeterReadsByFieldName.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InitiateMeterReadsByFieldName  implements java.io.Serializable {
    private java.lang.String[] meterNumbers;

    private java.lang.String[] fieldNames;

    private java.lang.String responseURL;

    private java.lang.String transactionID;

    private float expirationTime;

    private java.lang.String formattedBlockTemplateName;

    public InitiateMeterReadsByFieldName() {
    }

    public InitiateMeterReadsByFieldName(
           java.lang.String[] meterNumbers,
           java.lang.String[] fieldNames,
           java.lang.String responseURL,
           java.lang.String transactionID,
           float expirationTime,
           java.lang.String formattedBlockTemplateName) {
           this.meterNumbers = meterNumbers;
           this.fieldNames = fieldNames;
           this.responseURL = responseURL;
           this.transactionID = transactionID;
           this.expirationTime = expirationTime;
           this.formattedBlockTemplateName = formattedBlockTemplateName;
    }


    /**
     * Gets the meterNumbers value for this InitiateMeterReadsByFieldName.
     * 
     * @return meterNumbers
     */
    public java.lang.String[] getMeterNumbers() {
        return meterNumbers;
    }


    /**
     * Sets the meterNumbers value for this InitiateMeterReadsByFieldName.
     * 
     * @param meterNumbers
     */
    public void setMeterNumbers(java.lang.String[] meterNumbers) {
        this.meterNumbers = meterNumbers;
    }


    /**
     * Gets the fieldNames value for this InitiateMeterReadsByFieldName.
     * 
     * @return fieldNames
     */
    public java.lang.String[] getFieldNames() {
        return fieldNames;
    }


    /**
     * Sets the fieldNames value for this InitiateMeterReadsByFieldName.
     * 
     * @param fieldNames
     */
    public void setFieldNames(java.lang.String[] fieldNames) {
        this.fieldNames = fieldNames;
    }


    /**
     * Gets the responseURL value for this InitiateMeterReadsByFieldName.
     * 
     * @return responseURL
     */
    public java.lang.String getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this InitiateMeterReadsByFieldName.
     * 
     * @param responseURL
     */
    public void setResponseURL(java.lang.String responseURL) {
        this.responseURL = responseURL;
    }


    /**
     * Gets the transactionID value for this InitiateMeterReadsByFieldName.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this InitiateMeterReadsByFieldName.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the expirationTime value for this InitiateMeterReadsByFieldName.
     * 
     * @return expirationTime
     */
    public float getExpirationTime() {
        return expirationTime;
    }


    /**
     * Sets the expirationTime value for this InitiateMeterReadsByFieldName.
     * 
     * @param expirationTime
     */
    public void setExpirationTime(float expirationTime) {
        this.expirationTime = expirationTime;
    }


    /**
     * Gets the formattedBlockTemplateName value for this InitiateMeterReadsByFieldName.
     * 
     * @return formattedBlockTemplateName
     */
    public java.lang.String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }


    /**
     * Sets the formattedBlockTemplateName value for this InitiateMeterReadsByFieldName.
     * 
     * @param formattedBlockTemplateName
     */
    public void setFormattedBlockTemplateName(java.lang.String formattedBlockTemplateName) {
        this.formattedBlockTemplateName = formattedBlockTemplateName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateMeterReadsByFieldName)) return false;
        InitiateMeterReadsByFieldName other = (InitiateMeterReadsByFieldName) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterNumbers==null && other.getMeterNumbers()==null) || 
             (this.meterNumbers!=null &&
              java.util.Arrays.equals(this.meterNumbers, other.getMeterNumbers()))) &&
            ((this.fieldNames==null && other.getFieldNames()==null) || 
             (this.fieldNames!=null &&
              java.util.Arrays.equals(this.fieldNames, other.getFieldNames()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            this.expirationTime == other.getExpirationTime() &&
            ((this.formattedBlockTemplateName==null && other.getFormattedBlockTemplateName()==null) || 
             (this.formattedBlockTemplateName!=null &&
              this.formattedBlockTemplateName.equals(other.getFormattedBlockTemplateName())));
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
        if (getMeterNumbers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterNumbers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterNumbers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFieldNames() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFieldNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFieldNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        _hashCode += new Float(getExpirationTime()).hashCode();
        if (getFormattedBlockTemplateName() != null) {
            _hashCode += getFormattedBlockTemplateName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateMeterReadsByFieldName.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateMeterReadsByFieldName"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fieldNames");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fieldNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expirationTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "expirationTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formattedBlockTemplateName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplateName"));
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
