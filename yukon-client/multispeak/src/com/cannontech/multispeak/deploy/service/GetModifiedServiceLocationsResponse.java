/**
 * GetModifiedServiceLocationsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetModifiedServiceLocationsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocationsResult;

    public GetModifiedServiceLocationsResponse() {
    }

    public GetModifiedServiceLocationsResponse(
           com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocationsResult) {
           this.getModifiedServiceLocationsResult = getModifiedServiceLocationsResult;
    }


    /**
     * Gets the getModifiedServiceLocationsResult value for this GetModifiedServiceLocationsResponse.
     * 
     * @return getModifiedServiceLocationsResult
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getGetModifiedServiceLocationsResult() {
        return getModifiedServiceLocationsResult;
    }


    /**
     * Sets the getModifiedServiceLocationsResult value for this GetModifiedServiceLocationsResponse.
     * 
     * @param getModifiedServiceLocationsResult
     */
    public void setGetModifiedServiceLocationsResult(com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocationsResult) {
        this.getModifiedServiceLocationsResult = getModifiedServiceLocationsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedServiceLocationsResponse)) return false;
        GetModifiedServiceLocationsResponse other = (GetModifiedServiceLocationsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedServiceLocationsResult==null && other.getGetModifiedServiceLocationsResult()==null) || 
             (this.getModifiedServiceLocationsResult!=null &&
              java.util.Arrays.equals(this.getModifiedServiceLocationsResult, other.getGetModifiedServiceLocationsResult())));
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
        if (getGetModifiedServiceLocationsResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetModifiedServiceLocationsResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetModifiedServiceLocationsResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetModifiedServiceLocationsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocationsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedServiceLocationsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocationsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
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
