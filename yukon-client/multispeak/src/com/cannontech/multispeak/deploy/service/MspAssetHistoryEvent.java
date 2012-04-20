/**
 * MspAssetHistoryEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MspAssetHistoryEvent  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GpsPoint gpsPoint;

    private com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation;

    private com.cannontech.multispeak.deploy.service.TimeZone timeZone;

    private java.util.Calendar eventTime;

    private com.cannontech.multispeak.deploy.service.ActionTaken actionTaken;

    private java.lang.String[] informationList;

    public MspAssetHistoryEvent() {
    }

    public MspAssetHistoryEvent(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.GpsPoint gpsPoint,
           com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation,
           com.cannontech.multispeak.deploy.service.TimeZone timeZone,
           java.util.Calendar eventTime,
           com.cannontech.multispeak.deploy.service.ActionTaken actionTaken,
           java.lang.String[] informationList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.gpsPoint = gpsPoint;
        this.gpsLocation = gpsLocation;
        this.timeZone = timeZone;
        this.eventTime = eventTime;
        this.actionTaken = actionTaken;
        this.informationList = informationList;
    }


    /**
     * Gets the gpsPoint value for this MspAssetHistoryEvent.
     * 
     * @return gpsPoint
     */
    public com.cannontech.multispeak.deploy.service.GpsPoint getGpsPoint() {
        return gpsPoint;
    }


    /**
     * Sets the gpsPoint value for this MspAssetHistoryEvent.
     * 
     * @param gpsPoint
     */
    public void setGpsPoint(com.cannontech.multispeak.deploy.service.GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }


    /**
     * Gets the gpsLocation value for this MspAssetHistoryEvent.
     * 
     * @return gpsLocation
     */
    public com.cannontech.multispeak.deploy.service.GpsLocation getGpsLocation() {
        return gpsLocation;
    }


    /**
     * Sets the gpsLocation value for this MspAssetHistoryEvent.
     * 
     * @param gpsLocation
     */
    public void setGpsLocation(com.cannontech.multispeak.deploy.service.GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
    }


    /**
     * Gets the timeZone value for this MspAssetHistoryEvent.
     * 
     * @return timeZone
     */
    public com.cannontech.multispeak.deploy.service.TimeZone getTimeZone() {
        return timeZone;
    }


    /**
     * Sets the timeZone value for this MspAssetHistoryEvent.
     * 
     * @param timeZone
     */
    public void setTimeZone(com.cannontech.multispeak.deploy.service.TimeZone timeZone) {
        this.timeZone = timeZone;
    }


    /**
     * Gets the eventTime value for this MspAssetHistoryEvent.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this MspAssetHistoryEvent.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the actionTaken value for this MspAssetHistoryEvent.
     * 
     * @return actionTaken
     */
    public com.cannontech.multispeak.deploy.service.ActionTaken getActionTaken() {
        return actionTaken;
    }


    /**
     * Sets the actionTaken value for this MspAssetHistoryEvent.
     * 
     * @param actionTaken
     */
    public void setActionTaken(com.cannontech.multispeak.deploy.service.ActionTaken actionTaken) {
        this.actionTaken = actionTaken;
    }


    /**
     * Gets the informationList value for this MspAssetHistoryEvent.
     * 
     * @return informationList
     */
    public java.lang.String[] getInformationList() {
        return informationList;
    }


    /**
     * Sets the informationList value for this MspAssetHistoryEvent.
     * 
     * @param informationList
     */
    public void setInformationList(java.lang.String[] informationList) {
        this.informationList = informationList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspAssetHistoryEvent)) return false;
        MspAssetHistoryEvent other = (MspAssetHistoryEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.gpsPoint==null && other.getGpsPoint()==null) || 
             (this.gpsPoint!=null &&
              this.gpsPoint.equals(other.getGpsPoint()))) &&
            ((this.gpsLocation==null && other.getGpsLocation()==null) || 
             (this.gpsLocation!=null &&
              this.gpsLocation.equals(other.getGpsLocation()))) &&
            ((this.timeZone==null && other.getTimeZone()==null) || 
             (this.timeZone!=null &&
              this.timeZone.equals(other.getTimeZone()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.actionTaken==null && other.getActionTaken()==null) || 
             (this.actionTaken!=null &&
              this.actionTaken.equals(other.getActionTaken()))) &&
            ((this.informationList==null && other.getInformationList()==null) || 
             (this.informationList!=null &&
              java.util.Arrays.equals(this.informationList, other.getInformationList())));
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
        if (getGpsPoint() != null) {
            _hashCode += getGpsPoint().hashCode();
        }
        if (getGpsLocation() != null) {
            _hashCode += getGpsLocation().hashCode();
        }
        if (getTimeZone() != null) {
            _hashCode += getTimeZone().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getActionTaken() != null) {
            _hashCode += getActionTaken().hashCode();
        }
        if (getInformationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInformationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInformationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspAssetHistoryEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspAssetHistoryEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gpsPoint");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsPoint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsPoint"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gpsLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeZone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeZone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeZone"));
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
        elemField.setFieldName("actionTaken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("informationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "informationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "informationItem"));
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
