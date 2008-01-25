/**
 * ModifyCBDataForCustomerResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ModifyCBDataForCustomerResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForCustomerResult;

    public ModifyCBDataForCustomerResponse() {
    }

    public ModifyCBDataForCustomerResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForCustomerResult) {
           this.modifyCBDataForCustomerResult = modifyCBDataForCustomerResult;
    }


    /**
     * Gets the modifyCBDataForCustomerResult value for this ModifyCBDataForCustomerResponse.
     * 
     * @return modifyCBDataForCustomerResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getModifyCBDataForCustomerResult() {
        return modifyCBDataForCustomerResult;
    }


    /**
     * Sets the modifyCBDataForCustomerResult value for this ModifyCBDataForCustomerResponse.
     * 
     * @param modifyCBDataForCustomerResult
     */
    public void setModifyCBDataForCustomerResult(com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForCustomerResult) {
        this.modifyCBDataForCustomerResult = modifyCBDataForCustomerResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModifyCBDataForCustomerResponse)) return false;
        ModifyCBDataForCustomerResponse other = (ModifyCBDataForCustomerResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modifyCBDataForCustomerResult==null && other.getModifyCBDataForCustomerResult()==null) || 
             (this.modifyCBDataForCustomerResult!=null &&
              java.util.Arrays.equals(this.modifyCBDataForCustomerResult, other.getModifyCBDataForCustomerResult())));
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
        if (getModifyCBDataForCustomerResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModifyCBDataForCustomerResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModifyCBDataForCustomerResult(), i);
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
        new org.apache.axis.description.TypeDesc(ModifyCBDataForCustomerResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForCustomerResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifyCBDataForCustomerResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForCustomerResult"));
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
