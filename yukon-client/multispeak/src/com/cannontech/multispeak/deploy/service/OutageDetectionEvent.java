/**
 * OutageDetectionEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OutageDetectionEvent  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCd;

    private java.util.Calendar eventTime;

    private com.cannontech.multispeak.deploy.service.OutageEventType outageEventType;

    private java.lang.String outageDetectDeviceID;

    private com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType;

    private com.cannontech.multispeak.deploy.service.OutageLocation outageLocation;

    private com.cannontech.multispeak.deploy.service.Message[] messageList;

    private com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer;

    private com.cannontech.multispeak.deploy.service.PriorityType priority;

    private java.math.BigInteger percentConfident;

    private java.lang.String problemCode;

    private com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel;

    public OutageDetectionEvent() {
    }

    public OutageDetectionEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCd,
           java.util.Calendar eventTime,
           com.cannontech.multispeak.deploy.service.OutageEventType outageEventType,
           java.lang.String outageDetectDeviceID,
           com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType,
           com.cannontech.multispeak.deploy.service.OutageLocation outageLocation,
           com.cannontech.multispeak.deploy.service.Message[] messageList,
           com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer,
           com.cannontech.multispeak.deploy.service.PriorityType priority,
           java.math.BigInteger percentConfident,
           java.lang.String problemCode,
           com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.phaseCd = phaseCd;
        this.eventTime = eventTime;
        this.outageEventType = outageEventType;
        this.outageDetectDeviceID = outageDetectDeviceID;
        this.outageDetectDeviceType = outageDetectDeviceType;
        this.outageLocation = outageLocation;
        this.messageList = messageList;
        this.outageCustomer = outageCustomer;
        this.priority = priority;
        this.percentConfident = percentConfident;
        this.problemCode = problemCode;
        this.resolvedLevel = resolvedLevel;
    }


    /**
     * Gets the phaseCd value for this OutageDetectionEvent.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this OutageDetectionEvent.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.deploy.service.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the eventTime value for this OutageDetectionEvent.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this OutageDetectionEvent.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the outageEventType value for this OutageDetectionEvent.
     * 
     * @return outageEventType
     */
    public com.cannontech.multispeak.deploy.service.OutageEventType getOutageEventType() {
        return outageEventType;
    }


    /**
     * Sets the outageEventType value for this OutageDetectionEvent.
     * 
     * @param outageEventType
     */
    public void setOutageEventType(com.cannontech.multispeak.deploy.service.OutageEventType outageEventType) {
        this.outageEventType = outageEventType;
    }


    /**
     * Gets the outageDetectDeviceID value for this OutageDetectionEvent.
     * 
     * @return outageDetectDeviceID
     */
    public java.lang.String getOutageDetectDeviceID() {
        return outageDetectDeviceID;
    }


    /**
     * Sets the outageDetectDeviceID value for this OutageDetectionEvent.
     * 
     * @param outageDetectDeviceID
     */
    public void setOutageDetectDeviceID(java.lang.String outageDetectDeviceID) {
        this.outageDetectDeviceID = outageDetectDeviceID;
    }


    /**
     * Gets the outageDetectDeviceType value for this OutageDetectionEvent.
     * 
     * @return outageDetectDeviceType
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectDeviceType getOutageDetectDeviceType() {
        return outageDetectDeviceType;
    }


    /**
     * Sets the outageDetectDeviceType value for this OutageDetectionEvent.
     * 
     * @param outageDetectDeviceType
     */
    public void setOutageDetectDeviceType(com.cannontech.multispeak.deploy.service.OutageDetectDeviceType outageDetectDeviceType) {
        this.outageDetectDeviceType = outageDetectDeviceType;
    }


    /**
     * Gets the outageLocation value for this OutageDetectionEvent.
     * 
     * @return outageLocation
     */
    public com.cannontech.multispeak.deploy.service.OutageLocation getOutageLocation() {
        return outageLocation;
    }


    /**
     * Sets the outageLocation value for this OutageDetectionEvent.
     * 
     * @param outageLocation
     */
    public void setOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation outageLocation) {
        this.outageLocation = outageLocation;
    }


    /**
     * Gets the messageList value for this OutageDetectionEvent.
     * 
     * @return messageList
     */
    public com.cannontech.multispeak.deploy.service.Message[] getMessageList() {
        return messageList;
    }


    /**
     * Sets the messageList value for this OutageDetectionEvent.
     * 
     * @param messageList
     */
    public void setMessageList(com.cannontech.multispeak.deploy.service.Message[] messageList) {
        this.messageList = messageList;
    }


    /**
     * Gets the outageCustomer value for this OutageDetectionEvent.
     * 
     * @return outageCustomer
     */
    public com.cannontech.multispeak.deploy.service.OutageCustomer getOutageCustomer() {
        return outageCustomer;
    }


    /**
     * Sets the outageCustomer value for this OutageDetectionEvent.
     * 
     * @param outageCustomer
     */
    public void setOutageCustomer(com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer) {
        this.outageCustomer = outageCustomer;
    }


    /**
     * Gets the priority value for this OutageDetectionEvent.
     * 
     * @return priority
     */
    public com.cannontech.multispeak.deploy.service.PriorityType getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this OutageDetectionEvent.
     * 
     * @param priority
     */
    public void setPriority(com.cannontech.multispeak.deploy.service.PriorityType priority) {
        this.priority = priority;
    }


    /**
     * Gets the percentConfident value for this OutageDetectionEvent.
     * 
     * @return percentConfident
     */
    public java.math.BigInteger getPercentConfident() {
        return percentConfident;
    }


    /**
     * Sets the percentConfident value for this OutageDetectionEvent.
     * 
     * @param percentConfident
     */
    public void setPercentConfident(java.math.BigInteger percentConfident) {
        this.percentConfident = percentConfident;
    }


    /**
     * Gets the problemCode value for this OutageDetectionEvent.
     * 
     * @return problemCode
     */
    public java.lang.String getProblemCode() {
        return problemCode;
    }


    /**
     * Sets the problemCode value for this OutageDetectionEvent.
     * 
     * @param problemCode
     */
    public void setProblemCode(java.lang.String problemCode) {
        this.problemCode = problemCode;
    }


    /**
     * Gets the resolvedLevel value for this OutageDetectionEvent.
     * 
     * @return resolvedLevel
     */
    public com.cannontech.multispeak.deploy.service.ResolvedLevel getResolvedLevel() {
        return resolvedLevel;
    }


    /**
     * Sets the resolvedLevel value for this OutageDetectionEvent.
     * 
     * @param resolvedLevel
     */
    public void setResolvedLevel(com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel) {
        this.resolvedLevel = resolvedLevel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageDetectionEvent)) return false;
        OutageDetectionEvent other = (OutageDetectionEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.outageEventType==null && other.getOutageEventType()==null) || 
             (this.outageEventType!=null &&
              this.outageEventType.equals(other.getOutageEventType()))) &&
            ((this.outageDetectDeviceID==null && other.getOutageDetectDeviceID()==null) || 
             (this.outageDetectDeviceID!=null &&
              this.outageDetectDeviceID.equals(other.getOutageDetectDeviceID()))) &&
            ((this.outageDetectDeviceType==null && other.getOutageDetectDeviceType()==null) || 
             (this.outageDetectDeviceType!=null &&
              this.outageDetectDeviceType.equals(other.getOutageDetectDeviceType()))) &&
            ((this.outageLocation==null && other.getOutageLocation()==null) || 
             (this.outageLocation!=null &&
              this.outageLocation.equals(other.getOutageLocation()))) &&
            ((this.messageList==null && other.getMessageList()==null) || 
             (this.messageList!=null &&
              java.util.Arrays.equals(this.messageList, other.getMessageList()))) &&
            ((this.outageCustomer==null && other.getOutageCustomer()==null) || 
             (this.outageCustomer!=null &&
              this.outageCustomer.equals(other.getOutageCustomer()))) &&
            ((this.priority==null && other.getPriority()==null) || 
             (this.priority!=null &&
              this.priority.equals(other.getPriority()))) &&
            ((this.percentConfident==null && other.getPercentConfident()==null) || 
             (this.percentConfident!=null &&
              this.percentConfident.equals(other.getPercentConfident()))) &&
            ((this.problemCode==null && other.getProblemCode()==null) || 
             (this.problemCode!=null &&
              this.problemCode.equals(other.getProblemCode()))) &&
            ((this.resolvedLevel==null && other.getResolvedLevel()==null) || 
             (this.resolvedLevel!=null &&
              this.resolvedLevel.equals(other.getResolvedLevel())));
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
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getOutageEventType() != null) {
            _hashCode += getOutageEventType().hashCode();
        }
        if (getOutageDetectDeviceID() != null) {
            _hashCode += getOutageDetectDeviceID().hashCode();
        }
        if (getOutageDetectDeviceType() != null) {
            _hashCode += getOutageDetectDeviceType().hashCode();
        }
        if (getOutageLocation() != null) {
            _hashCode += getOutageLocation().hashCode();
        }
        if (getMessageList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMessageList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMessageList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOutageCustomer() != null) {
            _hashCode += getOutageCustomer().hashCode();
        }
        if (getPriority() != null) {
            _hashCode += getPriority().hashCode();
        }
        if (getPercentConfident() != null) {
            _hashCode += getPercentConfident().hashCode();
        }
        if (getProblemCode() != null) {
            _hashCode += getProblemCode().hashCode();
        }
        if (getResolvedLevel() != null) {
            _hashCode += getResolvedLevel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageDetectionEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
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
        elemField.setFieldName("outageEventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventType"));
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
        elemField.setFieldName("outageLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "messageList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "message"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageCustomer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("percentConfident");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "percentConfident"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
        elemField.setFieldName("resolvedLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel"));
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
