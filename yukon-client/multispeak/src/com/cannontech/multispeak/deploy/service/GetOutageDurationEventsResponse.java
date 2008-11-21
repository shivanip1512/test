/**
 * GetOutageDurationEventsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetOutageDurationEventsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageDurationEventsResult;

    public GetOutageDurationEventsResponse() {
    }

    public GetOutageDurationEventsResponse(
           com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageDurationEventsResult) {
           this.getOutageDurationEventsResult = getOutageDurationEventsResult;
    }


    /**
     * Gets the getOutageDurationEventsResult value for this GetOutageDurationEventsResponse.
     * 
     * @return getOutageDurationEventsResult
     */
    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getGetOutageDurationEventsResult() {
        return getOutageDurationEventsResult;
    }


    /**
     * Sets the getOutageDurationEventsResult value for this GetOutageDurationEventsResponse.
     * 
     * @param getOutageDurationEventsResult
     */
    public void setGetOutageDurationEventsResult(com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageDurationEventsResult) {
        this.getOutageDurationEventsResult = getOutageDurationEventsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetOutageDurationEventsResponse)) return false;
        GetOutageDurationEventsResponse other = (GetOutageDurationEventsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getOutageDurationEventsResult==null && other.getGetOutageDurationEventsResult()==null) || 
             (this.getOutageDurationEventsResult!=null &&
              java.util.Arrays.equals(this.getOutageDurationEventsResult, other.getGetOutageDurationEventsResult())));
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
        if (getGetOutageDurationEventsResult() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetOutageDurationEventsResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetOutageDurationEventsResult(), i);
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
        new org.apache.axis.description.TypeDesc(GetOutageDurationEventsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetOutageDurationEventsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getOutageDurationEventsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetOutageDurationEventsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDurationEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDurationEvent"));
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
