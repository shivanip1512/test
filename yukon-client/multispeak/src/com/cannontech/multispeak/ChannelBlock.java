/**
 * ChannelBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ChannelBlock  implements java.io.Serializable {
    private com.cannontech.multispeak.Status status;
    private com.cannontech.multispeak.Interval[] interval;
    private java.util.Calendar endTime;  // attribute
    private org.apache.axis.types.UnsignedInt intervalPeriod;  // attribute
    private double endReading;  // attribute
    private org.apache.axis.types.UnsignedInt endPulse;  // attribute
    private org.apache.axis.types.UnsignedInt sequenceNumber;  // attribute

    public ChannelBlock() {
    }

    public ChannelBlock(
           com.cannontech.multispeak.Status status,
           com.cannontech.multispeak.Interval[] interval,
           java.util.Calendar endTime,
           org.apache.axis.types.UnsignedInt intervalPeriod,
           double endReading,
           org.apache.axis.types.UnsignedInt endPulse,
           org.apache.axis.types.UnsignedInt sequenceNumber) {
           this.status = status;
           this.interval = interval;
           this.endTime = endTime;
           this.intervalPeriod = intervalPeriod;
           this.endReading = endReading;
           this.endPulse = endPulse;
           this.sequenceNumber = sequenceNumber;
    }


    /**
     * Gets the status value for this ChannelBlock.
     * 
     * @return status
     */
    public com.cannontech.multispeak.Status getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ChannelBlock.
     * 
     * @param status
     */
    public void setStatus(com.cannontech.multispeak.Status status) {
        this.status = status;
    }


    /**
     * Gets the interval value for this ChannelBlock.
     * 
     * @return interval
     */
    public com.cannontech.multispeak.Interval[] getInterval() {
        return interval;
    }


    /**
     * Sets the interval value for this ChannelBlock.
     * 
     * @param interval
     */
    public void setInterval(com.cannontech.multispeak.Interval[] interval) {
        this.interval = interval;
    }

    public com.cannontech.multispeak.Interval getInterval(int i) {
        return this.interval[i];
    }

    public void setInterval(int i, com.cannontech.multispeak.Interval _value) {
        this.interval[i] = _value;
    }


    /**
     * Gets the endTime value for this ChannelBlock.
     * 
     * @return endTime
     */
    public java.util.Calendar getEndTime() {
        return endTime;
    }


    /**
     * Sets the endTime value for this ChannelBlock.
     * 
     * @param endTime
     */
    public void setEndTime(java.util.Calendar endTime) {
        this.endTime = endTime;
    }


    /**
     * Gets the intervalPeriod value for this ChannelBlock.
     * 
     * @return intervalPeriod
     */
    public org.apache.axis.types.UnsignedInt getIntervalPeriod() {
        return intervalPeriod;
    }


    /**
     * Sets the intervalPeriod value for this ChannelBlock.
     * 
     * @param intervalPeriod
     */
    public void setIntervalPeriod(org.apache.axis.types.UnsignedInt intervalPeriod) {
        this.intervalPeriod = intervalPeriod;
    }


    /**
     * Gets the endReading value for this ChannelBlock.
     * 
     * @return endReading
     */
    public double getEndReading() {
        return endReading;
    }


    /**
     * Sets the endReading value for this ChannelBlock.
     * 
     * @param endReading
     */
    public void setEndReading(double endReading) {
        this.endReading = endReading;
    }


    /**
     * Gets the endPulse value for this ChannelBlock.
     * 
     * @return endPulse
     */
    public org.apache.axis.types.UnsignedInt getEndPulse() {
        return endPulse;
    }


    /**
     * Sets the endPulse value for this ChannelBlock.
     * 
     * @param endPulse
     */
    public void setEndPulse(org.apache.axis.types.UnsignedInt endPulse) {
        this.endPulse = endPulse;
    }


    /**
     * Gets the sequenceNumber value for this ChannelBlock.
     * 
     * @return sequenceNumber
     */
    public org.apache.axis.types.UnsignedInt getSequenceNumber() {
        return sequenceNumber;
    }


    /**
     * Sets the sequenceNumber value for this ChannelBlock.
     * 
     * @param sequenceNumber
     */
    public void setSequenceNumber(org.apache.axis.types.UnsignedInt sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChannelBlock)) return false;
        ChannelBlock other = (ChannelBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.interval==null && other.getInterval()==null) || 
             (this.interval!=null &&
              java.util.Arrays.equals(this.interval, other.getInterval()))) &&
            ((this.endTime==null && other.getEndTime()==null) || 
             (this.endTime!=null &&
              this.endTime.equals(other.getEndTime()))) &&
            ((this.intervalPeriod==null && other.getIntervalPeriod()==null) || 
             (this.intervalPeriod!=null &&
              this.intervalPeriod.equals(other.getIntervalPeriod()))) &&
            this.endReading == other.getEndReading() &&
            ((this.endPulse==null && other.getEndPulse()==null) || 
             (this.endPulse!=null &&
              this.endPulse.equals(other.getEndPulse()))) &&
            ((this.sequenceNumber==null && other.getSequenceNumber()==null) || 
             (this.sequenceNumber!=null &&
              this.sequenceNumber.equals(other.getSequenceNumber())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getInterval() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInterval());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInterval(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEndTime() != null) {
            _hashCode += getEndTime().hashCode();
        }
        if (getIntervalPeriod() != null) {
            _hashCode += getIntervalPeriod().hashCode();
        }
        _hashCode += new Double(getEndReading()).hashCode();
        if (getEndPulse() != null) {
            _hashCode += getEndPulse().hashCode();
        }
        if (getSequenceNumber() != null) {
            _hashCode += getSequenceNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChannelBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "channelBlock"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("endTime");
        attrField.setXmlName(new javax.xml.namespace.QName("", "endTime"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("intervalPeriod");
        attrField.setXmlName(new javax.xml.namespace.QName("", "intervalPeriod"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("endReading");
        attrField.setXmlName(new javax.xml.namespace.QName("", "endReading"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("endPulse");
        attrField.setXmlName(new javax.xml.namespace.QName("", "endPulse"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("sequenceNumber");
        attrField.setXmlName(new javax.xml.namespace.QName("", "sequenceNumber"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interval"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
