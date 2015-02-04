/**
 * MeasurementDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeasurementDevice  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeasurementDeviceType measurementDeviceType;

    private com.cannontech.multispeak.deploy.service.EaLoc eaLoc;

    private java.lang.String substation;

    private java.lang.String feeder;

    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCd;

    private com.cannontech.multispeak.deploy.service.MeasurementDeviceStatus measurementDeviceStatus;

    private java.lang.String pointID;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public MeasurementDevice() {
    }

    public MeasurementDevice(
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
           com.cannontech.multispeak.deploy.service.MeasurementDeviceType measurementDeviceType,
           com.cannontech.multispeak.deploy.service.EaLoc eaLoc,
           java.lang.String substation,
           java.lang.String feeder,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCd,
           com.cannontech.multispeak.deploy.service.MeasurementDeviceStatus measurementDeviceStatus,
           java.lang.String pointID,
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
        this.measurementDeviceType = measurementDeviceType;
        this.eaLoc = eaLoc;
        this.substation = substation;
        this.feeder = feeder;
        this.phaseCd = phaseCd;
        this.measurementDeviceStatus = measurementDeviceStatus;
        this.pointID = pointID;
        this.GPS = GPS;
    }


    /**
     * Gets the measurementDeviceType value for this MeasurementDevice.
     * 
     * @return measurementDeviceType
     */
    public com.cannontech.multispeak.deploy.service.MeasurementDeviceType getMeasurementDeviceType() {
        return measurementDeviceType;
    }


    /**
     * Sets the measurementDeviceType value for this MeasurementDevice.
     * 
     * @param measurementDeviceType
     */
    public void setMeasurementDeviceType(com.cannontech.multispeak.deploy.service.MeasurementDeviceType measurementDeviceType) {
        this.measurementDeviceType = measurementDeviceType;
    }


    /**
     * Gets the eaLoc value for this MeasurementDevice.
     * 
     * @return eaLoc
     */
    public com.cannontech.multispeak.deploy.service.EaLoc getEaLoc() {
        return eaLoc;
    }


    /**
     * Sets the eaLoc value for this MeasurementDevice.
     * 
     * @param eaLoc
     */
    public void setEaLoc(com.cannontech.multispeak.deploy.service.EaLoc eaLoc) {
        this.eaLoc = eaLoc;
    }


    /**
     * Gets the substation value for this MeasurementDevice.
     * 
     * @return substation
     */
    public java.lang.String getSubstation() {
        return substation;
    }


    /**
     * Sets the substation value for this MeasurementDevice.
     * 
     * @param substation
     */
    public void setSubstation(java.lang.String substation) {
        this.substation = substation;
    }


    /**
     * Gets the feeder value for this MeasurementDevice.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this MeasurementDevice.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the phaseCd value for this MeasurementDevice.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this MeasurementDevice.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.deploy.service.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the measurementDeviceStatus value for this MeasurementDevice.
     * 
     * @return measurementDeviceStatus
     */
    public com.cannontech.multispeak.deploy.service.MeasurementDeviceStatus getMeasurementDeviceStatus() {
        return measurementDeviceStatus;
    }


    /**
     * Sets the measurementDeviceStatus value for this MeasurementDevice.
     * 
     * @param measurementDeviceStatus
     */
    public void setMeasurementDeviceStatus(com.cannontech.multispeak.deploy.service.MeasurementDeviceStatus measurementDeviceStatus) {
        this.measurementDeviceStatus = measurementDeviceStatus;
    }


    /**
     * Gets the pointID value for this MeasurementDevice.
     * 
     * @return pointID
     */
    public java.lang.String getPointID() {
        return pointID;
    }


    /**
     * Sets the pointID value for this MeasurementDevice.
     * 
     * @param pointID
     */
    public void setPointID(java.lang.String pointID) {
        this.pointID = pointID;
    }


    /**
     * Gets the GPS value for this MeasurementDevice.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this MeasurementDevice.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeasurementDevice)) return false;
        MeasurementDevice other = (MeasurementDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.measurementDeviceType==null && other.getMeasurementDeviceType()==null) || 
             (this.measurementDeviceType!=null &&
              this.measurementDeviceType.equals(other.getMeasurementDeviceType()))) &&
            ((this.eaLoc==null && other.getEaLoc()==null) || 
             (this.eaLoc!=null &&
              this.eaLoc.equals(other.getEaLoc()))) &&
            ((this.substation==null && other.getSubstation()==null) || 
             (this.substation!=null &&
              this.substation.equals(other.getSubstation()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.measurementDeviceStatus==null && other.getMeasurementDeviceStatus()==null) || 
             (this.measurementDeviceStatus!=null &&
              this.measurementDeviceStatus.equals(other.getMeasurementDeviceStatus()))) &&
            ((this.pointID==null && other.getPointID()==null) || 
             (this.pointID!=null &&
              this.pointID.equals(other.getPointID()))) &&
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
        if (getMeasurementDeviceType() != null) {
            _hashCode += getMeasurementDeviceType().hashCode();
        }
        if (getEaLoc() != null) {
            _hashCode += getEaLoc().hashCode();
        }
        if (getSubstation() != null) {
            _hashCode += getSubstation().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getMeasurementDeviceStatus() != null) {
            _hashCode += getMeasurementDeviceStatus().hashCode();
        }
        if (getPointID() != null) {
            _hashCode += getPointID().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeasurementDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementDeviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementDeviceStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pointID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pointID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
