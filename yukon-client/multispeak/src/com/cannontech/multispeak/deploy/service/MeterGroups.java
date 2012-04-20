/**
 * MeterGroups.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeterGroups  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterGroup[] electricMeterGroups;

    private com.cannontech.multispeak.deploy.service.MeterGroup[] gasMeterGroups;

    private com.cannontech.multispeak.deploy.service.MeterGroup[] waterMeterGroups;

    private com.cannontech.multispeak.deploy.service.MeterGroup[] propaneMeterGroups;

    private com.cannontech.multispeak.deploy.service.MeterGroups[] mixedMeterGroups;

    public MeterGroups() {
    }

    public MeterGroups(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.MeterGroup[] electricMeterGroups,
           com.cannontech.multispeak.deploy.service.MeterGroup[] gasMeterGroups,
           com.cannontech.multispeak.deploy.service.MeterGroup[] waterMeterGroups,
           com.cannontech.multispeak.deploy.service.MeterGroup[] propaneMeterGroups,
           com.cannontech.multispeak.deploy.service.MeterGroups[] mixedMeterGroups) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.electricMeterGroups = electricMeterGroups;
        this.gasMeterGroups = gasMeterGroups;
        this.waterMeterGroups = waterMeterGroups;
        this.propaneMeterGroups = propaneMeterGroups;
        this.mixedMeterGroups = mixedMeterGroups;
    }


    /**
     * Gets the electricMeterGroups value for this MeterGroups.
     * 
     * @return electricMeterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup[] getElectricMeterGroups() {
        return electricMeterGroups;
    }


    /**
     * Sets the electricMeterGroups value for this MeterGroups.
     * 
     * @param electricMeterGroups
     */
    public void setElectricMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroup[] electricMeterGroups) {
        this.electricMeterGroups = electricMeterGroups;
    }


    /**
     * Gets the gasMeterGroups value for this MeterGroups.
     * 
     * @return gasMeterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup[] getGasMeterGroups() {
        return gasMeterGroups;
    }


    /**
     * Sets the gasMeterGroups value for this MeterGroups.
     * 
     * @param gasMeterGroups
     */
    public void setGasMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroup[] gasMeterGroups) {
        this.gasMeterGroups = gasMeterGroups;
    }


    /**
     * Gets the waterMeterGroups value for this MeterGroups.
     * 
     * @return waterMeterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup[] getWaterMeterGroups() {
        return waterMeterGroups;
    }


    /**
     * Sets the waterMeterGroups value for this MeterGroups.
     * 
     * @param waterMeterGroups
     */
    public void setWaterMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroup[] waterMeterGroups) {
        this.waterMeterGroups = waterMeterGroups;
    }


    /**
     * Gets the propaneMeterGroups value for this MeterGroups.
     * 
     * @return propaneMeterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup[] getPropaneMeterGroups() {
        return propaneMeterGroups;
    }


    /**
     * Sets the propaneMeterGroups value for this MeterGroups.
     * 
     * @param propaneMeterGroups
     */
    public void setPropaneMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroup[] propaneMeterGroups) {
        this.propaneMeterGroups = propaneMeterGroups;
    }


    /**
     * Gets the mixedMeterGroups value for this MeterGroups.
     * 
     * @return mixedMeterGroups
     */
    public com.cannontech.multispeak.deploy.service.MeterGroups[] getMixedMeterGroups() {
        return mixedMeterGroups;
    }


    /**
     * Sets the mixedMeterGroups value for this MeterGroups.
     * 
     * @param mixedMeterGroups
     */
    public void setMixedMeterGroups(com.cannontech.multispeak.deploy.service.MeterGroups[] mixedMeterGroups) {
        this.mixedMeterGroups = mixedMeterGroups;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterGroups)) return false;
        MeterGroups other = (MeterGroups) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.electricMeterGroups==null && other.getElectricMeterGroups()==null) || 
             (this.electricMeterGroups!=null &&
              java.util.Arrays.equals(this.electricMeterGroups, other.getElectricMeterGroups()))) &&
            ((this.gasMeterGroups==null && other.getGasMeterGroups()==null) || 
             (this.gasMeterGroups!=null &&
              java.util.Arrays.equals(this.gasMeterGroups, other.getGasMeterGroups()))) &&
            ((this.waterMeterGroups==null && other.getWaterMeterGroups()==null) || 
             (this.waterMeterGroups!=null &&
              java.util.Arrays.equals(this.waterMeterGroups, other.getWaterMeterGroups()))) &&
            ((this.propaneMeterGroups==null && other.getPropaneMeterGroups()==null) || 
             (this.propaneMeterGroups!=null &&
              java.util.Arrays.equals(this.propaneMeterGroups, other.getPropaneMeterGroups()))) &&
            ((this.mixedMeterGroups==null && other.getMixedMeterGroups()==null) || 
             (this.mixedMeterGroups!=null &&
              java.util.Arrays.equals(this.mixedMeterGroups, other.getMixedMeterGroups())));
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
        if (getElectricMeterGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getElectricMeterGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getElectricMeterGroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGasMeterGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGasMeterGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGasMeterGroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWaterMeterGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWaterMeterGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWaterMeterGroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPropaneMeterGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPropaneMeterGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPropaneMeterGroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMixedMeterGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMixedMeterGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMixedMeterGroups(), i);
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
        new org.apache.axis.description.TypeDesc(MeterGroups.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("electricMeterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "electricMeterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasMeterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasMeterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("waterMeterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterMeterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propaneMeterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "propaneMeterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mixedMeterGroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mixedMeterGroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroups"));
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
