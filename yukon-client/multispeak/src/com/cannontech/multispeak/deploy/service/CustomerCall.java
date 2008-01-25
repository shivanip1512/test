/**
 * CustomerCall.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CustomerCall  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String custID;

    private java.util.Calendar eventTime;

    private java.lang.String description;

    private java.lang.String problemCode;

    private com.cannontech.multispeak.deploy.service.CallType callType;

    private java.lang.String takenBy;

    private com.cannontech.multispeak.deploy.service.PriorityType callPriority;

    private com.cannontech.multispeak.deploy.service.OutageLocation location;

    private com.cannontech.multispeak.deploy.service.Message[] messageList;

    private com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel;

    public CustomerCall() {
    }

    public CustomerCall(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String custID,
           java.util.Calendar eventTime,
           java.lang.String description,
           java.lang.String problemCode,
           com.cannontech.multispeak.deploy.service.CallType callType,
           java.lang.String takenBy,
           com.cannontech.multispeak.deploy.service.PriorityType callPriority,
           com.cannontech.multispeak.deploy.service.OutageLocation location,
           com.cannontech.multispeak.deploy.service.Message[] messageList,
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
        this.custID = custID;
        this.eventTime = eventTime;
        this.description = description;
        this.problemCode = problemCode;
        this.callType = callType;
        this.takenBy = takenBy;
        this.callPriority = callPriority;
        this.location = location;
        this.messageList = messageList;
        this.resolvedLevel = resolvedLevel;
    }


    /**
     * Gets the custID value for this CustomerCall.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this CustomerCall.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the eventTime value for this CustomerCall.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this CustomerCall.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the description value for this CustomerCall.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this CustomerCall.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the problemCode value for this CustomerCall.
     * 
     * @return problemCode
     */
    public java.lang.String getProblemCode() {
        return problemCode;
    }


    /**
     * Sets the problemCode value for this CustomerCall.
     * 
     * @param problemCode
     */
    public void setProblemCode(java.lang.String problemCode) {
        this.problemCode = problemCode;
    }


    /**
     * Gets the callType value for this CustomerCall.
     * 
     * @return callType
     */
    public com.cannontech.multispeak.deploy.service.CallType getCallType() {
        return callType;
    }


    /**
     * Sets the callType value for this CustomerCall.
     * 
     * @param callType
     */
    public void setCallType(com.cannontech.multispeak.deploy.service.CallType callType) {
        this.callType = callType;
    }


    /**
     * Gets the takenBy value for this CustomerCall.
     * 
     * @return takenBy
     */
    public java.lang.String getTakenBy() {
        return takenBy;
    }


    /**
     * Sets the takenBy value for this CustomerCall.
     * 
     * @param takenBy
     */
    public void setTakenBy(java.lang.String takenBy) {
        this.takenBy = takenBy;
    }


    /**
     * Gets the callPriority value for this CustomerCall.
     * 
     * @return callPriority
     */
    public com.cannontech.multispeak.deploy.service.PriorityType getCallPriority() {
        return callPriority;
    }


    /**
     * Sets the callPriority value for this CustomerCall.
     * 
     * @param callPriority
     */
    public void setCallPriority(com.cannontech.multispeak.deploy.service.PriorityType callPriority) {
        this.callPriority = callPriority;
    }


    /**
     * Gets the location value for this CustomerCall.
     * 
     * @return location
     */
    public com.cannontech.multispeak.deploy.service.OutageLocation getLocation() {
        return location;
    }


    /**
     * Sets the location value for this CustomerCall.
     * 
     * @param location
     */
    public void setLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) {
        this.location = location;
    }


    /**
     * Gets the messageList value for this CustomerCall.
     * 
     * @return messageList
     */
    public com.cannontech.multispeak.deploy.service.Message[] getMessageList() {
        return messageList;
    }


    /**
     * Sets the messageList value for this CustomerCall.
     * 
     * @param messageList
     */
    public void setMessageList(com.cannontech.multispeak.deploy.service.Message[] messageList) {
        this.messageList = messageList;
    }


    /**
     * Gets the resolvedLevel value for this CustomerCall.
     * 
     * @return resolvedLevel
     */
    public com.cannontech.multispeak.deploy.service.ResolvedLevel getResolvedLevel() {
        return resolvedLevel;
    }


    /**
     * Sets the resolvedLevel value for this CustomerCall.
     * 
     * @param resolvedLevel
     */
    public void setResolvedLevel(com.cannontech.multispeak.deploy.service.ResolvedLevel resolvedLevel) {
        this.resolvedLevel = resolvedLevel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomerCall)) return false;
        CustomerCall other = (CustomerCall) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.problemCode==null && other.getProblemCode()==null) || 
             (this.problemCode!=null &&
              this.problemCode.equals(other.getProblemCode()))) &&
            ((this.callType==null && other.getCallType()==null) || 
             (this.callType!=null &&
              this.callType.equals(other.getCallType()))) &&
            ((this.takenBy==null && other.getTakenBy()==null) || 
             (this.takenBy!=null &&
              this.takenBy.equals(other.getTakenBy()))) &&
            ((this.callPriority==null && other.getCallPriority()==null) || 
             (this.callPriority!=null &&
              this.callPriority.equals(other.getCallPriority()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.messageList==null && other.getMessageList()==null) || 
             (this.messageList!=null &&
              java.util.Arrays.equals(this.messageList, other.getMessageList()))) &&
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
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getProblemCode() != null) {
            _hashCode += getProblemCode().hashCode();
        }
        if (getCallType() != null) {
            _hashCode += getCallType().hashCode();
        }
        if (getTakenBy() != null) {
            _hashCode += getTakenBy().hashCode();
        }
        if (getCallPriority() != null) {
            _hashCode += getCallPriority().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
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
        if (getResolvedLevel() != null) {
            _hashCode += getResolvedLevel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CustomerCall.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
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
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("callType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
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
        elemField.setFieldName("callPriority");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callPriority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "location"));
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
