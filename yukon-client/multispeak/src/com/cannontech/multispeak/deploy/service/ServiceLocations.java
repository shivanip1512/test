/**
 * ServiceLocations.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ServiceLocations  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] electricServiceLocations;

    private com.cannontech.multispeak.deploy.service.WaterServiceLocation[] waterServiceLocations;

    private com.cannontech.multispeak.deploy.service.GasServiceLocation[] gasServiceLocations;

    private com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] propaneServiceLocations;

    public ServiceLocations() {
    }

    public ServiceLocations(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] electricServiceLocations,
           com.cannontech.multispeak.deploy.service.WaterServiceLocation[] waterServiceLocations,
           com.cannontech.multispeak.deploy.service.GasServiceLocation[] gasServiceLocations,
           com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] propaneServiceLocations) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.electricServiceLocations = electricServiceLocations;
        this.waterServiceLocations = waterServiceLocations;
        this.gasServiceLocations = gasServiceLocations;
        this.propaneServiceLocations = propaneServiceLocations;
    }


    /**
     * Gets the electricServiceLocations value for this ServiceLocations.
     * 
     * @return electricServiceLocations
     */
    public com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] getElectricServiceLocations() {
        return electricServiceLocations;
    }


    /**
     * Sets the electricServiceLocations value for this ServiceLocations.
     * 
     * @param electricServiceLocations
     */
    public void setElectricServiceLocations(com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] electricServiceLocations) {
        this.electricServiceLocations = electricServiceLocations;
    }


    /**
     * Gets the waterServiceLocations value for this ServiceLocations.
     * 
     * @return waterServiceLocations
     */
    public com.cannontech.multispeak.deploy.service.WaterServiceLocation[] getWaterServiceLocations() {
        return waterServiceLocations;
    }


    /**
     * Sets the waterServiceLocations value for this ServiceLocations.
     * 
     * @param waterServiceLocations
     */
    public void setWaterServiceLocations(com.cannontech.multispeak.deploy.service.WaterServiceLocation[] waterServiceLocations) {
        this.waterServiceLocations = waterServiceLocations;
    }


    /**
     * Gets the gasServiceLocations value for this ServiceLocations.
     * 
     * @return gasServiceLocations
     */
    public com.cannontech.multispeak.deploy.service.GasServiceLocation[] getGasServiceLocations() {
        return gasServiceLocations;
    }


    /**
     * Sets the gasServiceLocations value for this ServiceLocations.
     * 
     * @param gasServiceLocations
     */
    public void setGasServiceLocations(com.cannontech.multispeak.deploy.service.GasServiceLocation[] gasServiceLocations) {
        this.gasServiceLocations = gasServiceLocations;
    }


    /**
     * Gets the propaneServiceLocations value for this ServiceLocations.
     * 
     * @return propaneServiceLocations
     */
    public com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] getPropaneServiceLocations() {
        return propaneServiceLocations;
    }


    /**
     * Sets the propaneServiceLocations value for this ServiceLocations.
     * 
     * @param propaneServiceLocations
     */
    public void setPropaneServiceLocations(com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] propaneServiceLocations) {
        this.propaneServiceLocations = propaneServiceLocations;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceLocations)) return false;
        ServiceLocations other = (ServiceLocations) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.electricServiceLocations==null && other.getElectricServiceLocations()==null) || 
             (this.electricServiceLocations!=null &&
              java.util.Arrays.equals(this.electricServiceLocations, other.getElectricServiceLocations()))) &&
            ((this.waterServiceLocations==null && other.getWaterServiceLocations()==null) || 
             (this.waterServiceLocations!=null &&
              java.util.Arrays.equals(this.waterServiceLocations, other.getWaterServiceLocations()))) &&
            ((this.gasServiceLocations==null && other.getGasServiceLocations()==null) || 
             (this.gasServiceLocations!=null &&
              java.util.Arrays.equals(this.gasServiceLocations, other.getGasServiceLocations()))) &&
            ((this.propaneServiceLocations==null && other.getPropaneServiceLocations()==null) || 
             (this.propaneServiceLocations!=null &&
              java.util.Arrays.equals(this.propaneServiceLocations, other.getPropaneServiceLocations())));
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
        if (getElectricServiceLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getElectricServiceLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getElectricServiceLocations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWaterServiceLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWaterServiceLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWaterServiceLocations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGasServiceLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGasServiceLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGasServiceLocations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPropaneServiceLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPropaneServiceLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPropaneServiceLocations(), i);
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
        new org.apache.axis.description.TypeDesc(ServiceLocations.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocations"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electricServiceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricServiceLocation"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waterServiceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasServiceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasServiceLocation"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propaneServiceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneServiceLocation"));
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
