/**
 * WaterMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WaterMeter  extends com.cannontech.multispeak.deploy.service.MspMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.WaterNameplate waterNameplate;

    private com.cannontech.multispeak.deploy.service.WaterUtilityInfo waterUtilityInfo;

    public WaterMeter() {
    }

    public WaterMeter(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String meterNo,
           java.lang.String manufacturer,
           java.lang.String catalogNumber,
           java.lang.String serialNumber,
           java.lang.String metrologyFirmwareVersion,
           java.lang.String metrologyFirmwareRevision,
           java.lang.String meterType,
           java.lang.String AMRDeviceType,
           java.lang.String AMRVendor,
           java.lang.String transponderID,
           com.cannontech.multispeak.deploy.service.Module[] moduleList,
           com.cannontech.multispeak.deploy.service.WaterNameplate waterNameplate,
           com.cannontech.multispeak.deploy.service.WaterUtilityInfo waterUtilityInfo) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            meterNo,
            manufacturer,
            catalogNumber,
            serialNumber,
            metrologyFirmwareVersion,
            metrologyFirmwareRevision,
            meterType,
            AMRDeviceType,
            AMRVendor,
            transponderID,
            moduleList);
        this.waterNameplate = waterNameplate;
        this.waterUtilityInfo = waterUtilityInfo;
    }


    /**
     * Gets the waterNameplate value for this WaterMeter.
     * 
     * @return waterNameplate
     */
    public com.cannontech.multispeak.deploy.service.WaterNameplate getWaterNameplate() {
        return waterNameplate;
    }


    /**
     * Sets the waterNameplate value for this WaterMeter.
     * 
     * @param waterNameplate
     */
    public void setWaterNameplate(com.cannontech.multispeak.deploy.service.WaterNameplate waterNameplate) {
        this.waterNameplate = waterNameplate;
    }


    /**
     * Gets the waterUtilityInfo value for this WaterMeter.
     * 
     * @return waterUtilityInfo
     */
    public com.cannontech.multispeak.deploy.service.WaterUtilityInfo getWaterUtilityInfo() {
        return waterUtilityInfo;
    }


    /**
     * Sets the waterUtilityInfo value for this WaterMeter.
     * 
     * @param waterUtilityInfo
     */
    public void setWaterUtilityInfo(com.cannontech.multispeak.deploy.service.WaterUtilityInfo waterUtilityInfo) {
        this.waterUtilityInfo = waterUtilityInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WaterMeter)) return false;
        WaterMeter other = (WaterMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.waterNameplate==null && other.getWaterNameplate()==null) || 
             (this.waterNameplate!=null &&
              this.waterNameplate.equals(other.getWaterNameplate()))) &&
            ((this.waterUtilityInfo==null && other.getWaterUtilityInfo()==null) || 
             (this.waterUtilityInfo!=null &&
              this.waterUtilityInfo.equals(other.getWaterUtilityInfo())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getWaterNameplate() != null) {
            _hashCode += getWaterNameplate().hashCode();
        }
        if (getWaterUtilityInfo() != null) {
            _hashCode += getWaterUtilityInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WaterMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waterNameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterNameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterNameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waterUtilityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterUtilityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterUtilityInfo"));
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
