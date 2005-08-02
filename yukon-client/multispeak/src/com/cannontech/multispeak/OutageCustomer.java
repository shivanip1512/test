/**
 * OutageCustomer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OutageCustomer  implements java.io.Serializable {
    private java.lang.String custID;
    private java.lang.String callBackAC;
    private java.lang.String callBackPhone;
    private com.cannontech.multispeak.OutageCustomerTimeToCall timeToCall;
    private java.lang.Boolean callBackFlag;
    private java.lang.String callBackContactFirstName;
    private java.lang.String callBackContactLastName;
    private java.lang.String callBackContactMName;
    private com.cannontech.multispeak.CallBackStatus callBackStatus;
    private java.util.Calendar callBackCompletedTime;
    private com.cannontech.multispeak.CallBackType callBackType;
    private java.lang.String callRecordID;
    private java.lang.String outageEventID;

    public OutageCustomer() {
    }

    public OutageCustomer(
           java.lang.String custID,
           java.lang.String callBackAC,
           java.lang.String callBackPhone,
           com.cannontech.multispeak.OutageCustomerTimeToCall timeToCall,
           java.lang.Boolean callBackFlag,
           java.lang.String callBackContactFirstName,
           java.lang.String callBackContactLastName,
           java.lang.String callBackContactMName,
           com.cannontech.multispeak.CallBackStatus callBackStatus,
           java.util.Calendar callBackCompletedTime,
           com.cannontech.multispeak.CallBackType callBackType,
           java.lang.String callRecordID,
           java.lang.String outageEventID) {
           this.custID = custID;
           this.callBackAC = callBackAC;
           this.callBackPhone = callBackPhone;
           this.timeToCall = timeToCall;
           this.callBackFlag = callBackFlag;
           this.callBackContactFirstName = callBackContactFirstName;
           this.callBackContactLastName = callBackContactLastName;
           this.callBackContactMName = callBackContactMName;
           this.callBackStatus = callBackStatus;
           this.callBackCompletedTime = callBackCompletedTime;
           this.callBackType = callBackType;
           this.callRecordID = callRecordID;
           this.outageEventID = outageEventID;
    }


    /**
     * Gets the custID value for this OutageCustomer.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this OutageCustomer.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the callBackAC value for this OutageCustomer.
     * 
     * @return callBackAC
     */
    public java.lang.String getCallBackAC() {
        return callBackAC;
    }


    /**
     * Sets the callBackAC value for this OutageCustomer.
     * 
     * @param callBackAC
     */
    public void setCallBackAC(java.lang.String callBackAC) {
        this.callBackAC = callBackAC;
    }


    /**
     * Gets the callBackPhone value for this OutageCustomer.
     * 
     * @return callBackPhone
     */
    public java.lang.String getCallBackPhone() {
        return callBackPhone;
    }


    /**
     * Sets the callBackPhone value for this OutageCustomer.
     * 
     * @param callBackPhone
     */
    public void setCallBackPhone(java.lang.String callBackPhone) {
        this.callBackPhone = callBackPhone;
    }


    /**
     * Gets the timeToCall value for this OutageCustomer.
     * 
     * @return timeToCall
     */
    public com.cannontech.multispeak.OutageCustomerTimeToCall getTimeToCall() {
        return timeToCall;
    }


    /**
     * Sets the timeToCall value for this OutageCustomer.
     * 
     * @param timeToCall
     */
    public void setTimeToCall(com.cannontech.multispeak.OutageCustomerTimeToCall timeToCall) {
        this.timeToCall = timeToCall;
    }


    /**
     * Gets the callBackFlag value for this OutageCustomer.
     * 
     * @return callBackFlag
     */
    public java.lang.Boolean getCallBackFlag() {
        return callBackFlag;
    }


    /**
     * Sets the callBackFlag value for this OutageCustomer.
     * 
     * @param callBackFlag
     */
    public void setCallBackFlag(java.lang.Boolean callBackFlag) {
        this.callBackFlag = callBackFlag;
    }


    /**
     * Gets the callBackContactFirstName value for this OutageCustomer.
     * 
     * @return callBackContactFirstName
     */
    public java.lang.String getCallBackContactFirstName() {
        return callBackContactFirstName;
    }


    /**
     * Sets the callBackContactFirstName value for this OutageCustomer.
     * 
     * @param callBackContactFirstName
     */
    public void setCallBackContactFirstName(java.lang.String callBackContactFirstName) {
        this.callBackContactFirstName = callBackContactFirstName;
    }


    /**
     * Gets the callBackContactLastName value for this OutageCustomer.
     * 
     * @return callBackContactLastName
     */
    public java.lang.String getCallBackContactLastName() {
        return callBackContactLastName;
    }


    /**
     * Sets the callBackContactLastName value for this OutageCustomer.
     * 
     * @param callBackContactLastName
     */
    public void setCallBackContactLastName(java.lang.String callBackContactLastName) {
        this.callBackContactLastName = callBackContactLastName;
    }


    /**
     * Gets the callBackContactMName value for this OutageCustomer.
     * 
     * @return callBackContactMName
     */
    public java.lang.String getCallBackContactMName() {
        return callBackContactMName;
    }


    /**
     * Sets the callBackContactMName value for this OutageCustomer.
     * 
     * @param callBackContactMName
     */
    public void setCallBackContactMName(java.lang.String callBackContactMName) {
        this.callBackContactMName = callBackContactMName;
    }


    /**
     * Gets the callBackStatus value for this OutageCustomer.
     * 
     * @return callBackStatus
     */
    public com.cannontech.multispeak.CallBackStatus getCallBackStatus() {
        return callBackStatus;
    }


    /**
     * Sets the callBackStatus value for this OutageCustomer.
     * 
     * @param callBackStatus
     */
    public void setCallBackStatus(com.cannontech.multispeak.CallBackStatus callBackStatus) {
        this.callBackStatus = callBackStatus;
    }


    /**
     * Gets the callBackCompletedTime value for this OutageCustomer.
     * 
     * @return callBackCompletedTime
     */
    public java.util.Calendar getCallBackCompletedTime() {
        return callBackCompletedTime;
    }


    /**
     * Sets the callBackCompletedTime value for this OutageCustomer.
     * 
     * @param callBackCompletedTime
     */
    public void setCallBackCompletedTime(java.util.Calendar callBackCompletedTime) {
        this.callBackCompletedTime = callBackCompletedTime;
    }


    /**
     * Gets the callBackType value for this OutageCustomer.
     * 
     * @return callBackType
     */
    public com.cannontech.multispeak.CallBackType getCallBackType() {
        return callBackType;
    }


    /**
     * Sets the callBackType value for this OutageCustomer.
     * 
     * @param callBackType
     */
    public void setCallBackType(com.cannontech.multispeak.CallBackType callBackType) {
        this.callBackType = callBackType;
    }


    /**
     * Gets the callRecordID value for this OutageCustomer.
     * 
     * @return callRecordID
     */
    public java.lang.String getCallRecordID() {
        return callRecordID;
    }


    /**
     * Sets the callRecordID value for this OutageCustomer.
     * 
     * @param callRecordID
     */
    public void setCallRecordID(java.lang.String callRecordID) {
        this.callRecordID = callRecordID;
    }


    /**
     * Gets the outageEventID value for this OutageCustomer.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this OutageCustomer.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OutageCustomer)) return false;
        OutageCustomer other = (OutageCustomer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.callBackAC==null && other.getCallBackAC()==null) || 
             (this.callBackAC!=null &&
              this.callBackAC.equals(other.getCallBackAC()))) &&
            ((this.callBackPhone==null && other.getCallBackPhone()==null) || 
             (this.callBackPhone!=null &&
              this.callBackPhone.equals(other.getCallBackPhone()))) &&
            ((this.timeToCall==null && other.getTimeToCall()==null) || 
             (this.timeToCall!=null &&
              this.timeToCall.equals(other.getTimeToCall()))) &&
            ((this.callBackFlag==null && other.getCallBackFlag()==null) || 
             (this.callBackFlag!=null &&
              this.callBackFlag.equals(other.getCallBackFlag()))) &&
            ((this.callBackContactFirstName==null && other.getCallBackContactFirstName()==null) || 
             (this.callBackContactFirstName!=null &&
              this.callBackContactFirstName.equals(other.getCallBackContactFirstName()))) &&
            ((this.callBackContactLastName==null && other.getCallBackContactLastName()==null) || 
             (this.callBackContactLastName!=null &&
              this.callBackContactLastName.equals(other.getCallBackContactLastName()))) &&
            ((this.callBackContactMName==null && other.getCallBackContactMName()==null) || 
             (this.callBackContactMName!=null &&
              this.callBackContactMName.equals(other.getCallBackContactMName()))) &&
            ((this.callBackStatus==null && other.getCallBackStatus()==null) || 
             (this.callBackStatus!=null &&
              this.callBackStatus.equals(other.getCallBackStatus()))) &&
            ((this.callBackCompletedTime==null && other.getCallBackCompletedTime()==null) || 
             (this.callBackCompletedTime!=null &&
              this.callBackCompletedTime.equals(other.getCallBackCompletedTime()))) &&
            ((this.callBackType==null && other.getCallBackType()==null) || 
             (this.callBackType!=null &&
              this.callBackType.equals(other.getCallBackType()))) &&
            ((this.callRecordID==null && other.getCallRecordID()==null) || 
             (this.callRecordID!=null &&
              this.callRecordID.equals(other.getCallRecordID()))) &&
            ((this.outageEventID==null && other.getOutageEventID()==null) || 
             (this.outageEventID!=null &&
              this.outageEventID.equals(other.getOutageEventID())));
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
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getCallBackAC() != null) {
            _hashCode += getCallBackAC().hashCode();
        }
        if (getCallBackPhone() != null) {
            _hashCode += getCallBackPhone().hashCode();
        }
        if (getTimeToCall() != null) {
            _hashCode += getTimeToCall().hashCode();
        }
        if (getCallBackFlag() != null) {
            _hashCode += getCallBackFlag().hashCode();
        }
        if (getCallBackContactFirstName() != null) {
            _hashCode += getCallBackContactFirstName().hashCode();
        }
        if (getCallBackContactLastName() != null) {
            _hashCode += getCallBackContactLastName().hashCode();
        }
        if (getCallBackContactMName() != null) {
            _hashCode += getCallBackContactMName().hashCode();
        }
        if (getCallBackStatus() != null) {
            _hashCode += getCallBackStatus().hashCode();
        }
        if (getCallBackCompletedTime() != null) {
            _hashCode += getCallBackCompletedTime().hashCode();
        }
        if (getCallBackType() != null) {
            _hashCode += getCallBackType().hashCode();
        }
        if (getCallRecordID() != null) {
            _hashCode += getCallRecordID().hashCode();
        }
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OutageCustomer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackAC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackAC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeToCall");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "timeToCall"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageCustomerTimeToCall"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackFlag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackFlag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackContactFirstName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackContactFirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackContactLastName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackContactLastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackContactMName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackContactMName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackCompletedTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackCompletedTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callBackType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callBackType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callRecordID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callRecordID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
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
