/**
 * ReadingScheduleResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingScheduleResult  implements java.io.Serializable {
    private java.lang.String readingScheduleID;

    private com.cannontech.multispeak.deploy.service.ReadingScheduleResultResult result;

    private java.lang.String reason;

    public ReadingScheduleResult() {
    }

    public ReadingScheduleResult(
           java.lang.String readingScheduleID,
           com.cannontech.multispeak.deploy.service.ReadingScheduleResultResult result,
           java.lang.String reason) {
           this.readingScheduleID = readingScheduleID;
           this.result = result;
           this.reason = reason;
    }


    /**
     * Gets the readingScheduleID value for this ReadingScheduleResult.
     * 
     * @return readingScheduleID
     */
    public java.lang.String getReadingScheduleID() {
        return readingScheduleID;
    }


    /**
     * Sets the readingScheduleID value for this ReadingScheduleResult.
     * 
     * @param readingScheduleID
     */
    public void setReadingScheduleID(java.lang.String readingScheduleID) {
        this.readingScheduleID = readingScheduleID;
    }


    /**
     * Gets the result value for this ReadingScheduleResult.
     * 
     * @return result
     */
    public com.cannontech.multispeak.deploy.service.ReadingScheduleResultResult getResult() {
        return result;
    }


    /**
     * Sets the result value for this ReadingScheduleResult.
     * 
     * @param result
     */
    public void setResult(com.cannontech.multispeak.deploy.service.ReadingScheduleResultResult result) {
        this.result = result;
    }


    /**
     * Gets the reason value for this ReadingScheduleResult.
     * 
     * @return reason
     */
    public java.lang.String getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this ReadingScheduleResult.
     * 
     * @param reason
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingScheduleResult)) return false;
        ReadingScheduleResult other = (ReadingScheduleResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.readingScheduleID==null && other.getReadingScheduleID()==null) || 
             (this.readingScheduleID!=null &&
              this.readingScheduleID.equals(other.getReadingScheduleID()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason())));
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
        if (getReadingScheduleID() != null) {
            _hashCode += getReadingScheduleID().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadingScheduleResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingScheduleID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingScheduleID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">readingScheduleResult>result"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reason"));
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
