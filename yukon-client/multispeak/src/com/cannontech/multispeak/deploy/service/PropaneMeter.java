/**
 * PropaneMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PropaneMeter  extends com.cannontech.multispeak.deploy.service.MspMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PropaneNameplate propaneNameplate;

    private com.cannontech.multispeak.deploy.service.PropaneUtilityInfo propaneUtilityInfo;

    public PropaneMeter() {
    }

    public PropaneMeter(
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
           com.cannontech.multispeak.deploy.service.PropaneNameplate propaneNameplate,
           com.cannontech.multispeak.deploy.service.PropaneUtilityInfo propaneUtilityInfo) {
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
        this.propaneNameplate = propaneNameplate;
        this.propaneUtilityInfo = propaneUtilityInfo;
    }


    /**
     * Gets the propaneNameplate value for this PropaneMeter.
     * 
     * @return propaneNameplate
     */
    public com.cannontech.multispeak.deploy.service.PropaneNameplate getPropaneNameplate() {
        return propaneNameplate;
    }


    /**
     * Sets the propaneNameplate value for this PropaneMeter.
     * 
     * @param propaneNameplate
     */
    public void setPropaneNameplate(com.cannontech.multispeak.deploy.service.PropaneNameplate propaneNameplate) {
        this.propaneNameplate = propaneNameplate;
    }


    /**
     * Gets the propaneUtilityInfo value for this PropaneMeter.
     * 
     * @return propaneUtilityInfo
     */
    public com.cannontech.multispeak.deploy.service.PropaneUtilityInfo getPropaneUtilityInfo() {
        return propaneUtilityInfo;
    }


    /**
     * Sets the propaneUtilityInfo value for this PropaneMeter.
     * 
     * @param propaneUtilityInfo
     */
    public void setPropaneUtilityInfo(com.cannontech.multispeak.deploy.service.PropaneUtilityInfo propaneUtilityInfo) {
        this.propaneUtilityInfo = propaneUtilityInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PropaneMeter)) return false;
        PropaneMeter other = (PropaneMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.propaneNameplate==null && other.getPropaneNameplate()==null) || 
             (this.propaneNameplate!=null &&
              this.propaneNameplate.equals(other.getPropaneNameplate()))) &&
            ((this.propaneUtilityInfo==null && other.getPropaneUtilityInfo()==null) || 
             (this.propaneUtilityInfo!=null &&
              this.propaneUtilityInfo.equals(other.getPropaneUtilityInfo())));
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
        if (getPropaneNameplate() != null) {
            _hashCode += getPropaneNameplate().hashCode();
        }
        if (getPropaneUtilityInfo() != null) {
            _hashCode += getPropaneUtilityInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PropaneMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propaneNameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneNameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneNameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propaneUtilityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneUtilityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneUtilityInfo"));
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
