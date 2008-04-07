/**
 * MeterBaseDeviceList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeterBaseDeviceList  implements java.io.Serializable {
    private java.lang.String CDDeviceID;

    private java.lang.String[] loadManagementlDeviceList;

    public MeterBaseDeviceList() {
    }

    public MeterBaseDeviceList(
           java.lang.String CDDeviceID,
           java.lang.String[] loadManagementlDeviceList) {
           this.CDDeviceID = CDDeviceID;
           this.loadManagementlDeviceList = loadManagementlDeviceList;
    }


    /**
     * Gets the CDDeviceID value for this MeterBaseDeviceList.
     * 
     * @return CDDeviceID
     */
    public java.lang.String getCDDeviceID() {
        return CDDeviceID;
    }


    /**
     * Sets the CDDeviceID value for this MeterBaseDeviceList.
     * 
     * @param CDDeviceID
     */
    public void setCDDeviceID(java.lang.String CDDeviceID) {
        this.CDDeviceID = CDDeviceID;
    }


    /**
     * Gets the loadManagementlDeviceList value for this MeterBaseDeviceList.
     * 
     * @return loadManagementlDeviceList
     */
    public java.lang.String[] getLoadManagementlDeviceList() {
        return loadManagementlDeviceList;
    }


    /**
     * Sets the loadManagementlDeviceList value for this MeterBaseDeviceList.
     * 
     * @param loadManagementlDeviceList
     */
    public void setLoadManagementlDeviceList(java.lang.String[] loadManagementlDeviceList) {
        this.loadManagementlDeviceList = loadManagementlDeviceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterBaseDeviceList)) return false;
        MeterBaseDeviceList other = (MeterBaseDeviceList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CDDeviceID==null && other.getCDDeviceID()==null) || 
             (this.CDDeviceID!=null &&
              this.CDDeviceID.equals(other.getCDDeviceID()))) &&
            ((this.loadManagementlDeviceList==null && other.getLoadManagementlDeviceList()==null) || 
             (this.loadManagementlDeviceList!=null &&
              java.util.Arrays.equals(this.loadManagementlDeviceList, other.getLoadManagementlDeviceList())));
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
        if (getCDDeviceID() != null) {
            _hashCode += getCDDeviceID().hashCode();
        }
        if (getLoadManagementlDeviceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLoadManagementlDeviceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLoadManagementlDeviceList(), i);
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
        new org.apache.axis.description.TypeDesc(MeterBaseDeviceList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">meterBase>deviceList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CDDeviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDDeviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadManagementlDeviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementlDeviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDeviceID"));
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
