/**
 * GetServiceLocationByCustIdResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByCustIdResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustIdResult;

    public GetServiceLocationByCustIdResponse() {
    }

    public GetServiceLocationByCustIdResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustIdResult) {
           this.getServiceLocationByCustIdResult = getServiceLocationByCustIdResult;
    }


    /**
     * Gets the getServiceLocationByCustIdResult value for this GetServiceLocationByCustIdResponse.
     * 
     * @return getServiceLocationByCustIdResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetServiceLocationByCustIdResult() {
        return getServiceLocationByCustIdResult;
    }


    /**
     * Sets the getServiceLocationByCustIdResult value for this GetServiceLocationByCustIdResponse.
     * 
     * @param getServiceLocationByCustIdResult
     */
    public void setGetServiceLocationByCustIdResult(com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustIdResult) {
        this.getServiceLocationByCustIdResult = getServiceLocationByCustIdResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByCustIdResponse)) return false;
        GetServiceLocationByCustIdResponse other = (GetServiceLocationByCustIdResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByCustIdResult==null && other.getGetServiceLocationByCustIdResult()==null) || 
             (this.getServiceLocationByCustIdResult!=null &&
              this.getServiceLocationByCustIdResult.equals(other.getGetServiceLocationByCustIdResult())));
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
        if (getGetServiceLocationByCustIdResult() != null) {
            _hashCode += getGetServiceLocationByCustIdResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByCustIdResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByCustIdResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByCustIdResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByCustIdResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
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
