/**
 * Transformer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Transformer  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String facilityID;
    private java.lang.String manufacturer;
    private java.lang.String serialNumber;
    private java.lang.Float kva;
    private java.lang.String xfmrType;
    private java.lang.String status;
    private java.lang.Long phases;
    private com.cannontech.multispeak.PhaseCd phase;
    private java.lang.Float impedance;
    private java.lang.Float nLLoss;
    private java.lang.Float fLLoss;
    private java.lang.Float price;
    private java.lang.Float priVoltsLo;
    private java.lang.Float priVoltsHi;
    private java.lang.Float secVoltsLo;
    private java.lang.Float secVoltsHi;
    private java.util.Date mfgDate;
    private java.lang.String pcb;
    private java.lang.String eaEquipment;
    private com.cannontech.multispeak.PcbTestList pcbTestList;

    public Transformer() {
    }

    public Transformer(
           java.lang.String facilityID,
           java.lang.String manufacturer,
           java.lang.String serialNumber,
           java.lang.Float kva,
           java.lang.String xfmrType,
           java.lang.String status,
           java.lang.Long phases,
           com.cannontech.multispeak.PhaseCd phase,
           java.lang.Float impedance,
           java.lang.Float nLLoss,
           java.lang.Float fLLoss,
           java.lang.Float price,
           java.lang.Float priVoltsLo,
           java.lang.Float priVoltsHi,
           java.lang.Float secVoltsLo,
           java.lang.Float secVoltsHi,
           java.util.Date mfgDate,
           java.lang.String pcb,
           java.lang.String eaEquipment,
           com.cannontech.multispeak.PcbTestList pcbTestList) {
           this.facilityID = facilityID;
           this.manufacturer = manufacturer;
           this.serialNumber = serialNumber;
           this.kva = kva;
           this.xfmrType = xfmrType;
           this.status = status;
           this.phases = phases;
           this.phase = phase;
           this.impedance = impedance;
           this.nLLoss = nLLoss;
           this.fLLoss = fLLoss;
           this.price = price;
           this.priVoltsLo = priVoltsLo;
           this.priVoltsHi = priVoltsHi;
           this.secVoltsLo = secVoltsLo;
           this.secVoltsHi = secVoltsHi;
           this.mfgDate = mfgDate;
           this.pcb = pcb;
           this.eaEquipment = eaEquipment;
           this.pcbTestList = pcbTestList;
    }


    /**
     * Gets the facilityID value for this Transformer.
     * 
     * @return facilityID
     */
    public java.lang.String getFacilityID() {
        return facilityID;
    }


    /**
     * Sets the facilityID value for this Transformer.
     * 
     * @param facilityID
     */
    public void setFacilityID(java.lang.String facilityID) {
        this.facilityID = facilityID;
    }


    /**
     * Gets the manufacturer value for this Transformer.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this Transformer.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the serialNumber value for this Transformer.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this Transformer.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the kva value for this Transformer.
     * 
     * @return kva
     */
    public java.lang.Float getKva() {
        return kva;
    }


    /**
     * Sets the kva value for this Transformer.
     * 
     * @param kva
     */
    public void setKva(java.lang.Float kva) {
        this.kva = kva;
    }


    /**
     * Gets the xfmrType value for this Transformer.
     * 
     * @return xfmrType
     */
    public java.lang.String getXfmrType() {
        return xfmrType;
    }


    /**
     * Sets the xfmrType value for this Transformer.
     * 
     * @param xfmrType
     */
    public void setXfmrType(java.lang.String xfmrType) {
        this.xfmrType = xfmrType;
    }


    /**
     * Gets the status value for this Transformer.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Transformer.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the phases value for this Transformer.
     * 
     * @return phases
     */
    public java.lang.Long getPhases() {
        return phases;
    }


    /**
     * Sets the phases value for this Transformer.
     * 
     * @param phases
     */
    public void setPhases(java.lang.Long phases) {
        this.phases = phases;
    }


    /**
     * Gets the phase value for this Transformer.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this Transformer.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }


    /**
     * Gets the impedance value for this Transformer.
     * 
     * @return impedance
     */
    public java.lang.Float getImpedance() {
        return impedance;
    }


    /**
     * Sets the impedance value for this Transformer.
     * 
     * @param impedance
     */
    public void setImpedance(java.lang.Float impedance) {
        this.impedance = impedance;
    }


    /**
     * Gets the nLLoss value for this Transformer.
     * 
     * @return nLLoss
     */
    public java.lang.Float getNLLoss() {
        return nLLoss;
    }


    /**
     * Sets the nLLoss value for this Transformer.
     * 
     * @param nLLoss
     */
    public void setNLLoss(java.lang.Float nLLoss) {
        this.nLLoss = nLLoss;
    }


    /**
     * Gets the fLLoss value for this Transformer.
     * 
     * @return fLLoss
     */
    public java.lang.Float getFLLoss() {
        return fLLoss;
    }


    /**
     * Sets the fLLoss value for this Transformer.
     * 
     * @param fLLoss
     */
    public void setFLLoss(java.lang.Float fLLoss) {
        this.fLLoss = fLLoss;
    }


    /**
     * Gets the price value for this Transformer.
     * 
     * @return price
     */
    public java.lang.Float getPrice() {
        return price;
    }


    /**
     * Sets the price value for this Transformer.
     * 
     * @param price
     */
    public void setPrice(java.lang.Float price) {
        this.price = price;
    }


    /**
     * Gets the priVoltsLo value for this Transformer.
     * 
     * @return priVoltsLo
     */
    public java.lang.Float getPriVoltsLo() {
        return priVoltsLo;
    }


    /**
     * Sets the priVoltsLo value for this Transformer.
     * 
     * @param priVoltsLo
     */
    public void setPriVoltsLo(java.lang.Float priVoltsLo) {
        this.priVoltsLo = priVoltsLo;
    }


    /**
     * Gets the priVoltsHi value for this Transformer.
     * 
     * @return priVoltsHi
     */
    public java.lang.Float getPriVoltsHi() {
        return priVoltsHi;
    }


    /**
     * Sets the priVoltsHi value for this Transformer.
     * 
     * @param priVoltsHi
     */
    public void setPriVoltsHi(java.lang.Float priVoltsHi) {
        this.priVoltsHi = priVoltsHi;
    }


    /**
     * Gets the secVoltsLo value for this Transformer.
     * 
     * @return secVoltsLo
     */
    public java.lang.Float getSecVoltsLo() {
        return secVoltsLo;
    }


    /**
     * Sets the secVoltsLo value for this Transformer.
     * 
     * @param secVoltsLo
     */
    public void setSecVoltsLo(java.lang.Float secVoltsLo) {
        this.secVoltsLo = secVoltsLo;
    }


    /**
     * Gets the secVoltsHi value for this Transformer.
     * 
     * @return secVoltsHi
     */
    public java.lang.Float getSecVoltsHi() {
        return secVoltsHi;
    }


    /**
     * Sets the secVoltsHi value for this Transformer.
     * 
     * @param secVoltsHi
     */
    public void setSecVoltsHi(java.lang.Float secVoltsHi) {
        this.secVoltsHi = secVoltsHi;
    }


    /**
     * Gets the mfgDate value for this Transformer.
     * 
     * @return mfgDate
     */
    public java.util.Date getMfgDate() {
        return mfgDate;
    }


    /**
     * Sets the mfgDate value for this Transformer.
     * 
     * @param mfgDate
     */
    public void setMfgDate(java.util.Date mfgDate) {
        this.mfgDate = mfgDate;
    }


    /**
     * Gets the pcb value for this Transformer.
     * 
     * @return pcb
     */
    public java.lang.String getPcb() {
        return pcb;
    }


    /**
     * Sets the pcb value for this Transformer.
     * 
     * @param pcb
     */
    public void setPcb(java.lang.String pcb) {
        this.pcb = pcb;
    }


    /**
     * Gets the eaEquipment value for this Transformer.
     * 
     * @return eaEquipment
     */
    public java.lang.String getEaEquipment() {
        return eaEquipment;
    }


    /**
     * Sets the eaEquipment value for this Transformer.
     * 
     * @param eaEquipment
     */
    public void setEaEquipment(java.lang.String eaEquipment) {
        this.eaEquipment = eaEquipment;
    }


    /**
     * Gets the pcbTestList value for this Transformer.
     * 
     * @return pcbTestList
     */
    public com.cannontech.multispeak.PcbTestList getPcbTestList() {
        return pcbTestList;
    }


    /**
     * Sets the pcbTestList value for this Transformer.
     * 
     * @param pcbTestList
     */
    public void setPcbTestList(com.cannontech.multispeak.PcbTestList pcbTestList) {
        this.pcbTestList = pcbTestList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Transformer)) return false;
        Transformer other = (Transformer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.facilityID==null && other.getFacilityID()==null) || 
             (this.facilityID!=null &&
              this.facilityID.equals(other.getFacilityID()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.kva==null && other.getKva()==null) || 
             (this.kva!=null &&
              this.kva.equals(other.getKva()))) &&
            ((this.xfmrType==null && other.getXfmrType()==null) || 
             (this.xfmrType!=null &&
              this.xfmrType.equals(other.getXfmrType()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.phases==null && other.getPhases()==null) || 
             (this.phases!=null &&
              this.phases.equals(other.getPhases()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.impedance==null && other.getImpedance()==null) || 
             (this.impedance!=null &&
              this.impedance.equals(other.getImpedance()))) &&
            ((this.nLLoss==null && other.getNLLoss()==null) || 
             (this.nLLoss!=null &&
              this.nLLoss.equals(other.getNLLoss()))) &&
            ((this.fLLoss==null && other.getFLLoss()==null) || 
             (this.fLLoss!=null &&
              this.fLLoss.equals(other.getFLLoss()))) &&
            ((this.price==null && other.getPrice()==null) || 
             (this.price!=null &&
              this.price.equals(other.getPrice()))) &&
            ((this.priVoltsLo==null && other.getPriVoltsLo()==null) || 
             (this.priVoltsLo!=null &&
              this.priVoltsLo.equals(other.getPriVoltsLo()))) &&
            ((this.priVoltsHi==null && other.getPriVoltsHi()==null) || 
             (this.priVoltsHi!=null &&
              this.priVoltsHi.equals(other.getPriVoltsHi()))) &&
            ((this.secVoltsLo==null && other.getSecVoltsLo()==null) || 
             (this.secVoltsLo!=null &&
              this.secVoltsLo.equals(other.getSecVoltsLo()))) &&
            ((this.secVoltsHi==null && other.getSecVoltsHi()==null) || 
             (this.secVoltsHi!=null &&
              this.secVoltsHi.equals(other.getSecVoltsHi()))) &&
            ((this.mfgDate==null && other.getMfgDate()==null) || 
             (this.mfgDate!=null &&
              this.mfgDate.equals(other.getMfgDate()))) &&
            ((this.pcb==null && other.getPcb()==null) || 
             (this.pcb!=null &&
              this.pcb.equals(other.getPcb()))) &&
            ((this.eaEquipment==null && other.getEaEquipment()==null) || 
             (this.eaEquipment!=null &&
              this.eaEquipment.equals(other.getEaEquipment()))) &&
            ((this.pcbTestList==null && other.getPcbTestList()==null) || 
             (this.pcbTestList!=null &&
              this.pcbTestList.equals(other.getPcbTestList())));
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
        if (getFacilityID() != null) {
            _hashCode += getFacilityID().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getKva() != null) {
            _hashCode += getKva().hashCode();
        }
        if (getXfmrType() != null) {
            _hashCode += getXfmrType().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getPhases() != null) {
            _hashCode += getPhases().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getImpedance() != null) {
            _hashCode += getImpedance().hashCode();
        }
        if (getNLLoss() != null) {
            _hashCode += getNLLoss().hashCode();
        }
        if (getFLLoss() != null) {
            _hashCode += getFLLoss().hashCode();
        }
        if (getPrice() != null) {
            _hashCode += getPrice().hashCode();
        }
        if (getPriVoltsLo() != null) {
            _hashCode += getPriVoltsLo().hashCode();
        }
        if (getPriVoltsHi() != null) {
            _hashCode += getPriVoltsHi().hashCode();
        }
        if (getSecVoltsLo() != null) {
            _hashCode += getSecVoltsLo().hashCode();
        }
        if (getSecVoltsHi() != null) {
            _hashCode += getSecVoltsHi().hashCode();
        }
        if (getMfgDate() != null) {
            _hashCode += getMfgDate().hashCode();
        }
        if (getPcb() != null) {
            _hashCode += getPcb().hashCode();
        }
        if (getEaEquipment() != null) {
            _hashCode += getEaEquipment().hashCode();
        }
        if (getPcbTestList() != null) {
            _hashCode += getPcbTestList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Transformer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transformer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facilityID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"));
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
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kva");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kva"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xfmrType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "xfmrType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phases");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("impedance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "impedance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NLLoss");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "nLLoss"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FLLoss");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fLLoss"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("price");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priVoltsLo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priVoltsLo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priVoltsHi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priVoltsHi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secVoltsLo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secVoltsLo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secVoltsHi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secVoltsHi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
        elemField.setFieldName("pcb");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcb"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaEquipment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaEquipment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pcbTestList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pcbTestList"));
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
