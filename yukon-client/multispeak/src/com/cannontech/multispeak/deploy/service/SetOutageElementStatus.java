/**
 * SetOutageElementStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SetOutageElementStatus  implements java.io.Serializable {
    private java.lang.String troubledElement;

    private com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA;

    private com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB;

    private com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC;

    private java.util.Calendar eventTime;

    private java.lang.String dispatcherResponsible;

    public SetOutageElementStatus() {
    }

    public SetOutageElementStatus(
           java.lang.String troubledElement,
           com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA,
           com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB,
           com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC,
           java.util.Calendar eventTime,
           java.lang.String dispatcherResponsible) {
           this.troubledElement = troubledElement;
           this.statusPhaseA = statusPhaseA;
           this.statusPhaseB = statusPhaseB;
           this.statusPhaseC = statusPhaseC;
           this.eventTime = eventTime;
           this.dispatcherResponsible = dispatcherResponsible;
    }


    /**
     * Gets the troubledElement value for this SetOutageElementStatus.
     * 
     * @return troubledElement
     */
    public java.lang.String getTroubledElement() {
        return troubledElement;
    }


    /**
     * Sets the troubledElement value for this SetOutageElementStatus.
     * 
     * @param troubledElement
     */
    public void setTroubledElement(java.lang.String troubledElement) {
        this.troubledElement = troubledElement;
    }


    /**
     * Gets the statusPhaseA value for this SetOutageElementStatus.
     * 
     * @return statusPhaseA
     */
    public com.cannontech.multispeak.deploy.service.OutageElementStatus getStatusPhaseA() {
        return statusPhaseA;
    }


    /**
     * Sets the statusPhaseA value for this SetOutageElementStatus.
     * 
     * @param statusPhaseA
     */
    public void setStatusPhaseA(com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA) {
        this.statusPhaseA = statusPhaseA;
    }


    /**
     * Gets the statusPhaseB value for this SetOutageElementStatus.
     * 
     * @return statusPhaseB
     */
    public com.cannontech.multispeak.deploy.service.OutageElementStatus getStatusPhaseB() {
        return statusPhaseB;
    }


    /**
     * Sets the statusPhaseB value for this SetOutageElementStatus.
     * 
     * @param statusPhaseB
     */
    public void setStatusPhaseB(com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB) {
        this.statusPhaseB = statusPhaseB;
    }


    /**
     * Gets the statusPhaseC value for this SetOutageElementStatus.
     * 
     * @return statusPhaseC
     */
    public com.cannontech.multispeak.deploy.service.OutageElementStatus getStatusPhaseC() {
        return statusPhaseC;
    }


    /**
     * Sets the statusPhaseC value for this SetOutageElementStatus.
     * 
     * @param statusPhaseC
     */
    public void setStatusPhaseC(com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC) {
        this.statusPhaseC = statusPhaseC;
    }


    /**
     * Gets the eventTime value for this SetOutageElementStatus.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this SetOutageElementStatus.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the dispatcherResponsible value for this SetOutageElementStatus.
     * 
     * @return dispatcherResponsible
     */
    public java.lang.String getDispatcherResponsible() {
        return dispatcherResponsible;
    }


    /**
     * Sets the dispatcherResponsible value for this SetOutageElementStatus.
     * 
     * @param dispatcherResponsible
     */
    public void setDispatcherResponsible(java.lang.String dispatcherResponsible) {
        this.dispatcherResponsible = dispatcherResponsible;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SetOutageElementStatus)) return false;
        SetOutageElementStatus other = (SetOutageElementStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.troubledElement==null && other.getTroubledElement()==null) || 
             (this.troubledElement!=null &&
              this.troubledElement.equals(other.getTroubledElement()))) &&
            ((this.statusPhaseA==null && other.getStatusPhaseA()==null) || 
             (this.statusPhaseA!=null &&
              this.statusPhaseA.equals(other.getStatusPhaseA()))) &&
            ((this.statusPhaseB==null && other.getStatusPhaseB()==null) || 
             (this.statusPhaseB!=null &&
              this.statusPhaseB.equals(other.getStatusPhaseB()))) &&
            ((this.statusPhaseC==null && other.getStatusPhaseC()==null) || 
             (this.statusPhaseC!=null &&
              this.statusPhaseC.equals(other.getStatusPhaseC()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.dispatcherResponsible==null && other.getDispatcherResponsible()==null) || 
             (this.dispatcherResponsible!=null &&
              this.dispatcherResponsible.equals(other.getDispatcherResponsible())));
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
        if (getTroubledElement() != null) {
            _hashCode += getTroubledElement().hashCode();
        }
        if (getStatusPhaseA() != null) {
            _hashCode += getStatusPhaseA().hashCode();
        }
        if (getStatusPhaseB() != null) {
            _hashCode += getStatusPhaseB().hashCode();
        }
        if (getStatusPhaseC() != null) {
            _hashCode += getStatusPhaseC().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getDispatcherResponsible() != null) {
            _hashCode += getDispatcherResponsible().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SetOutageElementStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SetOutageElementStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("troubledElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "troubledElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusPhaseA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusPhaseA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageElementStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusPhaseB");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusPhaseB"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageElementStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusPhaseC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusPhaseC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageElementStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dispatcherResponsible");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dispatcherResponsible"));
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
