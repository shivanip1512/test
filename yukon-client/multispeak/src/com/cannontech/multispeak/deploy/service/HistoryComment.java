/**
 * HistoryComment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class HistoryComment  implements java.io.Serializable {
    private java.lang.String customerID;

    private java.lang.String serviceLocationID;

    private com.cannontech.multispeak.deploy.service.ServiceType serviceType;

    private java.util.Calendar logTime;

    private java.lang.String comment;

    public HistoryComment() {
    }

    public HistoryComment(
           java.lang.String customerID,
           java.lang.String serviceLocationID,
           com.cannontech.multispeak.deploy.service.ServiceType serviceType,
           java.util.Calendar logTime,
           java.lang.String comment) {
           this.customerID = customerID;
           this.serviceLocationID = serviceLocationID;
           this.serviceType = serviceType;
           this.logTime = logTime;
           this.comment = comment;
    }


    /**
     * Gets the customerID value for this HistoryComment.
     * 
     * @return customerID
     */
    public java.lang.String getCustomerID() {
        return customerID;
    }


    /**
     * Sets the customerID value for this HistoryComment.
     * 
     * @param customerID
     */
    public void setCustomerID(java.lang.String customerID) {
        this.customerID = customerID;
    }


    /**
     * Gets the serviceLocationID value for this HistoryComment.
     * 
     * @return serviceLocationID
     */
    public java.lang.String getServiceLocationID() {
        return serviceLocationID;
    }


    /**
     * Sets the serviceLocationID value for this HistoryComment.
     * 
     * @param serviceLocationID
     */
    public void setServiceLocationID(java.lang.String serviceLocationID) {
        this.serviceLocationID = serviceLocationID;
    }


    /**
     * Gets the serviceType value for this HistoryComment.
     * 
     * @return serviceType
     */
    public com.cannontech.multispeak.deploy.service.ServiceType getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this HistoryComment.
     * 
     * @param serviceType
     */
    public void setServiceType(com.cannontech.multispeak.deploy.service.ServiceType serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the logTime value for this HistoryComment.
     * 
     * @return logTime
     */
    public java.util.Calendar getLogTime() {
        return logTime;
    }


    /**
     * Sets the logTime value for this HistoryComment.
     * 
     * @param logTime
     */
    public void setLogTime(java.util.Calendar logTime) {
        this.logTime = logTime;
    }


    /**
     * Gets the comment value for this HistoryComment.
     * 
     * @return comment
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this HistoryComment.
     * 
     * @param comment
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof HistoryComment)) return false;
        HistoryComment other = (HistoryComment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.customerID==null && other.getCustomerID()==null) || 
             (this.customerID!=null &&
              this.customerID.equals(other.getCustomerID()))) &&
            ((this.serviceLocationID==null && other.getServiceLocationID()==null) || 
             (this.serviceLocationID!=null &&
              this.serviceLocationID.equals(other.getServiceLocationID()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            ((this.logTime==null && other.getLogTime()==null) || 
             (this.logTime!=null &&
              this.logTime.equals(other.getLogTime()))) &&
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment())));
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
        if (getCustomerID() != null) {
            _hashCode += getCustomerID().hashCode();
        }
        if (getServiceLocationID() != null) {
            _hashCode += getServiceLocationID().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        if (getLogTime() != null) {
            _hashCode += getLogTime().hashCode();
        }
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(HistoryComment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyComment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "logTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "comment"));
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
