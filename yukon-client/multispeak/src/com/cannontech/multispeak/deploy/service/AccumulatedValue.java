/**
 * AccumulatedValue.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AccumulatedValue  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.math.BigInteger countedValue;

    private java.util.Calendar timeStamp;

    private java.math.BigInteger ceilingValue;

    private java.lang.Boolean latchesAtMaximum;

    private java.lang.String measurementTypeID;

    public AccumulatedValue() {
    }

    public AccumulatedValue(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.math.BigInteger countedValue,
           java.util.Calendar timeStamp,
           java.math.BigInteger ceilingValue,
           java.lang.Boolean latchesAtMaximum,
           java.lang.String measurementTypeID) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.countedValue = countedValue;
        this.timeStamp = timeStamp;
        this.ceilingValue = ceilingValue;
        this.latchesAtMaximum = latchesAtMaximum;
        this.measurementTypeID = measurementTypeID;
    }


    /**
     * Gets the countedValue value for this AccumulatedValue.
     * 
     * @return countedValue
     */
    public java.math.BigInteger getCountedValue() {
        return countedValue;
    }


    /**
     * Sets the countedValue value for this AccumulatedValue.
     * 
     * @param countedValue
     */
    public void setCountedValue(java.math.BigInteger countedValue) {
        this.countedValue = countedValue;
    }


    /**
     * Gets the timeStamp value for this AccumulatedValue.
     * 
     * @return timeStamp
     */
    public java.util.Calendar getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this AccumulatedValue.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(java.util.Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * Gets the ceilingValue value for this AccumulatedValue.
     * 
     * @return ceilingValue
     */
    public java.math.BigInteger getCeilingValue() {
        return ceilingValue;
    }


    /**
     * Sets the ceilingValue value for this AccumulatedValue.
     * 
     * @param ceilingValue
     */
    public void setCeilingValue(java.math.BigInteger ceilingValue) {
        this.ceilingValue = ceilingValue;
    }


    /**
     * Gets the latchesAtMaximum value for this AccumulatedValue.
     * 
     * @return latchesAtMaximum
     */
    public java.lang.Boolean getLatchesAtMaximum() {
        return latchesAtMaximum;
    }


    /**
     * Sets the latchesAtMaximum value for this AccumulatedValue.
     * 
     * @param latchesAtMaximum
     */
    public void setLatchesAtMaximum(java.lang.Boolean latchesAtMaximum) {
        this.latchesAtMaximum = latchesAtMaximum;
    }


    /**
     * Gets the measurementTypeID value for this AccumulatedValue.
     * 
     * @return measurementTypeID
     */
    public java.lang.String getMeasurementTypeID() {
        return measurementTypeID;
    }


    /**
     * Sets the measurementTypeID value for this AccumulatedValue.
     * 
     * @param measurementTypeID
     */
    public void setMeasurementTypeID(java.lang.String measurementTypeID) {
        this.measurementTypeID = measurementTypeID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccumulatedValue)) return false;
        AccumulatedValue other = (AccumulatedValue) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.countedValue==null && other.getCountedValue()==null) || 
             (this.countedValue!=null &&
              this.countedValue.equals(other.getCountedValue()))) &&
            ((this.timeStamp==null && other.getTimeStamp()==null) || 
             (this.timeStamp!=null &&
              this.timeStamp.equals(other.getTimeStamp()))) &&
            ((this.ceilingValue==null && other.getCeilingValue()==null) || 
             (this.ceilingValue!=null &&
              this.ceilingValue.equals(other.getCeilingValue()))) &&
            ((this.latchesAtMaximum==null && other.getLatchesAtMaximum()==null) || 
             (this.latchesAtMaximum!=null &&
              this.latchesAtMaximum.equals(other.getLatchesAtMaximum()))) &&
            ((this.measurementTypeID==null && other.getMeasurementTypeID()==null) || 
             (this.measurementTypeID!=null &&
              this.measurementTypeID.equals(other.getMeasurementTypeID())));
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
        if (getCountedValue() != null) {
            _hashCode += getCountedValue().hashCode();
        }
        if (getTimeStamp() != null) {
            _hashCode += getTimeStamp().hashCode();
        }
        if (getCeilingValue() != null) {
            _hashCode += getCeilingValue().hashCode();
        }
        if (getLatchesAtMaximum() != null) {
            _hashCode += getLatchesAtMaximum().hashCode();
        }
        if (getMeasurementTypeID() != null) {
            _hashCode += getMeasurementTypeID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccumulatedValue.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accumulatedValue"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countedValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "countedValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ceilingValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ceilingValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latchesAtMaximum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "latchesAtMaximum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementTypeID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementTypeID"));
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
