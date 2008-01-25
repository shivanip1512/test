/**
 * ReceivedElectricMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReceivedElectricMeter  extends com.cannontech.multispeak.deploy.service.MspMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate;

    private java.lang.String metrologyFirmwareVersion;

    private java.lang.String metrologyFirmwareRevision;

    private java.lang.String transponderFirmwareVersion;

    private java.lang.String catalogNumber;

    private com.cannontech.multispeak.deploy.service.Module[] moduleList;

    public ReceivedElectricMeter() {
    }

    public ReceivedElectricMeter(
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
           java.lang.String metrologyFirmwareVersion,
           java.lang.String metrologyFirmwareRevision,
           java.lang.String transponderFirmwareVersion,
           java.lang.String catalogNumber,
           com.cannontech.multispeak.deploy.service.Module[] moduleList) {
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
        this.metrologyFirmwareVersion = metrologyFirmwareVersion;
        this.metrologyFirmwareRevision = metrologyFirmwareRevision;
        this.transponderFirmwareVersion = transponderFirmwareVersion;
        this.catalogNumber = catalogNumber;
        this.moduleList = moduleList;
    }


    /**
     * Gets the electricNameplate value for this ReceivedElectricMeter.
     * 
     * @return electricNameplate
     */
    public com.cannontech.multispeak.deploy.service.ElectricNameplate getElectricNameplate() {
        return electricNameplate;
    }


    /**
     * Sets the electricNameplate value for this ReceivedElectricMeter.
     * 
     * @param electricNameplate
     */
    public void setElectricNameplate(com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate) {
        this.electricNameplate = electricNameplate;
    }


    /**
     * Gets the metrologyFirmwareVersion value for this ReceivedElectricMeter.
     * 
     * @return metrologyFirmwareVersion
     */
    public java.lang.String getMetrologyFirmwareVersion() {
        return metrologyFirmwareVersion;
    }


    /**
     * Sets the metrologyFirmwareVersion value for this ReceivedElectricMeter.
     * 
     * @param metrologyFirmwareVersion
     */
    public void setMetrologyFirmwareVersion(java.lang.String metrologyFirmwareVersion) {
        this.metrologyFirmwareVersion = metrologyFirmwareVersion;
    }


    /**
     * Gets the metrologyFirmwareRevision value for this ReceivedElectricMeter.
     * 
     * @return metrologyFirmwareRevision
     */
    public java.lang.String getMetrologyFirmwareRevision() {
        return metrologyFirmwareRevision;
    }


    /**
     * Sets the metrologyFirmwareRevision value for this ReceivedElectricMeter.
     * 
     * @param metrologyFirmwareRevision
     */
    public void setMetrologyFirmwareRevision(java.lang.String metrologyFirmwareRevision) {
        this.metrologyFirmwareRevision = metrologyFirmwareRevision;
    }


    /**
     * Gets the transponderFirmwareVersion value for this ReceivedElectricMeter.
     * 
     * @return transponderFirmwareVersion
     */
    public java.lang.String getTransponderFirmwareVersion() {
        return transponderFirmwareVersion;
    }


    /**
     * Sets the transponderFirmwareVersion value for this ReceivedElectricMeter.
     * 
     * @param transponderFirmwareVersion
     */
    public void setTransponderFirmwareVersion(java.lang.String transponderFirmwareVersion) {
        this.transponderFirmwareVersion = transponderFirmwareVersion;
    }


    /**
     * Gets the catalogNumber value for this ReceivedElectricMeter.
     * 
     * @return catalogNumber
     */
    public java.lang.String getCatalogNumber() {
        return catalogNumber;
    }


    /**
     * Sets the catalogNumber value for this ReceivedElectricMeter.
     * 
     * @param catalogNumber
     */
    public void setCatalogNumber(java.lang.String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }


    /**
     * Gets the moduleList value for this ReceivedElectricMeter.
     * 
     * @return moduleList
     */
    public com.cannontech.multispeak.deploy.service.Module[] getModuleList() {
        return moduleList;
    }


    /**
     * Sets the moduleList value for this ReceivedElectricMeter.
     * 
     * @param moduleList
     */
    public void setModuleList(com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        this.moduleList = moduleList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReceivedElectricMeter)) return false;
        ReceivedElectricMeter other = (ReceivedElectricMeter) obj;
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
            ((this.metrologyFirmwareVersion==null && other.getMetrologyFirmwareVersion()==null) || 
             (this.metrologyFirmwareVersion!=null &&
              this.metrologyFirmwareVersion.equals(other.getMetrologyFirmwareVersion()))) &&
            ((this.metrologyFirmwareRevision==null && other.getMetrologyFirmwareRevision()==null) || 
             (this.metrologyFirmwareRevision!=null &&
              this.metrologyFirmwareRevision.equals(other.getMetrologyFirmwareRevision()))) &&
            ((this.transponderFirmwareVersion==null && other.getTransponderFirmwareVersion()==null) || 
             (this.transponderFirmwareVersion!=null &&
              this.transponderFirmwareVersion.equals(other.getTransponderFirmwareVersion()))) &&
            ((this.catalogNumber==null && other.getCatalogNumber()==null) || 
             (this.catalogNumber!=null &&
              this.catalogNumber.equals(other.getCatalogNumber()))) &&
            ((this.moduleList==null && other.getModuleList()==null) || 
             (this.moduleList!=null &&
              java.util.Arrays.equals(this.moduleList, other.getModuleList())));
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
        if (getMetrologyFirmwareVersion() != null) {
            _hashCode += getMetrologyFirmwareVersion().hashCode();
        }
        if (getMetrologyFirmwareRevision() != null) {
            _hashCode += getMetrologyFirmwareRevision().hashCode();
        }
        if (getTransponderFirmwareVersion() != null) {
            _hashCode += getTransponderFirmwareVersion().hashCode();
        }
        if (getCatalogNumber() != null) {
            _hashCode += getCatalogNumber().hashCode();
        }
        if (getModuleList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModuleList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModuleList(), i);
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
        new org.apache.axis.description.TypeDesc(ReceivedElectricMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "receivedElectricMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electricNameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricNameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricNameplate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("metrologyFirmwareVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "metrologyFirmwareVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("metrologyFirmwareRevision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "metrologyFirmwareRevision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transponderFirmwareVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderFirmwareVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("catalogNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "catalogNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moduleList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "moduleList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "module"));
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
