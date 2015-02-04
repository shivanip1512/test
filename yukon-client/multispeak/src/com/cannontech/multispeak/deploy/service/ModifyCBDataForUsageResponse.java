/**
 * ModifyCBDataForUsageResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ModifyCBDataForUsageResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForUsageResult;

    public ModifyCBDataForUsageResponse() {
    }

    public ModifyCBDataForUsageResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForUsageResult) {
           this.modifyCBDataForUsageResult = modifyCBDataForUsageResult;
    }


    /**
     * Gets the modifyCBDataForUsageResult value for this ModifyCBDataForUsageResponse.
     * 
     * @return modifyCBDataForUsageResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getModifyCBDataForUsageResult() {
        return modifyCBDataForUsageResult;
    }


    /**
     * Sets the modifyCBDataForUsageResult value for this ModifyCBDataForUsageResponse.
     * 
     * @param modifyCBDataForUsageResult
     */
    public void setModifyCBDataForUsageResult(com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForUsageResult) {
        this.modifyCBDataForUsageResult = modifyCBDataForUsageResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModifyCBDataForUsageResponse)) return false;
        ModifyCBDataForUsageResponse other = (ModifyCBDataForUsageResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modifyCBDataForUsageResult==null && other.getModifyCBDataForUsageResult()==null) || 
             (this.modifyCBDataForUsageResult!=null &&
              java.util.Arrays.equals(this.modifyCBDataForUsageResult, other.getModifyCBDataForUsageResult())));
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
        if (getModifyCBDataForUsageResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModifyCBDataForUsageResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModifyCBDataForUsageResult(), i);
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
        new org.apache.axis.description.TypeDesc(ModifyCBDataForUsageResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ModifyCBDataForUsageResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifyCBDataForUsageResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ModifyCBDataForUsageResult"));
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
