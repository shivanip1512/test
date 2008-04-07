/**
 * Meter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Meter  extends com.cannontech.multispeak.deploy.service.MspDevice  implements java.io.Serializable {
    private java.lang.String meterNo;

    private java.lang.String meterType;

    private java.lang.String[] sealNumberList;

    private java.lang.String AMRType;

    private java.lang.String AMRDeviceType;

    private java.lang.String AMRVendor;

    private com.cannontech.multispeak.deploy.service.Nameplate nameplate;

    private com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo;

    private com.cannontech.multispeak.deploy.service.Module[] moduleList;

    public Meter() {
    }

    public Meter(
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
           java.lang.String meterNo,
           java.lang.String meterType,
           java.lang.String[] sealNumberList,
           java.lang.String AMRType,
           java.lang.String AMRDeviceType,
           java.lang.String AMRVendor,
           com.cannontech.multispeak.deploy.service.Nameplate nameplate,
           com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo,
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
            deviceClass,
            inServiceDate,
            outServiceDate,
            facilityID,
            manufacturer,
            serialNumber);
        this.meterNo = meterNo;
        this.meterType = meterType;
        this.sealNumberList = sealNumberList;
        this.AMRType = AMRType;
        this.AMRDeviceType = AMRDeviceType;
        this.AMRVendor = AMRVendor;
        this.nameplate = nameplate;
        this.utilityInfo = utilityInfo;
        this.moduleList = moduleList;
    }


    /**
     * Gets the meterNo value for this Meter.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this Meter.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the meterType value for this Meter.
     * 
     * @return meterType
     */
    public java.lang.String getMeterType() {
        return meterType;
    }


    /**
     * Sets the meterType value for this Meter.
     * 
     * @param meterType
     */
    public void setMeterType(java.lang.String meterType) {
        this.meterType = meterType;
    }


    /**
     * Gets the sealNumberList value for this Meter.
     * 
     * @return sealNumberList
     */
    public java.lang.String[] getSealNumberList() {
        return sealNumberList;
    }


    /**
     * Sets the sealNumberList value for this Meter.
     * 
     * @param sealNumberList
     */
    public void setSealNumberList(java.lang.String[] sealNumberList) {
        this.sealNumberList = sealNumberList;
    }


    /**
     * Gets the AMRType value for this Meter.
     * 
     * @return AMRType
     */
    public java.lang.String getAMRType() {
        return AMRType;
    }


    /**
     * Sets the AMRType value for this Meter.
     * 
     * @param AMRType
     */
    public void setAMRType(java.lang.String AMRType) {
        this.AMRType = AMRType;
    }


    /**
     * Gets the AMRDeviceType value for this Meter.
     * 
     * @return AMRDeviceType
     */
    public java.lang.String getAMRDeviceType() {
        return AMRDeviceType;
    }


    /**
     * Sets the AMRDeviceType value for this Meter.
     * 
     * @param AMRDeviceType
     */
    public void setAMRDeviceType(java.lang.String AMRDeviceType) {
        this.AMRDeviceType = AMRDeviceType;
    }


    /**
     * Gets the AMRVendor value for this Meter.
     * 
     * @return AMRVendor
     */
    public java.lang.String getAMRVendor() {
        return AMRVendor;
    }


    /**
     * Sets the AMRVendor value for this Meter.
     * 
     * @param AMRVendor
     */
    public void setAMRVendor(java.lang.String AMRVendor) {
        this.AMRVendor = AMRVendor;
    }


    /**
     * Gets the nameplate value for this Meter.
     * 
     * @return nameplate
     */
    public com.cannontech.multispeak.deploy.service.Nameplate getNameplate() {
        return nameplate;
    }


    /**
     * Sets the nameplate value for this Meter.
     * 
     * @param nameplate
     */
    public void setNameplate(com.cannontech.multispeak.deploy.service.Nameplate nameplate) {
        this.nameplate = nameplate;
    }


    /**
     * Gets the utilityInfo value for this Meter.
     * 
     * @return utilityInfo
     */
    public com.cannontech.multispeak.deploy.service.UtilityInfo getUtilityInfo() {
        return utilityInfo;
    }


    /**
     * Sets the utilityInfo value for this Meter.
     * 
     * @param utilityInfo
     */
    public void setUtilityInfo(com.cannontech.multispeak.deploy.service.UtilityInfo utilityInfo) {
        this.utilityInfo = utilityInfo;
    }


    /**
     * Gets the moduleList value for this Meter.
     * 
     * @return moduleList
     */
    public com.cannontech.multispeak.deploy.service.Module[] getModuleList() {
        return moduleList;
    }


    /**
     * Sets the moduleList value for this Meter.
     * 
     * @param moduleList
     */
    public void setModuleList(com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        this.moduleList = moduleList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Meter)) return false;
        Meter other = (Meter) obj;
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
            ((this.meterType==null && other.getMeterType()==null) || 
             (this.meterType!=null &&
              this.meterType.equals(other.getMeterType()))) &&
            ((this.sealNumberList==null && other.getSealNumberList()==null) || 
             (this.sealNumberList!=null &&
              java.util.Arrays.equals(this.sealNumberList, other.getSealNumberList()))) &&
            ((this.AMRType==null && other.getAMRType()==null) || 
             (this.AMRType!=null &&
              this.AMRType.equals(other.getAMRType()))) &&
            ((this.AMRDeviceType==null && other.getAMRDeviceType()==null) || 
             (this.AMRDeviceType!=null &&
              this.AMRDeviceType.equals(other.getAMRDeviceType()))) &&
            ((this.AMRVendor==null && other.getAMRVendor()==null) || 
             (this.AMRVendor!=null &&
              this.AMRVendor.equals(other.getAMRVendor()))) &&
            ((this.nameplate==null && other.getNameplate()==null) || 
             (this.nameplate!=null &&
              this.nameplate.equals(other.getNameplate()))) &&
            ((this.utilityInfo==null && other.getUtilityInfo()==null) || 
             (this.utilityInfo!=null &&
              this.utilityInfo.equals(other.getUtilityInfo()))) &&
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
        if (getMeterType() != null) {
            _hashCode += getMeterType().hashCode();
        }
        if (getSealNumberList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSealNumberList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSealNumberList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAMRType() != null) {
            _hashCode += getAMRType().hashCode();
        }
        if (getAMRDeviceType() != null) {
            _hashCode += getAMRDeviceType().hashCode();
        }
        if (getAMRVendor() != null) {
            _hashCode += getAMRVendor().hashCode();
        }
        if (getNameplate() != null) {
            _hashCode += getNameplate().hashCode();
        }
        if (getUtilityInfo() != null) {
            _hashCode += getUtilityInfo().hashCode();
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
        new org.apache.axis.description.TypeDesc(Meter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
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
        elemField.setFieldName("sealNumberList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sealNumberList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sealNumber"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AMRType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AMRType"));
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
        elemField.setFieldName("nameplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nameplate"));
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
