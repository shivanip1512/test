/**
 * BillingAccountLoad.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class BillingAccountLoad  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String accountNumber;
    private com.cannontech.multispeak.EaLoc eaLoc;
    private java.lang.String substation;
    private java.lang.String feeder;
    private com.cannontech.multispeak.PhaseCd phaseCd;
    private java.lang.Float kwhUse;
    private java.lang.Long demand;
    private java.lang.Float hp;
    private java.lang.Long kvarDemand;
    private java.lang.Long stLiteKwh;
    private java.lang.String meterNo;
    private java.lang.String servType;
    private java.lang.String revenueClass;
    private java.lang.Float kwhAdj;
    private java.lang.Long kwAdj;
    private java.lang.Long xfmrKva;
    private java.lang.String gLCode;
    private java.lang.String zone;
    private com.cannontech.multispeak.PointType mapLocation;
    private java.lang.String gridLocation;

    public BillingAccountLoad() {
    }

    public BillingAccountLoad(
           java.lang.String accountNumber,
           com.cannontech.multispeak.EaLoc eaLoc,
           java.lang.String substation,
           java.lang.String feeder,
           com.cannontech.multispeak.PhaseCd phaseCd,
           java.lang.Float kwhUse,
           java.lang.Long demand,
           java.lang.Float hp,
           java.lang.Long kvarDemand,
           java.lang.Long stLiteKwh,
           java.lang.String meterNo,
           java.lang.String servType,
           java.lang.String revenueClass,
           java.lang.Float kwhAdj,
           java.lang.Long kwAdj,
           java.lang.Long xfmrKva,
           java.lang.String gLCode,
           java.lang.String zone,
           com.cannontech.multispeak.PointType mapLocation,
           java.lang.String gridLocation) {
           this.accountNumber = accountNumber;
           this.eaLoc = eaLoc;
           this.substation = substation;
           this.feeder = feeder;
           this.phaseCd = phaseCd;
           this.kwhUse = kwhUse;
           this.demand = demand;
           this.hp = hp;
           this.kvarDemand = kvarDemand;
           this.stLiteKwh = stLiteKwh;
           this.meterNo = meterNo;
           this.servType = servType;
           this.revenueClass = revenueClass;
           this.kwhAdj = kwhAdj;
           this.kwAdj = kwAdj;
           this.xfmrKva = xfmrKva;
           this.gLCode = gLCode;
           this.zone = zone;
           this.mapLocation = mapLocation;
           this.gridLocation = gridLocation;
    }


    /**
     * Gets the accountNumber value for this BillingAccountLoad.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this BillingAccountLoad.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the eaLoc value for this BillingAccountLoad.
     * 
     * @return eaLoc
     */
    public com.cannontech.multispeak.EaLoc getEaLoc() {
        return eaLoc;
    }


    /**
     * Sets the eaLoc value for this BillingAccountLoad.
     * 
     * @param eaLoc
     */
    public void setEaLoc(com.cannontech.multispeak.EaLoc eaLoc) {
        this.eaLoc = eaLoc;
    }


    /**
     * Gets the substation value for this BillingAccountLoad.
     * 
     * @return substation
     */
    public java.lang.String getSubstation() {
        return substation;
    }


    /**
     * Sets the substation value for this BillingAccountLoad.
     * 
     * @param substation
     */
    public void setSubstation(java.lang.String substation) {
        this.substation = substation;
    }


    /**
     * Gets the feeder value for this BillingAccountLoad.
     * 
     * @return feeder
     */
    public java.lang.String getFeeder() {
        return feeder;
    }


    /**
     * Sets the feeder value for this BillingAccountLoad.
     * 
     * @param feeder
     */
    public void setFeeder(java.lang.String feeder) {
        this.feeder = feeder;
    }


    /**
     * Gets the phaseCd value for this BillingAccountLoad.
     * 
     * @return phaseCd
     */
    public com.cannontech.multispeak.PhaseCd getPhaseCd() {
        return phaseCd;
    }


    /**
     * Sets the phaseCd value for this BillingAccountLoad.
     * 
     * @param phaseCd
     */
    public void setPhaseCd(com.cannontech.multispeak.PhaseCd phaseCd) {
        this.phaseCd = phaseCd;
    }


    /**
     * Gets the kwhUse value for this BillingAccountLoad.
     * 
     * @return kwhUse
     */
    public java.lang.Float getKwhUse() {
        return kwhUse;
    }


    /**
     * Sets the kwhUse value for this BillingAccountLoad.
     * 
     * @param kwhUse
     */
    public void setKwhUse(java.lang.Float kwhUse) {
        this.kwhUse = kwhUse;
    }


    /**
     * Gets the demand value for this BillingAccountLoad.
     * 
     * @return demand
     */
    public java.lang.Long getDemand() {
        return demand;
    }


    /**
     * Sets the demand value for this BillingAccountLoad.
     * 
     * @param demand
     */
    public void setDemand(java.lang.Long demand) {
        this.demand = demand;
    }


    /**
     * Gets the hp value for this BillingAccountLoad.
     * 
     * @return hp
     */
    public java.lang.Float getHp() {
        return hp;
    }


    /**
     * Sets the hp value for this BillingAccountLoad.
     * 
     * @param hp
     */
    public void setHp(java.lang.Float hp) {
        this.hp = hp;
    }


    /**
     * Gets the kvarDemand value for this BillingAccountLoad.
     * 
     * @return kvarDemand
     */
    public java.lang.Long getKvarDemand() {
        return kvarDemand;
    }


    /**
     * Sets the kvarDemand value for this BillingAccountLoad.
     * 
     * @param kvarDemand
     */
    public void setKvarDemand(java.lang.Long kvarDemand) {
        this.kvarDemand = kvarDemand;
    }


    /**
     * Gets the stLiteKwh value for this BillingAccountLoad.
     * 
     * @return stLiteKwh
     */
    public java.lang.Long getStLiteKwh() {
        return stLiteKwh;
    }


    /**
     * Sets the stLiteKwh value for this BillingAccountLoad.
     * 
     * @param stLiteKwh
     */
    public void setStLiteKwh(java.lang.Long stLiteKwh) {
        this.stLiteKwh = stLiteKwh;
    }


    /**
     * Gets the meterNo value for this BillingAccountLoad.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this BillingAccountLoad.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the servType value for this BillingAccountLoad.
     * 
     * @return servType
     */
    public java.lang.String getServType() {
        return servType;
    }


    /**
     * Sets the servType value for this BillingAccountLoad.
     * 
     * @param servType
     */
    public void setServType(java.lang.String servType) {
        this.servType = servType;
    }


    /**
     * Gets the revenueClass value for this BillingAccountLoad.
     * 
     * @return revenueClass
     */
    public java.lang.String getRevenueClass() {
        return revenueClass;
    }


    /**
     * Sets the revenueClass value for this BillingAccountLoad.
     * 
     * @param revenueClass
     */
    public void setRevenueClass(java.lang.String revenueClass) {
        this.revenueClass = revenueClass;
    }


    /**
     * Gets the kwhAdj value for this BillingAccountLoad.
     * 
     * @return kwhAdj
     */
    public java.lang.Float getKwhAdj() {
        return kwhAdj;
    }


    /**
     * Sets the kwhAdj value for this BillingAccountLoad.
     * 
     * @param kwhAdj
     */
    public void setKwhAdj(java.lang.Float kwhAdj) {
        this.kwhAdj = kwhAdj;
    }


    /**
     * Gets the kwAdj value for this BillingAccountLoad.
     * 
     * @return kwAdj
     */
    public java.lang.Long getKwAdj() {
        return kwAdj;
    }


    /**
     * Sets the kwAdj value for this BillingAccountLoad.
     * 
     * @param kwAdj
     */
    public void setKwAdj(java.lang.Long kwAdj) {
        this.kwAdj = kwAdj;
    }


    /**
     * Gets the xfmrKva value for this BillingAccountLoad.
     * 
     * @return xfmrKva
     */
    public java.lang.Long getXfmrKva() {
        return xfmrKva;
    }


    /**
     * Sets the xfmrKva value for this BillingAccountLoad.
     * 
     * @param xfmrKva
     */
    public void setXfmrKva(java.lang.Long xfmrKva) {
        this.xfmrKva = xfmrKva;
    }


    /**
     * Gets the gLCode value for this BillingAccountLoad.
     * 
     * @return gLCode
     */
    public java.lang.String getGLCode() {
        return gLCode;
    }


    /**
     * Sets the gLCode value for this BillingAccountLoad.
     * 
     * @param gLCode
     */
    public void setGLCode(java.lang.String gLCode) {
        this.gLCode = gLCode;
    }


    /**
     * Gets the zone value for this BillingAccountLoad.
     * 
     * @return zone
     */
    public java.lang.String getZone() {
        return zone;
    }


    /**
     * Sets the zone value for this BillingAccountLoad.
     * 
     * @param zone
     */
    public void setZone(java.lang.String zone) {
        this.zone = zone;
    }


    /**
     * Gets the mapLocation value for this BillingAccountLoad.
     * 
     * @return mapLocation
     */
    public com.cannontech.multispeak.PointType getMapLocation() {
        return mapLocation;
    }


    /**
     * Sets the mapLocation value for this BillingAccountLoad.
     * 
     * @param mapLocation
     */
    public void setMapLocation(com.cannontech.multispeak.PointType mapLocation) {
        this.mapLocation = mapLocation;
    }


    /**
     * Gets the gridLocation value for this BillingAccountLoad.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this BillingAccountLoad.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BillingAccountLoad)) return false;
        BillingAccountLoad other = (BillingAccountLoad) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.eaLoc==null && other.getEaLoc()==null) || 
             (this.eaLoc!=null &&
              this.eaLoc.equals(other.getEaLoc()))) &&
            ((this.substation==null && other.getSubstation()==null) || 
             (this.substation!=null &&
              this.substation.equals(other.getSubstation()))) &&
            ((this.feeder==null && other.getFeeder()==null) || 
             (this.feeder!=null &&
              this.feeder.equals(other.getFeeder()))) &&
            ((this.phaseCd==null && other.getPhaseCd()==null) || 
             (this.phaseCd!=null &&
              this.phaseCd.equals(other.getPhaseCd()))) &&
            ((this.kwhUse==null && other.getKwhUse()==null) || 
             (this.kwhUse!=null &&
              this.kwhUse.equals(other.getKwhUse()))) &&
            ((this.demand==null && other.getDemand()==null) || 
             (this.demand!=null &&
              this.demand.equals(other.getDemand()))) &&
            ((this.hp==null && other.getHp()==null) || 
             (this.hp!=null &&
              this.hp.equals(other.getHp()))) &&
            ((this.kvarDemand==null && other.getKvarDemand()==null) || 
             (this.kvarDemand!=null &&
              this.kvarDemand.equals(other.getKvarDemand()))) &&
            ((this.stLiteKwh==null && other.getStLiteKwh()==null) || 
             (this.stLiteKwh!=null &&
              this.stLiteKwh.equals(other.getStLiteKwh()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.servType==null && other.getServType()==null) || 
             (this.servType!=null &&
              this.servType.equals(other.getServType()))) &&
            ((this.revenueClass==null && other.getRevenueClass()==null) || 
             (this.revenueClass!=null &&
              this.revenueClass.equals(other.getRevenueClass()))) &&
            ((this.kwhAdj==null && other.getKwhAdj()==null) || 
             (this.kwhAdj!=null &&
              this.kwhAdj.equals(other.getKwhAdj()))) &&
            ((this.kwAdj==null && other.getKwAdj()==null) || 
             (this.kwAdj!=null &&
              this.kwAdj.equals(other.getKwAdj()))) &&
            ((this.xfmrKva==null && other.getXfmrKva()==null) || 
             (this.xfmrKva!=null &&
              this.xfmrKva.equals(other.getXfmrKva()))) &&
            ((this.gLCode==null && other.getGLCode()==null) || 
             (this.gLCode!=null &&
              this.gLCode.equals(other.getGLCode()))) &&
            ((this.zone==null && other.getZone()==null) || 
             (this.zone!=null &&
              this.zone.equals(other.getZone()))) &&
            ((this.mapLocation==null && other.getMapLocation()==null) || 
             (this.mapLocation!=null &&
              this.mapLocation.equals(other.getMapLocation()))) &&
            ((this.gridLocation==null && other.getGridLocation()==null) || 
             (this.gridLocation!=null &&
              this.gridLocation.equals(other.getGridLocation())));
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
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getEaLoc() != null) {
            _hashCode += getEaLoc().hashCode();
        }
        if (getSubstation() != null) {
            _hashCode += getSubstation().hashCode();
        }
        if (getFeeder() != null) {
            _hashCode += getFeeder().hashCode();
        }
        if (getPhaseCd() != null) {
            _hashCode += getPhaseCd().hashCode();
        }
        if (getKwhUse() != null) {
            _hashCode += getKwhUse().hashCode();
        }
        if (getDemand() != null) {
            _hashCode += getDemand().hashCode();
        }
        if (getHp() != null) {
            _hashCode += getHp().hashCode();
        }
        if (getKvarDemand() != null) {
            _hashCode += getKvarDemand().hashCode();
        }
        if (getStLiteKwh() != null) {
            _hashCode += getStLiteKwh().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getServType() != null) {
            _hashCode += getServType().hashCode();
        }
        if (getRevenueClass() != null) {
            _hashCode += getRevenueClass().hashCode();
        }
        if (getKwhAdj() != null) {
            _hashCode += getKwhAdj().hashCode();
        }
        if (getKwAdj() != null) {
            _hashCode += getKwAdj().hashCode();
        }
        if (getXfmrKva() != null) {
            _hashCode += getXfmrKva().hashCode();
        }
        if (getGLCode() != null) {
            _hashCode += getGLCode().hashCode();
        }
        if (getZone() != null) {
            _hashCode += getZone().hashCode();
        }
        if (getMapLocation() != null) {
            _hashCode += getMapLocation().hashCode();
        }
        if (getGridLocation() != null) {
            _hashCode += getGridLocation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BillingAccountLoad.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "billingAccountLoad"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaLoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "substation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "feeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phaseCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kwhUse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kwhUse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("demand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "demand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kvarDemand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kvarDemand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stLiteKwh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stLiteKwh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("revenueClass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "revenueClass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kwhAdj");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kwhAdj"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kwAdj");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kwAdj"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xfmrKva");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "xfmrKva"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GLCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gLCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "zone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mapLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "PointType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gridLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"));
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
