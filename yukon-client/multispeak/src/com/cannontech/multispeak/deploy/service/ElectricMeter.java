/**
 * ElectricMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ElectricMeter  extends com.cannontech.multispeak.deploy.service.MspMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate;

    private com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo;

    public ElectricMeter() {
    }

    public ElectricMeter(
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
           java.lang.String serialNumber,
           java.lang.String meterType,
           java.lang.String AMRDeviceType,
           java.lang.String AMRVendor,
           java.lang.String transponderID,
           com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate,
           com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo) {
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
            serialNumber,
            meterType,
            AMRDeviceType,
            AMRVendor,
            transponderID);
        this.electricNameplate = electricNameplate;
        this.utilityInfo = utilityInfo;
    }


    /**
     * Gets the electricNameplate value for this ElectricMeter.
     * 
     * @return electricNameplate
     */
    public com.cannontech.multispeak.deploy.service.ElectricNameplate getElectricNameplate() {
        return electricNameplate;
    }


    /**
     * Sets the electricNameplate value for this ElectricMeter.
     * 
     * @param electricNameplate
     */
    public void setElectricNameplate(com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate) {
        this.electricNameplate = electricNameplate;
    }


    /**
     * Gets the utilityInfo value for this ElectricMeter.
     * 
     * @return utilityInfo
     */
    public com.cannontech.multispeak.deploy.service.UtilityInfo getUtilityInfo() {
        return utilityInfo;
    }


    /**
     * Sets the utilityInfo value for this ElectricMeter.
     * 
     * @param utilityInfo
     */
    public void setUtilityInfo(com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo) {
        this.utilityInfo = utilityInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ElectricMeter)) return false;
        ElectricMeter other = (ElectricMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.electricNameplate==null && other.getElectricNameplate()==null) || 
             (this.electricNameplate!=null &&
              this.electricNameplate.equals(other.getElectricNameplate()))) &&
            ((this.utilityInfo==null && other.getUtilityInfo()==null) || 
             (this.utilityInfo!=null &&
              this.utilityInfo.equals(other.getUtilityInfo())));
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
        if (getElectricNameplate() != null) {
            _hashCode += getElectricNameplate().hashCode();
        }
        if (getUtilityInfo() != null) {
            _hashCode += getUtilityInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ElectricMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electricNameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricNameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricNameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utilityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "utilityInfo"));
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
