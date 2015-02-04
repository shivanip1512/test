/**
 * GetCDMeterStateResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetCDMeterStateResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterStateResult;

    public GetCDMeterStateResponse() {
    }

    public GetCDMeterStateResponse(
           com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterStateResult) {
           this.getCDMeterStateResult = getCDMeterStateResult;
    }


    /**
     * Gets the getCDMeterStateResult value for this GetCDMeterStateResponse.
     * 
     * @return getCDMeterStateResult
     */
    public com.cannontech.multispeak.deploy.service.LoadActionCode getGetCDMeterStateResult() {
        return getCDMeterStateResult;
    }


    /**
     * Sets the getCDMeterStateResult value for this GetCDMeterStateResponse.
     * 
     * @param getCDMeterStateResult
     */
    public void setGetCDMeterStateResult(com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterStateResult) {
        this.getCDMeterStateResult = getCDMeterStateResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCDMeterStateResponse)) return false;
        GetCDMeterStateResponse other = (GetCDMeterStateResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCDMeterStateResult==null && other.getGetCDMeterStateResult()==null) || 
             (this.getCDMeterStateResult!=null &&
              this.getCDMeterStateResult.equals(other.getGetCDMeterStateResult())));
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
        if (getGetCDMeterStateResult() != null) {
            _hashCode += getGetCDMeterStateResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCDMeterStateResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCDMeterStateResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCDMeterStateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetCDMeterStateResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
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
