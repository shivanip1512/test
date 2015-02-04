/**
 * SwitchingOrder.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SwitchingOrder  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String title;

    private java.lang.String description;

    private java.lang.String purpose;

    private java.lang.String revision;

    private java.lang.String requestedBy;

    private java.util.Calendar requestedDateTime;

    private java.lang.String createdBy;

    private java.util.Calendar createdDateTime;

    private java.lang.String checkedBy;

    private java.util.Calendar checkedDateTime;

    private java.lang.String releasedBy;

    private java.util.Calendar releasedDateTime;

    private java.util.Calendar lastModifiedDateTime;

    private java.lang.String documentStatus;

    private com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule;

    public SwitchingOrder() {
    }

    public SwitchingOrder(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String title,
           java.lang.String description,
           java.lang.String purpose,
           java.lang.String revision,
           java.lang.String requestedBy,
           java.util.Calendar requestedDateTime,
           java.lang.String createdBy,
           java.util.Calendar createdDateTime,
           java.lang.String checkedBy,
           java.util.Calendar checkedDateTime,
           java.lang.String releasedBy,
           java.util.Calendar releasedDateTime,
           java.util.Calendar lastModifiedDateTime,
           java.lang.String documentStatus,
           com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.title = title;
        this.description = description;
        this.purpose = purpose;
        this.revision = revision;
        this.requestedBy = requestedBy;
        this.requestedDateTime = requestedDateTime;
        this.createdBy = createdBy;
        this.createdDateTime = createdDateTime;
        this.checkedBy = checkedBy;
        this.checkedDateTime = checkedDateTime;
        this.releasedBy = releasedBy;
        this.releasedDateTime = releasedDateTime;
        this.lastModifiedDateTime = lastModifiedDateTime;
        this.documentStatus = documentStatus;
        this.switchingSchedule = switchingSchedule;
    }


    /**
     * Gets the title value for this SwitchingOrder.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this SwitchingOrder.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the description value for this SwitchingOrder.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this SwitchingOrder.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the purpose value for this SwitchingOrder.
     * 
     * @return purpose
     */
    public java.lang.String getPurpose() {
        return purpose;
    }


    /**
     * Sets the purpose value for this SwitchingOrder.
     * 
     * @param purpose
     */
    public void setPurpose(java.lang.String purpose) {
        this.purpose = purpose;
    }


    /**
     * Gets the revision value for this SwitchingOrder.
     * 
     * @return revision
     */
    public java.lang.String getRevision() {
        return revision;
    }


    /**
     * Sets the revision value for this SwitchingOrder.
     * 
     * @param revision
     */
    public void setRevision(java.lang.String revision) {
        this.revision = revision;
    }


    /**
     * Gets the requestedBy value for this SwitchingOrder.
     * 
     * @return requestedBy
     */
    public java.lang.String getRequestedBy() {
        return requestedBy;
    }


    /**
     * Sets the requestedBy value for this SwitchingOrder.
     * 
     * @param requestedBy
     */
    public void setRequestedBy(java.lang.String requestedBy) {
        this.requestedBy = requestedBy;
    }


    /**
     * Gets the requestedDateTime value for this SwitchingOrder.
     * 
     * @return requestedDateTime
     */
    public java.util.Calendar getRequestedDateTime() {
        return requestedDateTime;
    }


    /**
     * Sets the requestedDateTime value for this SwitchingOrder.
     * 
     * @param requestedDateTime
     */
    public void setRequestedDateTime(java.util.Calendar requestedDateTime) {
        this.requestedDateTime = requestedDateTime;
    }


    /**
     * Gets the createdBy value for this SwitchingOrder.
     * 
     * @return createdBy
     */
    public java.lang.String getCreatedBy() {
        return createdBy;
    }


    /**
     * Sets the createdBy value for this SwitchingOrder.
     * 
     * @param createdBy
     */
    public void setCreatedBy(java.lang.String createdBy) {
        this.createdBy = createdBy;
    }


    /**
     * Gets the createdDateTime value for this SwitchingOrder.
     * 
     * @return createdDateTime
     */
    public java.util.Calendar getCreatedDateTime() {
        return createdDateTime;
    }


    /**
     * Sets the createdDateTime value for this SwitchingOrder.
     * 
     * @param createdDateTime
     */
    public void setCreatedDateTime(java.util.Calendar createdDateTime) {
        this.createdDateTime = createdDateTime;
    }


    /**
     * Gets the checkedBy value for this SwitchingOrder.
     * 
     * @return checkedBy
     */
    public java.lang.String getCheckedBy() {
        return checkedBy;
    }


    /**
     * Sets the checkedBy value for this SwitchingOrder.
     * 
     * @param checkedBy
     */
    public void setCheckedBy(java.lang.String checkedBy) {
        this.checkedBy = checkedBy;
    }


    /**
     * Gets the checkedDateTime value for this SwitchingOrder.
     * 
     * @return checkedDateTime
     */
    public java.util.Calendar getCheckedDateTime() {
        return checkedDateTime;
    }


    /**
     * Sets the checkedDateTime value for this SwitchingOrder.
     * 
     * @param checkedDateTime
     */
    public void setCheckedDateTime(java.util.Calendar checkedDateTime) {
        this.checkedDateTime = checkedDateTime;
    }


    /**
     * Gets the releasedBy value for this SwitchingOrder.
     * 
     * @return releasedBy
     */
    public java.lang.String getReleasedBy() {
        return releasedBy;
    }


    /**
     * Sets the releasedBy value for this SwitchingOrder.
     * 
     * @param releasedBy
     */
    public void setReleasedBy(java.lang.String releasedBy) {
        this.releasedBy = releasedBy;
    }


    /**
     * Gets the releasedDateTime value for this SwitchingOrder.
     * 
     * @return releasedDateTime
     */
    public java.util.Calendar getReleasedDateTime() {
        return releasedDateTime;
    }


    /**
     * Sets the releasedDateTime value for this SwitchingOrder.
     * 
     * @param releasedDateTime
     */
    public void setReleasedDateTime(java.util.Calendar releasedDateTime) {
        this.releasedDateTime = releasedDateTime;
    }


    /**
     * Gets the lastModifiedDateTime value for this SwitchingOrder.
     * 
     * @return lastModifiedDateTime
     */
    public java.util.Calendar getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }


    /**
     * Sets the lastModifiedDateTime value for this SwitchingOrder.
     * 
     * @param lastModifiedDateTime
     */
    public void setLastModifiedDateTime(java.util.Calendar lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }


    /**
     * Gets the documentStatus value for this SwitchingOrder.
     * 
     * @return documentStatus
     */
    public java.lang.String getDocumentStatus() {
        return documentStatus;
    }


    /**
     * Sets the documentStatus value for this SwitchingOrder.
     * 
     * @param documentStatus
     */
    public void setDocumentStatus(java.lang.String documentStatus) {
        this.documentStatus = documentStatus;
    }


    /**
     * Gets the switchingSchedule value for this SwitchingOrder.
     * 
     * @return switchingSchedule
     */
    public com.cannontech.multispeak.deploy.service.SwitchingSchedule getSwitchingSchedule() {
        return switchingSchedule;
    }


    /**
     * Sets the switchingSchedule value for this SwitchingOrder.
     * 
     * @param switchingSchedule
     */
    public void setSwitchingSchedule(com.cannontech.multispeak.deploy.service.SwitchingSchedule switchingSchedule) {
        this.switchingSchedule = switchingSchedule;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SwitchingOrder)) return false;
        SwitchingOrder other = (SwitchingOrder) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.purpose==null && other.getPurpose()==null) || 
             (this.purpose!=null &&
              this.purpose.equals(other.getPurpose()))) &&
            ((this.revision==null && other.getRevision()==null) || 
             (this.revision!=null &&
              this.revision.equals(other.getRevision()))) &&
            ((this.requestedBy==null && other.getRequestedBy()==null) || 
             (this.requestedBy!=null &&
              this.requestedBy.equals(other.getRequestedBy()))) &&
            ((this.requestedDateTime==null && other.getRequestedDateTime()==null) || 
             (this.requestedDateTime!=null &&
              this.requestedDateTime.equals(other.getRequestedDateTime()))) &&
            ((this.createdBy==null && other.getCreatedBy()==null) || 
             (this.createdBy!=null &&
              this.createdBy.equals(other.getCreatedBy()))) &&
            ((this.createdDateTime==null && other.getCreatedDateTime()==null) || 
             (this.createdDateTime!=null &&
              this.createdDateTime.equals(other.getCreatedDateTime()))) &&
            ((this.checkedBy==null && other.getCheckedBy()==null) || 
             (this.checkedBy!=null &&
              this.checkedBy.equals(other.getCheckedBy()))) &&
            ((this.checkedDateTime==null && other.getCheckedDateTime()==null) || 
             (this.checkedDateTime!=null &&
              this.checkedDateTime.equals(other.getCheckedDateTime()))) &&
            ((this.releasedBy==null && other.getReleasedBy()==null) || 
             (this.releasedBy!=null &&
              this.releasedBy.equals(other.getReleasedBy()))) &&
            ((this.releasedDateTime==null && other.getReleasedDateTime()==null) || 
             (this.releasedDateTime!=null &&
              this.releasedDateTime.equals(other.getReleasedDateTime()))) &&
            ((this.lastModifiedDateTime==null && other.getLastModifiedDateTime()==null) || 
             (this.lastModifiedDateTime!=null &&
              this.lastModifiedDateTime.equals(other.getLastModifiedDateTime()))) &&
            ((this.documentStatus==null && other.getDocumentStatus()==null) || 
             (this.documentStatus!=null &&
              this.documentStatus.equals(other.getDocumentStatus()))) &&
            ((this.switchingSchedule==null && other.getSwitchingSchedule()==null) || 
             (this.switchingSchedule!=null &&
              this.switchingSchedule.equals(other.getSwitchingSchedule())));
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
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getPurpose() != null) {
            _hashCode += getPurpose().hashCode();
        }
        if (getRevision() != null) {
            _hashCode += getRevision().hashCode();
        }
        if (getRequestedBy() != null) {
            _hashCode += getRequestedBy().hashCode();
        }
        if (getRequestedDateTime() != null) {
            _hashCode += getRequestedDateTime().hashCode();
        }
        if (getCreatedBy() != null) {
            _hashCode += getCreatedBy().hashCode();
        }
        if (getCreatedDateTime() != null) {
            _hashCode += getCreatedDateTime().hashCode();
        }
        if (getCheckedBy() != null) {
            _hashCode += getCheckedBy().hashCode();
        }
        if (getCheckedDateTime() != null) {
            _hashCode += getCheckedDateTime().hashCode();
        }
        if (getReleasedBy() != null) {
            _hashCode += getReleasedBy().hashCode();
        }
        if (getReleasedDateTime() != null) {
            _hashCode += getReleasedDateTime().hashCode();
        }
        if (getLastModifiedDateTime() != null) {
            _hashCode += getLastModifiedDateTime().hashCode();
        }
        if (getDocumentStatus() != null) {
            _hashCode += getDocumentStatus().hashCode();
        }
        if (getSwitchingSchedule() != null) {
            _hashCode += getSwitchingSchedule().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SwitchingOrder.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingOrder"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField.setFieldName("purpose");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "purpose"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createdBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "createdBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createdDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "createdDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "checkedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "checkedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("releasedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "releasedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("releasedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "releasedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastModifiedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "lastModifiedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "documentStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("switchingSchedule");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingSchedule"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchingSchedule"));
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
