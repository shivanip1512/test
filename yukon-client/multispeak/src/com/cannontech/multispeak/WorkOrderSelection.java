/**
 * WorkOrderSelection.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class WorkOrderSelection  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String woNumber;
    private java.lang.String jobNumber;
    private java.lang.String jobDescr;
    private java.lang.String statusCode;

    public WorkOrderSelection() {
    }

    public WorkOrderSelection(
           java.lang.String woNumber,
           java.lang.String jobNumber,
           java.lang.String jobDescr,
           java.lang.String statusCode) {
           this.woNumber = woNumber;
           this.jobNumber = jobNumber;
           this.jobDescr = jobDescr;
           this.statusCode = statusCode;
    }


    /**
     * Gets the woNumber value for this WorkOrderSelection.
     * 
     * @return woNumber
     */
    public java.lang.String getWoNumber() {
        return woNumber;
    }


    /**
     * Sets the woNumber value for this WorkOrderSelection.
     * 
     * @param woNumber
     */
    public void setWoNumber(java.lang.String woNumber) {
        this.woNumber = woNumber;
    }


    /**
     * Gets the jobNumber value for this WorkOrderSelection.
     * 
     * @return jobNumber
     */
    public java.lang.String getJobNumber() {
        return jobNumber;
    }


    /**
     * Sets the jobNumber value for this WorkOrderSelection.
     * 
     * @param jobNumber
     */
    public void setJobNumber(java.lang.String jobNumber) {
        this.jobNumber = jobNumber;
    }


    /**
     * Gets the jobDescr value for this WorkOrderSelection.
     * 
     * @return jobDescr
     */
    public java.lang.String getJobDescr() {
        return jobDescr;
    }


    /**
     * Sets the jobDescr value for this WorkOrderSelection.
     * 
     * @param jobDescr
     */
    public void setJobDescr(java.lang.String jobDescr) {
        this.jobDescr = jobDescr;
    }


    /**
     * Gets the statusCode value for this WorkOrderSelection.
     * 
     * @return statusCode
     */
    public java.lang.String getStatusCode() {
        return statusCode;
    }


    /**
     * Sets the statusCode value for this WorkOrderSelection.
     * 
     * @param statusCode
     */
    public void setStatusCode(java.lang.String statusCode) {
        this.statusCode = statusCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WorkOrderSelection)) return false;
        WorkOrderSelection other = (WorkOrderSelection) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.woNumber==null && other.getWoNumber()==null) || 
             (this.woNumber!=null &&
              this.woNumber.equals(other.getWoNumber()))) &&
            ((this.jobNumber==null && other.getJobNumber()==null) || 
             (this.jobNumber!=null &&
              this.jobNumber.equals(other.getJobNumber()))) &&
            ((this.jobDescr==null && other.getJobDescr()==null) || 
             (this.jobDescr!=null &&
              this.jobDescr.equals(other.getJobDescr()))) &&
            ((this.statusCode==null && other.getStatusCode()==null) || 
             (this.statusCode!=null &&
              this.statusCode.equals(other.getStatusCode())));
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
        if (getWoNumber() != null) {
            _hashCode += getWoNumber().hashCode();
        }
        if (getJobNumber() != null) {
            _hashCode += getJobNumber().hashCode();
        }
        if (getJobDescr() != null) {
            _hashCode += getJobDescr().hashCode();
        }
        if (getStatusCode() != null) {
            _hashCode += getStatusCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WorkOrderSelection.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workOrderSelection"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("woNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "woNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobDescr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jobDescr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "statusCode"));
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
