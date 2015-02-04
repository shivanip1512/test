/**
 * GetAllGasServiceLocationsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetAllGasServiceLocationsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocationsResult;

    public GetAllGasServiceLocationsResponse() {
    }

    public GetAllGasServiceLocationsResponse(
           com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocationsResult) {
           this.getAllGasServiceLocationsResult = getAllGasServiceLocationsResult;
    }


    /**
     * Gets the getAllGasServiceLocationsResult value for this GetAllGasServiceLocationsResponse.
     * 
     * @return getAllGasServiceLocationsResult
     */
    public com.cannontech.multispeak.deploy.service.GasServiceLocation[] getGetAllGasServiceLocationsResult() {
        return getAllGasServiceLocationsResult;
    }


    /**
     * Sets the getAllGasServiceLocationsResult value for this GetAllGasServiceLocationsResponse.
     * 
     * @param getAllGasServiceLocationsResult
     */
    public void setGetAllGasServiceLocationsResult(com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocationsResult) {
        this.getAllGasServiceLocationsResult = getAllGasServiceLocationsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetAllGasServiceLocationsResponse)) return false;
        GetAllGasServiceLocationsResponse other = (GetAllGasServiceLocationsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getAllGasServiceLocationsResult==null && other.getGetAllGasServiceLocationsResult()==null) || 
             (this.getAllGasServiceLocationsResult!=null &&
              java.util.Arrays.equals(this.getAllGasServiceLocationsResult, other.getGetAllGasServiceLocationsResult())));
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
        if (getGetAllGasServiceLocationsResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetAllGasServiceLocationsResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetAllGasServiceLocationsResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetAllGasServiceLocationsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetAllGasServiceLocationsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getAllGasServiceLocationsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetAllGasServiceLocationsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation"));
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
