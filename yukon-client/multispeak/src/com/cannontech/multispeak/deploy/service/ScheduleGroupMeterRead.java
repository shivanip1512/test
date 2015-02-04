/**
 * ScheduleGroupMeterRead.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ScheduleGroupMeterRead  implements java.io.Serializable {
    private java.lang.String meterGroupName;

    private java.util.Calendar timeToRead;

    private java.lang.String responseURL;

    private java.lang.String transactionID;

    public ScheduleGroupMeterRead() {
    }

    public ScheduleGroupMeterRead(
           java.lang.String meterGroupName,
           java.util.Calendar timeToRead,
           java.lang.String responseURL,
           java.lang.String transactionID) {
           this.meterGroupName = meterGroupName;
           this.timeToRead = timeToRead;
           this.responseURL = responseURL;
           this.transactionID = transactionID;
    }


    /**
     * Gets the meterGroupName value for this ScheduleGroupMeterRead.
     * 
     * @return meterGroupName
     */
    public java.lang.String getMeterGroupName() {
        return meterGroupName;
    }


    /**
     * Sets the meterGroupName value for this ScheduleGroupMeterRead.
     * 
     * @param meterGroupName
     */
    public void setMeterGroupName(java.lang.String meterGroupName) {
        this.meterGroupName = meterGroupName;
    }


    /**
     * Gets the timeToRead value for this ScheduleGroupMeterRead.
     * 
     * @return timeToRead
     */
    public java.util.Calendar getTimeToRead() {
        return timeToRead;
    }


    /**
     * Sets the timeToRead value for this ScheduleGroupMeterRead.
     * 
     * @param timeToRead
     */
    public void setTimeToRead(java.util.Calendar timeToRead) {
        this.timeToRead = timeToRead;
    }


    /**
     * Gets the responseURL value for this ScheduleGroupMeterRead.
     * 
     * @return responseURL
     */
    public java.lang.String getResponseURL() {
        return responseURL;
    }


    /**
     * Sets the responseURL value for this ScheduleGroupMeterRead.
     * 
     * @param responseURL
     */
    public void setResponseURL(java.lang.String responseURL) {
        this.responseURL = responseURL;
    }


    /**
     * Gets the transactionID value for this ScheduleGroupMeterRead.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this ScheduleGroupMeterRead.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScheduleGroupMeterRead)) return false;
        ScheduleGroupMeterRead other = (ScheduleGroupMeterRead) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterGroupName==null && other.getMeterGroupName()==null) || 
             (this.meterGroupName!=null &&
              this.meterGroupName.equals(other.getMeterGroupName()))) &&
            ((this.timeToRead==null && other.getTimeToRead()==null) || 
             (this.timeToRead!=null &&
              this.timeToRead.equals(other.getTimeToRead()))) &&
            ((this.responseURL==null && other.getResponseURL()==null) || 
             (this.responseURL!=null &&
              this.responseURL.equals(other.getResponseURL()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID())));
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
        if (getMeterGroupName() != null) {
            _hashCode += getMeterGroupName().hashCode();
        }
        if (getTimeToRead() != null) {
            _hashCode += getTimeToRead().hashCode();
        }
        if (getResponseURL() != null) {
            _hashCode += getResponseURL().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScheduleGroupMeterRead.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ScheduleGroupMeterRead"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroupName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeToRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeToRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "responseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transactionID"));
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
