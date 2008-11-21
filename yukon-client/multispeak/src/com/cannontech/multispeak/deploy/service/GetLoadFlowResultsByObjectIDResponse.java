/**
 * GetLoadFlowResultsByObjectIDResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetLoadFlowResultsByObjectIDResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.LoadFlowResult getLoadFlowResultsByObjectIDResult;

    public GetLoadFlowResultsByObjectIDResponse() {
    }

    public GetLoadFlowResultsByObjectIDResponse(
           com.cannontech.multispeak.deploy.service.LoadFlowResult getLoadFlowResultsByObjectIDResult) {
           this.getLoadFlowResultsByObjectIDResult = getLoadFlowResultsByObjectIDResult;
    }


    /**
     * Gets the getLoadFlowResultsByObjectIDResult value for this GetLoadFlowResultsByObjectIDResponse.
     * 
     * @return getLoadFlowResultsByObjectIDResult
     */
    public com.cannontech.multispeak.deploy.service.LoadFlowResult getGetLoadFlowResultsByObjectIDResult() {
        return getLoadFlowResultsByObjectIDResult;
    }


    /**
     * Sets the getLoadFlowResultsByObjectIDResult value for this GetLoadFlowResultsByObjectIDResponse.
     * 
     * @param getLoadFlowResultsByObjectIDResult
     */
    public void setGetLoadFlowResultsByObjectIDResult(com.cannontech.multispeak.deploy.service.LoadFlowResult getLoadFlowResultsByObjectIDResult) {
        this.getLoadFlowResultsByObjectIDResult = getLoadFlowResultsByObjectIDResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetLoadFlowResultsByObjectIDResponse)) return false;
        GetLoadFlowResultsByObjectIDResponse other = (GetLoadFlowResultsByObjectIDResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getLoadFlowResultsByObjectIDResult==null && other.getGetLoadFlowResultsByObjectIDResult()==null) || 
             (this.getLoadFlowResultsByObjectIDResult!=null &&
              this.getLoadFlowResultsByObjectIDResult.equals(other.getGetLoadFlowResultsByObjectIDResult())));
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
        if (getGetLoadFlowResultsByObjectIDResult() != null) {
            _hashCode += getGetLoadFlowResultsByObjectIDResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetLoadFlowResultsByObjectIDResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetLoadFlowResultsByObjectIDResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getLoadFlowResultsByObjectIDResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetLoadFlowResultsByObjectIDResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadFlowResult"));
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
