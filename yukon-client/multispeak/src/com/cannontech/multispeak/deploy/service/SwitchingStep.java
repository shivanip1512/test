/**
 * SwitchingStep.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SwitchingStep  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.math.BigInteger stepNumber;

    private java.lang.String operation;

    private java.lang.String deviceDescription;

    private com.cannontech.multispeak.deploy.service.ObjectRef deviceID;

    private com.cannontech.multispeak.deploy.service.WorkLocation workLocation;

    private java.lang.String desiredEndState;

    private java.lang.String resourceID;

    private java.lang.String workID;

    private java.lang.String instructedBy;

    private java.util.Calendar instructedDateTime;

    private java.lang.String completedBy;

    private java.util.Calendar completedDateTime;

    private java.lang.String stepStatus;

    private com.cannontech.multispeak.deploy.service.Clearance[] clearanceList;

    public SwitchingStep() {
    }

    public SwitchingStep(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.math.BigInteger stepNumber,
           java.lang.String operation,
           java.lang.String deviceDescription,
           com.cannontech.multispeak.deploy.service.ObjectRef deviceID,
           com.cannontech.multispeak.deploy.service.WorkLocation workLocation,
           java.lang.String desiredEndState,
           java.lang.String resourceID,
           java.lang.String workID,
           java.lang.String instructedBy,
           java.util.Calendar instructedDateTime,
           java.lang.String completedBy,
           java.util.Calendar completedDateTime,
           java.lang.String stepStatus,
           com.cannontech.multispeak.deploy.service.Clearance[] clearanceList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.stepNumber = stepNumber;
        this.operation = operation;
        this.deviceDescription = deviceDescription;
        this.deviceID = deviceID;
        this.workLocation = workLocation;
        this.desiredEndState = desiredEndState;
        this.resourceID = resourceID;
        this.workID = workID;
        this.instructedBy = instructedBy;
        this.instructedDateTime = instructedDateTime;
        this.completedBy = completedBy;
        this.completedDateTime = completedDateTime;
        this.stepStatus = stepStatus;
        this.clearanceList = clearanceList;
    }


    /**
     * Gets the stepNumber value for this SwitchingStep.
     * 
     * @return stepNumber
     */
    public java.math.BigInteger getStepNumber() {
        return stepNumber;
    }


    /**
     * Sets the stepNumber value for this SwitchingStep.
     * 
     * @param stepNumber
     */
    public void setStepNumber(java.math.BigInteger stepNumber) {
        this.stepNumber = stepNumber;
    }


    /**
     * Gets the operation value for this SwitchingStep.
     * 
     * @return operation
     */
    public java.lang.String getOperation() {
        return operation;
    }


    /**
     * Sets the operation value for this SwitchingStep.
     * 
     * @param operation
     */
    public void setOperation(java.lang.String operation) {
        this.operation = operation;
    }


    /**
     * Gets the deviceDescription value for this SwitchingStep.
     * 
     * @return deviceDescription
     */
    public java.lang.String getDeviceDescription() {
        return deviceDescription;
    }


    /**
     * Sets the deviceDescription value for this SwitchingStep.
     * 
     * @param deviceDescription
     */
    public void setDeviceDescription(java.lang.String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }


    /**
     * Gets the deviceID value for this SwitchingStep.
     * 
     * @return deviceID
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getDeviceID() {
        return deviceID;
    }


    /**
     * Sets the deviceID value for this SwitchingStep.
     * 
     * @param deviceID
     */
    public void setDeviceID(com.cannontech.multispeak.deploy.service.ObjectRef deviceID) {
        this.deviceID = deviceID;
    }


    /**
     * Gets the workLocation value for this SwitchingStep.
     * 
     * @return workLocation
     */
    public com.cannontech.multispeak.deploy.service.WorkLocation getWorkLocation() {
        return workLocation;
    }


    /**
     * Sets the workLocation value for this SwitchingStep.
     * 
     * @param workLocation
     */
    public void setWorkLocation(com.cannontech.multispeak.deploy.service.WorkLocation workLocation) {
        this.workLocation = workLocation;
    }


    /**
     * Gets the desiredEndState value for this SwitchingStep.
     * 
     * @return desiredEndState
     */
    public java.lang.String getDesiredEndState() {
        return desiredEndState;
    }


    /**
     * Sets the desiredEndState value for this SwitchingStep.
     * 
     * @param desiredEndState
     */
    public void setDesiredEndState(java.lang.String desiredEndState) {
        this.desiredEndState = desiredEndState;
    }


    /**
     * Gets the resourceID value for this SwitchingStep.
     * 
     * @return resourceID
     */
    public java.lang.String getResourceID() {
        return resourceID;
    }


    /**
     * Sets the resourceID value for this SwitchingStep.
     * 
     * @param resourceID
     */
    public void setResourceID(java.lang.String resourceID) {
        this.resourceID = resourceID;
    }


    /**
     * Gets the workID value for this SwitchingStep.
     * 
     * @return workID
     */
    public java.lang.String getWorkID() {
        return workID;
    }


    /**
     * Sets the workID value for this SwitchingStep.
     * 
     * @param workID
     */
    public void setWorkID(java.lang.String workID) {
        this.workID = workID;
    }


    /**
     * Gets the instructedBy value for this SwitchingStep.
     * 
     * @return instructedBy
     */
    public java.lang.String getInstructedBy() {
        return instructedBy;
    }


    /**
     * Sets the instructedBy value for this SwitchingStep.
     * 
     * @param instructedBy
     */
    public void setInstructedBy(java.lang.String instructedBy) {
        this.instructedBy = instructedBy;
    }


    /**
     * Gets the instructedDateTime value for this SwitchingStep.
     * 
     * @return instructedDateTime
     */
    public java.util.Calendar getInstructedDateTime() {
        return instructedDateTime;
    }


    /**
     * Sets the instructedDateTime value for this SwitchingStep.
     * 
     * @param instructedDateTime
     */
    public void setInstructedDateTime(java.util.Calendar instructedDateTime) {
        this.instructedDateTime = instructedDateTime;
    }


    /**
     * Gets the completedBy value for this SwitchingStep.
     * 
     * @return completedBy
     */
    public java.lang.String getCompletedBy() {
        return completedBy;
    }


    /**
     * Sets the completedBy value for this SwitchingStep.
     * 
     * @param completedBy
     */
    public void setCompletedBy(java.lang.String completedBy) {
        this.completedBy = completedBy;
    }


    /**
     * Gets the completedDateTime value for this SwitchingStep.
     * 
     * @return completedDateTime
     */
    public java.util.Calendar getCompletedDateTime() {
        return completedDateTime;
    }


    /**
     * Sets the completedDateTime value for this SwitchingStep.
     * 
     * @param completedDateTime
     */
    public void setCompletedDateTime(java.util.Calendar completedDateTime) {
        this.completedDateTime = completedDateTime;
    }


    /**
     * Gets the stepStatus value for this SwitchingStep.
     * 
     * @return stepStatus
     */
    public java.lang.String getStepStatus() {
        return stepStatus;
    }


    /**
     * Sets the stepStatus value for this SwitchingStep.
     * 
     * @param stepStatus
     */
    public void setStepStatus(java.lang.String stepStatus) {
        this.stepStatus = stepStatus;
    }


    /**
     * Gets the clearanceList value for this SwitchingStep.
     * 
     * @return clearanceList
     */
    public com.cannontech.multispeak.deploy.service.Clearance[] getClearanceList() {
        return clearanceList;
    }


    /**
     * Sets the clearanceList value for this SwitchingStep.
     * 
     * @param clearanceList
     */
    public void setClearanceList(com.cannontech.multispeak.deploy.service.Clearance[] clearanceList) {
        this.clearanceList = clearanceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SwitchingStep)) return false;
        SwitchingStep other = (SwitchingStep) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.stepNumber==null && other.getStepNumber()==null) || 
             (this.stepNumber!=null &&
              this.stepNumber.equals(other.getStepNumber()))) &&
            ((this.operation==null && other.getOperation()==null) || 
             (this.operation!=null &&
              this.operation.equals(other.getOperation()))) &&
            ((this.deviceDescription==null && other.getDeviceDescription()==null) || 
             (this.deviceDescription!=null &&
              this.deviceDescription.equals(other.getDeviceDescription()))) &&
            ((this.deviceID==null && other.getDeviceID()==null) || 
             (this.deviceID!=null &&
              this.deviceID.equals(other.getDeviceID()))) &&
            ((this.workLocation==null && other.getWorkLocation()==null) || 
             (this.workLocation!=null &&
              this.workLocation.equals(other.getWorkLocation()))) &&
            ((this.desiredEndState==null && other.getDesiredEndState()==null) || 
             (this.desiredEndState!=null &&
              this.desiredEndState.equals(other.getDesiredEndState()))) &&
            ((this.resourceID==null && other.getResourceID()==null) || 
             (this.resourceID!=null &&
              this.resourceID.equals(other.getResourceID()))) &&
            ((this.workID==null && other.getWorkID()==null) || 
             (this.workID!=null &&
              this.workID.equals(other.getWorkID()))) &&
            ((this.instructedBy==null && other.getInstructedBy()==null) || 
             (this.instructedBy!=null &&
              this.instructedBy.equals(other.getInstructedBy()))) &&
            ((this.instructedDateTime==null && other.getInstructedDateTime()==null) || 
             (this.instructedDateTime!=null &&
              this.instructedDateTime.equals(other.getInstructedDateTime()))) &&
            ((this.completedBy==null && other.getCompletedBy()==null) || 
             (this.completedBy!=null &&
              this.completedBy.equals(other.getCompletedBy()))) &&
            ((this.completedDateTime==null && other.getCompletedDateTime()==null) || 
             (this.completedDateTime!=null &&
              this.completedDateTime.equals(other.getCompletedDateTime()))) &&
            ((this.stepStatus==null && other.getStepStatus()==null) || 
             (this.stepStatus!=null &&
              this.stepStatus.equals(other.getStepStatus()))) &&
            ((this.clearanceList==null && other.getClearanceList()==null) || 
             (this.clearanceList!=null &&
              java.util.Arrays.equals(this.clearanceList, other.getClearanceList())));
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
        if (getStepNumber() != null) {
            _hashCode += getStepNumber().hashCode();
        }
        if (getOperation() != null) {
            _hashCode += getOperation().hashCode();
        }
        if (getDeviceDescription() != null) {
            _hashCode += getDeviceDescription().hashCode();
        }
        if (getDeviceID() != null) {
            _hashCode += getDeviceID().hashCode();
        }
        if (getWorkLocation() != null) {
            _hashCode += getWorkLocation().hashCode();
        }
        if (getDesiredEndState() != null) {
            _hashCode += getDesiredEndState().hashCode();
        }
        if (getResourceID() != null) {
            _hashCode += getResourceID().hashCode();
        }
        if (getWorkID() != null) {
            _hashCode += getWorkID().hashCode();
        }
        if (getInstructedBy() != null) {
            _hashCode += getInstructedBy().hashCode();
        }
        if (getInstructedDateTime() != null) {
            _hashCode += getInstructedDateTime().hashCode();
        }
        if (getCompletedBy() != null) {
            _hashCode += getCompletedBy().hashCode();
        }
        if (getCompletedDateTime() != null) {
            _hashCode += getCompletedDateTime().hashCode();
        }
        if (getStepStatus() != null) {
            _hashCode += getStepStatus().hashCode();
        }
        if (getClearanceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getClearanceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getClearanceList(), i);
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
        new org.apache.axis.description.TypeDesc(SwitchingStep.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingStep"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stepNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stepNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "operation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desiredEndState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "desiredEndState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resourceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resourceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "workID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instructedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instructedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instructedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instructedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "completedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "completedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stepStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stepStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clearanceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearanceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance"));
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
