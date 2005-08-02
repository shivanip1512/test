/**
 * Pole.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Pole  extends com.cannontech.multispeak.MspPointObject  implements java.io.Serializable {
    private java.lang.Long poleHeight;
    private com.cannontech.multispeak.PoleClass poleClass;
    private com.cannontech.multispeak.PoleType poleType;
    private java.lang.String poleNumber;
    private java.lang.String manufacturer;
    private java.util.Date mfgDate;
    private java.lang.String owner;
    private com.cannontech.multispeak.Treatment treatment;
    private java.util.Date inspDate;
    private com.cannontech.multispeak.PoleUseCode poleUse;
    private com.cannontech.multispeak.ArrayOfJointUse jointUseList;

    public Pole() {
    }

    public Pole(
           java.lang.Long poleHeight,
           com.cannontech.multispeak.PoleClass poleClass,
           com.cannontech.multispeak.PoleType poleType,
           java.lang.String poleNumber,
           java.lang.String manufacturer,
           java.util.Date mfgDate,
           java.lang.String owner,
           com.cannontech.multispeak.Treatment treatment,
           java.util.Date inspDate,
           com.cannontech.multispeak.PoleUseCode poleUse,
           com.cannontech.multispeak.ArrayOfJointUse jointUseList) {
           this.poleHeight = poleHeight;
           this.poleClass = poleClass;
           this.poleType = poleType;
           this.poleNumber = poleNumber;
           this.manufacturer = manufacturer;
           this.mfgDate = mfgDate;
           this.owner = owner;
           this.treatment = treatment;
           this.inspDate = inspDate;
           this.poleUse = poleUse;
           this.jointUseList = jointUseList;
    }


    /**
     * Gets the poleHeight value for this Pole.
     * 
     * @return poleHeight
     */
    public java.lang.Long getPoleHeight() {
        return poleHeight;
    }


    /**
     * Sets the poleHeight value for this Pole.
     * 
     * @param poleHeight
     */
    public void setPoleHeight(java.lang.Long poleHeight) {
        this.poleHeight = poleHeight;
    }


    /**
     * Gets the poleClass value for this Pole.
     * 
     * @return poleClass
     */
    public com.cannontech.multispeak.PoleClass getPoleClass() {
        return poleClass;
    }


    /**
     * Sets the poleClass value for this Pole.
     * 
     * @param poleClass
     */
    public void setPoleClass(com.cannontech.multispeak.PoleClass poleClass) {
        this.poleClass = poleClass;
    }


    /**
     * Gets the poleType value for this Pole.
     * 
     * @return poleType
     */
    public com.cannontech.multispeak.PoleType getPoleType() {
        return poleType;
    }


    /**
     * Sets the poleType value for this Pole.
     * 
     * @param poleType
     */
    public void setPoleType(com.cannontech.multispeak.PoleType poleType) {
        this.poleType = poleType;
    }


    /**
     * Gets the poleNumber value for this Pole.
     * 
     * @return poleNumber
     */
    public java.lang.String getPoleNumber() {
        return poleNumber;
    }


    /**
     * Sets the poleNumber value for this Pole.
     * 
     * @param poleNumber
     */
    public void setPoleNumber(java.lang.String poleNumber) {
        this.poleNumber = poleNumber;
    }


    /**
     * Gets the manufacturer value for this Pole.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this Pole.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the mfgDate value for this Pole.
     * 
     * @return mfgDate
     */
    public java.util.Date getMfgDate() {
        return mfgDate;
    }


    /**
     * Sets the mfgDate value for this Pole.
     * 
     * @param mfgDate
     */
    public void setMfgDate(java.util.Date mfgDate) {
        this.mfgDate = mfgDate;
    }


    /**
     * Gets the owner value for this Pole.
     * 
     * @return owner
     */
    public java.lang.String getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this Pole.
     * 
     * @param owner
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    /**
     * Gets the treatment value for this Pole.
     * 
     * @return treatment
     */
    public com.cannontech.multispeak.Treatment getTreatment() {
        return treatment;
    }


    /**
     * Sets the treatment value for this Pole.
     * 
     * @param treatment
     */
    public void setTreatment(com.cannontech.multispeak.Treatment treatment) {
        this.treatment = treatment;
    }


    /**
     * Gets the inspDate value for this Pole.
     * 
     * @return inspDate
     */
    public java.util.Date getInspDate() {
        return inspDate;
    }


    /**
     * Sets the inspDate value for this Pole.
     * 
     * @param inspDate
     */
    public void setInspDate(java.util.Date inspDate) {
        this.inspDate = inspDate;
    }


    /**
     * Gets the poleUse value for this Pole.
     * 
     * @return poleUse
     */
    public com.cannontech.multispeak.PoleUseCode getPoleUse() {
        return poleUse;
    }


    /**
     * Sets the poleUse value for this Pole.
     * 
     * @param poleUse
     */
    public void setPoleUse(com.cannontech.multispeak.PoleUseCode poleUse) {
        this.poleUse = poleUse;
    }


    /**
     * Gets the jointUseList value for this Pole.
     * 
     * @return jointUseList
     */
    public com.cannontech.multispeak.ArrayOfJointUse getJointUseList() {
        return jointUseList;
    }


    /**
     * Sets the jointUseList value for this Pole.
     * 
     * @param jointUseList
     */
    public void setJointUseList(com.cannontech.multispeak.ArrayOfJointUse jointUseList) {
        this.jointUseList = jointUseList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Pole)) return false;
        Pole other = (Pole) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.poleHeight==null && other.getPoleHeight()==null) || 
             (this.poleHeight!=null &&
              this.poleHeight.equals(other.getPoleHeight()))) &&
            ((this.poleClass==null && other.getPoleClass()==null) || 
             (this.poleClass!=null &&
              this.poleClass.equals(other.getPoleClass()))) &&
            ((this.poleType==null && other.getPoleType()==null) || 
             (this.poleType!=null &&
              this.poleType.equals(other.getPoleType()))) &&
            ((this.poleNumber==null && other.getPoleNumber()==null) || 
             (this.poleNumber!=null &&
              this.poleNumber.equals(other.getPoleNumber()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.mfgDate==null && other.getMfgDate()==null) || 
             (this.mfgDate!=null &&
              this.mfgDate.equals(other.getMfgDate()))) &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.treatment==null && other.getTreatment()==null) || 
             (this.treatment!=null &&
              this.treatment.equals(other.getTreatment()))) &&
            ((this.inspDate==null && other.getInspDate()==null) || 
             (this.inspDate!=null &&
              this.inspDate.equals(other.getInspDate()))) &&
            ((this.poleUse==null && other.getPoleUse()==null) || 
             (this.poleUse!=null &&
              this.poleUse.equals(other.getPoleUse()))) &&
            ((this.jointUseList==null && other.getJointUseList()==null) || 
             (this.jointUseList!=null &&
              this.jointUseList.equals(other.getJointUseList())));
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
        if (getPoleHeight() != null) {
            _hashCode += getPoleHeight().hashCode();
        }
        if (getPoleClass() != null) {
            _hashCode += getPoleClass().hashCode();
        }
        if (getPoleType() != null) {
            _hashCode += getPoleType().hashCode();
        }
        if (getPoleNumber() != null) {
            _hashCode += getPoleNumber().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getMfgDate() != null) {
            _hashCode += getMfgDate().hashCode();
        }
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getTreatment() != null) {
            _hashCode += getTreatment().hashCode();
        }
        if (getInspDate() != null) {
            _hashCode += getInspDate().hashCode();
        }
        if (getPoleUse() != null) {
            _hashCode += getPoleUse().hashCode();
        }
        if (getJointUseList() != null) {
            _hashCode += getJointUseList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Pole.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pole"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleClass"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manufacturer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mfgDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mfgDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("treatment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "treatment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "treatment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inspDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inspDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("poleUse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "poleUseCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jointUseList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUseList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfJointUse"));
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
