/**
 * LoadSection.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class LoadSection  implements java.io.Serializable {
    private java.lang.String loadGroup;
    private java.lang.String loadDesc;
    private java.lang.String loadZone;
    private com.cannontech.multispeak.LoadDistr loadDistr;
    private java.lang.Float loadGrowth;
    private com.cannontech.multispeak.LoadInterruptibleType interruptibleType;
    private com.cannontech.multispeak.ArrayOfAllocatedLoad allocated;

    public LoadSection() {
    }

    public LoadSection(
           java.lang.String loadGroup,
           java.lang.String loadDesc,
           java.lang.String loadZone,
           com.cannontech.multispeak.LoadDistr loadDistr,
           java.lang.Float loadGrowth,
           com.cannontech.multispeak.LoadInterruptibleType interruptibleType,
           com.cannontech.multispeak.ArrayOfAllocatedLoad allocated) {
           this.loadGroup = loadGroup;
           this.loadDesc = loadDesc;
           this.loadZone = loadZone;
           this.loadDistr = loadDistr;
           this.loadGrowth = loadGrowth;
           this.interruptibleType = interruptibleType;
           this.allocated = allocated;
    }


    /**
     * Gets the loadGroup value for this LoadSection.
     * 
     * @return loadGroup
     */
    public java.lang.String getLoadGroup() {
        return loadGroup;
    }


    /**
     * Sets the loadGroup value for this LoadSection.
     * 
     * @param loadGroup
     */
    public void setLoadGroup(java.lang.String loadGroup) {
        this.loadGroup = loadGroup;
    }


    /**
     * Gets the loadDesc value for this LoadSection.
     * 
     * @return loadDesc
     */
    public java.lang.String getLoadDesc() {
        return loadDesc;
    }


    /**
     * Sets the loadDesc value for this LoadSection.
     * 
     * @param loadDesc
     */
    public void setLoadDesc(java.lang.String loadDesc) {
        this.loadDesc = loadDesc;
    }


    /**
     * Gets the loadZone value for this LoadSection.
     * 
     * @return loadZone
     */
    public java.lang.String getLoadZone() {
        return loadZone;
    }


    /**
     * Sets the loadZone value for this LoadSection.
     * 
     * @param loadZone
     */
    public void setLoadZone(java.lang.String loadZone) {
        this.loadZone = loadZone;
    }


    /**
     * Gets the loadDistr value for this LoadSection.
     * 
     * @return loadDistr
     */
    public com.cannontech.multispeak.LoadDistr getLoadDistr() {
        return loadDistr;
    }


    /**
     * Sets the loadDistr value for this LoadSection.
     * 
     * @param loadDistr
     */
    public void setLoadDistr(com.cannontech.multispeak.LoadDistr loadDistr) {
        this.loadDistr = loadDistr;
    }


    /**
     * Gets the loadGrowth value for this LoadSection.
     * 
     * @return loadGrowth
     */
    public java.lang.Float getLoadGrowth() {
        return loadGrowth;
    }


    /**
     * Sets the loadGrowth value for this LoadSection.
     * 
     * @param loadGrowth
     */
    public void setLoadGrowth(java.lang.Float loadGrowth) {
        this.loadGrowth = loadGrowth;
    }


    /**
     * Gets the interruptibleType value for this LoadSection.
     * 
     * @return interruptibleType
     */
    public com.cannontech.multispeak.LoadInterruptibleType getInterruptibleType() {
        return interruptibleType;
    }


    /**
     * Sets the interruptibleType value for this LoadSection.
     * 
     * @param interruptibleType
     */
    public void setInterruptibleType(com.cannontech.multispeak.LoadInterruptibleType interruptibleType) {
        this.interruptibleType = interruptibleType;
    }


    /**
     * Gets the allocated value for this LoadSection.
     * 
     * @return allocated
     */
    public com.cannontech.multispeak.ArrayOfAllocatedLoad getAllocated() {
        return allocated;
    }


    /**
     * Sets the allocated value for this LoadSection.
     * 
     * @param allocated
     */
    public void setAllocated(com.cannontech.multispeak.ArrayOfAllocatedLoad allocated) {
        this.allocated = allocated;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LoadSection)) return false;
        LoadSection other = (LoadSection) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.loadGroup==null && other.getLoadGroup()==null) || 
             (this.loadGroup!=null &&
              this.loadGroup.equals(other.getLoadGroup()))) &&
            ((this.loadDesc==null && other.getLoadDesc()==null) || 
             (this.loadDesc!=null &&
              this.loadDesc.equals(other.getLoadDesc()))) &&
            ((this.loadZone==null && other.getLoadZone()==null) || 
             (this.loadZone!=null &&
              this.loadZone.equals(other.getLoadZone()))) &&
            ((this.loadDistr==null && other.getLoadDistr()==null) || 
             (this.loadDistr!=null &&
              this.loadDistr.equals(other.getLoadDistr()))) &&
            ((this.loadGrowth==null && other.getLoadGrowth()==null) || 
             (this.loadGrowth!=null &&
              this.loadGrowth.equals(other.getLoadGrowth()))) &&
            ((this.interruptibleType==null && other.getInterruptibleType()==null) || 
             (this.interruptibleType!=null &&
              this.interruptibleType.equals(other.getInterruptibleType()))) &&
            ((this.allocated==null && other.getAllocated()==null) || 
             (this.allocated!=null &&
              this.allocated.equals(other.getAllocated())));
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
        if (getLoadGroup() != null) {
            _hashCode += getLoadGroup().hashCode();
        }
        if (getLoadDesc() != null) {
            _hashCode += getLoadDesc().hashCode();
        }
        if (getLoadZone() != null) {
            _hashCode += getLoadZone().hashCode();
        }
        if (getLoadDistr() != null) {
            _hashCode += getLoadDistr().hashCode();
        }
        if (getLoadGrowth() != null) {
            _hashCode += getLoadGrowth().hashCode();
        }
        if (getInterruptibleType() != null) {
            _hashCode += getInterruptibleType().hashCode();
        }
        if (getAllocated() != null) {
            _hashCode += getAllocated().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LoadSection.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadSection"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadZone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadZone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadDistr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadDistr"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadGrowth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadGrowth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interruptibleType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "interruptibleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadInterruptibleType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocated");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfAllocatedLoad"));
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
