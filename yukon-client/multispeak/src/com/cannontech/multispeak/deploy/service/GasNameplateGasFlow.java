/**
 * GasNameplateGasFlow.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasNameplateGasFlow  implements java.io.Serializable {
    private java.lang.Object maxFlowRate;

    private com.cannontech.multispeak.deploy.service.Uom maxFlowRateUOM;

    public GasNameplateGasFlow() {
    }

    public GasNameplateGasFlow(
           java.lang.Object maxFlowRate,
           com.cannontech.multispeak.deploy.service.Uom maxFlowRateUOM) {
           this.maxFlowRate = maxFlowRate;
           this.maxFlowRateUOM = maxFlowRateUOM;
    }


    /**
     * Gets the maxFlowRate value for this GasNameplateGasFlow.
     * 
     * @return maxFlowRate
     */
    public java.lang.Object getMaxFlowRate() {
        return maxFlowRate;
    }


    /**
     * Sets the maxFlowRate value for this GasNameplateGasFlow.
     * 
     * @param maxFlowRate
     */
    public void setMaxFlowRate(java.lang.Object maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
    }


    /**
     * Gets the maxFlowRateUOM value for this GasNameplateGasFlow.
     * 
     * @return maxFlowRateUOM
     */
    public com.cannontech.multispeak.deploy.service.Uom getMaxFlowRateUOM() {
        return maxFlowRateUOM;
    }


    /**
     * Sets the maxFlowRateUOM value for this GasNameplateGasFlow.
     * 
     * @param maxFlowRateUOM
     */
    public void setMaxFlowRateUOM(com.cannontech.multispeak.deploy.service.Uom maxFlowRateUOM) {
        this.maxFlowRateUOM = maxFlowRateUOM;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GasNameplateGasFlow)) return false;
        GasNameplateGasFlow other = (GasNameplateGasFlow) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.maxFlowRate==null && other.getMaxFlowRate()==null) || 
             (this.maxFlowRate!=null &&
              this.maxFlowRate.equals(other.getMaxFlowRate()))) &&
            ((this.maxFlowRateUOM==null && other.getMaxFlowRateUOM()==null) || 
             (this.maxFlowRateUOM!=null &&
              this.maxFlowRateUOM.equals(other.getMaxFlowRateUOM())));
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
        if (getMaxFlowRate() != null) {
            _hashCode += getMaxFlowRate().hashCode();
        }
        if (getMaxFlowRateUOM() != null) {
            _hashCode += getMaxFlowRateUOM().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GasNameplateGasFlow.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasFlow"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxFlowRate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxFlowRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxFlowRateUOM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxFlowRateUOM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
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
