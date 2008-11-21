/**
 * AVLLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AVLLocation  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Vehicle vehicle;

    private java.lang.String AVLID;

    private com.cannontech.multispeak.deploy.service.AVLEvent[] AVLEvent;

    public AVLLocation() {
    }

    public AVLLocation(
           com.cannontech.multispeak.deploy.service.Vehicle vehicle,
           java.lang.String AVLID,
           com.cannontech.multispeak.deploy.service.AVLEvent[] AVLEvent) {
           this.vehicle = vehicle;
           this.AVLID = AVLID;
           this.AVLEvent = AVLEvent;
    }


    /**
     * Gets the vehicle value for this AVLLocation.
     * 
     * @return vehicle
     */
    public com.cannontech.multispeak.deploy.service.Vehicle getVehicle() {
        return vehicle;
    }


    /**
     * Sets the vehicle value for this AVLLocation.
     * 
     * @param vehicle
     */
    public void setVehicle(com.cannontech.multispeak.deploy.service.Vehicle vehicle) {
        this.vehicle = vehicle;
    }


    /**
     * Gets the AVLID value for this AVLLocation.
     * 
     * @return AVLID
     */
    public java.lang.String getAVLID() {
        return AVLID;
    }


    /**
     * Sets the AVLID value for this AVLLocation.
     * 
     * @param AVLID
     */
    public void setAVLID(java.lang.String AVLID) {
        this.AVLID = AVLID;
    }


    /**
     * Gets the AVLEvent value for this AVLLocation.
     * 
     * @return AVLEvent
     */
    public com.cannontech.multispeak.deploy.service.AVLEvent[] getAVLEvent() {
        return AVLEvent;
    }


    /**
     * Sets the AVLEvent value for this AVLLocation.
     * 
     * @param AVLEvent
     */
    public void setAVLEvent(com.cannontech.multispeak.deploy.service.AVLEvent[] AVLEvent) {
        this.AVLEvent = AVLEvent;
    }

    public com.cannontech.multispeak.deploy.service.AVLEvent getAVLEvent(int i) {
        return this.AVLEvent[i];
    }

    public void setAVLEvent(int i, com.cannontech.multispeak.deploy.service.AVLEvent _value) {
        this.AVLEvent[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AVLLocation)) return false;
        AVLLocation other = (AVLLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vehicle==null && other.getVehicle()==null) || 
             (this.vehicle!=null &&
              this.vehicle.equals(other.getVehicle()))) &&
            ((this.AVLID==null && other.getAVLID()==null) || 
             (this.AVLID!=null &&
              this.AVLID.equals(other.getAVLID()))) &&
            ((this.AVLEvent==null && other.getAVLEvent()==null) || 
             (this.AVLEvent!=null &&
              java.util.Arrays.equals(this.AVLEvent, other.getAVLEvent())));
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
        if (getVehicle() != null) {
            _hashCode += getVehicle().hashCode();
        }
        if (getAVLID() != null) {
            _hashCode += getAVLID().hashCode();
        }
        if (getAVLEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAVLEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAVLEvent(), i);
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
        new org.apache.axis.description.TypeDesc(AVLLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vehicle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vehicle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vehicle"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AVLID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AVLEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLEvent"));
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
