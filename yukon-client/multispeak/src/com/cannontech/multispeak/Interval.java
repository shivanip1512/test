/**
 * Interval.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Interval  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private double _value;
    private boolean DSTinEffect;  // attribute
    private boolean clockForward;  // attribute
    private boolean clockBackward;  // attribute
    private boolean overflow;  // attribute
    private boolean partialInterval;  // attribute
    private boolean longInterval;  // attribute
    private boolean testData;  // attribute
    private boolean invalid;  // attribute
    private boolean manuallyEntered;  // attribute
    private boolean manuallyModified;  // attribute
    private boolean recorderStopped;  // attribute
    private boolean configChanged;  // attribute

    public Interval() {
    }

    // Simple Types must have a String constructor
    public Interval(double _value) {
        this._value = _value;
    }
    public Interval(java.lang.String _value) {
        this._value = new Double(_value).doubleValue();
    }

    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return new Double(_value).toString();
    }


    /**
     * Gets the _value value for this Interval.
     * 
     * @return _value
     */
    public double get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this Interval.
     * 
     * @param _value
     */
    public void set_value(double _value) {
        this._value = _value;
    }


    /**
     * Gets the DSTinEffect value for this Interval.
     * 
     * @return DSTinEffect
     */
    public boolean isDSTinEffect() {
        return DSTinEffect;
    }


    /**
     * Sets the DSTinEffect value for this Interval.
     * 
     * @param DSTinEffect
     */
    public void setDSTinEffect(boolean DSTinEffect) {
        this.DSTinEffect = DSTinEffect;
    }


    /**
     * Gets the clockForward value for this Interval.
     * 
     * @return clockForward
     */
    public boolean isClockForward() {
        return clockForward;
    }


    /**
     * Sets the clockForward value for this Interval.
     * 
     * @param clockForward
     */
    public void setClockForward(boolean clockForward) {
        this.clockForward = clockForward;
    }


    /**
     * Gets the clockBackward value for this Interval.
     * 
     * @return clockBackward
     */
    public boolean isClockBackward() {
        return clockBackward;
    }


    /**
     * Sets the clockBackward value for this Interval.
     * 
     * @param clockBackward
     */
    public void setClockBackward(boolean clockBackward) {
        this.clockBackward = clockBackward;
    }


    /**
     * Gets the overflow value for this Interval.
     * 
     * @return overflow
     */
    public boolean isOverflow() {
        return overflow;
    }


    /**
     * Sets the overflow value for this Interval.
     * 
     * @param overflow
     */
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }


    /**
     * Gets the partialInterval value for this Interval.
     * 
     * @return partialInterval
     */
    public boolean isPartialInterval() {
        return partialInterval;
    }


    /**
     * Sets the partialInterval value for this Interval.
     * 
     * @param partialInterval
     */
    public void setPartialInterval(boolean partialInterval) {
        this.partialInterval = partialInterval;
    }


    /**
     * Gets the longInterval value for this Interval.
     * 
     * @return longInterval
     */
    public boolean isLongInterval() {
        return longInterval;
    }


    /**
     * Sets the longInterval value for this Interval.
     * 
     * @param longInterval
     */
    public void setLongInterval(boolean longInterval) {
        this.longInterval = longInterval;
    }


    /**
     * Gets the testData value for this Interval.
     * 
     * @return testData
     */
    public boolean isTestData() {
        return testData;
    }


    /**
     * Sets the testData value for this Interval.
     * 
     * @param testData
     */
    public void setTestData(boolean testData) {
        this.testData = testData;
    }


    /**
     * Gets the invalid value for this Interval.
     * 
     * @return invalid
     */
    public boolean isInvalid() {
        return invalid;
    }


    /**
     * Sets the invalid value for this Interval.
     * 
     * @param invalid
     */
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }


    /**
     * Gets the manuallyEntered value for this Interval.
     * 
     * @return manuallyEntered
     */
    public boolean isManuallyEntered() {
        return manuallyEntered;
    }


    /**
     * Sets the manuallyEntered value for this Interval.
     * 
     * @param manuallyEntered
     */
    public void setManuallyEntered(boolean manuallyEntered) {
        this.manuallyEntered = manuallyEntered;
    }


    /**
     * Gets the manuallyModified value for this Interval.
     * 
     * @return manuallyModified
     */
    public boolean isManuallyModified() {
        return manuallyModified;
    }


    /**
     * Sets the manuallyModified value for this Interval.
     * 
     * @param manuallyModified
     */
    public void setManuallyModified(boolean manuallyModified) {
        this.manuallyModified = manuallyModified;
    }


    /**
     * Gets the recorderStopped value for this Interval.
     * 
     * @return recorderStopped
     */
    public boolean isRecorderStopped() {
        return recorderStopped;
    }


    /**
     * Sets the recorderStopped value for this Interval.
     * 
     * @param recorderStopped
     */
    public void setRecorderStopped(boolean recorderStopped) {
        this.recorderStopped = recorderStopped;
    }


    /**
     * Gets the configChanged value for this Interval.
     * 
     * @return configChanged
     */
    public boolean isConfigChanged() {
        return configChanged;
    }


    /**
     * Sets the configChanged value for this Interval.
     * 
     * @param configChanged
     */
    public void setConfigChanged(boolean configChanged) {
        this.configChanged = configChanged;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Interval)) return false;
        Interval other = (Interval) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this._value == other.get_value() &&
            this.DSTinEffect == other.isDSTinEffect() &&
            this.clockForward == other.isClockForward() &&
            this.clockBackward == other.isClockBackward() &&
            this.overflow == other.isOverflow() &&
            this.partialInterval == other.isPartialInterval() &&
            this.longInterval == other.isLongInterval() &&
            this.testData == other.isTestData() &&
            this.invalid == other.isInvalid() &&
            this.manuallyEntered == other.isManuallyEntered() &&
            this.manuallyModified == other.isManuallyModified() &&
            this.recorderStopped == other.isRecorderStopped() &&
            this.configChanged == other.isConfigChanged();
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
        _hashCode += new Double(get_value()).hashCode();
        _hashCode += (isDSTinEffect() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isClockForward() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isClockBackward() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOverflow() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isPartialInterval() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isLongInterval() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isTestData() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isInvalid() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isManuallyEntered() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isManuallyModified() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isRecorderStopped() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isConfigChanged() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Interval.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interval"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("DSTinEffect");
        attrField.setXmlName(new javax.xml.namespace.QName("", "DSTinEffect"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("clockForward");
        attrField.setXmlName(new javax.xml.namespace.QName("", "clockForward"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("clockBackward");
        attrField.setXmlName(new javax.xml.namespace.QName("", "clockBackward"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("overflow");
        attrField.setXmlName(new javax.xml.namespace.QName("", "overflow"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("partialInterval");
        attrField.setXmlName(new javax.xml.namespace.QName("", "partialInterval"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("longInterval");
        attrField.setXmlName(new javax.xml.namespace.QName("", "longInterval"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("testData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "testData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("invalid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "invalid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("manuallyEntered");
        attrField.setXmlName(new javax.xml.namespace.QName("", "manuallyEntered"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("manuallyModified");
        attrField.setXmlName(new javax.xml.namespace.QName("", "manuallyModified"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("recorderStopped");
        attrField.setXmlName(new javax.xml.namespace.QName("", "recorderStopped"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("configChanged");
        attrField.setXmlName(new javax.xml.namespace.QName("", "configChanged"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
