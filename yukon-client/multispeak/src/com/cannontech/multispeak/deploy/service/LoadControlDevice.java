/**
 * LoadControlDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LoadControlDevice  extends com.cannontech.multispeak.deploy.service.MspDevice  implements java.io.Serializable {
    private java.lang.Float ratedVoltage;

    private java.lang.Float ratedCurrent;

    private java.math.BigInteger numberOfRelays;

    private com.cannontech.multispeak.deploy.service.Module[] moduleList;

    public LoadControlDevice() {
    }

    public LoadControlDevice(
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
           java.lang.Float ratedVoltage,
           java.lang.Float ratedCurrent,
           java.math.BigInteger numberOfRelays,
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
            facilityID);
        this.ratedVoltage = ratedVoltage;
        this.ratedCurrent = ratedCurrent;
        this.numberOfRelays = numberOfRelays;
        this.moduleList = moduleList;
    }


    /**
     * Gets the ratedVoltage value for this LoadControlDevice.
     * 
     * @return ratedVoltage
     */
    public java.lang.Float getRatedVoltage() {
        return ratedVoltage;
    }


    /**
     * Sets the ratedVoltage value for this LoadControlDevice.
     * 
     * @param ratedVoltage
     */
    public void setRatedVoltage(java.lang.Float ratedVoltage) {
        this.ratedVoltage = ratedVoltage;
    }


    /**
     * Gets the ratedCurrent value for this LoadControlDevice.
     * 
     * @return ratedCurrent
     */
    public java.lang.Float getRatedCurrent() {
        return ratedCurrent;
    }


    /**
     * Sets the ratedCurrent value for this LoadControlDevice.
     * 
     * @param ratedCurrent
     */
    public void setRatedCurrent(java.lang.Float ratedCurrent) {
        this.ratedCurrent = ratedCurrent;
    }


    /**
     * Gets the numberOfRelays value for this LoadControlDevice.
     * 
     * @return numberOfRelays
     */
    public java.math.BigInteger getNumberOfRelays() {
        return numberOfRelays;
    }


    /**
     * Sets the numberOfRelays value for this LoadControlDevice.
     * 
     * @param numberOfRelays
     */
    public void setNumberOfRelays(java.math.BigInteger numberOfRelays) {
        this.numberOfRelays = numberOfRelays;
    }


    /**
     * Gets the moduleList value for this LoadControlDevice.
     * 
     * @return moduleList
     */
    public com.cannontech.multispeak.deploy.service.Module[] getModuleList() {
        return moduleList;
    }


    /**
     * Sets the moduleList value for this LoadControlDevice.
     * 
     * @param moduleList
     */
    public void setModuleList(com.cannontech.multispeak.deploy.service.Module[] moduleList) {
        this.moduleList = moduleList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadControlDevice)) return false;
        LoadControlDevice other = (LoadControlDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.ratedVoltage==null && other.getRatedVoltage()==null) || 
             (this.ratedVoltage!=null &&
              this.ratedVoltage.equals(other.getRatedVoltage()))) &&
            ((this.ratedCurrent==null && other.getRatedCurrent()==null) || 
             (this.ratedCurrent!=null &&
              this.ratedCurrent.equals(other.getRatedCurrent()))) &&
            ((this.numberOfRelays==null && other.getNumberOfRelays()==null) || 
             (this.numberOfRelays!=null &&
              this.numberOfRelays.equals(other.getNumberOfRelays()))) &&
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
        if (getRatedVoltage() != null) {
            _hashCode += getRatedVoltage().hashCode();
        }
        if (getRatedCurrent() != null) {
            _hashCode += getRatedCurrent().hashCode();
        }
        if (getNumberOfRelays() != null) {
            _hashCode += getNumberOfRelays().hashCode();
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
        new org.apache.axis.description.TypeDesc(LoadControlDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadControlDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("numberOfRelays");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfRelays"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
