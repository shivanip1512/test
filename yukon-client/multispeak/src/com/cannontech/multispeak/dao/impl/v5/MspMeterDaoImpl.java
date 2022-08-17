package com.cannontech.multispeak.dao.impl.v5;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AbstractRowCallbackHandler;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.SingleIdentifier;
import com.cannontech.msp.beans.v5.enumerations.ServiceKind;
import com.cannontech.msp.beans.v5.multispeak.AssetDetails;
import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.msp.beans.v5.multispeak.CommunicationsAddress;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.Module;
import com.cannontech.msp.beans.v5.multispeak.Modules;
import com.cannontech.msp.beans.v5.multispeak.MspMeter;
import com.cannontech.msp.beans.v5.multispeak.WaterMeter;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDaoBase;
import com.cannontech.multispeak.data.v5.MspCDDeviceReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReturnList;

public final class MspMeterDaoImpl extends MspMeterDaoBase {

    private static final YukonRowMapper<MspMeter> mspMeterRowMapper = new YukonRowMapper<MspMeter>() {
        @Override
        public MspMeter mapRow(YukonResultSet rset) throws SQLException {
            MspMeter mspMeter = createMeter(rset);
            return mspMeter;
        };
    };

    private static final YukonRowMapper<CDDevice> mspCDDeviceRowMapper = new YukonRowMapper<CDDevice>() {
        @Override
        public CDDevice mapRow(YukonResultSet rset) throws SQLException {
            CDDevice cdDevice = createMeterID(rset);
            return cdDevice;
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

    @Override
    public MspCDDeviceReturnList getAllCDDevices(String lastReceived, int maxRecords) {

        SqlStatementBuilder sql = buildSqlStatementForCDSupportedMeters(lastReceived);
        List<CDDevice> mspCDMeters = new ArrayList<CDDevice>();
        CollectionRowCallbackHandler<CDDevice> crcHandler =
            new CollectionRowCallbackHandler<CDDevice>(mspCDDeviceRowMapper, mspCDMeters);
        jdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));

        MspCDDeviceReturnList mspCDDeviceReturnList = new MspCDDeviceReturnList();
        mspCDDeviceReturnList.setCDDevices(mspCDMeters);
        mspCDDeviceReturnList.setReturnFields(mspCDMeters, maxRecords);
        return mspCDDeviceReturnList;
    }


    /**
     * Creates a CD Device object.
     * 
     * @param rset The YukonResultSet to pull data values from. View actual method to see required column
     *        names.
     * @return Meter
     */
    private static CDDevice createMeterID(YukonResultSet rset) throws SQLException {
        PaoIdentifier paoIdentifier = rset.getPaoIdentifier("paobjectid", "type");
        String paoName = rset.getString("PaoName");
        String meterNumber = rset.getString("meternumber");
        String carrierAddress = rset.getString("address");
        String discCollarAddress = rset.getString("disconnectaddress");
        String rfnSerialNumber = rset.getString("serialnumber");
        String rfnManufacturer = rset.getString("manufacturer");

        CDDevice cdDevice = new CDDevice();
        cdDevice.setComments("Device Name: " + paoName);

        SingleIdentifier singleIdentifier = new SingleIdentifier();
        singleIdentifier.setValue(meterNumber);
        cdDevice.setPrimaryIdentifier(singleIdentifier);
        cdDevice.setObjectGUID(meterNumber);

        cdDevice.setAMRVendor(MultispeakDefines.AMR_VENDOR);
        cdDevice.setAMRDeviceType(paoIdentifier.getPaoType().getPaoTypeName()); 

        // TODO probably we need to remove this object if it is not required (optional field in CD device)
        MeterID meterID = new MeterID();
        meterID.setValue(meterNumber);
        meterID.setMeterName(meterNumber);
        if (paoIdentifier.getPaoType().isMeter()) {
            if (paoIdentifier.getPaoType().isWaterMeter()) {
                meterID.setServiceType(ServiceKind.WATER);
            } else {
                meterID.setServiceType(ServiceKind.ELECTRIC);
            }
        } else {
            meterID.setServiceType(ServiceKind.OTHER);
        }

        AssetDetails assetDetails = new AssetDetails();
        if (StringUtils.isNotBlank(rfnManufacturer)) {
            assetDetails.setManufacturer(rfnManufacturer); // Meter manufacturer.
        }

        // TODO How to get communication port in yukon application
        if (StringUtils.isNotBlank(carrierAddress)) {
            cdDevice.setCommunicationsAddress(getCommunicationsAddress(carrierAddress));
            meterID.setCommunicationAddress(getCommunicationsAddress(carrierAddress).getValue());

        } else if (StringUtils.isNotBlank(rfnSerialNumber)) {
            cdDevice.setCommunicationsAddress(getCommunicationsAddress(rfnSerialNumber));
            meterID.setCommunicationAddress(getCommunicationsAddress(rfnSerialNumber).getValue());
            assetDetails.setSerialNumber(rfnSerialNumber);
        }
        
        meterID.setRegisteredName("Eaton");
        meterID.setSystemName("Yukon");

        // Add Module for Disconnect information
        if (discCollarAddress != null) {
            Modules discModules = new Modules();
            List<Module> moduleList = discModules.getModule();
            Module module = new Module();
            module.setObjectGUID(discCollarAddress);
            // TODO Please confirm this change as ModuleType is not present in msp 5.0
            module.setComments("Disconnect Collar");
            moduleList.add(module);
            cdDevice.setModules(discModules);
        }
        cdDevice.setMeterID(meterID);
        cdDevice.setAssetDetails(assetDetails);
        return cdDevice;
    }

