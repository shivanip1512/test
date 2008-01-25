/**
 * Telemetry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Telemetry  implements java.io.Serializable {
    private java.lang.Float speed;

    private java.lang.Float heading;

    private java.lang.String cardinalHeading;

    private java.lang.Float odometer;

    private java.lang.Float maxVehSpeedLastReading;

    private org.apache.axis.types.PositiveInteger maxEngineSpeedLastReading;

    private java.lang.String engineRunningState;

    private java.lang.String gearShiftPosition;

    private java.lang.String VTCUEvent;

    public Telemetry() {
    }

    public Telemetry(
           java.lang.Float speed,
           java.lang.Float heading,
           java.lang.String cardinalHeading,
           java.lang.Float odometer,
           java.lang.Float maxVehSpeedLastReading,
           org.apache.axis.types.PositiveInteger maxEngineSpeedLastReading,
           java.lang.String engineRunningState,
           java.lang.String gearShiftPosition,
           java.lang.String VTCUEvent) {
           this.speed = speed;
           this.heading = heading;
           this.cardinalHeading = cardinalHeading;
           this.odometer = odometer;
           this.maxVehSpeedLastReading = maxVehSpeedLastReading;
           this.maxEngineSpeedLastReading = maxEngineSpeedLastReading;
           this.engineRunningState = engineRunningState;
           this.gearShiftPosition = gearShiftPosition;
           this.VTCUEvent = VTCUEvent;
    }


    /**
     * Gets the speed value for this Telemetry.
     * 
     * @return speed
     */
    public java.lang.Float getSpeed() {
        return speed;
    }


    /**
     * Sets the speed value for this Telemetry.
     * 
     * @param speed
     */
    public void setSpeed(java.lang.Float speed) {
        this.speed = speed;
    }


    /**
     * Gets the heading value for this Telemetry.
     * 
     * @return heading
     */
    public java.lang.Float getHeading() {
        return heading;
    }


    /**
     * Sets the heading value for this Telemetry.
     * 
     * @param heading
     */
    public void setHeading(java.lang.Float heading) {
        this.heading = heading;
    }


    /**
     * Gets the cardinalHeading value for this Telemetry.
     * 
     * @return cardinalHeading
     */
    public java.lang.String getCardinalHeading() {
        return cardinalHeading;
    }


    /**
     * Sets the cardinalHeading value for this Telemetry.
     * 
     * @param cardinalHeading
     */
    public void setCardinalHeading(java.lang.String cardinalHeading) {
        this.cardinalHeading = cardinalHeading;
    }


    /**
     * Gets the odometer value for this Telemetry.
     * 
     * @return odometer
     */
    public java.lang.Float getOdometer() {
        return odometer;
    }


    /**
     * Sets the odometer value for this Telemetry.
     * 
     * @param odometer
     */
    public void setOdometer(java.lang.Float odometer) {
        this.odometer = odometer;
    }


    /**
     * Gets the maxVehSpeedLastReading value for this Telemetry.
     * 
     * @return maxVehSpeedLastReading
     */
    public java.lang.Float getMaxVehSpeedLastReading() {
        return maxVehSpeedLastReading;
    }


    /**
     * Sets the maxVehSpeedLastReading value for this Telemetry.
     * 
     * @param maxVehSpeedLastReading
     */
    public void setMaxVehSpeedLastReading(java.lang.Float maxVehSpeedLastReading) {
        this.maxVehSpeedLastReading = maxVehSpeedLastReading;
    }


    /**
     * Gets the maxEngineSpeedLastReading value for this Telemetry.
     * 
     * @return maxEngineSpeedLastReading
     */
    public org.apache.axis.types.PositiveInteger getMaxEngineSpeedLastReading() {
        return maxEngineSpeedLastReading;
    }


    /**
     * Sets the maxEngineSpeedLastReading value for this Telemetry.
     * 
     * @param maxEngineSpeedLastReading
     */
    public void setMaxEngineSpeedLastReading(org.apache.axis.types.PositiveInteger maxEngineSpeedLastReading) {
        this.maxEngineSpeedLastReading = maxEngineSpeedLastReading;
    }


    /**
     * Gets the engineRunningState value for this Telemetry.
     * 
     * @return engineRunningState
     */
    public java.lang.String getEngineRunningState() {
        return engineRunningState;
    }


    /**
     * Sets the engineRunningState value for this Telemetry.
     * 
     * @param engineRunningState
     */
    public void setEngineRunningState(java.lang.String engineRunningState) {
        this.engineRunningState = engineRunningState;
    }


    /**
     * Gets the gearShiftPosition value for this Telemetry.
     * 
     * @return gearShiftPosition
     */
    public java.lang.String getGearShiftPosition() {
        return gearShiftPosition;
    }


    /**
     * Sets the gearShiftPosition value for this Telemetry.
     * 
     * @param gearShiftPosition
     */
    public void setGearShiftPosition(java.lang.String gearShiftPosition) {
        this.gearShiftPosition = gearShiftPosition;
    }


    /**
     * Gets the VTCUEvent value for this Telemetry.
     * 
     * @return VTCUEvent
     */
    public java.lang.String getVTCUEvent() {
        return VTCUEvent;
    }


    /**
     * Sets the VTCUEvent value for this Telemetry.
     * 
     * @param VTCUEvent
     */
    public void setVTCUEvent(java.lang.String VTCUEvent) {
        this.VTCUEvent = VTCUEvent;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Telemetry)) return false;
        Telemetry other = (Telemetry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.speed==null && other.getSpeed()==null) || 
             (this.speed!=null &&
              this.speed.equals(other.getSpeed()))) &&
            ((this.heading==null && other.getHeading()==null) || 
             (this.heading!=null &&
              this.heading.equals(other.getHeading()))) &&
            ((this.cardinalHeading==null && other.getCardinalHeading()==null) || 
             (this.cardinalHeading!=null &&
              this.cardinalHeading.equals(other.getCardinalHeading()))) &&
            ((this.odometer==null && other.getOdometer()==null) || 
             (this.odometer!=null &&
              this.odometer.equals(other.getOdometer()))) &&
            ((this.maxVehSpeedLastReading==null && other.getMaxVehSpeedLastReading()==null) || 
             (this.maxVehSpeedLastReading!=null &&
              this.maxVehSpeedLastReading.equals(other.getMaxVehSpeedLastReading()))) &&
            ((this.maxEngineSpeedLastReading==null && other.getMaxEngineSpeedLastReading()==null) || 
             (this.maxEngineSpeedLastReading!=null &&
              this.maxEngineSpeedLastReading.equals(other.getMaxEngineSpeedLastReading()))) &&
            ((this.engineRunningState==null && other.getEngineRunningState()==null) || 
             (this.engineRunningState!=null &&
              this.engineRunningState.equals(other.getEngineRunningState()))) &&
            ((this.gearShiftPosition==null && other.getGearShiftPosition()==null) || 
             (this.gearShiftPosition!=null &&
              this.gearShiftPosition.equals(other.getGearShiftPosition()))) &&
            ((this.VTCUEvent==null && other.getVTCUEvent()==null) || 
             (this.VTCUEvent!=null &&
              this.VTCUEvent.equals(other.getVTCUEvent())));
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
        if (getSpeed() != null) {
            _hashCode += getSpeed().hashCode();
        }
        if (getHeading() != null) {
            _hashCode += getHeading().hashCode();
        }
        if (getCardinalHeading() != null) {
            _hashCode += getCardinalHeading().hashCode();
        }
        if (getOdometer() != null) {
            _hashCode += getOdometer().hashCode();
        }
        if (getMaxVehSpeedLastReading() != null) {
            _hashCode += getMaxVehSpeedLastReading().hashCode();
        }
        if (getMaxEngineSpeedLastReading() != null) {
            _hashCode += getMaxEngineSpeedLastReading().hashCode();
        }
        if (getEngineRunningState() != null) {
            _hashCode += getEngineRunningState().hashCode();
        }
        if (getGearShiftPosition() != null) {
            _hashCode += getGearShiftPosition().hashCode();
        }
        if (getVTCUEvent() != null) {
            _hashCode += getVTCUEvent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Telemetry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "telemetry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("speed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "speed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("heading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "heading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardinalHeading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cardinalHeading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("odometer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "odometer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxVehSpeedLastReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxVehSpeedLastReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxEngineSpeedLastReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxEngineSpeedLastReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "positiveInteger"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("engineRunningState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "engineRunningState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gearShiftPosition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gearShiftPosition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("VTCUEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "VTCUEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
