/**
 * CircuitElementStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CircuitElementStatus  implements java.io.Serializable {
    private java.lang.String outageEventID;

    private com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase;

    private com.cannontech.multispeak.deploy.service.CircuitElementState circuitElementState;

    public CircuitElementStatus() {
    }

    public CircuitElementStatus(
           java.lang.String outageEventID,
           com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase,
           com.cannontech.multispeak.deploy.service.CircuitElementState circuitElementState) {
           this.outageEventID = outageEventID;
           this.outagedPhase = outagedPhase;
           this.circuitElementState = circuitElementState;
    }


    /**
     * Gets the outageEventID value for this CircuitElementStatus.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this CircuitElementStatus.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }


    /**
     * Gets the outagedPhase value for this CircuitElementStatus.
     * 
     * @return outagedPhase
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getOutagedPhase() {
        return outagedPhase;
    }


    /**
     * Sets the outagedPhase value for this CircuitElementStatus.
     * 
     * @param outagedPhase
     */
    public void setOutagedPhase(com.cannontech.multispeak.deploy.service.PhaseCd outagedPhase) {
        this.outagedPhase = outagedPhase;
    }


    /**
     * Gets the circuitElementState value for this CircuitElementStatus.
     * 
     * @return circuitElementState
     */
    public com.cannontech.multispeak.deploy.service.CircuitElementState getCircuitElementState() {
        return circuitElementState;
    }


    /**
     * Sets the circuitElementState value for this CircuitElementStatus.
     * 
     * @param circuitElementState
     */
    public void setCircuitElementState(com.cannontech.multispeak.deploy.service.CircuitElementState circuitElementState) {
        this.circuitElementState = circuitElementState;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CircuitElementStatus)) return false;
        CircuitElementStatus other = (CircuitElementStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.outageEventID==null && other.getOutageEventID()==null) || 
             (this.outageEventID!=null &&
              this.outageEventID.equals(other.getOutageEventID()))) &&
            ((this.outagedPhase==null && other.getOutagedPhase()==null) || 
             (this.outagedPhase!=null &&
              this.outagedPhase.equals(other.getOutagedPhase()))) &&
            ((this.circuitElementState==null && other.getCircuitElementState()==null) || 
             (this.circuitElementState!=null &&
              this.circuitElementState.equals(other.getCircuitElementState())));
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
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        if (getOutagedPhase() != null) {
            _hashCode += getOutagedPhase().hashCode();
        }
        if (getCircuitElementState() != null) {
            _hashCode += getCircuitElementState().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CircuitElementStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outagedPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outagedPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitElementState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "circuitElementState"));
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
