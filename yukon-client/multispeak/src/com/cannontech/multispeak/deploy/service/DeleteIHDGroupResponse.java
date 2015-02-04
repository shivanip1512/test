/**
 * DeleteIHDGroupResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class DeleteIHDGroupResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject[] deleteIHDGroupResult;

    public DeleteIHDGroupResponse() {
    }

    public DeleteIHDGroupResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject[] deleteIHDGroupResult) {
           this.deleteIHDGroupResult = deleteIHDGroupResult;
    }


    /**
     * Gets the deleteIHDGroupResult value for this DeleteIHDGroupResponse.
     * 
     * @return deleteIHDGroupResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] getDeleteIHDGroupResult() {
        return deleteIHDGroupResult;
    }


    /**
     * Sets the deleteIHDGroupResult value for this DeleteIHDGroupResponse.
     * 
     * @param deleteIHDGroupResult
     */
    public void setDeleteIHDGroupResult(com.cannontech.multispeak.deploy.service.ErrorObject[] deleteIHDGroupResult) {
        this.deleteIHDGroupResult = deleteIHDGroupResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeleteIHDGroupResponse)) return false;
        DeleteIHDGroupResponse other = (DeleteIHDGroupResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.deleteIHDGroupResult==null && other.getDeleteIHDGroupResult()==null) || 
             (this.deleteIHDGroupResult!=null &&
              java.util.Arrays.equals(this.deleteIHDGroupResult, other.getDeleteIHDGroupResult())));
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
        if (getDeleteIHDGroupResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDeleteIHDGroupResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDeleteIHDGroupResult(), i);
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
        new org.apache.axis.description.TypeDesc(DeleteIHDGroupResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DeleteIHDGroupResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deleteIHDGroupResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DeleteIHDGroupResult"));
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
