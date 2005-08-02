/**
 * Substation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Substation  extends com.cannontech.multispeak.MspElectricPoint  implements java.io.Serializable {
    private java.lang.String zMin;
    private java.lang.String zMax;
    private com.cannontech.multispeak.ZUnit units;
    private com.cannontech.multispeak.ComplexNum posSeqZ;
    private com.cannontech.multispeak.ComplexNum zeroSeqZ;
    private com.cannontech.multispeak.ComplexNum negSeqZ;
    private java.lang.Float busVolts;
    private java.lang.Float ohGndZ;
    private java.lang.Float ugGndZ;
    private java.lang.Float nomVolts;
    private java.lang.Boolean ldAolloc;
    private com.cannontech.multispeak.LdCon ldCon;
    private java.lang.Boolean isRegulated;
    private com.cannontech.multispeak.ArrayOfFeederObject feederList;
    private java.lang.String name;

    public Substation() {
    }

    public Substation(
           java.lang.String zMin,
           java.lang.String zMax,
           com.cannontech.multispeak.ZUnit units,
           com.cannontech.multispeak.ComplexNum posSeqZ,
           com.cannontech.multispeak.ComplexNum zeroSeqZ,
           com.cannontech.multispeak.ComplexNum negSeqZ,
           java.lang.Float busVolts,
           java.lang.Float ohGndZ,
           java.lang.Float ugGndZ,
           java.lang.Float nomVolts,
           java.lang.Boolean ldAolloc,
           com.cannontech.multispeak.LdCon ldCon,
           java.lang.Boolean isRegulated,
           com.cannontech.multispeak.ArrayOfFeederObject feederList,
           java.lang.String name) {
           this.zMin = zMin;
           this.zMax = zMax;
           this.units = units;
           this.posSeqZ = posSeqZ;
           this.zeroSeqZ = zeroSeqZ;
           this.negSeqZ = negSeqZ;
           this.busVolts = busVolts;
           this.ohGndZ = ohGndZ;
           this.ugGndZ = ugGndZ;
           this.nomVolts = nomVolts;
           this.ldAolloc = ldAolloc;
           this.ldCon = ldCon;
           this.isRegulated = isRegulated;
           this.feederList = feederList;
           this.name = name;
    }


    /**
     * Gets the zMin value for this Substation.
     * 
     * @return zMin
     */
    public java.lang.String getZMin() {
        return zMin;
    }


    /**
     * Sets the zMin value for this Substation.
     * 
     * @param zMin
     */
    public void setZMin(java.lang.String zMin) {
        this.zMin = zMin;
    }


    /**
     * Gets the zMax value for this Substation.
     * 
     * @return zMax
     */
    public java.lang.String getZMax() {
        return zMax;
    }


    /**
     * Sets the zMax value for this Substation.
     * 
     * @param zMax
     */
    public void setZMax(java.lang.String zMax) {
        this.zMax = zMax;
    }


    /**
     * Gets the units value for this Substation.
     * 
     * @return units
     */
    public com.cannontech.multispeak.ZUnit getUnits() {
        return units;
    }


    /**
     * Sets the units value for this Substation.
     * 
     * @param units
     */
    public void setUnits(com.cannontech.multispeak.ZUnit units) {
        this.units = units;
    }


    /**
     * Gets the posSeqZ value for this Substation.
     * 
     * @return posSeqZ
     */
    public com.cannontech.multispeak.ComplexNum getPosSeqZ() {
        return posSeqZ;
    }


    /**
     * Sets the posSeqZ value for this Substation.
     * 
     * @param posSeqZ
     */
    public void setPosSeqZ(com.cannontech.multispeak.ComplexNum posSeqZ) {
        this.posSeqZ = posSeqZ;
    }


    /**
     * Gets the zeroSeqZ value for this Substation.
     * 
     * @return zeroSeqZ
     */
    public com.cannontech.multispeak.ComplexNum getZeroSeqZ() {
        return zeroSeqZ;
    }


    /**
     * Sets the zeroSeqZ value for this Substation.
     * 
     * @param zeroSeqZ
     */
    public void setZeroSeqZ(com.cannontech.multispeak.ComplexNum zeroSeqZ) {
        this.zeroSeqZ = zeroSeqZ;
    }


    /**
     * Gets the negSeqZ value for this Substation.
     * 
     * @return negSeqZ
     */
    public com.cannontech.multispeak.ComplexNum getNegSeqZ() {
        return negSeqZ;
    }


    /**
     * Sets the negSeqZ value for this Substation.
     * 
     * @param negSeqZ
     */
    public void setNegSeqZ(com.cannontech.multispeak.ComplexNum negSeqZ) {
        this.negSeqZ = negSeqZ;
    }


    /**
     * Gets the busVolts value for this Substation.
     * 
     * @return busVolts
     */
    public java.lang.Float getBusVolts() {
        return busVolts;
    }


    /**
     * Sets the busVolts value for this Substation.
     * 
     * @param busVolts
     */
    public void setBusVolts(java.lang.Float busVolts) {
        this.busVolts = busVolts;
    }


    /**
     * Gets the ohGndZ value for this Substation.
     * 
     * @return ohGndZ
     */
    public java.lang.Float getOhGndZ() {
        return ohGndZ;
    }


    /**
     * Sets the ohGndZ value for this Substation.
     * 
     * @param ohGndZ
     */
    public void setOhGndZ(java.lang.Float ohGndZ) {
        this.ohGndZ = ohGndZ;
    }


    /**
     * Gets the ugGndZ value for this Substation.
     * 
     * @return ugGndZ
     */
    public java.lang.Float getUgGndZ() {
        return ugGndZ;
    }


    /**
     * Sets the ugGndZ value for this Substation.
     * 
     * @param ugGndZ
     */
    public void setUgGndZ(java.lang.Float ugGndZ) {
        this.ugGndZ = ugGndZ;
    }


    /**
     * Gets the nomVolts value for this Substation.
     * 
     * @return nomVolts
     */
    public java.lang.Float getNomVolts() {
        return nomVolts;
    }


    /**
     * Sets the nomVolts value for this Substation.
     * 
     * @param nomVolts
     */
    public void setNomVolts(java.lang.Float nomVolts) {
        this.nomVolts = nomVolts;
    }


    /**
     * Gets the ldAolloc value for this Substation.
     * 
     * @return ldAolloc
     */
    public java.lang.Boolean getLdAolloc() {
        return ldAolloc;
    }


    /**
     * Sets the ldAolloc value for this Substation.
     * 
     * @param ldAolloc
     */
    public void setLdAolloc(java.lang.Boolean ldAolloc) {
        this.ldAolloc = ldAolloc;
    }


    /**
     * Gets the ldCon value for this Substation.
     * 
     * @return ldCon
     */
    public com.cannontech.multispeak.LdCon getLdCon() {
        return ldCon;
    }


    /**
     * Sets the ldCon value for this Substation.
     * 
     * @param ldCon
     */
    public void setLdCon(com.cannontech.multispeak.LdCon ldCon) {
        this.ldCon = ldCon;
    }


    /**
     * Gets the isRegulated value for this Substation.
     * 
     * @return isRegulated
     */
    public java.lang.Boolean getIsRegulated() {
        return isRegulated;
    }


    /**
     * Sets the isRegulated value for this Substation.
     * 
     * @param isRegulated
     */
    public void setIsRegulated(java.lang.Boolean isRegulated) {
        this.isRegulated = isRegulated;
    }


    /**
     * Gets the feederList value for this Substation.
     * 
     * @return feederList
     */
    public com.cannontech.multispeak.ArrayOfFeederObject getFeederList() {
        return feederList;
    }


    /**
     * Sets the feederList value for this Substation.
     * 
     * @param feederList
     */
    public void setFeederList(com.cannontech.multispeak.ArrayOfFeederObject feederList) {
        this.feederList = feederList;
    }


    /**
     * Gets the name value for this Substation.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this Substation.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Substation)) return false;
        Substation other = (Substation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.zMin==null && other.getZMin()==null) || 
             (this.zMin!=null &&
              this.zMin.equals(other.getZMin()))) &&
            ((this.zMax==null && other.getZMax()==null) || 
             (this.zMax!=null &&
              this.zMax.equals(other.getZMax()))) &&
            ((this.units==null && other.getUnits()==null) || 
             (this.units!=null &&
              this.units.equals(other.getUnits()))) &&
            ((this.posSeqZ==null && other.getPosSeqZ()==null) || 
             (this.posSeqZ!=null &&
              this.posSeqZ.equals(other.getPosSeqZ()))) &&
            ((this.zeroSeqZ==null && other.getZeroSeqZ()==null) || 
             (this.zeroSeqZ!=null &&
              this.zeroSeqZ.equals(other.getZeroSeqZ()))) &&
            ((this.negSeqZ==null && other.getNegSeqZ()==null) || 
             (this.negSeqZ!=null &&
              this.negSeqZ.equals(other.getNegSeqZ()))) &&
            ((this.busVolts==null && other.getBusVolts()==null) || 
             (this.busVolts!=null &&
              this.busVolts.equals(other.getBusVolts()))) &&
            ((this.ohGndZ==null && other.getOhGndZ()==null) || 
             (this.ohGndZ!=null &&
              this.ohGndZ.equals(other.getOhGndZ()))) &&
            ((this.ugGndZ==null && other.getUgGndZ()==null) || 
             (this.ugGndZ!=null &&
              this.ugGndZ.equals(other.getUgGndZ()))) &&
            ((this.nomVolts==null && other.getNomVolts()==null) || 
             (this.nomVolts!=null &&
              this.nomVolts.equals(other.getNomVolts()))) &&
            ((this.ldAolloc==null && other.getLdAolloc()==null) || 
             (this.ldAolloc!=null &&
              this.ldAolloc.equals(other.getLdAolloc()))) &&
            ((this.ldCon==null && other.getLdCon()==null) || 
             (this.ldCon!=null &&
              this.ldCon.equals(other.getLdCon()))) &&
            ((this.isRegulated==null && other.getIsRegulated()==null) || 
             (this.isRegulated!=null &&
              this.isRegulated.equals(other.getIsRegulated()))) &&
            ((this.feederList==null && other.getFeederList()==null) || 
             (this.feederList!=null &&
              this.feederList.equals(other.getFeederList()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName())));
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
        if (getZMin() != null) {
            _hashCode += getZMin().hashCode();
        }
        if (getZMax() != null) {
            _hashCode += getZMax().hashCode();
        }
        if (getUnits() != null) {
            _hashCode += getUnits().hashCode();
        }
        if (getPosSeqZ() != null) {
            _hashCode += getPosSeqZ().hashCode();
        }
        if (getZeroSeqZ() != null) {
            _hashCode += getZeroSeqZ().hashCode();
        }
        if (getNegSeqZ() != null) {
            _hashCode += getNegSeqZ().hashCode();
        }
        if (getBusVolts() != null) {
            _hashCode += getBusVolts().hashCode();
        }
        if (getOhGndZ() != null) {
            _hashCode += getOhGndZ().hashCode();
        }
        if (getUgGndZ() != null) {
            _hashCode += getUgGndZ().hashCode();
        }
        if (getNomVolts() != null) {
            _hashCode += getNomVolts().hashCode();
        }
        if (getLdAolloc() != null) {
            _hashCode += getLdAolloc().hashCode();
        }
        if (getLdCon() != null) {
            _hashCode += getLdCon().hashCode();
        }
        if (getIsRegulated() != null) {
            _hashCode += getIsRegulated().hashCode();
        }
        if (getFeederList() != null) {
            _hashCode += getFeederList().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Substation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ZMin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zMin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ZMax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zMax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("units");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "units"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zUnit"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posSeqZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "posSeqZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zeroSeqZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zeroSeqZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("negSeqZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "negSeqZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "complexNum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("busVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "busVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ohGndZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ohGndZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ugGndZ");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ugGndZ"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nomVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldAolloc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldAolloc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldCon");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldCon"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isRegulated");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isRegulated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feederList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feederList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfFeederObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "name"));
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
