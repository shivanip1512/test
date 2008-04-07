/**
 * MspMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public abstract class MspMeter  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String meterNo;

    private java.lang.String manufacturer;

    private java.lang.String catalogNumber;

    private java.lang.String serialNumber;

    private java.lang.String metrologyFirmwareVersion;

    private java.lang.String metrologyFirmwareRevision;

    private java.lang.String meterType;

    private java.lang.String AMRDeviceType;

    private java.lang.String AMRVendor;

    private java.lang.String transponderID;

    private com.cannontech.multispeak.deploy.service.Module[] moduleList;

    public MspMeter() {
    }

    public MspMeter(
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
           com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.meterNo = meterNo;
        this.manufacturer = manufacturer;
        this.catalogNumber = catalogNumber;
        this.serialNumber = serialNumber;
        this.metrologyFirmwareVersion = metrologyFirmwareVersion;
        this.metrologyFirmwareRevision = metrologyFirmwareRevision;
        this.meterType = meterType;
        this.AMRDeviceType = AMRDeviceType;
        this.AMRVendor = AMRVendor;
        this.transponderID = transponderID;
        this.moduleList = moduleList;
    }


    /**
     * Gets the meterNo value for this MspMeter.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this MspMeter.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the manufacturer value for this MspMeter.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this MspMeter.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the catalogNumber value for this MspMeter.
     * 
     * @return catalogNumber
     */
    public java.lang.String getCatalogNumber() {
        return catalogNumber;
    }


    /**
     * Sets the catalogNumber value for this MspMeter.
     * 
     * @param catalogNumber
     */
    public void setCatalogNumber(java.lang.String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }


    /**
     * Gets the serialNumber value for this MspMeter.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this MspMeter.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the metrologyFirmwareVersion value for this MspMeter.
     * 
     * @return metrologyFirmwareVersion
     */
    public java.lang.String getMetrologyFirmwareVersion() {
        return metrologyFirmwareVersion;
    }


    /**
     * Sets the metrologyFirmwareVersion value for this MspMeter.
     * 
     * @param metrologyFirmwareVersion
     */
    public void setMetrologyFirmwareVersion(java.lang.String metrologyFirmwareVersion) {
        this.metrologyFirmwareVersion = metrologyFirmwareVersion;
    }


    /**
     * Gets the metrologyFirmwareRevision value for this MspMeter.
     * 
     * @return metrologyFirmwareRevision
     */
    public java.lang.String getMetrologyFirmwareRevision() {
        return metrologyFirmwareRevision;
    }


    /**
     * Sets the metrologyFirmwareRevision value for this MspMeter.
     * 
     * @param metrologyFirmwareRevision
     */
    public void setMetrologyFirmwareRevision(java.lang.String metrologyFirmwareRevision) {
        this.metrologyFirmwareRevision = metrologyFirmwareRevision;
    }


    /**
     * Gets the meterType value for this MspMeter.
     * 
     * @return meterType
     */
    public java.lang.String getMeterType() {
        return meterType;
    }


    /**
     * Sets the meterType value for this MspMeter.
     * 
     * @param meterType
     */
    public void setMeterType(java.lang.String meterType) {
        this.meterType = meterType;
    }


    /**
     * Gets the AMRDeviceType value for this MspMeter.
     * 
     * @return AMRDeviceType
     */
    public java.lang.String getAMRDeviceType() {
        return AMRDeviceType;
    }


    /**
     * Sets the AMRDeviceType value for this MspMeter.
     * 
     * @param AMRDeviceType
     */
    public void setAMRDeviceType(java.lang.String AMRDeviceType) {
        this.AMRDeviceType = AMRDeviceType;
    }


    /**
     * Gets the AMRVendor value for this MspMeter.
     * 
     * @return AMRVendor
     */
    public java.lang.String getAMRVendor() {
        return AMRVendor;
    }


    /**
     * Sets the AMRVendor value for this MspMeter.
     * 
     * @param AMRVendor
     */
    public void setAMRVendor(java.lang.String AMRVendor) {
        this.AMRVendor = AMRVendor;
    }


    /**
     * Gets the transponderID value for this MspMeter.
     * 
     * @return transponderID
     */
    public java.lang.String getTransponderID() {
        return transponderID;
    }


    /**
     * Sets the transponderID value for this MspMeter.
     * 
     * @param transponderID
     */
    public void setTransponderID(java.lang.String transponderID) {
        this.transponderID = transponderID;
    }


    /**
     * Gets the moduleList value for this MspMeter.
     * 
     * @return moduleList
     */
    public com.cannontech.multispeak.deploy.service.Module[] getModuleList() {
        return moduleList;
    }


    /**
     * Sets the moduleList value for this MspMeter.
     * 
     * @param moduleList
     */
    public void setModuleList(com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        this.moduleList = moduleList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspMeter)) return false;
        MspMeter other = (MspMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.catalogNumber==null && other.getCatalogNumber()==null) || 
             (this.catalogNumber!=null &&
              this.catalogNumber.equals(other.getCatalogNumber()))) &&
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.metrologyFirmwareVersion==null && other.getMetrologyFirmwareVersion()==null) || 
             (this.metrologyFirmwareVersion!=null &&
              this.metrologyFirmwareVersion.equals(other.getMetrologyFirmwareVersion()))) &&
            ((this.metrologyFirmwareRevision==null && other.getMetrologyFirmwareRevision()==null) || 
             (this.metrologyFirmwareRevision!=null &&
              this.metrologyFirmwareRevision.equals(other.getMetrologyFirmwareRevision()))) &&
            ((this.meterType==null && other.getMeterType()==null) || 
             (this.meterType!=null &&
              this.meterType.equals(other.getMeterType()))) &&
            ((this.AMRDeviceType==null && other.getAMRDeviceType()==null) || 
             (this.AMRDeviceType!=null &&
              this.AMRDeviceType.equals(other.getAMRDeviceType()))) &&
            ((this.AMRVendor==null && other.getAMRVendor()==null) || 
             (this.AMRVendor!=null &&
              this.AMRVendor.equals(other.getAMRVendor()))) &&
            ((this.transponderID==null && other.getTransponderID()==null) || 
             (this.transponderID!=null &&
              this.transponderID.equals(other.getTransponderID()))) &&
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
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getCatalogNumber() != null) {
            _hashCode += getCatalogNumber().hashCode();
        }
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getMetrologyFirmwareVersion() != null) {
            _hashCode += getMetrologyFirmwareVersion().hashCode();
        }
        if (getMetrologyFirmwareRevision() != null) {
            _hashCode += getMetrologyFirmwareRevision().hashCode();
        }
        if (getMeterType() != null) {
            _hashCode += getMeterType().hashCode();
        }
        if (getAMRDeviceType() != null) {
            _hashCode += getAMRDeviceType().hashCode();
        }
        if (getAMRVendor() != null) {
            _hashCode += getAMRVendor().hashCode();
        }
        if (getTransponderID() != null) {
            _hashCode += getTransponderID().hashCode();
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
        new org.apache.axis.description.TypeDesc(MspMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manufacturer"));
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
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("meterType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AMRDeviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AMRDeviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AMRVendor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AMRVendor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transponderID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderID"));
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
