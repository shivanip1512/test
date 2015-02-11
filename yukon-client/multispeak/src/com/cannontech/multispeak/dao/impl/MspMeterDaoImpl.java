package com.cannontech.multispeak.dao.impl;

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
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.Module;
import com.cannontech.msp.beans.v3.ModuleList;
import com.cannontech.msp.beans.v3.Nameplate;
import com.cannontech.msp.beans.v3.UtilityInfo;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public final class MspMeterDaoImpl implements MspMeterDao
{
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate = null;
	@Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterRowMapper meterRowMapper;

    private static String selectSql;
    
    static {
        selectSql = "SELECT PaobjectId, Type, PaoName, MeterNumber, Address, DisconnectAddress, " +
                        "SerialNumber, Manufacturer, Model " +
                    "FROM YukonPaobject pao " +
                        "JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId " +
                        "LEFT JOIN DeviceCarrierSettings dcs ON pao.PAObjectID = dcs.DEVICEID " +
                        "LEFT JOIN DeviceMCT400Series mct ON pao.paobjectId = mct.deviceId " + 
                        "LEFT JOIN RFNAddress rfn ON pao.PAObjectID = rfn.DeviceId";
    };
    
    private static final YukonRowMapper<Meter> mspMeterRowMapper = new YukonRowMapper<Meter>() {
    	@Override
        public Meter mapRow(YukonResultSet rset) throws SQLException {
            Meter mspMeter = createMeter(rset);
            return mspMeter;
    	};
   };

    @Override
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords) {
        
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        if(lastReceived != null) {
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
        List<Meter> mspMeters = new ArrayList<Meter>();
        CollectionRowCallbackHandler<Meter> crcHandler = new CollectionRowCallbackHandler<Meter>(mspMeterRowMapper, mspMeters);
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
        if(lastReceived != null) {
        	sql.append("AND MeterNumber").gt(lastReceived);
        }
        if (excludeDisabled) {
            sql.append("AND DisableFlag").eq_k(YNBoolean.NO);
        }
        sql.append("ORDER BY MeterNumber");

        List<Meter> mspMeters = new ArrayList<Meter>();
        CollectionRowCallbackHandler<Meter> crcHandler = new CollectionRowCallbackHandler<Meter>(mspMeterRowMapper, mspMeters);
        yukonJdbcTemplate.query(sql, new MaxRowCalbackHandlerRse(crcHandler, maxRecords));
    
        MspMeterReturnList mspMeterReturnList = new MspMeterReturnList();
        mspMeterReturnList.setMeters(mspMeters);
        mspMeterReturnList.setReturnFields(mspMeters, maxRecords);
        return mspMeterReturnList;
    }

    /**
     * Returns true is meterNumber is a disconnect meter. 
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
    		
    		List<Meter> cdMeters = yukonJdbcTemplate.query(sql,mspMeterRowMapper);
          return !cdMeters.isEmpty();
      } catch (IncorrectResultSizeDataAccessException e) {
    	  //No results simply mean that the meterNumber is not a CD supported meter 
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
 
    /**
     * Helper method to return a collection of PaoTypes that support "integrated" disconnect capabilities.
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
    	discIntegrated.removeAll(discCollar);	// Remove disconnect collar compatible

    	Collection<PaoType> collection = Collections2.transform(discIntegrated, new Function<PaoDefinition, PaoType>() {
            @Override
            public PaoType apply(PaoDefinition paoDefinition) {
                return paoDefinition.getType();
            }
        });
    	
    	return collection;
    }
}