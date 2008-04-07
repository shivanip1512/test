/**
 * Module.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Module  extends com.cannontech.multispeak.deploy.service.MspDevice  implements java.io.Serializable {
    private java.lang.String description;

    private java.lang.String moduleType;

    private java.lang.String firmwareVersion;

    public Module() {
    }

    public Module(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String deviceClass,
           java.util.Calendar inServiceDate,
           java.util.Calendar outServiceDate,
           java.lang.String facilityID,
           java.lang.String manufacturer,
           java.lang.String serialNumber,
           java.lang.String description,
           java.lang.String moduleType,
           java.lang.String firmwareVersion) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            deviceClass,
            inServiceDate,
            outServiceDate,
            facilityID,
            manufacturer,
            serialNumber);
        this.description = description;
        this.moduleType = moduleType;
        this.firmwareVersion = firmwareVersion;
    }


    /**
     * Gets the description value for this Module.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Module.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the moduleType value for this Module.
     * 
     * @return moduleType
     */
    public java.lang.String getModuleType() {
        return moduleType;
    }


    /**
     * Sets the moduleType value for this Module.
     * 
     * @param moduleType
     */
    public void setModuleType(java.lang.String moduleType) {
        this.moduleType = moduleType;
    }


    /**
     * Gets the firmwareVersion value for this Module.
     * 
     * @return firmwareVersion
     */
    public java.lang.String getFirmwareVersion() {
        return firmwareVersion;
    }


    /**
     * Sets the firmwareVersion value for this Module.
     * 
     * @param firmwareVersion
     */
    public void setFirmwareVersion(java.lang.String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Module)) return false;
        Module other = (Module) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.moduleType==null && other.getModuleType()==null) || 
             (this.moduleType!=null &&
              this.moduleType.equals(other.getModuleType()))) &&
            ((this.firmwareVersion==null && other.getFirmwareVersion()==null) || 
             (this.firmwareVersion!=null &&
              this.firmwareVersion.equals(other.getFirmwareVersion())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getModuleType() != null) {
            _hashCode += getModuleType().hashCode();
        }
        if (getFirmwareVersion() != null) {
            _hashCode += getFirmwareVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Module.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moduleType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "moduleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firmwareVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "firmwareVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
