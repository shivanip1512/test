/**
 * LoadManagementEventStrategy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LoadManagementEventStrategy  implements java.io.Serializable {
    private java.lang.String strategyName;

    private com.cannontech.multispeak.deploy.service.ObjectRef[] applicationPointList;

    public LoadManagementEventStrategy() {
    }

    public LoadManagementEventStrategy(
           java.lang.String strategyName,
           com.cannontech.multispeak.deploy.service.ObjectRef[] applicationPointList) {
           this.strategyName = strategyName;
           this.applicationPointList = applicationPointList;
    }


    /**
     * Gets the strategyName value for this LoadManagementEventStrategy.
     * 
     * @return strategyName
     */
    public java.lang.String getStrategyName() {
        return strategyName;
    }


    /**
     * Sets the strategyName value for this LoadManagementEventStrategy.
     * 
     * @param strategyName
     */
    public void setStrategyName(java.lang.String strategyName) {
        this.strategyName = strategyName;
    }


    /**
     * Gets the applicationPointList value for this LoadManagementEventStrategy.
     * 
     * @return applicationPointList
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef[] getApplicationPointList() {
        return applicationPointList;
    }


    /**
     * Sets the applicationPointList value for this LoadManagementEventStrategy.
     * 
     * @param applicationPointList
     */
    public void setApplicationPointList(com.cannontech.multispeak.deploy.service.ObjectRef[] applicationPointList) {
        this.applicationPointList = applicationPointList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadManagementEventStrategy)) return false;
        LoadManagementEventStrategy other = (LoadManagementEventStrategy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.strategyName==null && other.getStrategyName()==null) || 
             (this.strategyName!=null &&
              this.strategyName.equals(other.getStrategyName()))) &&
            ((this.applicationPointList==null && other.getApplicationPointList()==null) || 
             (this.applicationPointList!=null &&
              java.util.Arrays.equals(this.applicationPointList, other.getApplicationPointList())));
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
        if (getStrategyName() != null) {
            _hashCode += getStrategyName().hashCode();
        }
        if (getApplicationPointList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getApplicationPointList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getApplicationPointList(), i);
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
        new org.apache.axis.description.TypeDesc(LoadManagementEventStrategy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">loadManagementEvent>strategy"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("strategyName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "strategyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applicationPointList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationPointList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "applicationPoint"));
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
