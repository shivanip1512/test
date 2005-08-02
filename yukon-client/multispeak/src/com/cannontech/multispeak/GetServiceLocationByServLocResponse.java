/**
 * GetServiceLocationByServLocResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByServLocResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ServiceLocation getServiceLocationByServLocResult;

    public GetServiceLocationByServLocResponse() {
    }

    public GetServiceLocationByServLocResponse(
           com.cannontech.multispeak.ServiceLocation getServiceLocationByServLocResult) {
           this.getServiceLocationByServLocResult = getServiceLocationByServLocResult;
    }


    /**
     * Gets the getServiceLocationByServLocResult value for this GetServiceLocationByServLocResponse.
     * 
     * @return getServiceLocationByServLocResult
     */
    public com.cannontech.multispeak.ServiceLocation getGetServiceLocationByServLocResult() {
        return getServiceLocationByServLocResult;
    }


    /**
     * Sets the getServiceLocationByServLocResult value for this GetServiceLocationByServLocResponse.
     * 
     * @param getServiceLocationByServLocResult
     */
    public void setGetServiceLocationByServLocResult(com.cannontech.multispeak.ServiceLocation getServiceLocationByServLocResult) {
        this.getServiceLocationByServLocResult = getServiceLocationByServLocResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByServLocResponse)) return false;
        GetServiceLocationByServLocResponse other = (GetServiceLocationByServLocResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByServLocResult==null && other.getGetServiceLocationByServLocResult()==null) || 
             (this.getServiceLocationByServLocResult!=null &&
              this.getServiceLocationByServLocResult.equals(other.getGetServiceLocationByServLocResult())));
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
        if (getGetServiceLocationByServLocResult() != null) {
            _hashCode += getGetServiceLocationByServLocResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByServLocResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByServLocResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByServLocResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByServLocResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
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
