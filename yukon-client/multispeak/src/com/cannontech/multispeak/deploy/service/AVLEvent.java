/**
 * AVLEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AVLEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.util.Calendar GMTTime;

    private java.lang.String eventType;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    private com.cannontech.multispeak.deploy.service.AVLAddress AVLAddress;

    private com.cannontech.multispeak.deploy.service.Telemetry telemetry;

    public AVLEvent() {
    }

    public AVLEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.util.Calendar GMTTime,
           java.lang.String eventType,
           com.cannontech.multispeak.deploy.service.GPS GPS,
           com.cannontech.multispeak.deploy.service.AVLAddress AVLAddress,
           com.cannontech.multispeak.deploy.service.Telemetry telemetry) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.GMTTime = GMTTime;
        this.eventType = eventType;
        this.GPS = GPS;
        this.AVLAddress = AVLAddress;
        this.telemetry = telemetry;
    }


    /**
     * Gets the GMTTime value for this AVLEvent.
     * 
     * @return GMTTime
     */
    public java.util.Calendar getGMTTime() {
        return GMTTime;
    }


    /**
     * Sets the GMTTime value for this AVLEvent.
     * 
     * @param GMTTime
     */
    public void setGMTTime(java.util.Calendar GMTTime) {
        this.GMTTime = GMTTime;
    }


    /**
     * Gets the eventType value for this AVLEvent.
     * 
     * @return eventType
     */
    public java.lang.String getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this AVLEvent.
     * 
     * @param eventType
     */
    public void setEventType(java.lang.String eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the GPS value for this AVLEvent.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this AVLEvent.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }


    /**
     * Gets the AVLAddress value for this AVLEvent.
     * 
     * @return AVLAddress
     */
    public com.cannontech.multispeak.deploy.service.AVLAddress getAVLAddress() {
        return AVLAddress;
    }


    /**
     * Sets the AVLAddress value for this AVLEvent.
     * 
     * @param AVLAddress
     */
    public void setAVLAddress(com.cannontech.multispeak.deploy.service.AVLAddress AVLAddress) {
        this.AVLAddress = AVLAddress;
    }


    /**
     * Gets the telemetry value for this AVLEvent.
     * 
     * @return telemetry
     */
    public com.cannontech.multispeak.deploy.service.Telemetry getTelemetry() {
        return telemetry;
    }


    /**
     * Sets the telemetry value for this AVLEvent.
     * 
     * @param telemetry
     */
    public void setTelemetry(com.cannontech.multispeak.deploy.service.Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AVLEvent)) return false;
        AVLEvent other = (AVLEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.GMTTime==null && other.getGMTTime()==null) || 
             (this.GMTTime!=null &&
              this.GMTTime.equals(other.getGMTTime()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            ((this.GPS==null && other.getGPS()==null) || 
             (this.GPS!=null &&
              this.GPS.equals(other.getGPS()))) &&
            ((this.AVLAddress==null && other.getAVLAddress()==null) || 
             (this.AVLAddress!=null &&
              this.AVLAddress.equals(other.getAVLAddress()))) &&
            ((this.telemetry==null && other.getTelemetry()==null) || 
             (this.telemetry!=null &&
              this.telemetry.equals(other.getTelemetry())));
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
        if (getGMTTime() != null) {
            _hashCode += getGMTTime().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        if (getAVLAddress() != null) {
            _hashCode += getAVLAddress().hashCode();
        }
        if (getTelemetry() != null) {
            _hashCode += getTelemetry().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AVLEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GMTTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GMTTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GPS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("AVLAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "AVLAddress"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telemetry");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "telemetry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "telemetry"));
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
