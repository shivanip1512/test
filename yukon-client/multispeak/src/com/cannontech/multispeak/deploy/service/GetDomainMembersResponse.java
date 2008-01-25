/**
 * GetDomainMembersResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetDomainMembersResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembersResult;

    public GetDomainMembersResponse() {
    }

    public GetDomainMembersResponse(
           com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembersResult) {
           this.getDomainMembersResult = getDomainMembersResult;
    }


    /**
     * Gets the getDomainMembersResult value for this GetDomainMembersResponse.
     * 
     * @return getDomainMembersResult
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getGetDomainMembersResult() {
        return getDomainMembersResult;
    }


    /**
     * Sets the getDomainMembersResult value for this GetDomainMembersResponse.
     * 
     * @param getDomainMembersResult
     */
    public void setGetDomainMembersResult(com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembersResult) {
        this.getDomainMembersResult = getDomainMembersResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetDomainMembersResponse)) return false;
        GetDomainMembersResponse other = (GetDomainMembersResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getDomainMembersResult==null && other.getGetDomainMembersResult()==null) || 
             (this.getDomainMembersResult!=null &&
              java.util.Arrays.equals(this.getDomainMembersResult, other.getGetDomainMembersResult())));
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
        if (getGetDomainMembersResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetDomainMembersResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetDomainMembersResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetDomainMembersResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetDomainMembersResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getDomainMembersResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetDomainMembersResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
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
