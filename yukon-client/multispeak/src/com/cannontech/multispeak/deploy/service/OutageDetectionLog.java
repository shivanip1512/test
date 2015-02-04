/**
 * OutageDetectionLog.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OutageDetectionLog  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.PhaseCd phaseCd;

    private com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent[] loggedODEventList;

    private com.cannontech.multispeak.deploy.service.OutageLocation outageLocation;

    private com.cannontech.multispeak.deploy.service.Message[] messageList;

    private com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer;

    private com.cannontech.multispeak.deploy.service.PriorityType priority;

    private com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel;

    private java.util.Calendar timeTroubleBegan;

    private java.util.Calendar timeOfInitialEvent;

    private java.util.Calendar timeOfLastEvent;

    private java.lang.Long eventsLogged;

    public OutageDetectionLog() {
    }

    public OutageDetectionLog(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCd,
           com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent[] loggedODEventList,
           com.cannontech.multispeak.deploy.service.OutageLocation outageLocation,
           com.cannontech.multispeak.deploy.service.Message[] messageList,
           com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer,
           com.cannontech.multispeak.deploy.service.PriorityType priority,
           com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel,
           java.util.Calendar timeTroubleBegan,
           java.util.Calendar timeOfInitialEvent,
           java.util.Calendar timeOfLastEvent,
           java.lang.Long eventsLogged) {
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
        this.loggedODEventList = loggedODEventList;
        this.outageLocation = outageLocation;
        this.messageList = messageList;
        this.outageCustomer = outageCustomer;
        this.priority = priority;
        this.resolvedLevel = resolvedLevel;
        this.timeTroubleBegan = timeTroubleBegan;
        this.timeOfInitialEvent = timeOfInitialEvent;
        this.timeOfLastEvent = timeOfLastEvent;
        this.eventsLogged = eventsLogged;
    }


    /**
     * Gets the phaseCd value for this OutageDetectionLog.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.deploy.service.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this OutageDetectionLog.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.deploy.service.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the loggedODEventList value for this OutageDetectionLog.
     * 
     * @return loggedODEventList
     */
    public com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent[] getLoggedODEventList() {
        return loggedODEventList;
    }


    /**
     * Sets the loggedODEventList value for this OutageDetectionLog.
     * 
     * @param loggedODEventList
     */
    public void setLoggedODEventList(com.cannontech.multispeak.deploy.service.LoggedOutageDetectionEvent[] loggedODEventList) {
        this.loggedODEventList = loggedODEventList;
    }


    /**
     * Gets the outageLocation value for this OutageDetectionLog.
     * 
     * @return outageLocation
     */
    public com.cannontech.multispeak.deploy.service.OutageLocation getOutageLocation() {
        return outageLocation;
    }


    /**
     * Sets the outageLocation value for this OutageDetectionLog.
     * 
     * @param outageLocation
     */
    public void setOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation outageLocation) {
        this.outageLocation = outageLocation;
    }


    /**
     * Gets the messageList value for this OutageDetectionLog.
     * 
     * @return messageList
     */
    public com.cannontech.multispeak.deploy.service.Message[] getMessageList() {
        return messageList;
    }


    /**
     * Sets the messageList value for this OutageDetectionLog.
     * 
     * @param messageList
     */
    public void setMessageList(com.cannontech.multispeak.deploy.service.Message[] messageList) {
        this.messageList = messageList;
    }


    /**
     * Gets the outageCustomer value for this OutageDetectionLog.
     * 
     * @return outageCustomer
     */
    public com.cannontech.multispeak.deploy.service.OutageCustomer getOutageCustomer() {
        return outageCustomer;
    }


    /**
     * Sets the outageCustomer value for this OutageDetectionLog.
     * 
     * @param outageCustomer
     */
    public void setOutageCustomer(com.cannontech.multispeak.deploy.service.OutageCustomer outageCustomer) {
        this.outageCustomer = outageCustomer;
    }


    /**
     * Gets the priority value for this OutageDetectionLog.
     * 
     * @return priority
     */
    public com.cannontech.multispeak.deploy.service.PriorityType getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this OutageDetectionLog.
     * 
     * @param priority
     */
    public void setPriority(com.cannontech.multispeak.deploy.service.PriorityType priority) {
        this.priority = priority;
    }


    /**
     * Gets the resolvedLevel value for this OutageDetectionLog.
     * 
     * @return resolvedLevel
     */
    public com.cannontech.multispeak.deploy.service.ResolvedLevel getResolvedLevel() {
        return resolvedLevel;
    }


    /**
     * Sets the resolvedLevel value for this OutageDetectionLog.
     * 
     * @param resolvedLevel
     */
    public void setResolvedLevel(com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel) {
        this.resolvedLevel = resolvedLevel;
    }


    /**
     * Gets the timeTroubleBegan value for this OutageDetectionLog.
     * 
     * @return timeTroubleBegan
     */
    public java.util.Calendar getTimeTroubleBegan() {
        return timeTroubleBegan;
    }


    /**
     * Sets the timeTroubleBegan value for this OutageDetectionLog.
     * 
     * @param timeTroubleBegan
     */
    public void setTimeTroubleBegan(java.util.Calendar timeTroubleBegan) {
        this.timeTroubleBegan = timeTroubleBegan;
    }


    /**
     * Gets the timeOfInitialEvent value for this OutageDetectionLog.
     * 
     * @return timeOfInitialEvent
     */
    public java.util.Calendar getTimeOfInitialEvent() {
        return timeOfInitialEvent;
    }


    /**
     * Sets the timeOfInitialEvent value for this OutageDetectionLog.
     * 
     * @param timeOfInitialEvent
     */
    public void setTimeOfInitialEvent(java.util.Calendar timeOfInitialEvent) {
        this.timeOfInitialEvent = timeOfInitialEvent;
    }


    /**
     * Gets the timeOfLastEvent value for this OutageDetectionLog.
     * 
     * @return timeOfLastEvent
     */
    public java.util.Calendar getTimeOfLastEvent() {
        return timeOfLastEvent;
    }


    /**
     * Sets the timeOfLastEvent value for this OutageDetectionLog.
     * 
     * @param timeOfLastEvent
     */
    public void setTimeOfLastEvent(java.util.Calendar timeOfLastEvent) {
        this.timeOfLastEvent = timeOfLastEvent;
    }


    /**
     * Gets the eventsLogged value for this OutageDetectionLog.
     * 
     * @return eventsLogged
     */
    public java.lang.Long getEventsLogged() {
        return eventsLogged;
    }


    /**
     * Sets the eventsLogged value for this OutageDetectionLog.
     * 
     * @param eventsLogged
     */
    public void setEventsLogged(java.lang.Long eventsLogged) {
        this.eventsLogged = eventsLogged;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageDetectionLog)) return false;
        OutageDetectionLog other = (OutageDetectionLog) obj;
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
            ((this.loggedODEventList==null && other.getLoggedODEventList()==null) || 
             (this.loggedODEventList!=null &&
              java.util.Arrays.equals(this.loggedODEventList, other.getLoggedODEventList()))) &&
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
            ((this.resolvedLevel==null && other.getResolvedLevel()==null) || 
             (this.resolvedLevel!=null &&
              this.resolvedLevel.equals(other.getResolvedLevel()))) &&
            ((this.timeTroubleBegan==null && other.getTimeTroubleBegan()==null) || 
             (this.timeTroubleBegan!=null &&
              this.timeTroubleBegan.equals(other.getTimeTroubleBegan()))) &&
            ((this.timeOfInitialEvent==null && other.getTimeOfInitialEvent()==null) || 
             (this.timeOfInitialEvent!=null &&
              this.timeOfInitialEvent.equals(other.getTimeOfInitialEvent()))) &&
            ((this.timeOfLastEvent==null && other.getTimeOfLastEvent()==null) || 
             (this.timeOfLastEvent!=null &&
              this.timeOfLastEvent.equals(other.getTimeOfLastEvent()))) &&
            ((this.eventsLogged==null && other.getEventsLogged()==null) || 
             (this.eventsLogged!=null &&
              this.eventsLogged.equals(other.getEventsLogged())));
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
        if (getLoggedODEventList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLoggedODEventList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLoggedODEventList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        if (getResolvedLevel() != null) {
            _hashCode += getResolvedLevel().hashCode();
        }
        if (getTimeTroubleBegan() != null) {
            _hashCode += getTimeTroubleBegan().hashCode();
        }
        if (getTimeOfInitialEvent() != null) {
            _hashCode += getTimeOfInitialEvent().hashCode();
        }
        if (getTimeOfLastEvent() != null) {
            _hashCode += getTimeOfLastEvent().hashCode();
        }
        if (getEventsLogged() != null) {
            _hashCode += getEventsLogged().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageDetectionLog.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionLog"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loggedODEventList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedODEventList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loggedOutageDetectionEvent"));
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
        elemField.setFieldName("resolvedLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resolvedLevel"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeTroubleBegan");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeTroubleBegan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfInitialEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeOfInitialEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfLastEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeOfLastEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventsLogged");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventsLogged"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
