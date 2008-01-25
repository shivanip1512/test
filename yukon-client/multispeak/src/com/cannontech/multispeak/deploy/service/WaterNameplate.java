/**
 * WaterNameplate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WaterNameplate  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.WaterNameplateInstallType installType;

    private com.cannontech.multispeak.deploy.service.WaterNameplateFluidType fluidType;

    private com.cannontech.multispeak.deploy.service.WaterNameplateDriveType driveType;

    private com.cannontech.multispeak.deploy.service.WaterNameplatePipeSize pipeSize;

    public WaterNameplate() {
    }

    public WaterNameplate(
           com.cannontech.multispeak.deploy.service.WaterNameplateInstallType installType,
           com.cannontech.multispeak.deploy.service.WaterNameplateFluidType fluidType,
           com.cannontech.multispeak.deploy.service.WaterNameplateDriveType driveType,
           com.cannontech.multispeak.deploy.service.WaterNameplatePipeSize pipeSize) {
           this.installType = installType;
           this.fluidType = fluidType;
           this.driveType = driveType;
           this.pipeSize = pipeSize;
    }


    /**
     * Gets the installType value for this WaterNameplate.
     * 
     * @return installType
     */
    public com.cannontech.multispeak.deploy.service.WaterNameplateInstallType getInstallType() {
        return installType;
    }


    /**
     * Sets the installType value for this WaterNameplate.
     * 
     * @param installType
     */
    public void setInstallType(com.cannontech.multispeak.deploy.service.WaterNameplateInstallType installType) {
        this.installType = installType;
    }


    /**
     * Gets the fluidType value for this WaterNameplate.
     * 
     * @return fluidType
     */
    public com.cannontech.multispeak.deploy.service.WaterNameplateFluidType getFluidType() {
        return fluidType;
    }


    /**
     * Sets the fluidType value for this WaterNameplate.
     * 
     * @param fluidType
     */
    public void setFluidType(com.cannontech.multispeak.deploy.service.WaterNameplateFluidType fluidType) {
        this.fluidType = fluidType;
    }


    /**
     * Gets the driveType value for this WaterNameplate.
     * 
     * @return driveType
     */
    public com.cannontech.multispeak.deploy.service.WaterNameplateDriveType getDriveType() {
        return driveType;
    }


    /**
     * Sets the driveType value for this WaterNameplate.
     * 
     * @param driveType
     */
    public void setDriveType(com.cannontech.multispeak.deploy.service.WaterNameplateDriveType driveType) {
        this.driveType = driveType;
    }


    /**
     * Gets the pipeSize value for this WaterNameplate.
     * 
     * @return pipeSize
     */
    public com.cannontech.multispeak.deploy.service.WaterNameplatePipeSize getPipeSize() {
        return pipeSize;
    }


    /**
     * Sets the pipeSize value for this WaterNameplate.
     * 
     * @param pipeSize
     */
    public void setPipeSize(com.cannontech.multispeak.deploy.service.WaterNameplatePipeSize pipeSize) {
        this.pipeSize = pipeSize;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WaterNameplate)) return false;
        WaterNameplate other = (WaterNameplate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.installType==null && other.getInstallType()==null) || 
             (this.installType!=null &&
              this.installType.equals(other.getInstallType()))) &&
            ((this.fluidType==null && other.getFluidType()==null) || 
             (this.fluidType!=null &&
              this.fluidType.equals(other.getFluidType()))) &&
            ((this.driveType==null && other.getDriveType()==null) || 
             (this.driveType!=null &&
              this.driveType.equals(other.getDriveType()))) &&
            ((this.pipeSize==null && other.getPipeSize()==null) || 
             (this.pipeSize!=null &&
              this.pipeSize.equals(other.getPipeSize())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getInstallType() != null) {
            _hashCode += getInstallType().hashCode();
        }
        if (getFluidType() != null) {
            _hashCode += getFluidType().hashCode();
        }
        if (getDriveType() != null) {
            _hashCode += getDriveType().hashCode();
        }
        if (getPipeSize() != null) {
            _hashCode += getPipeSize().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WaterNameplate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterNameplate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("installType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "installType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>installType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fluidType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fluidType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>fluidType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("driveType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "driveType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>driveType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pipeSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pipeSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">waterNameplate>pipeSize"));
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
