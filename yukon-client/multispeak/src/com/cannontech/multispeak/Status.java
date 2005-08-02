/**
 * Status.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Status  implements java.io.Serializable {
    private java.lang.Boolean clockBackward;
    private java.lang.Boolean overflow;
    private java.lang.Boolean invalid;
    private java.lang.Boolean partialInterval;
    private java.lang.Boolean manuallyModified;
    private java.lang.Boolean longInterval;
    private java.lang.Boolean recorderStopped;
    private java.lang.Boolean manuallyEntered;
    private java.lang.Boolean clockForward;
    private java.lang.Boolean DSTinEffect;
    private java.lang.Boolean configChanged;
    private java.lang.Boolean testData;
    private org.apache.axis.types.UnsignedInt intervalIndex;  // attribute

    public Status() {
    }

    public Status(
           java.lang.Boolean clockBackward,
           java.lang.Boolean overflow,
           java.lang.Boolean invalid,
           java.lang.Boolean partialInterval,
           java.lang.Boolean manuallyModified,
           java.lang.Boolean longInterval,
           java.lang.Boolean recorderStopped,
           java.lang.Boolean manuallyEntered,
           java.lang.Boolean clockForward,
           java.lang.Boolean DSTinEffect,
           java.lang.Boolean configChanged,
           java.lang.Boolean testData,
           org.apache.axis.types.UnsignedInt intervalIndex) {
           this.clockBackward = clockBackward;
           this.overflow = overflow;
           this.invalid = invalid;
           this.partialInterval = partialInterval;
           this.manuallyModified = manuallyModified;
           this.longInterval = longInterval;
           this.recorderStopped = recorderStopped;
           this.manuallyEntered = manuallyEntered;
           this.clockForward = clockForward;
           this.DSTinEffect = DSTinEffect;
           this.configChanged = configChanged;
           this.testData = testData;
           this.intervalIndex = intervalIndex;
    }


    /**
     * Gets the clockBackward value for this Status.
     * 
     * @return clockBackward
     */
    public java.lang.Boolean getClockBackward() {
        return clockBackward;
    }


    /**
     * Sets the clockBackward value for this Status.
     * 
     * @param clockBackward
     */
    public void setClockBackward(java.lang.Boolean clockBackward) {
        this.clockBackward = clockBackward;
    }


    /**
     * Gets the overflow value for this Status.
     * 
     * @return overflow
     */
    public java.lang.Boolean getOverflow() {
        return overflow;
    }


    /**
     * Sets the overflow value for this Status.
     * 
     * @param overflow
     */
    public void setOverflow(java.lang.Boolean overflow) {
        this.overflow = overflow;
    }


    /**
     * Gets the invalid value for this Status.
     * 
     * @return invalid
     */
    public java.lang.Boolean getInvalid() {
        return invalid;
    }


    /**
     * Sets the invalid value for this Status.
     * 
     * @param invalid
     */
    public void setInvalid(java.lang.Boolean invalid) {
        this.invalid = invalid;
    }


    /**
     * Gets the partialInterval value for this Status.
     * 
     * @return partialInterval
     */
    public java.lang.Boolean getPartialInterval() {
        return partialInterval;
    }


    /**
     * Sets the partialInterval value for this Status.
     * 
     * @param partialInterval
     */
    public void setPartialInterval(java.lang.Boolean partialInterval) {
        this.partialInterval = partialInterval;
    }


    /**
     * Gets the manuallyModified value for this Status.
     * 
     * @return manuallyModified
     */
    public java.lang.Boolean getManuallyModified() {
        return manuallyModified;
    }


    /**
     * Sets the manuallyModified value for this Status.
     * 
     * @param manuallyModified
     */
    public void setManuallyModified(java.lang.Boolean manuallyModified) {
        this.manuallyModified = manuallyModified;
    }


    /**
     * Gets the longInterval value for this Status.
     * 
     * @return longInterval
     */
    public java.lang.Boolean getLongInterval() {
        return longInterval;
    }


    /**
     * Sets the longInterval value for this Status.
     * 
     * @param longInterval
     */
    public void setLongInterval(java.lang.Boolean longInterval) {
        this.longInterval = longInterval;
    }


    /**
     * Gets the recorderStopped value for this Status.
     * 
     * @return recorderStopped
     */
    public java.lang.Boolean getRecorderStopped() {
        return recorderStopped;
    }


    /**
     * Sets the recorderStopped value for this Status.
     * 
     * @param recorderStopped
     */
    public void setRecorderStopped(java.lang.Boolean recorderStopped) {
        this.recorderStopped = recorderStopped;
    }


    /**
     * Gets the manuallyEntered value for this Status.
     * 
     * @return manuallyEntered
     */
    public java.lang.Boolean getManuallyEntered() {
        return manuallyEntered;
    }


    /**
     * Sets the manuallyEntered value for this Status.
     * 
     * @param manuallyEntered
     */
    public void setManuallyEntered(java.lang.Boolean manuallyEntered) {
        this.manuallyEntered = manuallyEntered;
    }


    /**
     * Gets the clockForward value for this Status.
     * 
     * @return clockForward
     */
    public java.lang.Boolean getClockForward() {
        return clockForward;
    }


    /**
     * Sets the clockForward value for this Status.
     * 
     * @param clockForward
     */
    public void setClockForward(java.lang.Boolean clockForward) {
        this.clockForward = clockForward;
    }


    /**
     * Gets the DSTinEffect value for this Status.
     * 
     * @return DSTinEffect
     */
    public java.lang.Boolean getDSTinEffect() {
        return DSTinEffect;
    }


    /**
     * Sets the DSTinEffect value for this Status.
     * 
     * @param DSTinEffect
     */
    public void setDSTinEffect(java.lang.Boolean DSTinEffect) {
        this.DSTinEffect = DSTinEffect;
    }


    /**
     * Gets the configChanged value for this Status.
     * 
     * @return configChanged
     */
    public java.lang.Boolean getConfigChanged() {
        return configChanged;
    }


    /**
     * Sets the configChanged value for this Status.
     * 
     * @param configChanged
     */
    public void setConfigChanged(java.lang.Boolean configChanged) {
        this.configChanged = configChanged;
    }


    /**
     * Gets the testData value for this Status.
     * 
     * @return testData
     */
    public java.lang.Boolean getTestData() {
        return testData;
    }


    /**
     * Sets the testData value for this Status.
     * 
     * @param testData
     */
    public void setTestData(java.lang.Boolean testData) {
        this.testData = testData;
    }


    /**
     * Gets the intervalIndex value for this Status.
     * 
     * @return intervalIndex
     */
    public org.apache.axis.types.UnsignedInt getIntervalIndex() {
        return intervalIndex;
    }


    /**
     * Sets the intervalIndex value for this Status.
     * 
     * @param intervalIndex
     */
    public void setIntervalIndex(org.apache.axis.types.UnsignedInt intervalIndex) {
        this.intervalIndex = intervalIndex;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Status)) return false;
        Status other = (Status) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.clockBackward==null && other.getClockBackward()==null) || 
             (this.clockBackward!=null &&
              this.clockBackward.equals(other.getClockBackward()))) &&
            ((this.overflow==null && other.getOverflow()==null) || 
             (this.overflow!=null &&
              this.overflow.equals(other.getOverflow()))) &&
            ((this.invalid==null && other.getInvalid()==null) || 
             (this.invalid!=null &&
              this.invalid.equals(other.getInvalid()))) &&
            ((this.partialInterval==null && other.getPartialInterval()==null) || 
             (this.partialInterval!=null &&
              this.partialInterval.equals(other.getPartialInterval()))) &&
            ((this.manuallyModified==null && other.getManuallyModified()==null) || 
             (this.manuallyModified!=null &&
              this.manuallyModified.equals(other.getManuallyModified()))) &&
            ((this.longInterval==null && other.getLongInterval()==null) || 
             (this.longInterval!=null &&
              this.longInterval.equals(other.getLongInterval()))) &&
            ((this.recorderStopped==null && other.getRecorderStopped()==null) || 
             (this.recorderStopped!=null &&
              this.recorderStopped.equals(other.getRecorderStopped()))) &&
            ((this.manuallyEntered==null && other.getManuallyEntered()==null) || 
             (this.manuallyEntered!=null &&
              this.manuallyEntered.equals(other.getManuallyEntered()))) &&
            ((this.clockForward==null && other.getClockForward()==null) || 
             (this.clockForward!=null &&
              this.clockForward.equals(other.getClockForward()))) &&
            ((this.DSTinEffect==null && other.getDSTinEffect()==null) || 
             (this.DSTinEffect!=null &&
              this.DSTinEffect.equals(other.getDSTinEffect()))) &&
            ((this.configChanged==null && other.getConfigChanged()==null) || 
             (this.configChanged!=null &&
              this.configChanged.equals(other.getConfigChanged()))) &&
            ((this.testData==null && other.getTestData()==null) || 
             (this.testData!=null &&
              this.testData.equals(other.getTestData()))) &&
            ((this.intervalIndex==null && other.getIntervalIndex()==null) || 
             (this.intervalIndex!=null &&
              this.intervalIndex.equals(other.getIntervalIndex())));
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
        if (getClockBackward() != null) {
            _hashCode += getClockBackward().hashCode();
        }
        if (getOverflow() != null) {
            _hashCode += getOverflow().hashCode();
        }
        if (getInvalid() != null) {
            _hashCode += getInvalid().hashCode();
        }
        if (getPartialInterval() != null) {
            _hashCode += getPartialInterval().hashCode();
        }
        if (getManuallyModified() != null) {
            _hashCode += getManuallyModified().hashCode();
        }
        if (getLongInterval() != null) {
            _hashCode += getLongInterval().hashCode();
        }
        if (getRecorderStopped() != null) {
            _hashCode += getRecorderStopped().hashCode();
        }
        if (getManuallyEntered() != null) {
            _hashCode += getManuallyEntered().hashCode();
        }
        if (getClockForward() != null) {
            _hashCode += getClockForward().hashCode();
        }
        if (getDSTinEffect() != null) {
            _hashCode += getDSTinEffect().hashCode();
        }
        if (getConfigChanged() != null) {
            _hashCode += getConfigChanged().hashCode();
        }
        if (getTestData() != null) {
            _hashCode += getTestData().hashCode();
        }
        if (getIntervalIndex() != null) {
            _hashCode += getIntervalIndex().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Status.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("intervalIndex");
        attrField.setXmlName(new javax.xml.namespace.QName("", "intervalIndex"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clockBackward");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clockBackward"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("overflow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overflow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invalid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "invalid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partialInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "partialInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manuallyModified");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manuallyModified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "longInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recorderStopped");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recorderStopped"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manuallyEntered");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manuallyEntered"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clockForward");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clockForward"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DSTinEffect");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DSTinEffect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configChanged");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configChanged"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
