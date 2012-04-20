/**
 * LoadManagementDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LoadManagementDevice  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private java.lang.String serialNumber;

    private java.lang.String deviceType;

    private java.lang.String manufacturer;

    private java.lang.Long phases;

    private com.cannontech.multispeak.deploy.service.DeviceStatus status;

    private java.lang.Float powerLimit;

    private java.lang.String meterID;

    private java.lang.String meterNo;

    private com.cannontech.multispeak.deploy.service.Module[] moduleList;

    private java.math.BigInteger numberOfRelays;

    private java.lang.Float ratedVoltage;

    private java.lang.Float ratedCurrent;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public LoadManagementDevice() {
    }

    public LoadManagementDevice(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           java.lang.String serialNumber,
           java.lang.String deviceType,
           java.lang.String manufacturer,
           java.lang.Long phases,
           com.cannontech.multispeak.deploy.service.DeviceStatus status,
           java.lang.Float powerLimit,
           java.lang.String meterID,
           java.lang.String meterNo,
           com.cannontech.multispeak.deploy.service.Module[] moduleList,
           java.math.BigInteger numberOfRelays,
           java.lang.Float ratedVoltage,
           java.lang.Float ratedCurrent,
           com.cannontech.multispeak.deploy.service.GPS GPS) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.serialNumber = serialNumber;
        this.deviceType = deviceType;
        this.manufacturer = manufacturer;
        this.phases = phases;
        this.status = status;
        this.powerLimit = powerLimit;
        this.meterID = meterID;
        this.meterNo = meterNo;
        this.moduleList = moduleList;
        this.numberOfRelays = numberOfRelays;
        this.ratedVoltage = ratedVoltage;
        this.ratedCurrent = ratedCurrent;
        this.GPS = GPS;
    }


    /**
     * Gets the serialNumber value for this LoadManagementDevice.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this LoadManagementDevice.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the deviceType value for this LoadManagementDevice.
     * 
     * @return deviceType
     */
    public java.lang.String getDeviceType() {
        return deviceType;
    }


    /**
     * Sets the deviceType value for this LoadManagementDevice.
     * 
     * @param deviceType
     */
    public void setDeviceType(java.lang.String deviceType) {
        this.deviceType = deviceType;
    }


    /**
     * Gets the manufacturer value for this LoadManagementDevice.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this LoadManagementDevice.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the phases value for this LoadManagementDevice.
     * 
     * @return phases
     */
    public java.lang.Long getPhases() {
        return phases;
    }


    /**
     * Sets the phases value for this LoadManagementDevice.
     * 
     * @param phases
     */
    public void setPhases(java.lang.Long phases) {
        this.phases = phases;
    }


    /**
     * Gets the status value for this LoadManagementDevice.
     * 
     * @return status
     */
    public com.cannontech.multispeak.deploy.service.DeviceStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this LoadManagementDevice.
     * 
     * @param status
     */
    public void setStatus(com.cannontech.multispeak.deploy.service.DeviceStatus status) {
        this.status = status;
    }


    /**
     * Gets the powerLimit value for this LoadManagementDevice.
     * 
     * @return powerLimit
     */
    public java.lang.Float getPowerLimit() {
        return powerLimit;
    }


    /**
     * Sets the powerLimit value for this LoadManagementDevice.
     * 
     * @param powerLimit
     */
    public void setPowerLimit(java.lang.Float powerLimit) {
        this.powerLimit = powerLimit;
    }


    /**
     * Gets the meterID value for this LoadManagementDevice.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this LoadManagementDevice.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the meterNo value for this LoadManagementDevice.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this LoadManagementDevice.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the moduleList value for this LoadManagementDevice.
     * 
     * @return moduleList
     */
    public com.cannontech.multispeak.deploy.service.Module[] getModuleList() {
        return moduleList;
    }


    /**
     * Sets the moduleList value for this LoadManagementDevice.
     * 
     * @param moduleList
     */
    public void setModuleList(com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        this.moduleList = moduleList;
    }


    /**
     * Gets the numberOfRelays value for this LoadManagementDevice.
     * 
     * @return numberOfRelays
     */
    public java.math.BigInteger getNumberOfRelays() {
        return numberOfRelays;
    }


    /**
     * Sets the numberOfRelays value for this LoadManagementDevice.
     * 
     * @param numberOfRelays
     */
    public void setNumberOfRelays(java.math.BigInteger numberOfRelays) {
        this.numberOfRelays = numberOfRelays;
    }


    /**
     * Gets the ratedVoltage value for this LoadManagementDevice.
     * 
     * @return ratedVoltage
     */
    public java.lang.Float getRatedVoltage() {
        return ratedVoltage;
    }


    /**
     * Sets the ratedVoltage value for this LoadManagementDevice.
     * 
     * @param ratedVoltage
     */
    public void setRatedVoltage(java.lang.Float ratedVoltage) {
        this.ratedVoltage = ratedVoltage;
    }


    /**
     * Gets the ratedCurrent value for this LoadManagementDevice.
     * 
     * @return ratedCurrent
     */
    public java.lang.Float getRatedCurrent() {
        return ratedCurrent;
    }


    /**
     * Sets the ratedCurrent value for this LoadManagementDevice.
     * 
     * @param ratedCurrent
     */
    public void setRatedCurrent(java.lang.Float ratedCurrent) {
        this.ratedCurrent = ratedCurrent;
    }


    /**
     * Gets the GPS value for this LoadManagementDevice.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this LoadManagementDevice.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadManagementDevice)) return false;
        LoadManagementDevice other = (LoadManagementDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.deviceType==null && other.getDeviceType()==null) || 
             (this.deviceType!=null &&
              this.deviceType.equals(other.getDeviceType()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.phases==null && other.getPhases()==null) || 
             (this.phases!=null &&
              this.phases.equals(other.getPhases()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.powerLimit==null && other.getPowerLimit()==null) || 
             (this.powerLimit!=null &&
              this.powerLimit.equals(other.getPowerLimit()))) &&
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.moduleList==null && other.getModuleList()==null) || 
             (this.moduleList!=null &&
              java.util.Arrays.equals(this.moduleList, other.getModuleList()))) &&
            ((this.numberOfRelays==null && other.getNumberOfRelays()==null) || 
             (this.numberOfRelays!=null &&
              this.numberOfRelays.equals(other.getNumberOfRelays()))) &&
            ((this.ratedVoltage==null && other.getRatedVoltage()==null) || 
             (this.ratedVoltage!=null &&
              this.ratedVoltage.equals(other.getRatedVoltage()))) &&
            ((this.ratedCurrent==null && other.getRatedCurrent()==null) || 
             (this.ratedCurrent!=null &&
              this.ratedCurrent.equals(other.getRatedCurrent()))) &&
            ((this.GPS==null && other.getGPS()==null) || 
             (this.GPS!=null &&
              this.GPS.equals(other.getGPS())));
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
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getDeviceType() != null) {
            _hashCode += getDeviceType().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getPhases() != null) {
            _hashCode += getPhases().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getPowerLimit() != null) {
            _hashCode += getPowerLimit().hashCode();
        }
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
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
        if (getNumberOfRelays() != null) {
            _hashCode += getNumberOfRelays().hashCode();
        }
        if (getRatedVoltage() != null) {
            _hashCode += getRatedVoltage().hashCode();
        }
        if (getRatedCurrent() != null) {
            _hashCode += getRatedCurrent().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoadManagementDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceType"));
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
        elemField.setFieldName("phases");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfRelays");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfRelays"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratedVoltage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ratedVoltage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratedCurrent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ratedCurrent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GPS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
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
