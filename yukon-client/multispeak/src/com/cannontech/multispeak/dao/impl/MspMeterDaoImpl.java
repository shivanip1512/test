package com.cannontech.multispeak.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.Module;
import com.cannontech.msp.beans.v3.ModuleList;
import com.cannontech.msp.beans.v3.Nameplate;
import com.cannontech.msp.beans.v3.UtilityInfo;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDaoBase;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.data.MspReturnList;

public final class MspMeterDaoImpl extends MspMeterDaoBase {

    private static final YukonRowMapper<Meter> mspMeterRowMapper = new YukonRowMapper<Meter>() {
    	@Override
        public Meter mapRow(YukonResultSet rset) throws SQLException {
            Meter mspMeter = createMeter(rset);
            return mspMeter;
    	};
   };

    @Override
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords) {
        
        SqlStatementBuilder sql = buildSqlStatementForAMRSupportedMeters(lastReceived);
        List<Meter> mspMeters = new ArrayList<Meter>();
        CollectionRowCallbackHandler<Meter> crcHandler = new CollectionRowCallbackHandler<Meter>(mspMeterRowMapper, mspMeters);
        jdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));
        MspMeterReturnList mspMeterReturnList = new MspMeterReturnList();
        mspMeterReturnList.setMeters(mspMeters);
        mspMeterReturnList.setReturnFields(mspMeters, maxRecords);
        return mspMeterReturnList;
    }

    @Override
    public MspMeterReturnList getCDSupportedMeters(String lastReceived, int maxRecords) {

        SqlStatementBuilder sql = buildSqlStatementForCDSupportedMeters(lastReceived);
        List<Meter> mspMeters = new ArrayList<Meter>();
        CollectionRowCallbackHandler<Meter> crcHandler = new CollectionRowCallbackHandler<Meter>(mspMeterRowMapper, mspMeters);
        jdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));
    
        MspMeterReturnList mspMeterReturnList = new MspMeterReturnList();
        mspMeterReturnList.setMeters(mspMeters);
        mspMeterReturnList.setReturnFields(mspMeters, maxRecords);
        return mspMeterReturnList;
    }

    /**
     * Creates a new (MSP) Meter object.
     * The information commented out is being used for in-line documentation of available MultiSpeak fields. 
     * @param rset The YukonResultSet to pull data values from. View actual method to see required column names. 
     * @return Meter
     */
    private static Meter createMeter(YukonResultSet rset) throws SQLException
    {
        PaoIdentifier paoIdentifier = rset.getPaoIdentifier("paobjectid", "type");
        String paoName = rset.getString("PaoName");
        String meterNumber = rset.getString("meternumber");
        String carrierAddress = rset.getString("address");
        String discCollarAddress = rset.getString("disconnectaddress");
        String rfnSerialNumber = rset.getString("serialnumber");
        String rfnManufacturer = rset.getString("manufacturer");
        String rfnModel = rset.getString("model");
        
        Meter meter = new Meter();
        meter.setObjectID(meterNumber);
        meter.setComments("Device Name: " + paoName);
        // MessageElement element = new MessageElement(QName.valueOf("AMRMeterType"), paoType);
        // MessageElement element2 = new MessageElement(QName.valueOf("AMRRdgGrp"), collectionGroup);
        // Extensions ext = new Extensions();
        // ext.set_any(new MessageElement[]{element2});
        // meter.setExtensions(ext);
        meter.setMeterNo(meterNumber);
        
        meter.setMeterType(paoIdentifier.getPaoType().getPaoTypeName());  //Meter type/model. Always use paoType
        
        if (StringUtils.isNotBlank(rfnManufacturer)) {
            meter.setManufacturer(rfnManufacturer);    //Meter manufacturer.
        }
//        meter.setSealNumberList(null);  //Seal Number List
//        meter.setAMRDeviceType(paoType);	//This is the Yukon Template Field, Yukon doesn't store what this meter used as it's template.
        
        meter.setAMRVendor(MultispeakDefines.AMR_VENDOR);
        
        // For RF meters, set serialNumber and transponderId; for PLC set only transponderId
        if (StringUtils.isNotBlank(carrierAddress)) {
            meter.setNameplate(getNameplate(meterNumber, carrierAddress));
            // serialNumber not known for carrier meters.
            //meter.setSerialNumber( );    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        } else if (StringUtils.isNotBlank(rfnSerialNumber)) {
            meter.setNameplate(getNameplate(meterNumber, rfnSerialNumber));
            meter.setSerialNumber(rfnSerialNumber);    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        }
        
//        meter.setSealNumberList(null);  //List of seals applied to this meter.
//        meter.setUtilityInfo(null);     //This information relates the meter to the account with which it is associated
        
        //Add Module for Disconnect information
        if( discCollarAddress != null) {
            Module discModule = new Module();
            ModuleList moduleList = new ModuleList();
            List<Module> listOfModules = moduleList.getModule();
            discModule.setObjectID(discCollarAddress);
            discModule.setModuleType("Disconnect Collar");
            listOfModules.add(discModule);
            meter.setModuleList(moduleList); 
        }
        
        //MSPDevice
//        meter.setDeviceClass(null); //A high-level description of this type of object (e.g., "kWh meter", "demand meter", etc.).
//        meter.setFacilityID(null);  //A utility-defined string designation for this device.
//        meter.setInServiceDate(null);   //The date and time that a device was placed into active service.
//        meter.setOutServiceDate(null);  //The date and time that a device was removed from active service.
        
        //MSPObject
//        meter.setUtility(null);
//        meter.setComments(null);
//        meter.setErrorString(null);
//        meter.setReplaceID(null);
        
        return meter;
    }

    /**
     * Creates a new (MSP) Nameplate object
     * The information commented out is being used for in-line documentation of available MultiSpeak fields. 
     * @param meterNumber The multispeak objectID
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Nameplate getNameplate(String meterNumber, String address)
    {
        Nameplate nameplate = new Nameplate();
        /*nameplate.setKh();
        nameplate.setKr();
        nameplate.setFrequency();
        nameplate.setNumberOfElements();
        nameplate.setBaseType();
        nameplate.setAccuracyClass();
        nameplate.setElementsVoltage();
        nameplate.setSupplyVoltage();
        nameplate.setMaxAmperage();
        nameplate.setTestAmperage();
        nameplate.setRegRatio();
        nameplate.setPhases();
        nameplate.setWires();
        nameplate.setDials();
        nameplate.setForm();
        nameplate.setMultiplier();
        nameplate.setDemandMult();
        nameplate.setTransformerRatio();*/
        nameplate.setTransponderID(address);
        return nameplate;
    }
    
    /**
     * Creates a new (MSP) UtilityInfo object
     * The information commented out is being used for in-line documentation of available MultiSpeak fields. 
     * @param meterNumber The Multispeak objectID
     * @return
     */
    public static UtilityInfo getUtilityInfo(String meterNumber)
    {
        UtilityInfo utilityInfo = new UtilityInfo();
        /*utilityInfo.setAccountNumber();
        utilityInfo.setBus();
        utilityInfo.setCustID();
        utilityInfo.setDistrict();
        utilityInfo.setEaLoc();
        utilityInfo.setFeeder();
        utilityInfo.setOwner();
        utilityInfo.setPhaseCd();
        utilityInfo.setServLoc();
        utilityInfo.setSubstationCode();
        utilityInfo.setSubstationName();
        utilityInfo.setTransformerBankID();*/
        return utilityInfo;
    }

    @Override
    public MspReturnList getAllCDDevices(String lastReceived, int maxRecords) {
        // This method should not be called for v3, only v5 implementation.
        return null;
    }
}