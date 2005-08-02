/**
 * GetServiceLocationByLoadGroupResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByLoadGroupResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroupResult;

    public GetServiceLocationByLoadGroupResponse() {
    }

    public GetServiceLocationByLoadGroupResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroupResult) {
           this.getServiceLocationByLoadGroupResult = getServiceLocationByLoadGroupResult;
    }


    /**
     * Gets the getServiceLocationByLoadGroupResult value for this GetServiceLocationByLoadGroupResponse.
     * 
     * @return getServiceLocationByLoadGroupResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetServiceLocationByLoadGroupResult() {
        return getServiceLocationByLoadGroupResult;
    }


    /**
     * Sets the getServiceLocationByLoadGroupResult value for this GetServiceLocationByLoadGroupResponse.
     * 
     * @param getServiceLocationByLoadGroupResult
     */
    public void setGetServiceLocationByLoadGroupResult(com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroupResult) {
        this.getServiceLocationByLoadGroupResult = getServiceLocationByLoadGroupResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByLoadGroupResponse)) return false;
        GetServiceLocationByLoadGroupResponse other = (GetServiceLocationByLoadGroupResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByLoadGroupResult==null && other.getGetServiceLocationByLoadGroupResult()==null) || 
             (this.getServiceLocationByLoadGroupResult!=null &&
              this.getServiceLocationByLoadGroupResult.equals(other.getGetServiceLocationByLoadGroupResult())));
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
        if (getGetServiceLocationByLoadGroupResult() != null) {
            _hashCode += getGetServiceLocationByLoadGroupResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByLoadGroupResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByLoadGroupResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByLoadGroupResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByLoadGroupResult"));
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
