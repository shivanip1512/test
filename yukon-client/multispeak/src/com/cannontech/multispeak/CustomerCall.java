/**
 * CustomerCall.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CustomerCall  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String custID;
    private java.util.Calendar callTime;
    private java.lang.String description;
    private com.cannontech.multispeak.CallType callType;
    private java.lang.String takenBy;
    private com.cannontech.multispeak.PriorityType callPriority;
    private com.cannontech.multispeak.OutageLocation location;

    public CustomerCall() {
    }

    public CustomerCall(
           java.lang.String custID,
           java.util.Calendar callTime,
           java.lang.String description,
           com.cannontech.multispeak.CallType callType,
           java.lang.String takenBy,
           com.cannontech.multispeak.PriorityType callPriority,
           com.cannontech.multispeak.OutageLocation location) {
           this.custID = custID;
           this.callTime = callTime;
           this.description = description;
           this.callType = callType;
           this.takenBy = takenBy;
           this.callPriority = callPriority;
           this.location = location;
    }


    /**
     * Gets the custID value for this CustomerCall.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this CustomerCall.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the callTime value for this CustomerCall.
     * 
     * @return callTime
     */
    public java.util.Calendar getCallTime() {
        return callTime;
    }


    /**
     * Sets the callTime value for this CustomerCall.
     * 
     * @param callTime
     */
    public void setCallTime(java.util.Calendar callTime) {
        this.callTime = callTime;
    }


    /**
     * Gets the description value for this CustomerCall.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this CustomerCall.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the callType value for this CustomerCall.
     * 
     * @return callType
     */
    public com.cannontech.multispeak.CallType getCallType() {
        return callType;
    }


    /**
     * Sets the callType value for this CustomerCall.
     * 
     * @param callType
     */
    public void setCallType(com.cannontech.multispeak.CallType callType) {
        this.callType = callType;
    }


    /**
     * Gets the takenBy value for this CustomerCall.
     * 
     * @return takenBy
     */
    public java.lang.String getTakenBy() {
        return takenBy;
    }


    /**
     * Sets the takenBy value for this CustomerCall.
     * 
     * @param takenBy
     */
    public void setTakenBy(java.lang.String takenBy) {
        this.takenBy = takenBy;
    }


    /**
     * Gets the callPriority value for this CustomerCall.
     * 
     * @return callPriority
     */
    public com.cannontech.multispeak.PriorityType getCallPriority() {
        return callPriority;
    }


    /**
     * Sets the callPriority value for this CustomerCall.
     * 
     * @param callPriority
     */
    public void setCallPriority(com.cannontech.multispeak.PriorityType callPriority) {
        this.callPriority = callPriority;
    }


    /**
     * Gets the location value for this CustomerCall.
     * 
     * @return location
     */
    public com.cannontech.multispeak.OutageLocation getLocation() {
        return location;
    }


    /**
     * Sets the location value for this CustomerCall.
     * 
     * @param location
     */
    public void setLocation(com.cannontech.multispeak.OutageLocation location) {
        this.location = location;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CustomerCall)) return false;
        CustomerCall other = (CustomerCall) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.callTime==null && other.getCallTime()==null) || 
             (this.callTime!=null &&
              this.callTime.equals(other.getCallTime()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.callType==null && other.getCallType()==null) || 
             (this.callType!=null &&
              this.callType.equals(other.getCallType()))) &&
            ((this.takenBy==null && other.getTakenBy()==null) || 
             (this.takenBy!=null &&
              this.takenBy.equals(other.getTakenBy()))) &&
            ((this.callPriority==null && other.getCallPriority()==null) || 
             (this.callPriority!=null &&
              this.callPriority.equals(other.getCallPriority()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation())));
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
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getCallTime() != null) {
            _hashCode += getCallTime().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getCallType() != null) {
            _hashCode += getCallType().hashCode();
        }
        if (getTakenBy() != null) {
            _hashCode += getTakenBy().hashCode();
        }
        if (getCallPriority() != null) {
            _hashCode += getCallPriority().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CustomerCall.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "customerCall"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("takenBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "takenBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callPriority");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callPriority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageLocation"));
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
