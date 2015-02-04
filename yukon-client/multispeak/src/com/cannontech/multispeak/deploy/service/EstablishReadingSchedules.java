/**
 * EstablishReadingSchedules.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EstablishReadingSchedules  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ReadingSchedule[] readingSchedules;

    public EstablishReadingSchedules() {
    }

    public EstablishReadingSchedules(
           com.cannontech.multispeak.deploy.service.ReadingSchedule[] readingSchedules) {
           this.readingSchedules = readingSchedules;
    }


    /**
     * Gets the readingSchedules value for this EstablishReadingSchedules.
     * 
     * @return readingSchedules
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule[] getReadingSchedules() {
        return readingSchedules;
    }


    /**
     * Sets the readingSchedules value for this EstablishReadingSchedules.
     * 
     * @param readingSchedules
     */
    public void setReadingSchedules(com.cannontech.multispeak.deploy.service.ReadingSchedule[] readingSchedules) {
        this.readingSchedules = readingSchedules;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EstablishReadingSchedules)) return false;
        EstablishReadingSchedules other = (EstablishReadingSchedules) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.readingSchedules==null && other.getReadingSchedules()==null) || 
             (this.readingSchedules!=null &&
              java.util.Arrays.equals(this.readingSchedules, other.getReadingSchedules())));
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
        if (getReadingSchedules() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReadingSchedules());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReadingSchedules(), i);
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
        new org.apache.axis.description.TypeDesc(EstablishReadingSchedules.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">EstablishReadingSchedules"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingSchedules");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedules"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
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
