/**
 * GetServiceLocationByShutOffDateResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByShutOffDateResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDateResult;

    public GetServiceLocationByShutOffDateResponse() {
    }

    public GetServiceLocationByShutOffDateResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDateResult) {
           this.getServiceLocationByShutOffDateResult = getServiceLocationByShutOffDateResult;
    }


    /**
     * Gets the getServiceLocationByShutOffDateResult value for this GetServiceLocationByShutOffDateResponse.
     * 
     * @return getServiceLocationByShutOffDateResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetServiceLocationByShutOffDateResult() {
        return getServiceLocationByShutOffDateResult;
    }


    /**
     * Sets the getServiceLocationByShutOffDateResult value for this GetServiceLocationByShutOffDateResponse.
     * 
     * @param getServiceLocationByShutOffDateResult
     */
    public void setGetServiceLocationByShutOffDateResult(com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDateResult) {
        this.getServiceLocationByShutOffDateResult = getServiceLocationByShutOffDateResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByShutOffDateResponse)) return false;
        GetServiceLocationByShutOffDateResponse other = (GetServiceLocationByShutOffDateResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByShutOffDateResult==null && other.getGetServiceLocationByShutOffDateResult()==null) || 
             (this.getServiceLocationByShutOffDateResult!=null &&
              this.getServiceLocationByShutOffDateResult.equals(other.getGetServiceLocationByShutOffDateResult())));
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
        if (getGetServiceLocationByShutOffDateResult() != null) {
            _hashCode += getGetServiceLocationByShutOffDateResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByShutOffDateResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByShutOffDateResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByShutOffDateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByShutOffDateResult"));
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
