/**
 * MeterReadTOUReadings.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterReadTOUReadings  implements java.io.Serializable {
    private com.cannontech.multispeak.TOUReading[] TOUReading;

    public MeterReadTOUReadings() {
    }

    public MeterReadTOUReadings(
           com.cannontech.multispeak.TOUReading[] TOUReading) {
           this.TOUReading = TOUReading;
    }


    /**
     * Gets the TOUReading value for this MeterReadTOUReadings.
     * 
     * @return TOUReading
     */
    public com.cannontech.multispeak.TOUReading[] getTOUReading() {
        return TOUReading;
    }


    /**
     * Sets the TOUReading value for this MeterReadTOUReadings.
     * 
     * @param TOUReading
     */
    public void setTOUReading(com.cannontech.multispeak.TOUReading[] TOUReading) {
        this.TOUReading = TOUReading;
    }

    public com.cannontech.multispeak.TOUReading getTOUReading(int i) {
        return this.TOUReading[i];
    }

    public void setTOUReading(int i, com.cannontech.multispeak.TOUReading _value) {
        this.TOUReading[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterReadTOUReadings)) return false;
        MeterReadTOUReadings other = (MeterReadTOUReadings) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.TOUReading==null && other.getTOUReading()==null) || 
             (this.TOUReading!=null &&
              java.util.Arrays.equals(this.TOUReading, other.getTOUReading())));
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
        if (getTOUReading() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTOUReading());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTOUReading(), i);
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
        new org.apache.axis.description.TypeDesc(MeterReadTOUReadings.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReadTOUReadings"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("TOUReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReading"));
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
