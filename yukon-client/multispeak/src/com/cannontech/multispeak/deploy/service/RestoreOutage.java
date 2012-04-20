/**
 * RestoreOutage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RestoreOutage  implements java.io.Serializable {
    private java.lang.String outageEventID;

    private java.util.Calendar eventTime;

    private boolean callBackCustomersThatCalled;

    private com.cannontech.multispeak.deploy.service.OutageReasonContainer outageCause;

    private java.lang.String dispatcherResponsible;

    public RestoreOutage() {
    }

    public RestoreOutage(
           java.lang.String outageEventID,
           java.util.Calendar eventTime,
           boolean callBackCustomersThatCalled,
           com.cannontech.multispeak.deploy.service.OutageReasonContainer outageCause,
           java.lang.String dispatcherResponsible) {
           this.outageEventID = outageEventID;
           this.eventTime = eventTime;
           this.callBackCustomersThatCalled = callBackCustomersThatCalled;
           this.outageCause = outageCause;
           this.dispatcherResponsible = dispatcherResponsible;
    }


    /**
     * Gets the outageEventID value for this RestoreOutage.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this RestoreOutage.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }


    /**
     * Gets the eventTime value for this RestoreOutage.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this RestoreOutage.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the callBackCustomersThatCalled value for this RestoreOutage.
     * 
     * @return callBackCustomersThatCalled
     */
    public boolean isCallBackCustomersThatCalled() {
        return callBackCustomersThatCalled;
    }


    /**
     * Sets the callBackCustomersThatCalled value for this RestoreOutage.
     * 
     * @param callBackCustomersThatCalled
     */
    public void setCallBackCustomersThatCalled(boolean callBackCustomersThatCalled) {
        this.callBackCustomersThatCalled = callBackCustomersThatCalled;
    }


    /**
     * Gets the outageCause value for this RestoreOutage.
     * 
     * @return outageCause
     */
    public com.cannontech.multispeak.deploy.service.OutageReasonContainer getOutageCause() {
        return outageCause;
    }


    /**
     * Sets the outageCause value for this RestoreOutage.
     * 
     * @param outageCause
     */
    public void setOutageCause(com.cannontech.multispeak.deploy.service.OutageReasonContainer outageCause) {
        this.outageCause = outageCause;
    }


    /**
     * Gets the dispatcherResponsible value for this RestoreOutage.
     * 
     * @return dispatcherResponsible
     */
    public java.lang.String getDispatcherResponsible() {
        return dispatcherResponsible;
    }


    /**
     * Sets the dispatcherResponsible value for this RestoreOutage.
     * 
     * @param dispatcherResponsible
     */
    public void setDispatcherResponsible(java.lang.String dispatcherResponsible) {
        this.dispatcherResponsible = dispatcherResponsible;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RestoreOutage)) return false;
        RestoreOutage other = (RestoreOutage) obj;
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
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            this.callBackCustomersThatCalled == other.isCallBackCustomersThatCalled() &&
            ((this.outageCause==null && other.getOutageCause()==null) || 
             (this.outageCause!=null &&
              this.outageCause.equals(other.getOutageCause()))) &&
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
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        _hashCode += (isCallBackCustomersThatCalled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOutageCause() != null) {
            _hashCode += getOutageCause().hashCode();
        }
        if (getDispatcherResponsible() != null) {
            _hashCode += getDispatcherResponsible().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RestoreOutage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RestoreOutage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("callBackCustomersThatCalled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackCustomersThatCalled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageCause");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageReasonContainer"));
        elemField.setMinOccurs(0);
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
