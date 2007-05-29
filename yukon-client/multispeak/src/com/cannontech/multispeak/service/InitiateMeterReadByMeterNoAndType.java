/**
 * InitiateMeterReadByMeterNoAndType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class InitiateMeterReadByMeterNoAndType  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ArrayOfString meterNos;
    private java.lang.String responseURL;
    private java.lang.String readingType;

    public InitiateMeterReadByMeterNoAndType() {
    }

    public InitiateMeterReadByMeterNoAndType(
           com.cannontech.multispeak.service.ArrayOfString meterNos,
           java.lang.String responseURL,
           java.lang.String readingType) {
           this.meterNos = meterNos;
           this.responseURL = responseURL;
           this.readingType = readingType;
    }


    /**
     * Gets the meterNos value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @return meterNos
     */
    public com.cannontech.multispeak.service.ArrayOfString getMeterNos() {
        return meterNos;
    }


    /**
     * Sets the meterNos value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @param meterNos
     */
    public void setMeterNos(com.cannontech.multispeak.service.ArrayOfString meterNos) {
        this.meterNos = meterNos;
    }


    /**
     * Gets the responseURL value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @return responseURL
     */
    public java.lang.String getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @param responseURL
     */
    public void setResponseURL(java.lang.String responseURL) {
        this.responseURL = responseURL;
    }


    /**
     * Gets the readingType value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @return readingType
     */
    public java.lang.String getReadingType() {
        return readingType;
    }


    /**
     * Sets the readingType value for this InitiateMeterReadByMeterNoAndType.
     * 
     * @param readingType
     */
    public void setReadingType(java.lang.String readingType) {
        this.readingType = readingType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateMeterReadByMeterNoAndType)) return false;
        InitiateMeterReadByMeterNoAndType other = (InitiateMeterReadByMeterNoAndType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterNos==null && other.getMeterNos()==null) || 
             (this.meterNos!=null &&
              this.meterNos.equals(other.getMeterNos()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL()))) &&
            ((this.readingType==null && other.getReadingType()==null) || 
             (this.readingType!=null &&
              this.readingType.equals(other.getReadingType())));
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
        if (getMeterNos() != null) {
            _hashCode += getMeterNos().hashCode();
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        if (getReadingType() != null) {
            _hashCode += getReadingType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateMeterReadByMeterNoAndType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateMeterReadByMeterNoAndType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
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
