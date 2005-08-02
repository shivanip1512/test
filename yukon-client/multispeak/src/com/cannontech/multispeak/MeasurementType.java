/**
 * MeasurementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeasurementType  implements java.io.Serializable {
    private java.util.Calendar dateTime;
    private java.lang.Float value;
    private com.cannontech.multispeak.Uom uom;
    private com.cannontech.multispeak.QualityDescription quality;
    private com.cannontech.multispeak.TimeSpan timeSpan;
    private java.util.Calendar stopTime;
    private com.cannontech.multispeak.DurationDescription durationDescription;
    private com.cannontech.multispeak.UnitPrefix unitPrefix;
    private com.cannontech.multispeak.Accountability quadrant;
    private com.cannontech.multispeak.PhaseCd phase;
    private java.lang.Long harmonic;

    public MeasurementType() {
    }

    public MeasurementType(
           java.util.Calendar dateTime,
           java.lang.Float value,
           com.cannontech.multispeak.Uom uom,
           com.cannontech.multispeak.QualityDescription quality,
           com.cannontech.multispeak.TimeSpan timeSpan,
           java.util.Calendar stopTime,
           com.cannontech.multispeak.DurationDescription durationDescription,
           com.cannontech.multispeak.UnitPrefix unitPrefix,
           com.cannontech.multispeak.Accountability quadrant,
           com.cannontech.multispeak.PhaseCd phase,
           java.lang.Long harmonic) {
           this.dateTime = dateTime;
           this.value = value;
           this.uom = uom;
           this.quality = quality;
           this.timeSpan = timeSpan;
           this.stopTime = stopTime;
           this.durationDescription = durationDescription;
           this.unitPrefix = unitPrefix;
           this.quadrant = quadrant;
           this.phase = phase;
           this.harmonic = harmonic;
    }


    /**
     * Gets the dateTime value for this MeasurementType.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this MeasurementType.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the value value for this MeasurementType.
     * 
     * @return value
     */
    public java.lang.Float getValue() {
        return value;
    }


    /**
     * Sets the value value for this MeasurementType.
     * 
     * @param value
     */
    public void setValue(java.lang.Float value) {
        this.value = value;
    }


    /**
     * Gets the uom value for this MeasurementType.
     * 
     * @return uom
     */
    public com.cannontech.multispeak.Uom getUom() {
        return uom;
    }


    /**
     * Sets the uom value for this MeasurementType.
     * 
     * @param uom
     */
    public void setUom(com.cannontech.multispeak.Uom uom) {
        this.uom = uom;
    }


    /**
     * Gets the quality value for this MeasurementType.
     * 
     * @return quality
     */
    public com.cannontech.multispeak.QualityDescription getQuality() {
        return quality;
    }


    /**
     * Sets the quality value for this MeasurementType.
     * 
     * @param quality
     */
    public void setQuality(com.cannontech.multispeak.QualityDescription quality) {
        this.quality = quality;
    }


    /**
     * Gets the timeSpan value for this MeasurementType.
     * 
     * @return timeSpan
     */
    public com.cannontech.multispeak.TimeSpan getTimeSpan() {
        return timeSpan;
    }


    /**
     * Sets the timeSpan value for this MeasurementType.
     * 
     * @param timeSpan
     */
    public void setTimeSpan(com.cannontech.multispeak.TimeSpan timeSpan) {
        this.timeSpan = timeSpan;
    }


    /**
     * Gets the stopTime value for this MeasurementType.
     * 
     * @return stopTime
     */
    public java.util.Calendar getStopTime() {
        return stopTime;
    }


    /**
     * Sets the stopTime value for this MeasurementType.
     * 
     * @param stopTime
     */
    public void setStopTime(java.util.Calendar stopTime) {
        this.stopTime = stopTime;
    }


    /**
     * Gets the durationDescription value for this MeasurementType.
     * 
     * @return durationDescription
     */
    public com.cannontech.multispeak.DurationDescription getDurationDescription() {
        return durationDescription;
    }


    /**
     * Sets the durationDescription value for this MeasurementType.
     * 
     * @param durationDescription
     */
    public void setDurationDescription(com.cannontech.multispeak.DurationDescription durationDescription) {
        this.durationDescription = durationDescription;
    }


    /**
     * Gets the unitPrefix value for this MeasurementType.
     * 
     * @return unitPrefix
     */
    public com.cannontech.multispeak.UnitPrefix getUnitPrefix() {
        return unitPrefix;
    }


    /**
     * Sets the unitPrefix value for this MeasurementType.
     * 
     * @param unitPrefix
     */
    public void setUnitPrefix(com.cannontech.multispeak.UnitPrefix unitPrefix) {
        this.unitPrefix = unitPrefix;
    }


    /**
     * Gets the quadrant value for this MeasurementType.
     * 
     * @return quadrant
     */
    public com.cannontech.multispeak.Accountability getQuadrant() {
        return quadrant;
    }


    /**
     * Sets the quadrant value for this MeasurementType.
     * 
     * @param quadrant
     */
    public void setQuadrant(com.cannontech.multispeak.Accountability quadrant) {
        this.quadrant = quadrant;
    }


    /**
     * Gets the phase value for this MeasurementType.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this MeasurementType.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }


    /**
     * Gets the harmonic value for this MeasurementType.
     * 
     * @return harmonic
     */
    public java.lang.Long getHarmonic() {
        return harmonic;
    }


    /**
     * Sets the harmonic value for this MeasurementType.
     * 
     * @param harmonic
     */
    public void setHarmonic(java.lang.Long harmonic) {
        this.harmonic = harmonic;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeasurementType)) return false;
        MeasurementType other = (MeasurementType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.uom==null && other.getUom()==null) || 
             (this.uom!=null &&
              this.uom.equals(other.getUom()))) &&
            ((this.quality==null && other.getQuality()==null) || 
             (this.quality!=null &&
              this.quality.equals(other.getQuality()))) &&
            ((this.timeSpan==null && other.getTimeSpan()==null) || 
             (this.timeSpan!=null &&
              this.timeSpan.equals(other.getTimeSpan()))) &&
            ((this.stopTime==null && other.getStopTime()==null) || 
             (this.stopTime!=null &&
              this.stopTime.equals(other.getStopTime()))) &&
            ((this.durationDescription==null && other.getDurationDescription()==null) || 
             (this.durationDescription!=null &&
              this.durationDescription.equals(other.getDurationDescription()))) &&
            ((this.unitPrefix==null && other.getUnitPrefix()==null) || 
             (this.unitPrefix!=null &&
              this.unitPrefix.equals(other.getUnitPrefix()))) &&
            ((this.quadrant==null && other.getQuadrant()==null) || 
             (this.quadrant!=null &&
              this.quadrant.equals(other.getQuadrant()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.harmonic==null && other.getHarmonic()==null) || 
             (this.harmonic!=null &&
              this.harmonic.equals(other.getHarmonic())));
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
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getUom() != null) {
            _hashCode += getUom().hashCode();
        }
        if (getQuality() != null) {
            _hashCode += getQuality().hashCode();
        }
        if (getTimeSpan() != null) {
            _hashCode += getTimeSpan().hashCode();
        }
        if (getStopTime() != null) {
            _hashCode += getStopTime().hashCode();
        }
        if (getDurationDescription() != null) {
            _hashCode += getDurationDescription().hashCode();
        }
        if (getUnitPrefix() != null) {
            _hashCode += getUnitPrefix().hashCode();
        }
        if (getQuadrant() != null) {
            _hashCode += getQuadrant().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getHarmonic() != null) {
            _hashCode += getHarmonic().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeasurementType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "uom"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "qualityDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeSpan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeSpan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeSpan"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stopTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stopTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("durationDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "durationDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "durationDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitPrefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitPrefix"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quadrant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quadrant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountability"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("harmonic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "harmonic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
