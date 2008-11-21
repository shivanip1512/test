/**
 * GetShortCircuitAnalysisResultsByObjectIDResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetShortCircuitAnalysisResultsByObjectIDResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult getShortCircuitAnalysisResultsByObjectIDResult;

    public GetShortCircuitAnalysisResultsByObjectIDResponse() {
    }

    public GetShortCircuitAnalysisResultsByObjectIDResponse(
           com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult getShortCircuitAnalysisResultsByObjectIDResult) {
           this.getShortCircuitAnalysisResultsByObjectIDResult = getShortCircuitAnalysisResultsByObjectIDResult;
    }


    /**
     * Gets the getShortCircuitAnalysisResultsByObjectIDResult value for this GetShortCircuitAnalysisResultsByObjectIDResponse.
     * 
     * @return getShortCircuitAnalysisResultsByObjectIDResult
     */
    public com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult getGetShortCircuitAnalysisResultsByObjectIDResult() {
        return getShortCircuitAnalysisResultsByObjectIDResult;
    }


    /**
     * Sets the getShortCircuitAnalysisResultsByObjectIDResult value for this GetShortCircuitAnalysisResultsByObjectIDResponse.
     * 
     * @param getShortCircuitAnalysisResultsByObjectIDResult
     */
    public void setGetShortCircuitAnalysisResultsByObjectIDResult(com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult getShortCircuitAnalysisResultsByObjectIDResult) {
        this.getShortCircuitAnalysisResultsByObjectIDResult = getShortCircuitAnalysisResultsByObjectIDResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetShortCircuitAnalysisResultsByObjectIDResponse)) return false;
        GetShortCircuitAnalysisResultsByObjectIDResponse other = (GetShortCircuitAnalysisResultsByObjectIDResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getShortCircuitAnalysisResultsByObjectIDResult==null && other.getGetShortCircuitAnalysisResultsByObjectIDResult()==null) || 
             (this.getShortCircuitAnalysisResultsByObjectIDResult!=null &&
              this.getShortCircuitAnalysisResultsByObjectIDResult.equals(other.getGetShortCircuitAnalysisResultsByObjectIDResult())));
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
        if (getGetShortCircuitAnalysisResultsByObjectIDResult() != null) {
            _hashCode += getGetShortCircuitAnalysisResultsByObjectIDResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetShortCircuitAnalysisResultsByObjectIDResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetShortCircuitAnalysisResultsByObjectIDResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getShortCircuitAnalysisResultsByObjectIDResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetShortCircuitAnalysisResultsByObjectIDResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "shortCircuitAnalysisResult"));
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
