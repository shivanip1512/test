/**
 * GetBillingDataResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetBillingDataResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.BillingData[] getBillingDataResult;

    public GetBillingDataResponse() {
    }

    public GetBillingDataResponse(
           com.cannontech.multispeak.deploy.service.BillingData[] getBillingDataResult) {
           this.getBillingDataResult = getBillingDataResult;
    }


    /**
     * Gets the getBillingDataResult value for this GetBillingDataResponse.
     * 
     * @return getBillingDataResult
     */
    public com.cannontech.multispeak.deploy.service.BillingData[] getGetBillingDataResult() {
        return getBillingDataResult;
    }


    /**
     * Sets the getBillingDataResult value for this GetBillingDataResponse.
     * 
     * @param getBillingDataResult
     */
    public void setGetBillingDataResult(com.cannontech.multispeak.deploy.service.BillingData[] getBillingDataResult) {
        this.getBillingDataResult = getBillingDataResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetBillingDataResponse)) return false;
        GetBillingDataResponse other = (GetBillingDataResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getBillingDataResult==null && other.getGetBillingDataResult()==null) || 
             (this.getBillingDataResult!=null &&
              java.util.Arrays.equals(this.getBillingDataResult, other.getGetBillingDataResult())));
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
        if (getGetBillingDataResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetBillingDataResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetBillingDataResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetBillingDataResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetBillingDataResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getBillingDataResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetBillingDataResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingData"));
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
