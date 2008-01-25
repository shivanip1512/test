/**
 * CancelODMonitoringRequestByObjectResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CancelODMonitoringRequestByObjectResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] cancelODMonitoringRequestByObjectResult;

    public CancelODMonitoringRequestByObjectResponse() {
    }

    public CancelODMonitoringRequestByObjectResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] cancelODMonitoringRequestByObjectResult) {
           this.cancelODMonitoringRequestByObjectResult = cancelODMonitoringRequestByObjectResult;
    }


    /**
     * Gets the cancelODMonitoringRequestByObjectResult value for this CancelODMonitoringRequestByObjectResponse.
     * 
     * @return cancelODMonitoringRequestByObjectResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getCancelODMonitoringRequestByObjectResult() {
        return cancelODMonitoringRequestByObjectResult;
    }


    /**
     * Sets the cancelODMonitoringRequestByObjectResult value for this CancelODMonitoringRequestByObjectResponse.
     * 
     * @param cancelODMonitoringRequestByObjectResult
     */
    public void setCancelODMonitoringRequestByObjectResult(com.cannontech.multispeak.deploy.service.ErrorObject[] cancelODMonitoringRequestByObjectResult) {
        this.cancelODMonitoringRequestByObjectResult = cancelODMonitoringRequestByObjectResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CancelODMonitoringRequestByObjectResponse)) return false;
        CancelODMonitoringRequestByObjectResponse other = (CancelODMonitoringRequestByObjectResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cancelODMonitoringRequestByObjectResult==null && other.getCancelODMonitoringRequestByObjectResult()==null) || 
             (this.cancelODMonitoringRequestByObjectResult!=null &&
              java.util.Arrays.equals(this.cancelODMonitoringRequestByObjectResult, other.getCancelODMonitoringRequestByObjectResult())));
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
        if (getCancelODMonitoringRequestByObjectResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCancelODMonitoringRequestByObjectResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCancelODMonitoringRequestByObjectResult(), i);
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
        new org.apache.axis.description.TypeDesc(CancelODMonitoringRequestByObjectResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CancelODMonitoringRequestByObjectResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cancelODMonitoringRequestByObjectResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelODMonitoringRequestByObjectResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
