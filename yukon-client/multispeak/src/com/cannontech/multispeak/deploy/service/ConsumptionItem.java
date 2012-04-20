/**
 * ConsumptionItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ConsumptionItem  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ReadingValue readingValue;

    private com.cannontech.multispeak.deploy.service.TimePeriod periodOfUse;

    public ConsumptionItem() {
    }

    public ConsumptionItem(
           com.cannontech.multispeak.deploy.service.ReadingValue readingValue,
           com.cannontech.multispeak.deploy.service.TimePeriod periodOfUse) {
           this.readingValue = readingValue;
           this.periodOfUse = periodOfUse;
    }


    /**
     * Gets the readingValue value for this ConsumptionItem.
     * 
     * @return readingValue
     */
    public com.cannontech.multispeak.deploy.service.ReadingValue getReadingValue() {
        return readingValue;
    }


    /**
     * Sets the readingValue value for this ConsumptionItem.
     * 
     * @param readingValue
     */
    public void setReadingValue(com.cannontech.multispeak.deploy.service.ReadingValue readingValue) {
        this.readingValue = readingValue;
    }


    /**
     * Gets the periodOfUse value for this ConsumptionItem.
     * 
     * @return periodOfUse
     */
    public com.cannontech.multispeak.deploy.service.TimePeriod getPeriodOfUse() {
        return periodOfUse;
    }


    /**
     * Sets the periodOfUse value for this ConsumptionItem.
     * 
     * @param periodOfUse
     */
    public void setPeriodOfUse(com.cannontech.multispeak.deploy.service.TimePeriod periodOfUse) {
        this.periodOfUse = periodOfUse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConsumptionItem)) return false;
        ConsumptionItem other = (ConsumptionItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.readingValue==null && other.getReadingValue()==null) || 
             (this.readingValue!=null &&
              this.readingValue.equals(other.getReadingValue()))) &&
            ((this.periodOfUse==null && other.getPeriodOfUse()==null) || 
             (this.periodOfUse!=null &&
              this.periodOfUse.equals(other.getPeriodOfUse())));
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
        if (getReadingValue() != null) {
            _hashCode += getReadingValue().hashCode();
        }
        if (getPeriodOfUse() != null) {
            _hashCode += getPeriodOfUse().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConsumptionItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "consumptionItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValue"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("periodOfUse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "periodOfUse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timePeriod"));
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
