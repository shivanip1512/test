/**
 * ArrayOfOutageCustomerListOutageCustomerOutageCustomer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ArrayOfOutageCustomerListOutageCustomerOutageCustomer  extends com.cannontech.multispeak.deploy.service.OutageCustomer  implements java.io.Serializable {
    private java.lang.String servLoc;

    private java.lang.String meterNo;

    public ArrayOfOutageCustomerListOutageCustomerOutageCustomer() {
    }

    public ArrayOfOutageCustomerListOutageCustomerOutageCustomer(
           java.lang.String custID,
           java.lang.String callBackAC,
           java.lang.String callBackPhone,
           com.cannontech.multispeak.deploy.service.OutageCustomerTimeToCall timeToCall,
           java.lang.Boolean callBackFlag,
           java.lang.String callBackContactFirstName,
           java.lang.String callBackContactLastName,
           java.lang.String callBackContactMName,
           com.cannontech.multispeak.deploy.service.CallBackStatus callBackStatus,
           java.util.Calendar callBackCompletedTime,
           com.cannontech.multispeak.deploy.service.CallBackType callBackType,
           java.lang.String callRecordID,
           java.lang.String outageEventID,
           java.lang.String servLoc,
           java.lang.String meterNo) {
        super(
            custID,
            callBackAC,
            callBackPhone,
            new TimePeriod(timeToCall.getStartTime(), timeToCall.getEndTime()),
            callBackFlag,
            callBackContactFirstName,
            callBackContactLastName,
            callBackContactMName,
            callBackStatus,
            callBackCompletedTime,
            callBackType,
            callRecordID,
            outageEventID,
            null,
            null,
            servLoc,
            meterNo);
        this.servLoc = servLoc;
        this.meterNo = meterNo;
    }


    /**
     * Gets the servLoc value for this ArrayOfOutageCustomerListOutageCustomerOutageCustomer.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this ArrayOfOutageCustomerListOutageCustomerOutageCustomer.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the meterNo value for this ArrayOfOutageCustomerListOutageCustomerOutageCustomer.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this ArrayOfOutageCustomerListOutageCustomerOutageCustomer.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfOutageCustomerListOutageCustomerOutageCustomer)) return false;
        ArrayOfOutageCustomerListOutageCustomerOutageCustomer other = (ArrayOfOutageCustomerListOutageCustomerOutageCustomer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo())));
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
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfOutageCustomerListOutageCustomerOutageCustomer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ArrayOfOutageCustomerListOutageCustomer>outageCustomer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
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
