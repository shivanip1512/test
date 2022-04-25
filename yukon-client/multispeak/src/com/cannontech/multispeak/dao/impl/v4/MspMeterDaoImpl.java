package com.cannontech.multispeak.dao.impl.v4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AbstractRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.msp.beans.v3.Module;
import com.cannontech.msp.beans.v3.ModuleList;
import com.cannontech.msp.beans.v3.Nameplate;
import com.cannontech.msp.beans.v3.UtilityInfo;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.WaterMeter;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDaoBase;
import com.cannontech.multispeak.data.MspReturnList;
import com.cannontech.multispeak.data.v4.MspMeterReturnList;

public final class MspMeterDaoImpl extends MspMeterDaoBase {

    private static final YukonRowMapper<MspMeter> mspMeterRowMapper = new YukonRowMapper<MspMeter>() {
        @Override
        public MspMeter mapRow(YukonResultSet rset) throws SQLException {
            MspMeter mspMeter = createMeter(rset);
            return mspMeter;
        };
    };

    @Override
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords) {

        SqlStatementBuilder sql = buildSqlStatementForAMRSupportedMeters(lastReceived);

        MspMeterReturnList mspMeters = new MspMeterReturnList();
        List<ElectricMeter> electricMeters = new ArrayList<>();
        List<WaterMeter> waterMeters = new ArrayList<>();
        jdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(new AbstractRowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                MspMeter mspMeter = mspMeterRowMapper.mapRow(new YukonResultSet(rs));
                if (mspMeter instanceof ElectricMeter) {
                    electricMeters.add((ElectricMeter) mspMeter);
                } else if (mspMeter instanceof WaterMeter) {
                    waterMeters.add((WaterMeter) mspMeter);
                }

                // setting the last object we'll process as we loop through them.
                mspMeters.setLastProcessed(mspMeter);
            }
        }, maxRecords));

        mspMeters.setElectricMeters(electricMeters);
        mspMeters.setWaterMeters(waterMeters);

        mspMeters.setReturnFields(mspMeters.getLastProcessed(), mspMeters.getSize(), maxRecords);
        return mspMeters;
    }

    @Override
    public MspMeterReturnList getCDSupportedMeters(String lastReceived, int maxRecords) {

        SqlStatementBuilder sql = buildSqlStatementForCDSupportedMeters(lastReceived);

        MspMeterReturnList mspMeters = new MspMeterReturnList();
        List<ElectricMeter> electricMeters = new ArrayList<>();

        jdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(new AbstractRowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                MspMeter mspMeter = mspMeterRowMapper.mapRow(new YukonResultSet(rs));
                electricMeters.add((ElectricMeter) mspMeter);
            }
        }, maxRecords));
        
        MspMeter lastProcessedMeter = null;
        if(CollectionUtils.isNotEmpty(electricMeters)) {
            lastProcessedMeter = electricMeters.get(electricMeters.size()-1);
        }
        mspMeters.setElectricMeters(electricMeters);
        mspMeters.setReturnFields(lastProcessedMeter, mspMeters.getSize(), maxRecords);
        return mspMeters;
    }

    /**
     * Creates a new (MSP) Meter object.
     * The information commented out is being used for in-line documentation of available MultiSpeak fields. 
     * @param rset The YukonResultSet to pull data values from. View actual method to see required column names. 
     * @return Meter
     */
    private static MspMeter createMeter(YukonResultSet rset) throws SQLException
    {
        PaoIdentifier paoIdentifier = rset.getPaoIdentifier("paobjectid", "type");
        String paoName = rset.getString("PaoName");
        String meterNumber = rset.getString("meternumber");
        String carrierAddress = rset.getString("address");
        String discCollarAddress = rset.getString("disconnectaddress");
        String rfnSerialNumber = rset.getString("serialnumber");
        String rfnManufacturer = rset.getString("manufacturer");
        String rfnModel = rset.getString("model");
        
        MspMeter meter = null;
        if (paoIdentifier.getPaoType().isWaterMeter()) {
            meter = new WaterMeter();
        } else {
            meter = new ElectricMeter();
        }
        meter.setObjectID(meterNumber);
        meter.setComments("Device Name: " + paoName);
       
        meter.setMeterNo(meterNumber);
        meter.setMeterType(paoIdentifier.getPaoType().getPaoTypeName());  //Meter type/model. Always use paoType
        
        if (StringUtils.isNotBlank(rfnManufacturer)) {
            meter.setManufacturer(rfnManufacturer);    //Meter manufacturer.
        }
        
//        meter.setAMRDeviceType(paoType);	//This is the Yukon Template Field, Yukon doesn't store what this meter used as it's template.
        
        meter.setAMRVendor(MultispeakDefines.AMR_VENDOR);
        
        // For RF meters, set serialNumber and transponderId; for PLC set only transponderId
        if (StringUtils.isNotBlank(carrierAddress)) {
//            meter.setNameplate(getNameplate(meterNumber, carrierAddress));
        } else if (StringUtils.isNotBlank(rfnSerialNumber)) {
//            meter.setNameplate(getNameplate(meterNumber, rfnSerialNumber));
            meter.setSerialNumber(rfnSerialNumber);    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        }
        
        //Add Module for Disconnect information
        if( discCollarAddress != null) {
            Module discModule = new Module();
            ModuleList moduleList = new ModuleList();
            List<Module> listOfModules = moduleList.getModule();
            discModule.setObjectID(discCollarAddress);
            discModule.setModuleType("Disconnect Collar");
            listOfModules.add(discModule);
//            meter.setModuleList(moduleList); 
        }
        
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
        return utilityInfo;
    }

    @Override
    public MspReturnList getAllCDDevices(String lastReceived, int maxRecords) {
        // This method should not be called for v3, only v5 implementation.
        return null;
    }
}