/**
 * GetFormattedBlockTemplatesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetFormattedBlockTemplatesResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.FormattedBlockTemplate[] getFormattedBlockTemplatesResult;

    public GetFormattedBlockTemplatesResponse() {
    }

    public GetFormattedBlockTemplatesResponse(
           com.cannontech.multispeak.deploy.service.FormattedBlockTemplate[] getFormattedBlockTemplatesResult) {
           this.getFormattedBlockTemplatesResult = getFormattedBlockTemplatesResult;
    }


    /**
     * Gets the getFormattedBlockTemplatesResult value for this GetFormattedBlockTemplatesResponse.
     * 
     * @return getFormattedBlockTemplatesResult
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlockTemplate[] getGetFormattedBlockTemplatesResult() {
        return getFormattedBlockTemplatesResult;
    }


    /**
     * Sets the getFormattedBlockTemplatesResult value for this GetFormattedBlockTemplatesResponse.
     * 
     * @param getFormattedBlockTemplatesResult
     */
    public void setGetFormattedBlockTemplatesResult(com.cannontech.multispeak.deploy.service.FormattedBlockTemplate[] getFormattedBlockTemplatesResult) {
        this.getFormattedBlockTemplatesResult = getFormattedBlockTemplatesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetFormattedBlockTemplatesResponse)) return false;
        GetFormattedBlockTemplatesResponse other = (GetFormattedBlockTemplatesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getFormattedBlockTemplatesResult==null && other.getGetFormattedBlockTemplatesResult()==null) || 
             (this.getFormattedBlockTemplatesResult!=null &&
              java.util.Arrays.equals(this.getFormattedBlockTemplatesResult, other.getGetFormattedBlockTemplatesResult())));
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
        if (getGetFormattedBlockTemplatesResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetFormattedBlockTemplatesResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetFormattedBlockTemplatesResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetFormattedBlockTemplatesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetFormattedBlockTemplatesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getFormattedBlockTemplatesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetFormattedBlockTemplatesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlockTemplate"));
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
