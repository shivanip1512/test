/**
 * CancelDisconnectedStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CancelDisconnectedStatusResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatusResult;

    public CancelDisconnectedStatusResponse() {
    }

    public CancelDisconnectedStatusResponse(
           com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatusResult) {
           this.cancelDisconnectedStatusResult = cancelDisconnectedStatusResult;
    }


    /**
     * Gets the cancelDisconnectedStatusResult value for this CancelDisconnectedStatusResponse.
     * 
     * @return cancelDisconnectedStatusResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getCancelDisconnectedStatusResult() {
        return cancelDisconnectedStatusResult;
    }


    /**
     * Sets the cancelDisconnectedStatusResult value for this CancelDisconnectedStatusResponse.
     * 
     * @param cancelDisconnectedStatusResult
     */
    public void setCancelDisconnectedStatusResult(com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatusResult) {
        this.cancelDisconnectedStatusResult = cancelDisconnectedStatusResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CancelDisconnectedStatusResponse)) return false;
        CancelDisconnectedStatusResponse other = (CancelDisconnectedStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cancelDisconnectedStatusResult==null && other.getCancelDisconnectedStatusResult()==null) || 
             (this.cancelDisconnectedStatusResult!=null &&
              this.cancelDisconnectedStatusResult.equals(other.getCancelDisconnectedStatusResult())));
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
        if (getCancelDisconnectedStatusResult() != null) {
            _hashCode += getCancelDisconnectedStatusResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CancelDisconnectedStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CancelDisconnectedStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cancelDisconnectedStatusResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CancelDisconnectedStatusResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
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
