/**
 * Schedule.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Schedule  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.Float maximumRuntime;

    private com.cannontech.multispeak.deploy.service.TimePeriod effectiveWindow;

    private java.lang.Boolean isEnabled;

    private java.lang.Float offset;

    private com.cannontech.multispeak.deploy.service.TimePoint[] absoluteTimeSchedule;

    private com.cannontech.multispeak.deploy.service.PeriodicSchedule periodicSchedule;

    private java.lang.String purpose;

    public Schedule() {
    }

    public Schedule(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.Float maximumRuntime,
           com.cannontech.multispeak.deploy.service.TimePeriod effectiveWindow,
           java.lang.Boolean isEnabled,
           java.lang.Float offset,
           com.cannontech.multispeak.deploy.service.TimePoint[] absoluteTimeSchedule,
           com.cannontech.multispeak.deploy.service.PeriodicSchedule periodicSchedule,
           java.lang.String purpose) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.maximumRuntime = maximumRuntime;
        this.effectiveWindow = effectiveWindow;
        this.isEnabled = isEnabled;
        this.offset = offset;
        this.absoluteTimeSchedule = absoluteTimeSchedule;
        this.periodicSchedule = periodicSchedule;
        this.purpose = purpose;
    }


    /**
     * Gets the maximumRuntime value for this Schedule.
     * 
     * @return maximumRuntime
     */
    public java.lang.Float getMaximumRuntime() {
        return maximumRuntime;
    }


    /**
     * Sets the maximumRuntime value for this Schedule.
     * 
     * @param maximumRuntime
     */
    public void setMaximumRuntime(java.lang.Float maximumRuntime) {
        this.maximumRuntime = maximumRuntime;
    }


    /**
     * Gets the effectiveWindow value for this Schedule.
     * 
     * @return effectiveWindow
     */
    public com.cannontech.multispeak.deploy.service.TimePeriod getEffectiveWindow() {
        return effectiveWindow;
    }


    /**
     * Sets the effectiveWindow value for this Schedule.
     * 
     * @param effectiveWindow
     */
    public void setEffectiveWindow(com.cannontech.multispeak.deploy.service.TimePeriod effectiveWindow) {
        this.effectiveWindow = effectiveWindow;
    }


    /**
     * Gets the isEnabled value for this Schedule.
     * 
     * @return isEnabled
     */
    public java.lang.Boolean getIsEnabled() {
        return isEnabled;
    }


    /**
     * Sets the isEnabled value for this Schedule.
     * 
     * @param isEnabled
     */
    public void setIsEnabled(java.lang.Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }


    /**
     * Gets the offset value for this Schedule.
     * 
     * @return offset
     */
    public java.lang.Float getOffset() {
        return offset;
    }


    /**
     * Sets the offset value for this Schedule.
     * 
     * @param offset
     */
    public void setOffset(java.lang.Float offset) {
        this.offset = offset;
    }


    /**
     * Gets the absoluteTimeSchedule value for this Schedule.
     * 
     * @return absoluteTimeSchedule
     */
    public com.cannontech.multispeak.deploy.service.TimePoint[] getAbsoluteTimeSchedule() {
        return absoluteTimeSchedule;
    }


    /**
     * Sets the absoluteTimeSchedule value for this Schedule.
     * 
     * @param absoluteTimeSchedule
     */
    public void setAbsoluteTimeSchedule(com.cannontech.multispeak.deploy.service.TimePoint[] absoluteTimeSchedule) {
        this.absoluteTimeSchedule = absoluteTimeSchedule;
    }


    /**
     * Gets the periodicSchedule value for this Schedule.
     * 
     * @return periodicSchedule
     */
    public com.cannontech.multispeak.deploy.service.PeriodicSchedule getPeriodicSchedule() {
        return periodicSchedule;
    }


    /**
     * Sets the periodicSchedule value for this Schedule.
     * 
     * @param periodicSchedule
     */
    public void setPeriodicSchedule(com.cannontech.multispeak.deploy.service.PeriodicSchedule periodicSchedule) {
        this.periodicSchedule = periodicSchedule;
    }


    /**
     * Gets the purpose value for this Schedule.
     * 
     * @return purpose
     */
    public java.lang.String getPurpose() {
        return purpose;
    }


    /**
     * Sets the purpose value for this Schedule.
     * 
     * @param purpose
     */
    public void setPurpose(java.lang.String purpose) {
        this.purpose = purpose;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Schedule)) return false;
        Schedule other = (Schedule) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.maximumRuntime==null && other.getMaximumRuntime()==null) || 
             (this.maximumRuntime!=null &&
              this.maximumRuntime.equals(other.getMaximumRuntime()))) &&
            ((this.effectiveWindow==null && other.getEffectiveWindow()==null) || 
             (this.effectiveWindow!=null &&
              this.effectiveWindow.equals(other.getEffectiveWindow()))) &&
            ((this.isEnabled==null && other.getIsEnabled()==null) || 
             (this.isEnabled!=null &&
              this.isEnabled.equals(other.getIsEnabled()))) &&
            ((this.offset==null && other.getOffset()==null) || 
             (this.offset!=null &&
              this.offset.equals(other.getOffset()))) &&
            ((this.absoluteTimeSchedule==null && other.getAbsoluteTimeSchedule()==null) || 
             (this.absoluteTimeSchedule!=null &&
              java.util.Arrays.equals(this.absoluteTimeSchedule, other.getAbsoluteTimeSchedule()))) &&
            ((this.periodicSchedule==null && other.getPeriodicSchedule()==null) || 
             (this.periodicSchedule!=null &&
              this.periodicSchedule.equals(other.getPeriodicSchedule()))) &&
            ((this.purpose==null && other.getPurpose()==null) || 
             (this.purpose!=null &&
              this.purpose.equals(other.getPurpose())));
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
        if (getMaximumRuntime() != null) {
            _hashCode += getMaximumRuntime().hashCode();
        }
        if (getEffectiveWindow() != null) {
            _hashCode += getEffectiveWindow().hashCode();
        }
        if (getIsEnabled() != null) {
            _hashCode += getIsEnabled().hashCode();
        }
        if (getOffset() != null) {
            _hashCode += getOffset().hashCode();
        }
        if (getAbsoluteTimeSchedule() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAbsoluteTimeSchedule());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAbsoluteTimeSchedule(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPeriodicSchedule() != null) {
            _hashCode += getPeriodicSchedule().hashCode();
        }
        if (getPurpose() != null) {
            _hashCode += getPurpose().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Schedule.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "schedule"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumRuntime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maximumRuntime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("effectiveWindow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "effectiveWindow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isEnabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offset");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "offset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("absoluteTimeSchedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "absoluteTimeSchedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePoint"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePoint"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("periodicSchedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "periodicSchedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "periodicSchedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purpose");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "purpose"));
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
