/**
 * Cut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Cut  extends com.cannontech.multispeak.deploy.service.MspSwitchingDevice  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.EaLoc lineSectionAffected;

    private com.cannontech.multispeak.deploy.service.CutActionTaken actionTaken;

    private java.util.Calendar timeOfAction;

    public Cut() {
    }

    public Cut(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String eaEquipment,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.PhaseCd phase,
           com.cannontech.multispeak.deploy.service.Position position,
           java.lang.Float ratedVolt,
           java.lang.Float operVolt,
           java.lang.Float maxContAmp,
           java.lang.String manufacturer,
           com.cannontech.multispeak.deploy.service.Mounting mounting,
           com.cannontech.multispeak.deploy.service.EaLoc lineSectionAffected,
           com.cannontech.multispeak.deploy.service.CutActionTaken actionTaken,
           java.util.Calendar timeOfAction) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            eaEquipment,
            facilityID,
            phase,
            position,
            ratedVolt,
            operVolt,
            maxContAmp,
            manufacturer,
            mounting);
        this.lineSectionAffected = lineSectionAffected;
        this.actionTaken = actionTaken;
        this.timeOfAction = timeOfAction;
    }


    /**
     * Gets the lineSectionAffected value for this Cut.
     * 
     * @return lineSectionAffected
     */
    public com.cannontech.multispeak.deploy.service.EaLoc getLineSectionAffected() {
        return lineSectionAffected;
    }


    /**
     * Sets the lineSectionAffected value for this Cut.
     * 
     * @param lineSectionAffected
     */
    public void setLineSectionAffected(com.cannontech.multispeak.deploy.service.EaLoc lineSectionAffected) {
        this.lineSectionAffected = lineSectionAffected;
    }


    /**
     * Gets the actionTaken value for this Cut.
     * 
     * @return actionTaken
     */
    public com.cannontech.multispeak.deploy.service.CutActionTaken getActionTaken() {
        return actionTaken;
    }


    /**
     * Sets the actionTaken value for this Cut.
     * 
     * @param actionTaken
     */
    public void setActionTaken(com.cannontech.multispeak.deploy.service.CutActionTaken actionTaken) {
        this.actionTaken = actionTaken;
    }


    /**
     * Gets the timeOfAction value for this Cut.
     * 
     * @return timeOfAction
     */
    public java.util.Calendar getTimeOfAction() {
        return timeOfAction;
    }


    /**
     * Sets the timeOfAction value for this Cut.
     * 
     * @param timeOfAction
     */
    public void setTimeOfAction(java.util.Calendar timeOfAction) {
        this.timeOfAction = timeOfAction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Cut)) return false;
        Cut other = (Cut) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.lineSectionAffected==null && other.getLineSectionAffected()==null) || 
             (this.lineSectionAffected!=null &&
              this.lineSectionAffected.equals(other.getLineSectionAffected()))) &&
            ((this.actionTaken==null && other.getActionTaken()==null) || 
             (this.actionTaken!=null &&
              this.actionTaken.equals(other.getActionTaken()))) &&
            ((this.timeOfAction==null && other.getTimeOfAction()==null) || 
             (this.timeOfAction!=null &&
              this.timeOfAction.equals(other.getTimeOfAction())));
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
        if (getLineSectionAffected() != null) {
            _hashCode += getLineSectionAffected().hashCode();
        }
        if (getActionTaken() != null) {
            _hashCode += getActionTaken().hashCode();
        }
        if (getTimeOfAction() != null) {
            _hashCode += getTimeOfAction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Cut.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cut"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lineSectionAffected");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lineSectionAffected"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionTaken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "actionTaken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">cut>actionTaken"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeOfAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeOfAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
