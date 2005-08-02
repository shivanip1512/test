/**
 * InitiateOutageDetectionEventRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class InitiateOutageDetectionEventRequest  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfString meterNos;
    private java.util.Calendar requestDate;

    public InitiateOutageDetectionEventRequest() {
    }

    public InitiateOutageDetectionEventRequest(
           com.cannontech.multispeak.ArrayOfString meterNos,
           java.util.Calendar requestDate) {
           this.meterNos = meterNos;
           this.requestDate = requestDate;
    }


    /**
     * Gets the meterNos value for this InitiateOutageDetectionEventRequest.
     * 
     * @return meterNos
     */
    public com.cannontech.multispeak.ArrayOfString getMeterNos() {
        return meterNos;
    }


    /**
     * Sets the meterNos value for this InitiateOutageDetectionEventRequest.
     * 
     * @param meterNos
     */
    public void setMeterNos(com.cannontech.multispeak.ArrayOfString meterNos) {
        this.meterNos = meterNos;
    }


    /**
     * Gets the requestDate value for this InitiateOutageDetectionEventRequest.
     * 
     * @return requestDate
     */
    public java.util.Calendar getRequestDate() {
        return requestDate;
    }


    /**
     * Sets the requestDate value for this InitiateOutageDetectionEventRequest.
     * 
     * @param requestDate
     */
    public void setRequestDate(java.util.Calendar requestDate) {
        this.requestDate = requestDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InitiateOutageDetectionEventRequest)) return false;
        InitiateOutageDetectionEventRequest other = (InitiateOutageDetectionEventRequest) obj;
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
            ((this.requestDate==null && other.getRequestDate()==null) || 
             (this.requestDate!=null &&
              this.requestDate.equals(other.getRequestDate())));
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
        if (getRequestDate() != null) {
            _hashCode += getRequestDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InitiateOutageDetectionEventRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InitiateOutageDetectionEventRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
