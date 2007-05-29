/**
 * InnerBoundaryIs.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class InnerBoundaryIs  implements java.io.Serializable {
    private com.cannontech.multispeak.service.LinearRingType linearRing;

    public InnerBoundaryIs() {
    }

    public InnerBoundaryIs(
           com.cannontech.multispeak.service.LinearRingType linearRing) {
           this.linearRing = linearRing;
    }


    /**
     * Gets the linearRing value for this InnerBoundaryIs.
     * 
     * @return linearRing
     */
    public com.cannontech.multispeak.service.LinearRingType getLinearRing() {
        return linearRing;
    }


    /**
     * Sets the linearRing value for this InnerBoundaryIs.
     * 
     * @param linearRing
     */
    public void setLinearRing(com.cannontech.multispeak.service.LinearRingType linearRing) {
        this.linearRing = linearRing;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InnerBoundaryIs)) return false;
        InnerBoundaryIs other = (InnerBoundaryIs) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.linearRing==null && other.getLinearRing()==null) || 
             (this.linearRing!=null &&
              this.linearRing.equals(other.getLinearRing())));
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
        if (getLinearRing() != null) {
            _hashCode += getLinearRing().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InnerBoundaryIs.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "innerBoundaryIs"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linearRing");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LinearRing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "LinearRingType"));
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
