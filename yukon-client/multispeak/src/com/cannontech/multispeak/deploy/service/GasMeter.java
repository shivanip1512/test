/**
 * GasMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasMeter  extends com.cannontech.multispeak.deploy.service.MspMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GasNameplate gasNameplate;

    private com.cannontech.multispeak.deploy.service.GasUtilityInfo gasUtilityInfo;

    public GasMeter() {
    }

    public GasMeter(
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
           com.cannontech.multispeak.deploy.service.GasNameplate gasNameplate,
           com.cannontech.multispeak.deploy.service.GasUtilityInfo gasUtilityInfo) {
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
        this.gasNameplate = gasNameplate;
        this.gasUtilityInfo = gasUtilityInfo;
    }


    /**
     * Gets the gasNameplate value for this GasMeter.
     * 
     * @return gasNameplate
     */
    public com.cannontech.multispeak.deploy.service.GasNameplate getGasNameplate() {
        return gasNameplate;
    }


    /**
     * Sets the gasNameplate value for this GasMeter.
     * 
     * @param gasNameplate
     */
    public void setGasNameplate(com.cannontech.multispeak.deploy.service.GasNameplate gasNameplate) {
        this.gasNameplate = gasNameplate;
    }


    /**
     * Gets the gasUtilityInfo value for this GasMeter.
     * 
     * @return gasUtilityInfo
     */
    public com.cannontech.multispeak.deploy.service.GasUtilityInfo getGasUtilityInfo() {
        return gasUtilityInfo;
    }


    /**
     * Sets the gasUtilityInfo value for this GasMeter.
     * 
     * @param gasUtilityInfo
     */
    public void setGasUtilityInfo(com.cannontech.multispeak.deploy.service.GasUtilityInfo gasUtilityInfo) {
        this.gasUtilityInfo = gasUtilityInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GasMeter)) return false;
        GasMeter other = (GasMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.gasNameplate==null && other.getGasNameplate()==null) || 
             (this.gasNameplate!=null &&
              this.gasNameplate.equals(other.getGasNameplate()))) &&
            ((this.gasUtilityInfo==null && other.getGasUtilityInfo()==null) || 
             (this.gasUtilityInfo!=null &&
              this.gasUtilityInfo.equals(other.getGasUtilityInfo())));
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
        if (getGasNameplate() != null) {
            _hashCode += getGasNameplate().hashCode();
        }
        if (getGasUtilityInfo() != null) {
            _hashCode += getGasUtilityInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GasMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasNameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasNameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasNameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasUtilityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasUtilityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasUtilityInfo"));
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
