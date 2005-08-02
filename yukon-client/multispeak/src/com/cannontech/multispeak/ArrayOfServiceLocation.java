/**
 * ArrayOfServiceLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfServiceLocation  implements java.io.Serializable {
    private com.cannontech.multispeak.ServiceLocation[] serviceLocation;

    public ArrayOfServiceLocation() {
    }

    public ArrayOfServiceLocation(
           com.cannontech.multispeak.ServiceLocation[] serviceLocation) {
           this.serviceLocation = serviceLocation;
    }


    /**
     * Gets the serviceLocation value for this ArrayOfServiceLocation.
     * 
     * @return serviceLocation
     */
    public com.cannontech.multispeak.ServiceLocation[] getServiceLocation() {
        return serviceLocation;
    }


    /**
     * Sets the serviceLocation value for this ArrayOfServiceLocation.
     * 
     * @param serviceLocation
     */
    public void setServiceLocation(com.cannontech.multispeak.ServiceLocation[] serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public com.cannontech.multispeak.ServiceLocation getServiceLocation(int i) {
        return this.serviceLocation[i];
    }

    public void setServiceLocation(int i, com.cannontech.multispeak.ServiceLocation _value) {
        this.serviceLocation[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfServiceLocation)) return false;
        ArrayOfServiceLocation other = (ArrayOfServiceLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceLocation==null && other.getServiceLocation()==null) || 
             (this.serviceLocation!=null &&
              java.util.Arrays.equals(this.serviceLocation, other.getServiceLocation())));
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
        if (getServiceLocation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getServiceLocation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServiceLocation(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfServiceLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
