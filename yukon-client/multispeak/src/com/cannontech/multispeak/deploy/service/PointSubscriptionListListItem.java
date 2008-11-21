/**
 * PointSubscriptionListListItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PointSubscriptionListListItem  implements java.io.Serializable {
    private java.lang.String pointID;

    private java.lang.Float deadband;

    private java.lang.Object debounceTime;

    public PointSubscriptionListListItem() {
    }

    public PointSubscriptionListListItem(
           java.lang.String pointID,
           java.lang.Float deadband,
           java.lang.Object debounceTime) {
           this.pointID = pointID;
           this.deadband = deadband;
           this.debounceTime = debounceTime;
    }


    /**
     * Gets the pointID value for this PointSubscriptionListListItem.
     * 
     * @return pointID
     */
    public java.lang.String getPointID() {
        return pointID;
    }


    /**
     * Sets the pointID value for this PointSubscriptionListListItem.
     * 
     * @param pointID
     */
    public void setPointID(java.lang.String pointID) {
        this.pointID = pointID;
    }


    /**
     * Gets the deadband value for this PointSubscriptionListListItem.
     * 
     * @return deadband
     */
    public java.lang.Float getDeadband() {
        return deadband;
    }


    /**
     * Sets the deadband value for this PointSubscriptionListListItem.
     * 
     * @param deadband
     */
    public void setDeadband(java.lang.Float deadband) {
        this.deadband = deadband;
    }


    /**
     * Gets the debounceTime value for this PointSubscriptionListListItem.
     * 
     * @return debounceTime
     */
    public java.lang.Object getDebounceTime() {
        return debounceTime;
    }


    /**
     * Sets the debounceTime value for this PointSubscriptionListListItem.
     * 
     * @param debounceTime
     */
    public void setDebounceTime(java.lang.Object debounceTime) {
        this.debounceTime = debounceTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PointSubscriptionListListItem)) return false;
        PointSubscriptionListListItem other = (PointSubscriptionListListItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pointID==null && other.getPointID()==null) || 
             (this.pointID!=null &&
              this.pointID.equals(other.getPointID()))) &&
            ((this.deadband==null && other.getDeadband()==null) || 
             (this.deadband!=null &&
              this.deadband.equals(other.getDeadband()))) &&
            ((this.debounceTime==null && other.getDebounceTime()==null) || 
             (this.debounceTime!=null &&
              this.debounceTime.equals(other.getDebounceTime())));
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
        if (getPointID() != null) {
            _hashCode += getPointID().hashCode();
        }
        if (getDeadband() != null) {
            _hashCode += getDeadband().hashCode();
        }
        if (getDebounceTime() != null) {
            _hashCode += getDebounceTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PointSubscriptionListListItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">pointSubscriptionList>listItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pointID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pointID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deadband");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deadband"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("debounceTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "debounceTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
