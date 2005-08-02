/**
 * CrewActionEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CrewActionEvent  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String outageEventID;
    private java.lang.String requestedCrewID;
    private java.lang.String assignedCrewID;
    private com.cannontech.multispeak.CrewAction crewAction;

    public CrewActionEvent() {
    }

    public CrewActionEvent(
           java.lang.String outageEventID,
           java.lang.String requestedCrewID,
           java.lang.String assignedCrewID,
           com.cannontech.multispeak.CrewAction crewAction) {
           this.outageEventID = outageEventID;
           this.requestedCrewID = requestedCrewID;
           this.assignedCrewID = assignedCrewID;
           this.crewAction = crewAction;
    }


    /**
     * Gets the outageEventID value for this CrewActionEvent.
     * 
     * @return outageEventID
     */
    public java.lang.String getOutageEventID() {
        return outageEventID;
    }


    /**
     * Sets the outageEventID value for this CrewActionEvent.
     * 
     * @param outageEventID
     */
    public void setOutageEventID(java.lang.String outageEventID) {
        this.outageEventID = outageEventID;
    }


    /**
     * Gets the requestedCrewID value for this CrewActionEvent.
     * 
     * @return requestedCrewID
     */
    public java.lang.String getRequestedCrewID() {
        return requestedCrewID;
    }


    /**
     * Sets the requestedCrewID value for this CrewActionEvent.
     * 
     * @param requestedCrewID
     */
    public void setRequestedCrewID(java.lang.String requestedCrewID) {
        this.requestedCrewID = requestedCrewID;
    }


    /**
     * Gets the assignedCrewID value for this CrewActionEvent.
     * 
     * @return assignedCrewID
     */
    public java.lang.String getAssignedCrewID() {
        return assignedCrewID;
    }


    /**
     * Sets the assignedCrewID value for this CrewActionEvent.
     * 
     * @param assignedCrewID
     */
    public void setAssignedCrewID(java.lang.String assignedCrewID) {
        this.assignedCrewID = assignedCrewID;
    }


    /**
     * Gets the crewAction value for this CrewActionEvent.
     * 
     * @return crewAction
     */
    public com.cannontech.multispeak.CrewAction getCrewAction() {
        return crewAction;
    }


    /**
     * Sets the crewAction value for this CrewActionEvent.
     * 
     * @param crewAction
     */
    public void setCrewAction(com.cannontech.multispeak.CrewAction crewAction) {
        this.crewAction = crewAction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrewActionEvent)) return false;
        CrewActionEvent other = (CrewActionEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageEventID==null && other.getOutageEventID()==null) || 
             (this.outageEventID!=null &&
              this.outageEventID.equals(other.getOutageEventID()))) &&
            ((this.requestedCrewID==null && other.getRequestedCrewID()==null) || 
             (this.requestedCrewID!=null &&
              this.requestedCrewID.equals(other.getRequestedCrewID()))) &&
            ((this.assignedCrewID==null && other.getAssignedCrewID()==null) || 
             (this.assignedCrewID!=null &&
              this.assignedCrewID.equals(other.getAssignedCrewID()))) &&
            ((this.crewAction==null && other.getCrewAction()==null) || 
             (this.crewAction!=null &&
              this.crewAction.equals(other.getCrewAction())));
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
        if (getOutageEventID() != null) {
            _hashCode += getOutageEventID().hashCode();
        }
        if (getRequestedCrewID() != null) {
            _hashCode += getRequestedCrewID().hashCode();
        }
        if (getAssignedCrewID() != null) {
            _hashCode += getAssignedCrewID().hashCode();
        }
        if (getCrewAction() != null) {
            _hashCode += getCrewAction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CrewActionEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewActionEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageEventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageEventID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedCrewID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedCrewID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assignedCrewID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assignedCrewID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("crewAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "crewAction"));
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
