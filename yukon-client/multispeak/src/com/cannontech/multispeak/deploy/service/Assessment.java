/**
 * Assessment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Assessment  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String category;

    private java.util.Calendar created;

    private java.lang.String createdBy;

    private java.lang.String closedBy;

    private java.util.Calendar closedOn;

    private java.lang.String elementName;

    private java.lang.String elementType;

    private java.lang.Float estimated;

    private java.lang.String eventID;

    private java.lang.String jobNumber;

    private com.cannontech.multispeak.deploy.service.Base64Image photo;

    private java.lang.String elementID;

    public Assessment() {
    }

    public Assessment(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String category,
           java.util.Calendar created,
           java.lang.String createdBy,
           java.lang.String closedBy,
           java.util.Calendar closedOn,
           java.lang.String elementName,
           java.lang.String elementType,
           java.lang.Float estimated,
           java.lang.String eventID,
           java.lang.String jobNumber,
           com.cannontech.multispeak.deploy.service.Base64Image photo,
           java.lang.String elementID) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.category = category;
        this.created = created;
        this.createdBy = createdBy;
        this.closedBy = closedBy;
        this.closedOn = closedOn;
        this.elementName = elementName;
        this.elementType = elementType;
        this.estimated = estimated;
        this.eventID = eventID;
        this.jobNumber = jobNumber;
        this.photo = photo;
        this.elementID = elementID;
    }


    /**
     * Gets the category value for this Assessment.
     * 
     * @return category
     */
    public java.lang.String getCategory() {
        return category;
    }


    /**
     * Sets the category value for this Assessment.
     * 
     * @param category
     */
    public void setCategory(java.lang.String category) {
        this.category = category;
    }


    /**
     * Gets the created value for this Assessment.
     * 
     * @return created
     */
    public java.util.Calendar getCreated() {
        return created;
    }


    /**
     * Sets the created value for this Assessment.
     * 
     * @param created
     */
    public void setCreated(java.util.Calendar created) {
        this.created = created;
    }


    /**
     * Gets the createdBy value for this Assessment.
     * 
     * @return createdBy
     */
    public java.lang.String getCreatedBy() {
        return createdBy;
    }


    /**
     * Sets the createdBy value for this Assessment.
     * 
     * @param createdBy
     */
    public void setCreatedBy(java.lang.String createdBy) {
        this.createdBy = createdBy;
    }


    /**
     * Gets the closedBy value for this Assessment.
     * 
     * @return closedBy
     */
    public java.lang.String getClosedBy() {
        return closedBy;
    }


    /**
     * Sets the closedBy value for this Assessment.
     * 
     * @param closedBy
     */
    public void setClosedBy(java.lang.String closedBy) {
        this.closedBy = closedBy;
    }


    /**
     * Gets the closedOn value for this Assessment.
     * 
     * @return closedOn
     */
    public java.util.Calendar getClosedOn() {
        return closedOn;
    }


    /**
     * Sets the closedOn value for this Assessment.
     * 
     * @param closedOn
     */
    public void setClosedOn(java.util.Calendar closedOn) {
        this.closedOn = closedOn;
    }


    /**
     * Gets the elementName value for this Assessment.
     * 
     * @return elementName
     */
    public java.lang.String getElementName() {
        return elementName;
    }


    /**
     * Sets the elementName value for this Assessment.
     * 
     * @param elementName
     */
    public void setElementName(java.lang.String elementName) {
        this.elementName = elementName;
    }


    /**
     * Gets the elementType value for this Assessment.
     * 
     * @return elementType
     */
    public java.lang.String getElementType() {
        return elementType;
    }


    /**
     * Sets the elementType value for this Assessment.
     * 
     * @param elementType
     */
    public void setElementType(java.lang.String elementType) {
        this.elementType = elementType;
    }


    /**
     * Gets the estimated value for this Assessment.
     * 
     * @return estimated
     */
    public java.lang.Float getEstimated() {
        return estimated;
    }


    /**
     * Sets the estimated value for this Assessment.
     * 
     * @param estimated
     */
    public void setEstimated(java.lang.Float estimated) {
        this.estimated = estimated;
    }


    /**
     * Gets the eventID value for this Assessment.
     * 
     * @return eventID
     */
    public java.lang.String getEventID() {
        return eventID;
    }


    /**
     * Sets the eventID value for this Assessment.
     * 
     * @param eventID
     */
    public void setEventID(java.lang.String eventID) {
        this.eventID = eventID;
    }


    /**
     * Gets the jobNumber value for this Assessment.
     * 
     * @return jobNumber
     */
    public java.lang.String getJobNumber() {
        return jobNumber;
    }


    /**
     * Sets the jobNumber value for this Assessment.
     * 
     * @param jobNumber
     */
    public void setJobNumber(java.lang.String jobNumber) {
        this.jobNumber = jobNumber;
    }


    /**
     * Gets the photo value for this Assessment.
     * 
     * @return photo
     */
    public com.cannontech.multispeak.deploy.service.Base64Image getPhoto() {
        return photo;
    }


    /**
     * Sets the photo value for this Assessment.
     * 
     * @param photo
     */
    public void setPhoto(com.cannontech.multispeak.deploy.service.Base64Image photo) {
        this.photo = photo;
    }


    /**
     * Gets the elementID value for this Assessment.
     * 
     * @return elementID
     */
    public java.lang.String getElementID() {
        return elementID;
    }


    /**
     * Sets the elementID value for this Assessment.
     * 
     * @param elementID
     */
    public void setElementID(java.lang.String elementID) {
        this.elementID = elementID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Assessment)) return false;
        Assessment other = (Assessment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.category==null && other.getCategory()==null) || 
             (this.category!=null &&
              this.category.equals(other.getCategory()))) &&
            ((this.created==null && other.getCreated()==null) || 
             (this.created!=null &&
              this.created.equals(other.getCreated()))) &&
            ((this.createdBy==null && other.getCreatedBy()==null) || 
             (this.createdBy!=null &&
              this.createdBy.equals(other.getCreatedBy()))) &&
            ((this.closedBy==null && other.getClosedBy()==null) || 
             (this.closedBy!=null &&
              this.closedBy.equals(other.getClosedBy()))) &&
            ((this.closedOn==null && other.getClosedOn()==null) || 
             (this.closedOn!=null &&
              this.closedOn.equals(other.getClosedOn()))) &&
            ((this.elementName==null && other.getElementName()==null) || 
             (this.elementName!=null &&
              this.elementName.equals(other.getElementName()))) &&
            ((this.elementType==null && other.getElementType()==null) || 
             (this.elementType!=null &&
              this.elementType.equals(other.getElementType()))) &&
            ((this.estimated==null && other.getEstimated()==null) || 
             (this.estimated!=null &&
              this.estimated.equals(other.getEstimated()))) &&
            ((this.eventID==null && other.getEventID()==null) || 
             (this.eventID!=null &&
              this.eventID.equals(other.getEventID()))) &&
            ((this.jobNumber==null && other.getJobNumber()==null) || 
             (this.jobNumber!=null &&
              this.jobNumber.equals(other.getJobNumber()))) &&
            ((this.photo==null && other.getPhoto()==null) || 
             (this.photo!=null &&
              this.photo.equals(other.getPhoto()))) &&
            ((this.elementID==null && other.getElementID()==null) || 
             (this.elementID!=null &&
              this.elementID.equals(other.getElementID())));
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
        if (getCategory() != null) {
            _hashCode += getCategory().hashCode();
        }
        if (getCreated() != null) {
            _hashCode += getCreated().hashCode();
        }
        if (getCreatedBy() != null) {
            _hashCode += getCreatedBy().hashCode();
        }
        if (getClosedBy() != null) {
            _hashCode += getClosedBy().hashCode();
        }
        if (getClosedOn() != null) {
            _hashCode += getClosedOn().hashCode();
        }
        if (getElementName() != null) {
            _hashCode += getElementName().hashCode();
        }
        if (getElementType() != null) {
            _hashCode += getElementType().hashCode();
        }
        if (getEstimated() != null) {
            _hashCode += getEstimated().hashCode();
        }
        if (getEventID() != null) {
            _hashCode += getEventID().hashCode();
        }
        if (getJobNumber() != null) {
            _hashCode += getJobNumber().hashCode();
        }
        if (getPhoto() != null) {
            _hashCode += getPhoto().hashCode();
        }
        if (getElementID() != null) {
            _hashCode += getElementID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Assessment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "assessment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("category");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "category"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("created");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "created"));
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
        elemField.setFieldName("closedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "closedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closedOn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "closedOn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimated");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "estimated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventID"));
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
        elemField.setFieldName("photo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "photo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "base64Image"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elementID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elementID"));
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
