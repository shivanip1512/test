/**
 * Meters.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Meters  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ElectricMeter[] electricMeters;

    private com.cannontech.multispeak.deploy.service.WaterMeter[] waterMeters;

    private com.cannontech.multispeak.deploy.service.GasMeter[] gasMeters;

    private com.cannontech.multispeak.deploy.service.PropaneMeter[] propaneMeters;

    public Meters() {
    }

    public Meters(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.ElectricMeter[] electricMeters,
           com.cannontech.multispeak.deploy.service.WaterMeter[] waterMeters,
           com.cannontech.multispeak.deploy.service.GasMeter[] gasMeters,
           com.cannontech.multispeak.deploy.service.PropaneMeter[] propaneMeters) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.electricMeters = electricMeters;
        this.waterMeters = waterMeters;
        this.gasMeters = gasMeters;
        this.propaneMeters = propaneMeters;
    }


    /**
     * Gets the electricMeters value for this Meters.
     * 
     * @return electricMeters
     */
    public com.cannontech.multispeak.deploy.service.ElectricMeter[] getElectricMeters() {
        return electricMeters;
    }


    /**
     * Sets the electricMeters value for this Meters.
     * 
     * @param electricMeters
     */
    public void setElectricMeters(com.cannontech.multispeak.deploy.service.ElectricMeter[] electricMeters) {
        this.electricMeters = electricMeters;
    }


    /**
     * Gets the waterMeters value for this Meters.
     * 
     * @return waterMeters
     */
    public com.cannontech.multispeak.deploy.service.WaterMeter[] getWaterMeters() {
        return waterMeters;
    }


    /**
     * Sets the waterMeters value for this Meters.
     * 
     * @param waterMeters
     */
    public void setWaterMeters(com.cannontech.multispeak.deploy.service.WaterMeter[] waterMeters) {
        this.waterMeters = waterMeters;
    }


    /**
     * Gets the gasMeters value for this Meters.
     * 
     * @return gasMeters
     */
    public com.cannontech.multispeak.deploy.service.GasMeter[] getGasMeters() {
        return gasMeters;
    }


    /**
     * Sets the gasMeters value for this Meters.
     * 
     * @param gasMeters
     */
    public void setGasMeters(com.cannontech.multispeak.deploy.service.GasMeter[] gasMeters) {
        this.gasMeters = gasMeters;
    }


    /**
     * Gets the propaneMeters value for this Meters.
     * 
     * @return propaneMeters
     */
    public com.cannontech.multispeak.deploy.service.PropaneMeter[] getPropaneMeters() {
        return propaneMeters;
    }


    /**
     * Sets the propaneMeters value for this Meters.
     * 
     * @param propaneMeters
     */
    public void setPropaneMeters(com.cannontech.multispeak.deploy.service.PropaneMeter[] propaneMeters) {
        this.propaneMeters = propaneMeters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Meters)) return false;
        Meters other = (Meters) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.electricMeters==null && other.getElectricMeters()==null) || 
             (this.electricMeters!=null &&
              java.util.Arrays.equals(this.electricMeters, other.getElectricMeters()))) &&
            ((this.waterMeters==null && other.getWaterMeters()==null) || 
             (this.waterMeters!=null &&
              java.util.Arrays.equals(this.waterMeters, other.getWaterMeters()))) &&
            ((this.gasMeters==null && other.getGasMeters()==null) || 
             (this.gasMeters!=null &&
              java.util.Arrays.equals(this.gasMeters, other.getGasMeters()))) &&
            ((this.propaneMeters==null && other.getPropaneMeters()==null) || 
             (this.propaneMeters!=null &&
              java.util.Arrays.equals(this.propaneMeters, other.getPropaneMeters())));
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
        if (getElectricMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getElectricMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getElectricMeters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWaterMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWaterMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWaterMeters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGasMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGasMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGasMeters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPropaneMeters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPropaneMeters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPropaneMeters(), i);
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
        new org.apache.axis.description.TypeDesc(Meters.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meters"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electricMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeter"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waterMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeter"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeter"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propaneMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeter"));
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
