package com.cannontech.multispeak.dao.impl.v5;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
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
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.v5.MspMeterDao;
import com.cannontech.multispeak.data.v5.MspCDDeviceReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReturnList;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public final class MspMeterDaoImpl implements MspMeterDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterRowMapper meterRowMapper;

    private static String selectSql;

    static {
        selectSql =
            "SELECT PaobjectId, Type, PaoName, MeterNumber, Address, DisconnectAddress, "
                + "SerialNumber, Manufacturer, Model " + "FROM YukonPaobject pao "
                + "JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId "
                + "LEFT JOIN DeviceCarrierSettings dcs ON pao.PAObjectID = dcs.DEVICEID "
                + "LEFT JOIN DeviceMCT400Series mct ON pao.paobjectId = mct.deviceId "
                + "LEFT JOIN RFNAddress rfn ON pao.PAObjectID = rfn.DeviceId";
    };

    private static final YukonRowMapper<ElectricMeter> mspMeterRowMapper = new YukonRowMapper<ElectricMeter>() {
        @Override
        public ElectricMeter mapRow(YukonResultSet rset) throws SQLException {
            ElectricMeter mspMeter = createMeter(rset);
            return mspMeter;
        };
    };

    private static final YukonRowMapper<CDDevice> mspCDDeviceRowMapper = new YukonRowMapper<CDDevice>() {
        @Override
        public CDDevice mapRow(YukonResultSet rset) throws SQLException {
            CDDevice mspMeter = createMeterID(rset);
            return mspMeter;
        };
    };

    @Override
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords) {

        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        if (lastReceived != null) {
            sql.append("WHERE MeterNumber").gt(lastReceived);
        }
        if (excludeDisabled) {
            if (StringUtils.containsIgnoreCase(sql.getSql(), "WHERE ")) {
                sql.append("AND");
            } else {
                sql.append("WHERE");
            }
            sql.append("DisableFlag").eq_k(YNBoolean.NO);
        }

        sql.append("ORDER BY METERNUMBER");
        List<ElectricMeter> mspMeters = new ArrayList<ElectricMeter>();
        CollectionRowCallbackHandler<ElectricMeter> crcHandler =
            new CollectionRowCallbackHandler<ElectricMeter>(mspMeterRowMapper, mspMeters);
        yukonJdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));
        MspMeterReturnList mspMeterReturnList = new MspMeterReturnList();
        mspMeterReturnList.setMeters(mspMeters);
        mspMeterReturnList.setReturnFields(mspMeters, maxRecords);
        return mspMeterReturnList;
    }

    @Override
    public MspMeterReturnList getCDSupportedMeters(String lastReceived, int maxRecords) {

        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);
        Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("WHERE (pao.type IN (").appendArgumentList(collection).append(")");
        sql.append(" OR (DisconnectAddress IS NOT NULL) )");
        if (lastReceived != null) {
            sql.append("AND MeterNumber").gt(lastReceived);
        }
        if (excludeDisabled) {
            sql.append("AND DisableFlag").eq_k(YNBoolean.NO);
        }
        sql.append("ORDER BY MeterNumber");

        List<ElectricMeter> mspMeters = new ArrayList<ElectricMeter>();
        CollectionRowCallbackHandler<ElectricMeter> crcHandler =
            new CollectionRowCallbackHandler<ElectricMeter>(mspMeterRowMapper, mspMeters);
        yukonJdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));

        MspMeterReturnList mspMeterReturnList = new MspMeterReturnList();
        mspMeterReturnList.setMeters(mspMeters);
        mspMeterReturnList.setReturnFields(mspMeters, maxRecords);
        return mspMeterReturnList;
    }

    @Override
    public MspCDDeviceReturnList getAllCDDevices(String lastReceived, int maxRecords) {

        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);
        Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("WHERE (pao.type IN (").appendArgumentList(collection).append(")");
        sql.append(" OR (DisconnectAddress IS NOT NULL) )");
        if (lastReceived != null) {
            sql.append("AND MeterNumber").gt(lastReceived);
        }
        if (excludeDisabled) {
            sql.append("AND DisableFlag").eq_k(YNBoolean.NO);
        }
        sql.append("ORDER BY MeterNumber");

        List<CDDevice> mspCDMeters = new ArrayList<CDDevice>();
        CollectionRowCallbackHandler<CDDevice> crcHandler =
            new CollectionRowCallbackHandler<CDDevice>(mspCDDeviceRowMapper, mspCDMeters);
        yukonJdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));

        MspCDDeviceReturnList mspCDDeviceReturnList = new MspCDDeviceReturnList();
        mspCDDeviceReturnList.setCDMeters(mspCDMeters);
        mspCDDeviceReturnList.setReturnFields(mspCDMeters, maxRecords);
        return mspCDDeviceReturnList;
    }

    /**
     * Returns true is meterNumber is a disconnect meter.
     * 
     * @return
     */
    @Override
    public boolean isCDSupportedMeter(String meterNumber) {
        Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();

        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(selectSql);
            sql.append("WHERE (pao.type IN (").appendArgumentList(collection).append(")");
            sql.append(" OR (DisconnectAddress IS NOT NULL) )");
            sql.append("AND METERNUMBER").eq(meterNumber);

            List<ElectricMeter> cdMeters = yukonJdbcTemplate.query(sql, mspMeterRowMapper);
            return !cdMeters.isEmpty();
        } catch (IncorrectResultSizeDataAccessException e) {
            // No results simply mean that the meterNumber is not a CD supported meter
            if (e.getActualSize() > 0)
                return true;
            return false;
        }
    }

    @Override
    public YukonMeter getMeterForMeterNumber(String meterNumber) {

        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
        if (excludeDisabled) {
            sql.append("AND ypo.DisableFlag").eq(YNBoolean.NO);
        }

        try {
            YukonMeter yukonMeter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            return yukonMeter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no meter matches " + meterNumber);
        }
    }

    @Override
    public YukonMeter getForSerialNumberOrAddress(String serialNumberOrAddress) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE dcs.Address").eq(serialNumberOrAddress);
            sql.append("OR SerialNumber").eq(serialNumberOrAddress);
            YukonMeter meter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown physical address or rfn sensorSerialNumber " + serialNumberOrAddress);
        }
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
     * Creates a new (MSP) ElectricMeter object.
     * The information commented out is being used for in-line documentation of available MultiSpeak fields.
     * 
     * @param rset The YukonResultSet to pull data values from. View actual method to see required column
     *        names.
     * @return Meter
     */
    private static ElectricMeter createMeter(YukonResultSet rset) throws SQLException {
        PaoIdentifier paoIdentifier = rset.getPaoIdentifier("paobjectid", "type");
        String paoName = rset.getString("PaoName");
        String meterNumber = rset.getString("meternumber");
        String carrierAddress = rset.getString("address");
        String discCollarAddress = rset.getString("disconnectaddress");
        String rfnSerialNumber = rset.getString("serialnumber");
        String rfnManufacturer = rset.getString("manufacturer");

        ElectricMeter meter = new ElectricMeter();
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

    /**
     * Helper method to return a collection of PaoTypes that support "integrated" disconnect capabilities.
     * 
     * @return
     */
    private Collection<PaoType> getIntegratedDisconnectPaoDefinitions() {
        Set<PaoDefinition> discCollar = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        Set<PaoDefinition> disc410 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_410);
        Set<PaoDefinition> disc310 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_310);
        Set<PaoDefinition> disc213 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_213);
        Set<PaoDefinition> discRfn = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_RFN);

        List<PaoDefinition> discIntegrated = Lists.newArrayList();
        discIntegrated.addAll(disc410);
        discIntegrated.addAll(disc310);
        discIntegrated.addAll(disc213);
        discIntegrated.addAll(discRfn);
        discIntegrated.removeAll(discCollar); // Remove disconnect collar compatible

        Collection<PaoType> collection = Collections2.transform(discIntegrated, new Function<PaoDefinition, PaoType>() {
            @Override
            public PaoType apply(PaoDefinition paoDefinition) {
                return paoDefinition.getType();
            }
        });

        return collection;
    }
}