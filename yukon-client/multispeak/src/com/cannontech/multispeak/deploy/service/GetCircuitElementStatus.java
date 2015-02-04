/**
 * GetCircuitElementStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetCircuitElementStatus  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ObjectRef cktElementRef;

    public GetCircuitElementStatus() {
    }

    public GetCircuitElementStatus(
           com.cannontech.multispeak.deploy.service.ObjectRef cktElementRef) {
           this.cktElementRef = cktElementRef;
    }


    /**
     * Gets the cktElementRef value for this GetCircuitElementStatus.
     * 
     * @return cktElementRef
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getCktElementRef() {
        return cktElementRef;
    }


    /**
     * Sets the cktElementRef value for this GetCircuitElementStatus.
     * 
     * @param cktElementRef
     */
    public void setCktElementRef(com.cannontech.multispeak.deploy.service.ObjectRef cktElementRef) {
        this.cktElementRef = cktElementRef;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCircuitElementStatus)) return false;
        GetCircuitElementStatus other = (GetCircuitElementStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cktElementRef==null && other.getCktElementRef()==null) || 
             (this.cktElementRef!=null &&
              this.cktElementRef.equals(other.getCktElementRef())));
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
        if (getCktElementRef() != null) {
            _hashCode += getCktElementRef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCircuitElementStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetCircuitElementStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cktElementRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cktElementRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
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
