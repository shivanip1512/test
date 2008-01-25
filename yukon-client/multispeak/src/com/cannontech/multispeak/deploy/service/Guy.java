/**
 * Guy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Guy  extends com.cannontech.multispeak.deploy.service.MspPointObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GuyStatus status;

    private com.cannontech.multispeak.deploy.service.Anchor[] anchorList;

    private com.cannontech.multispeak.deploy.service.GuyGuyType guyType;

    private com.cannontech.multispeak.deploy.service.GuyGuyAgainst guyAgainst;

    private java.lang.Float attachmentHeight;

    public Guy() {
    }

    public Guy(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.GuyStatus status,
           com.cannontech.multispeak.deploy.service.Anchor[] anchorList,
           com.cannontech.multispeak.deploy.service.GuyGuyType guyType,
           com.cannontech.multispeak.deploy.service.GuyGuyAgainst guyAgainst,
           java.lang.Float attachmentHeight) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID);
        this.status = status;
        this.anchorList = anchorList;
        this.guyType = guyType;
        this.guyAgainst = guyAgainst;
        this.attachmentHeight = attachmentHeight;
    }


    /**
     * Gets the status value for this Guy.
     * 
     * @return status
     */
    public com.cannontech.multispeak.deploy.service.GuyStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Guy.
     * 
     * @param status
     */
    public void setStatus(com.cannontech.multispeak.deploy.service.GuyStatus status) {
        this.status = status;
    }


    /**
     * Gets the anchorList value for this Guy.
     * 
     * @return anchorList
     */
    public com.cannontech.multispeak.deploy.service.Anchor[] getAnchorList() {
        return anchorList;
    }


    /**
     * Sets the anchorList value for this Guy.
     * 
     * @param anchorList
     */
    public void setAnchorList(com.cannontech.multispeak.deploy.service.Anchor[] anchorList) {
        this.anchorList = anchorList;
    }


    /**
     * Gets the guyType value for this Guy.
     * 
     * @return guyType
     */
    public com.cannontech.multispeak.deploy.service.GuyGuyType getGuyType() {
        return guyType;
    }


    /**
     * Sets the guyType value for this Guy.
     * 
     * @param guyType
     */
    public void setGuyType(com.cannontech.multispeak.deploy.service.GuyGuyType guyType) {
        this.guyType = guyType;
    }


    /**
     * Gets the guyAgainst value for this Guy.
     * 
     * @return guyAgainst
     */
    public com.cannontech.multispeak.deploy.service.GuyGuyAgainst getGuyAgainst() {
        return guyAgainst;
    }


    /**
     * Sets the guyAgainst value for this Guy.
     * 
     * @param guyAgainst
     */
    public void setGuyAgainst(com.cannontech.multispeak.deploy.service.GuyGuyAgainst guyAgainst) {
        this.guyAgainst = guyAgainst;
    }


    /**
     * Gets the attachmentHeight value for this Guy.
     * 
     * @return attachmentHeight
     */
    public java.lang.Float getAttachmentHeight() {
        return attachmentHeight;
    }


    /**
     * Sets the attachmentHeight value for this Guy.
     * 
     * @param attachmentHeight
     */
    public void setAttachmentHeight(java.lang.Float attachmentHeight) {
        this.attachmentHeight = attachmentHeight;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Guy)) return false;
        Guy other = (Guy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.anchorList==null && other.getAnchorList()==null) || 
             (this.anchorList!=null &&
              java.util.Arrays.equals(this.anchorList, other.getAnchorList()))) &&
            ((this.guyType==null && other.getGuyType()==null) || 
             (this.guyType!=null &&
              this.guyType.equals(other.getGuyType()))) &&
            ((this.guyAgainst==null && other.getGuyAgainst()==null) || 
             (this.guyAgainst!=null &&
              this.guyAgainst.equals(other.getGuyAgainst()))) &&
            ((this.attachmentHeight==null && other.getAttachmentHeight()==null) || 
             (this.attachmentHeight!=null &&
              this.attachmentHeight.equals(other.getAttachmentHeight())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getAnchorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnchorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnchorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGuyType() != null) {
            _hashCode += getGuyType().hashCode();
        }
        if (getGuyAgainst() != null) {
            _hashCode += getGuyAgainst().hashCode();
        }
        if (getAttachmentHeight() != null) {
            _hashCode += getAttachmentHeight().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Guy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "guy"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>status"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("anchorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "anchor"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guyType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "guyType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guyAgainst");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "guyAgainst"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">guy>guyAgainst"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attachmentHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "attachmentHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
