/**
 * AddRemarkToOutageResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AddRemarkToOutageResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutageResult;

    public AddRemarkToOutageResponse() {
    }

    public AddRemarkToOutageResponse(
           com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutageResult) {
           this.addRemarkToOutageResult = addRemarkToOutageResult;
    }


    /**
     * Gets the addRemarkToOutageResult value for this AddRemarkToOutageResponse.
     * 
     * @return addRemarkToOutageResult
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject getAddRemarkToOutageResult() {
        return addRemarkToOutageResult;
    }


    /**
     * Sets the addRemarkToOutageResult value for this AddRemarkToOutageResponse.
     * 
     * @param addRemarkToOutageResult
     */
    public void setAddRemarkToOutageResult(com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutageResult) {
        this.addRemarkToOutageResult = addRemarkToOutageResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AddRemarkToOutageResponse)) return false;
        AddRemarkToOutageResponse other = (AddRemarkToOutageResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addRemarkToOutageResult==null && other.getAddRemarkToOutageResult()==null) || 
             (this.addRemarkToOutageResult!=null &&
              this.addRemarkToOutageResult.equals(other.getAddRemarkToOutageResult())));
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
        if (getAddRemarkToOutageResult() != null) {
            _hashCode += getAddRemarkToOutageResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddRemarkToOutageResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">AddRemarkToOutageResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addRemarkToOutageResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AddRemarkToOutageResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "errorObject"));
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
