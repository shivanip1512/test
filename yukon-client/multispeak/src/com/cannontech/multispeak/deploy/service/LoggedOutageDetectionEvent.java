/**
 * LoggedOutageDetectionEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LoggedOutageDetectionEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String outageDetectionRecordID;

    private java.util.Calendar eventTime;

    private java.lang.String outageDetectDeviceID;

    private com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType;

    private java.lang.String problemCode;

    private com.cannontech.multispeak.deploy.service.OutageEventType outageEventType;

    private java.lang.String takenBy;

    private com.cannontech.multispeak.deploy.service.CallType callType;

    private java.lang.String description;

    public LoggedOutageDetectionEvent() {
    }

    public LoggedOutageDetectionEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String outageDetectionRecordID,
           java.util.Calendar eventTime,
           java.lang.String outageDetectDeviceID,
           com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType,
           java.lang.String problemCode,
           com.cannontech.multispeak.deploy.service.OutageEventType outageEventType,
           java.lang.String takenBy,
           com.cannontech.multispeak.deploy.service.CallType callType,
           java.lang.String description) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.outageDetectionRecordID = outageDetectionRecordID;
        this.eventTime = eventTime;
        this.outageDetectDeviceID = outageDetectDeviceID;
        this.outageDetectDeviceType = outageDetectDeviceType;
        this.problemCode = problemCode;
        this.outageEventType = outageEventType;
        this.takenBy = takenBy;
        this.callType = callType;
        this.description = description;
    }


    /**
     * Gets the outageDetectionRecordID value for this LoggedOutageDetectionEvent.
     * 
     * @return outageDetectionRecordID
     */
    public java.lang.String getOutageDetectionRecordID() {
        return outageDetectionRecordID;
    }


    /**
     * Sets the outageDetectionRecordID value for this LoggedOutageDetectionEvent.
     * 
     * @param outageDetectionRecordID
     */
    public void setOutageDetectionRecordID(java.lang.String outageDetectionRecordID) {
        this.outageDetectionRecordID = outageDetectionRecordID;
    }


    /**
     * Gets the eventTime value for this LoggedOutageDetectionEvent.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this LoggedOutageDetectionEvent.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the outageDetectDeviceID value for this LoggedOutageDetectionEvent.
     * 
     * @return outageDetectDeviceID
     */
    public java.lang.String getOutageDetectDeviceID() {
        return outageDetectDeviceID;
    }


    /**
     * Sets the outageDetectDeviceID value for this LoggedOutageDetectionEvent.
     * 
     * @param outageDetectDeviceID
     */
    public void setOutageDetectDeviceID(java.lang.String outageDetectDeviceID) {
        this.outageDetectDeviceID = outageDetectDeviceID;
    }


    /**
     * Gets the outageDetectDeviceType value for this LoggedOutageDetectionEvent.
     * 
     * @return outageDetectDeviceType
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectDeviceType getOutageDetectDeviceType() {
        return outageDetectDeviceType;
    }


    /**
     * Sets the outageDetectDeviceType value for this LoggedOutageDetectionEvent.
     * 
     * @param outageDetectDeviceType
     */
    public void setOutageDetectDeviceType(com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType) {
        this.outageDetectDeviceType = outageDetectDeviceType;
    }


    /**
     * Gets the problemCode value for this LoggedOutageDetectionEvent.
     * 
     * @return problemCode
     */
    public java.lang.String getProblemCode() {
        return problemCode;
    }


    /**
     * Sets the problemCode value for this LoggedOutageDetectionEvent.
     * 
     * @param problemCode
     */
    public void setProblemCode(java.lang.String problemCode) {
        this.problemCode = problemCode;
    }


    /**
     * Gets the outageEventType value for this LoggedOutageDetectionEvent.
     * 
     * @return outageEventType
     */
    public com.cannontech.multispeak.deploy.service.OutageEventType getOutageEventType() {
        return outageEventType;
    }


    /**
     * Sets the outageEventType value for this LoggedOutageDetectionEvent.
     * 
     * @param outageEventType
     */
    public void setOutageEventType(com.cannontech.multispeak.deploy.service.OutageEventType outageEventType) {
        this.outageEventType = outageEventType;
    }


    /**
     * Gets the takenBy value for this LoggedOutageDetectionEvent.
     * 
     * @return takenBy
     */
    public java.lang.String getTakenBy() {
        return takenBy;
    }


    /**
     * Sets the takenBy value for this LoggedOutageDetectionEvent.
     * 
     * @param takenBy
     */
    public void setTakenBy(java.lang.String takenBy) {
        this.takenBy = takenBy;
    }


    /**
     * Gets the callType value for this LoggedOutageDetectionEvent.
     * 
     * @return callType
     */
    public com.cannontech.multispeak.deploy.service.CallType getCallType() {
        return callType;
    }


    /**
     * Sets the callType value for this LoggedOutageDetectionEvent.
     * 
     * @param callType
     */
    public void setCallType(com.cannontech.multispeak.deploy.service.CallType callType) {
        this.callType = callType;
    }


    /**
     * Gets the description value for this LoggedOutageDetectionEvent.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this LoggedOutageDetectionEvent.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoggedOutageDetectionEvent)) return false;
        LoggedOutageDetectionEvent other = (LoggedOutageDetectionEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageDetectionRecordID==null && other.getOutageDetectionRecordID()==null) || 
             (this.outageDetectionRecordID!=null &&
              this.outageDetectionRecordID.equals(other.getOutageDetectionRecordID()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.outageDetectDeviceID==null && other.getOutageDetectDeviceID()==null) || 
             (this.outageDetectDeviceID!=null &&
              this.outageDetectDeviceID.equals(other.getOutageDetectDeviceID()))) &&
            ((this.outageDetectDeviceType==null && other.getOutageDetectDeviceType()==null) || 
             (this.outageDetectDeviceType!=null &&
              this.outageDetectDeviceType.equals(other.getOutageDetectDeviceType()))) &&
            ((this.problemCode==null && other.getProblemCode()==null) || 
             (this.problemCode!=null &&
              this.problemCode.equals(other.getProblemCode()))) &&
            ((this.outageEventType==null && other.getOutageEventType()==null) || 
             (this.outageEventType!=null &&
              this.outageEventType.equals(other.getOutageEventType()))) &&
            ((this.takenBy==null && other.getTakenBy()==null) || 
             (this.takenBy!=null &&
              this.takenBy.equals(other.getTakenBy()))) &&
            ((this.callType==null && other.getCallType()==null) || 
             (this.callType!=null &&
              this.callType.equals(other.getCallType()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription())));
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
        if (getOutageDetectionRecordID() != null) {
            _hashCode += getOutageDetectionRecordID().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getOutageDetectDeviceID() != null) {
            _hashCode += getOutageDetectDeviceID().hashCode();
        }
        if (getOutageDetectDeviceType() != null) {
            _hashCode += getOutageDetectDeviceType().hashCode();
        }
        if (getProblemCode() != null) {
            _hashCode += getProblemCode().hashCode();
        }
        if (getOutageEventType() != null) {
            _hashCode += getOutageEventType().hashCode();
        }
        if (getTakenBy() != null) {
            _hashCode += getTakenBy().hashCode();
        }
        if (getCallType() != null) {
            _hashCode += getCallType().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoggedOutageDetectionEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectionRecordID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionRecordID"));
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
        elemField.setFieldName("outageDetectDeviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectDeviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectDeviceType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("problemCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "problemCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("takenBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "takenBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
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
