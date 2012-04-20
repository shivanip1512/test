/**
 * InHomeDisplayMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InHomeDisplayMessage  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String inHomeDisplayID;

    private com.cannontech.multispeak.deploy.service.MsgLine[] msgLineList;

    private java.lang.Float duration;

    private java.math.BigInteger priorityOrder;

    private java.lang.Boolean isAlert;

    private java.lang.String alertLevel;

    private java.lang.String reason;

    public InHomeDisplayMessage() {
    }

    public InHomeDisplayMessage(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String inHomeDisplayID,
           com.cannontech.multispeak.deploy.service.MsgLine[] msgLineList,
           java.lang.Float duration,
           java.math.BigInteger priorityOrder,
           java.lang.Boolean isAlert,
           java.lang.String alertLevel,
           java.lang.String reason) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.inHomeDisplayID = inHomeDisplayID;
        this.msgLineList = msgLineList;
        this.duration = duration;
        this.priorityOrder = priorityOrder;
        this.isAlert = isAlert;
        this.alertLevel = alertLevel;
        this.reason = reason;
    }


    /**
     * Gets the inHomeDisplayID value for this InHomeDisplayMessage.
     * 
     * @return inHomeDisplayID
     */
    public java.lang.String getInHomeDisplayID() {
        return inHomeDisplayID;
    }


    /**
     * Sets the inHomeDisplayID value for this InHomeDisplayMessage.
     * 
     * @param inHomeDisplayID
     */
    public void setInHomeDisplayID(java.lang.String inHomeDisplayID) {
        this.inHomeDisplayID = inHomeDisplayID;
    }


    /**
     * Gets the msgLineList value for this InHomeDisplayMessage.
     * 
     * @return msgLineList
     */
    public com.cannontech.multispeak.deploy.service.MsgLine[] getMsgLineList() {
        return msgLineList;
    }


    /**
     * Sets the msgLineList value for this InHomeDisplayMessage.
     * 
     * @param msgLineList
     */
    public void setMsgLineList(com.cannontech.multispeak.deploy.service.MsgLine[] msgLineList) {
        this.msgLineList = msgLineList;
    }


    /**
     * Gets the duration value for this InHomeDisplayMessage.
     * 
     * @return duration
     */
    public java.lang.Float getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this InHomeDisplayMessage.
     * 
     * @param duration
     */
    public void setDuration(java.lang.Float duration) {
        this.duration = duration;
    }


    /**
     * Gets the priorityOrder value for this InHomeDisplayMessage.
     * 
     * @return priorityOrder
     */
    public java.math.BigInteger getPriorityOrder() {
        return priorityOrder;
    }


    /**
     * Sets the priorityOrder value for this InHomeDisplayMessage.
     * 
     * @param priorityOrder
     */
    public void setPriorityOrder(java.math.BigInteger priorityOrder) {
        this.priorityOrder = priorityOrder;
    }


    /**
     * Gets the isAlert value for this InHomeDisplayMessage.
     * 
     * @return isAlert
     */
    public java.lang.Boolean getIsAlert() {
        return isAlert;
    }


    /**
     * Sets the isAlert value for this InHomeDisplayMessage.
     * 
     * @param isAlert
     */
    public void setIsAlert(java.lang.Boolean isAlert) {
        this.isAlert = isAlert;
    }


    /**
     * Gets the alertLevel value for this InHomeDisplayMessage.
     * 
     * @return alertLevel
     */
    public java.lang.String getAlertLevel() {
        return alertLevel;
    }


    /**
     * Sets the alertLevel value for this InHomeDisplayMessage.
     * 
     * @param alertLevel
     */
    public void setAlertLevel(java.lang.String alertLevel) {
        this.alertLevel = alertLevel;
    }


    /**
     * Gets the reason value for this InHomeDisplayMessage.
     * 
     * @return reason
     */
    public java.lang.String getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this InHomeDisplayMessage.
     * 
     * @param reason
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InHomeDisplayMessage)) return false;
        InHomeDisplayMessage other = (InHomeDisplayMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.inHomeDisplayID==null && other.getInHomeDisplayID()==null) || 
             (this.inHomeDisplayID!=null &&
              this.inHomeDisplayID.equals(other.getInHomeDisplayID()))) &&
            ((this.msgLineList==null && other.getMsgLineList()==null) || 
             (this.msgLineList!=null &&
              java.util.Arrays.equals(this.msgLineList, other.getMsgLineList()))) &&
            ((this.duration==null && other.getDuration()==null) || 
             (this.duration!=null &&
              this.duration.equals(other.getDuration()))) &&
            ((this.priorityOrder==null && other.getPriorityOrder()==null) || 
             (this.priorityOrder!=null &&
              this.priorityOrder.equals(other.getPriorityOrder()))) &&
            ((this.isAlert==null && other.getIsAlert()==null) || 
             (this.isAlert!=null &&
              this.isAlert.equals(other.getIsAlert()))) &&
            ((this.alertLevel==null && other.getAlertLevel()==null) || 
             (this.alertLevel!=null &&
              this.alertLevel.equals(other.getAlertLevel()))) &&
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason())));
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
        if (getInHomeDisplayID() != null) {
            _hashCode += getInHomeDisplayID().hashCode();
        }
        if (getMsgLineList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMsgLineList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMsgLineList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDuration() != null) {
            _hashCode += getDuration().hashCode();
        }
        if (getPriorityOrder() != null) {
            _hashCode += getPriorityOrder().hashCode();
        }
        if (getIsAlert() != null) {
            _hashCode += getIsAlert().hashCode();
        }
        if (getAlertLevel() != null) {
            _hashCode += getAlertLevel().hashCode();
        }
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InHomeDisplayMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgLineList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLineList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLine"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "msgLine"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isAlert");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isAlert"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alertLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "alertLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reason"));
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