    /**
     * Creates a new (MSP) MspMeter object.
     * The information commented out is being used for in-line documentation of available MultiSpeak fields.
     * 
     * @param rset The YukonResultSet to pull data values from. View actual method to see required column
     *        names.
     * @return Meter
     */
    private static MspMeter createMeter(YukonResultSet rset) throws SQLException {
        PaoIdentifier paoIdentifier = rset.getPaoIdentifier("paobjectid", "type");
        String paoName = rset.getString("PaoName");
        String meterNumber = rset.getString("meternumber");
        String carrierAddress = rset.getString("address");
        String discCollarAddress = rset.getString("disconnectaddress");
        String rfnSerialNumber = rset.getString("serialnumber");
        String rfnManufacturer = rset.getString("manufacturer");
        MspMeter meter = null;
        if (paoIdentifier.getPaoType().isWaterMeter()) {
            meter = new WaterMeter();
        } else {
            meter = new ElectricMeter();
        }
        meter.setComments("Device Name: " + paoName);
        meter.setObjectGUID(meterNumber);
        
        SingleIdentifier singleIdentifier = new SingleIdentifier();
        singleIdentifier.setValue(meterNumber);
        meter.setPrimaryIdentifier(singleIdentifier);
        
        meter.setAMIVendor(MultispeakDefines.AMR_VENDOR);
        meter.setAMIDeviceType(paoIdentifier.getPaoType().getPaoTypeName());
        
        AssetDetails assetDetails = new AssetDetails();

        if (StringUtils.isNotBlank(rfnManufacturer)) {
            assetDetails.setManufacturer(rfnManufacturer); // Meter manufacturer.
        }

        // TODO also need to set communication port
        // For RF meters, set serialNumber and transponderId; for PLC set only transponderId
        // TODO How to get communication port in yukon application
        if (StringUtils.isNotBlank(carrierAddress)) {
            meter.setCommunicationsAddress(getCommunicationsAddress(carrierAddress));
            // serialNumber not known for carrier meters.
            // meter.setSerialNumber( ); //Meter serial number. This is the original number assigned to the
            // meter by the manufacturer.
        } else if (StringUtils.isNotBlank(rfnSerialNumber)) {
            meter.setCommunicationsAddress(getCommunicationsAddress(rfnSerialNumber));
            assetDetails.setSerialNumber(rfnSerialNumber); // Meter serial number. This is the original number
                                                           // assigned to the meter by the manufacturer.
        }

        // Add Module for Disconnect information
        if (discCollarAddress != null) {
            Modules discModules = new Modules();
            List<Module> moduleList = discModules.getModule();
            Module module = new Module();
            module.setObjectGUID(discCollarAddress);
            // TODO Please confirm this change as ModuleType is not present in msp 5.0
            module.setComments("Disconnect Collar");
            moduleList.add(module);
            meter.setModules(discModules);
        }
        meter.setAssetDetails(assetDetails);
        return meter;
    }

    /**
     * Creates a communication address object
     * The information commented out is being used for in-line documentation of available MultiSpeak fields.
     * 
     * @param address Physical Address)
     * @return
     */
    public static CommunicationsAddress getCommunicationsAddress(String address) {
        CommunicationsAddress communicationsAddress = new CommunicationsAddress();
        communicationsAddress.setValue(address);
        return communicationsAddress;
    }
}