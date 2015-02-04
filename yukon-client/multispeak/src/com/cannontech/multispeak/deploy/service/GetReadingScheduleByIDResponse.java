/**
 * GetReadingScheduleByIDResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetReadingScheduleByIDResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ReadingSchedule getReadingScheduleByIDResult;

    public GetReadingScheduleByIDResponse() {
    }

    public GetReadingScheduleByIDResponse(
           com.cannontech.multispeak.deploy.service.ReadingSchedule getReadingScheduleByIDResult) {
           this.getReadingScheduleByIDResult = getReadingScheduleByIDResult;
    }


    /**
     * Gets the getReadingScheduleByIDResult value for this GetReadingScheduleByIDResponse.
     * 
     * @return getReadingScheduleByIDResult
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule getGetReadingScheduleByIDResult() {
        return getReadingScheduleByIDResult;
    }


    /**
     * Sets the getReadingScheduleByIDResult value for this GetReadingScheduleByIDResponse.
     * 
     * @param getReadingScheduleByIDResult
     */
    public void setGetReadingScheduleByIDResult(com.cannontech.multispeak.deploy.service.ReadingSchedule getReadingScheduleByIDResult) {
        this.getReadingScheduleByIDResult = getReadingScheduleByIDResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetReadingScheduleByIDResponse)) return false;
        GetReadingScheduleByIDResponse other = (GetReadingScheduleByIDResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getReadingScheduleByIDResult==null && other.getGetReadingScheduleByIDResult()==null) || 
             (this.getReadingScheduleByIDResult!=null &&
              this.getReadingScheduleByIDResult.equals(other.getGetReadingScheduleByIDResult())));
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
        if (getGetReadingScheduleByIDResult() != null) {
            _hashCode += getGetReadingScheduleByIDResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetReadingScheduleByIDResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetReadingScheduleByIDResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getReadingScheduleByIDResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingScheduleByIDResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
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
