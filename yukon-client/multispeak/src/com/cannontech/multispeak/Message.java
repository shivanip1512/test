/**
 * Message.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Message  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String recordID;
    private java.util.Calendar eventTime;
    private java.lang.String wavPointer;
    private java.util.Calendar listenedOn;
    private java.lang.String listenedBy;

    public Message() {
    }

    public Message(
           java.lang.String recordID,
           java.util.Calendar eventTime,
           java.lang.String wavPointer,
           java.util.Calendar listenedOn,
           java.lang.String listenedBy) {
           this.recordID = recordID;
           this.eventTime = eventTime;
           this.wavPointer = wavPointer;
           this.listenedOn = listenedOn;
           this.listenedBy = listenedBy;
    }


    /**
     * Gets the recordID value for this Message.
     * 
     * @return recordID
     */
    public java.lang.String getRecordID() {
        return recordID;
    }


    /**
     * Sets the recordID value for this Message.
     * 
     * @param recordID
     */
    public void setRecordID(java.lang.String recordID) {
        this.recordID = recordID;
    }


    /**
     * Gets the eventTime value for this Message.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this Message.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the wavPointer value for this Message.
     * 
     * @return wavPointer
     */
    public java.lang.String getWavPointer() {
        return wavPointer;
    }


    /**
     * Sets the wavPointer value for this Message.
     * 
     * @param wavPointer
     */
    public void setWavPointer(java.lang.String wavPointer) {
        this.wavPointer = wavPointer;
    }


    /**
     * Gets the listenedOn value for this Message.
     * 
     * @return listenedOn
     */
    public java.util.Calendar getListenedOn() {
        return listenedOn;
    }


    /**
     * Sets the listenedOn value for this Message.
     * 
     * @param listenedOn
     */
    public void setListenedOn(java.util.Calendar listenedOn) {
        this.listenedOn = listenedOn;
    }


    /**
     * Gets the listenedBy value for this Message.
     * 
     * @return listenedBy
     */
    public java.lang.String getListenedBy() {
        return listenedBy;
    }


    /**
     * Sets the listenedBy value for this Message.
     * 
     * @param listenedBy
     */
    public void setListenedBy(java.lang.String listenedBy) {
        this.listenedBy = listenedBy;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Message)) return false;
        Message other = (Message) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.recordID==null && other.getRecordID()==null) || 
             (this.recordID!=null &&
              this.recordID.equals(other.getRecordID()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.wavPointer==null && other.getWavPointer()==null) || 
             (this.wavPointer!=null &&
              this.wavPointer.equals(other.getWavPointer()))) &&
            ((this.listenedOn==null && other.getListenedOn()==null) || 
             (this.listenedOn!=null &&
              this.listenedOn.equals(other.getListenedOn()))) &&
            ((this.listenedBy==null && other.getListenedBy()==null) || 
             (this.listenedBy!=null &&
              this.listenedBy.equals(other.getListenedBy())));
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
        if (getRecordID() != null) {
            _hashCode += getRecordID().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getWavPointer() != null) {
            _hashCode += getWavPointer().hashCode();
        }
        if (getListenedOn() != null) {
            _hashCode += getListenedOn().hashCode();
        }
        if (getListenedBy() != null) {
            _hashCode += getListenedBy().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Message.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recordID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wavPointer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wavPointer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listenedOn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "listenedOn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listenedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "listenedBy"));
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
