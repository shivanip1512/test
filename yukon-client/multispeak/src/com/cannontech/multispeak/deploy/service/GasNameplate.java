/**
 * GasNameplate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GasNameplate  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GasNameplateMechanicalForm mechanicalForm;

    private com.cannontech.multispeak.deploy.service.GasNameplateMeasurementSystem measurementSystem;

    private com.cannontech.multispeak.deploy.service.GasNameplateGasPressure gasPressure;

    private com.cannontech.multispeak.deploy.service.GasNameplateGasFlow gasFlow;

    private com.cannontech.multispeak.deploy.service.GasNameplateGearDriveSize gearDriveSize;

    private com.cannontech.multispeak.deploy.service.GasNameplateInternalPipeDiameter internalPipeDiameter;

    private com.cannontech.multispeak.deploy.service.GasNameplateTemperatureCompensationType temperatureCompensationType;

    private com.cannontech.multispeak.deploy.service.GasNameplatePressureCompensationType pressureCompensationType;

    public GasNameplate() {
    }

    public GasNameplate(
           com.cannontech.multispeak.deploy.service.GasNameplateMechanicalForm mechanicalForm,
           com.cannontech.multispeak.deploy.service.GasNameplateMeasurementSystem measurementSystem,
           com.cannontech.multispeak.deploy.service.GasNameplateGasPressure gasPressure,
           com.cannontech.multispeak.deploy.service.GasNameplateGasFlow gasFlow,
           com.cannontech.multispeak.deploy.service.GasNameplateGearDriveSize gearDriveSize,
           com.cannontech.multispeak.deploy.service.GasNameplateInternalPipeDiameter internalPipeDiameter,
           com.cannontech.multispeak.deploy.service.GasNameplateTemperatureCompensationType temperatureCompensationType,
           com.cannontech.multispeak.deploy.service.GasNameplatePressureCompensationType pressureCompensationType) {
           this.mechanicalForm = mechanicalForm;
           this.measurementSystem = measurementSystem;
           this.gasPressure = gasPressure;
           this.gasFlow = gasFlow;
           this.gearDriveSize = gearDriveSize;
           this.internalPipeDiameter = internalPipeDiameter;
           this.temperatureCompensationType = temperatureCompensationType;
           this.pressureCompensationType = pressureCompensationType;
    }


    /**
     * Gets the mechanicalForm value for this GasNameplate.
     * 
     * @return mechanicalForm
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateMechanicalForm getMechanicalForm() {
        return mechanicalForm;
    }


    /**
     * Sets the mechanicalForm value for this GasNameplate.
     * 
     * @param mechanicalForm
     */
    public void setMechanicalForm(com.cannontech.multispeak.deploy.service.GasNameplateMechanicalForm mechanicalForm) {
        this.mechanicalForm = mechanicalForm;
    }


    /**
     * Gets the measurementSystem value for this GasNameplate.
     * 
     * @return measurementSystem
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateMeasurementSystem getMeasurementSystem() {
        return measurementSystem;
    }


    /**
     * Sets the measurementSystem value for this GasNameplate.
     * 
     * @param measurementSystem
     */
    public void setMeasurementSystem(com.cannontech.multispeak.deploy.service.GasNameplateMeasurementSystem measurementSystem) {
        this.measurementSystem = measurementSystem;
    }


    /**
     * Gets the gasPressure value for this GasNameplate.
     * 
     * @return gasPressure
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateGasPressure getGasPressure() {
        return gasPressure;
    }


    /**
     * Sets the gasPressure value for this GasNameplate.
     * 
     * @param gasPressure
     */
    public void setGasPressure(com.cannontech.multispeak.deploy.service.GasNameplateGasPressure gasPressure) {
        this.gasPressure = gasPressure;
    }


    /**
     * Gets the gasFlow value for this GasNameplate.
     * 
     * @return gasFlow
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateGasFlow getGasFlow() {
        return gasFlow;
    }


    /**
     * Sets the gasFlow value for this GasNameplate.
     * 
     * @param gasFlow
     */
    public void setGasFlow(com.cannontech.multispeak.deploy.service.GasNameplateGasFlow gasFlow) {
        this.gasFlow = gasFlow;
    }


    /**
     * Gets the gearDriveSize value for this GasNameplate.
     * 
     * @return gearDriveSize
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateGearDriveSize getGearDriveSize() {
        return gearDriveSize;
    }


    /**
     * Sets the gearDriveSize value for this GasNameplate.
     * 
     * @param gearDriveSize
     */
    public void setGearDriveSize(com.cannontech.multispeak.deploy.service.GasNameplateGearDriveSize gearDriveSize) {
        this.gearDriveSize = gearDriveSize;
    }


    /**
     * Gets the internalPipeDiameter value for this GasNameplate.
     * 
     * @return internalPipeDiameter
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateInternalPipeDiameter getInternalPipeDiameter() {
        return internalPipeDiameter;
    }


    /**
     * Sets the internalPipeDiameter value for this GasNameplate.
     * 
     * @param internalPipeDiameter
     */
    public void setInternalPipeDiameter(com.cannontech.multispeak.deploy.service.GasNameplateInternalPipeDiameter internalPipeDiameter) {
        this.internalPipeDiameter = internalPipeDiameter;
    }


    /**
     * Gets the temperatureCompensationType value for this GasNameplate.
     * 
     * @return temperatureCompensationType
     */
    public com.cannontech.multispeak.deploy.service.GasNameplateTemperatureCompensationType getTemperatureCompensationType() {
        return temperatureCompensationType;
    }


    /**
     * Sets the temperatureCompensationType value for this GasNameplate.
     * 
     * @param temperatureCompensationType
     */
    public void setTemperatureCompensationType(com.cannontech.multispeak.deploy.service.GasNameplateTemperatureCompensationType temperatureCompensationType) {
        this.temperatureCompensationType = temperatureCompensationType;
    }


    /**
     * Gets the pressureCompensationType value for this GasNameplate.
     * 
     * @return pressureCompensationType
     */
    public com.cannontech.multispeak.deploy.service.GasNameplatePressureCompensationType getPressureCompensationType() {
        return pressureCompensationType;
    }


    /**
     * Sets the pressureCompensationType value for this GasNameplate.
     * 
     * @param pressureCompensationType
     */
    public void setPressureCompensationType(com.cannontech.multispeak.deploy.service.GasNameplatePressureCompensationType pressureCompensationType) {
        this.pressureCompensationType = pressureCompensationType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GasNameplate)) return false;
        GasNameplate other = (GasNameplate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mechanicalForm==null && other.getMechanicalForm()==null) || 
             (this.mechanicalForm!=null &&
              this.mechanicalForm.equals(other.getMechanicalForm()))) &&
            ((this.measurementSystem==null && other.getMeasurementSystem()==null) || 
             (this.measurementSystem!=null &&
              this.measurementSystem.equals(other.getMeasurementSystem()))) &&
            ((this.gasPressure==null && other.getGasPressure()==null) || 
             (this.gasPressure!=null &&
              this.gasPressure.equals(other.getGasPressure()))) &&
            ((this.gasFlow==null && other.getGasFlow()==null) || 
             (this.gasFlow!=null &&
              this.gasFlow.equals(other.getGasFlow()))) &&
            ((this.gearDriveSize==null && other.getGearDriveSize()==null) || 
             (this.gearDriveSize!=null &&
              this.gearDriveSize.equals(other.getGearDriveSize()))) &&
            ((this.internalPipeDiameter==null && other.getInternalPipeDiameter()==null) || 
             (this.internalPipeDiameter!=null &&
              this.internalPipeDiameter.equals(other.getInternalPipeDiameter()))) &&
            ((this.temperatureCompensationType==null && other.getTemperatureCompensationType()==null) || 
             (this.temperatureCompensationType!=null &&
              this.temperatureCompensationType.equals(other.getTemperatureCompensationType()))) &&
            ((this.pressureCompensationType==null && other.getPressureCompensationType()==null) || 
             (this.pressureCompensationType!=null &&
              this.pressureCompensationType.equals(other.getPressureCompensationType())));
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
        if (getMechanicalForm() != null) {
            _hashCode += getMechanicalForm().hashCode();
        }
        if (getMeasurementSystem() != null) {
            _hashCode += getMeasurementSystem().hashCode();
        }
        if (getGasPressure() != null) {
            _hashCode += getGasPressure().hashCode();
        }
        if (getGasFlow() != null) {
            _hashCode += getGasFlow().hashCode();
        }
        if (getGearDriveSize() != null) {
            _hashCode += getGearDriveSize().hashCode();
        }
        if (getInternalPipeDiameter() != null) {
            _hashCode += getInternalPipeDiameter().hashCode();
        }
        if (getTemperatureCompensationType() != null) {
            _hashCode += getTemperatureCompensationType().hashCode();
        }
        if (getPressureCompensationType() != null) {
            _hashCode += getPressureCompensationType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GasNameplate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasNameplate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mechanicalForm");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mechanicalForm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>mechanicalForm"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementSystem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementSystem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>measurementSystem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasPressure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasPressure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasPressure"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gasFlow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gasFlow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gasFlow"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gearDriveSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gearDriveSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>gearDriveSize"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("internalPipeDiameter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "internalPipeDiameter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>internalPipeDiameter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("temperatureCompensationType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "temperatureCompensationType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>temperatureCompensationType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pressureCompensationType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pressureCompensationType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">gasNameplate>pressureCompensationType"));
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
