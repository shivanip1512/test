/**
 * GetServiceLocationByGridLocationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetServiceLocationByGridLocationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByGridLocationResult;

    public GetServiceLocationByGridLocationResponse() {
    }

    public GetServiceLocationByGridLocationResponse(
           com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByGridLocationResult) {
           this.getServiceLocationByGridLocationResult = getServiceLocationByGridLocationResult;
    }


    /**
     * Gets the getServiceLocationByGridLocationResult value for this GetServiceLocationByGridLocationResponse.
     * 
     * @return getServiceLocationByGridLocationResult
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getGetServiceLocationByGridLocationResult() {
        return getServiceLocationByGridLocationResult;
    }


    /**
     * Sets the getServiceLocationByGridLocationResult value for this GetServiceLocationByGridLocationResponse.
     * 
     * @param getServiceLocationByGridLocationResult
     */
    public void setGetServiceLocationByGridLocationResult(com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByGridLocationResult) {
        this.getServiceLocationByGridLocationResult = getServiceLocationByGridLocationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByGridLocationResponse)) return false;
        GetServiceLocationByGridLocationResponse other = (GetServiceLocationByGridLocationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByGridLocationResult==null && other.getGetServiceLocationByGridLocationResult()==null) || 
             (this.getServiceLocationByGridLocationResult!=null &&
              java.util.Arrays.equals(this.getServiceLocationByGridLocationResult, other.getGetServiceLocationByGridLocationResult())));
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
        if (getGetServiceLocationByGridLocationResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetServiceLocationByGridLocationResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetServiceLocationByGridLocationResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetServiceLocationByGridLocationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByGridLocationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocationResult"));
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
