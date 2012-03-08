package com.cannontech.multispeak.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.Module;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.UtilityInfo;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public final class MspMeterDaoImpl implements MspMeterDao
{
	private YukonJdbcTemplate yukonJdbcTemplate = null;
    private PaoDefinitionDao paoDefinitionDao;
    private static String selectSql;
    
    static {
        selectSql = "SELECT MeterNumber, PaobjectId, Type, Address, SerialNumber, DisconnectAddress " +
                    "FROM YukonPaobject pao " +
                        "JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId " +
                        "LEFT JOIN DeviceCarrierSettings dcs on pao.PAObjectID = dcs.DEVICEID " +
                        "LEFT JOIN DeviceMCT400Series mct ON pao.paobjectId = mct.deviceId " + 
                        "LEFT JOIN RFNAddress rfn ON pao.PAObjectID = rfn.DeviceId";
    };
    
    private static final YukonRowMapper<Meter> mspMeterRowMapper = new YukonRowMapper<Meter>() {
    	public Meter mapRow(YukonResultSet rset) throws SQLException {
            String meterNumber = rset.getString("meternumber");
            int paobjectID = rset.getInt("paobjectid");
            String address = rset.getString("address");
            String serialNumber = rset.getString("serialnumber");
            PaoType paoType = rset.getEnum("type", PaoType.class);
            String discAddress = rset.getString("disconnectaddress");
            PaoIdentifier paoIdentifier = new PaoIdentifier(paobjectID, paoType);
            Meter mspMeter = createMeter(paoIdentifier, meterNumber, address, serialNumber, discAddress);
            return mspMeter;
    	};
   };

    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        if(lastReceived != null) {
        	sql.append("WHERE MeterNumber").gt(lastReceived);
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

    public MspMeterReturnList getCDSupportedMeters(String lastReceived, int maxRecords) {

    	Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("WHERE (pao.type IN (").appendArgumentList(collection).append(")");
        sql.append(" OR (DisconnectAddress IS NOT NULL) )");
        if(lastReceived != null) {
        	sql.append("AND MeterNumber").gt(lastReceived);
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

    /**
     * Creates a new (MSP) Meter object.
     * The information commented out is being used for in-line documentation of available MultiSpeak fields. 
     * @param meterNumber The Multispeak objectID.
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Meter createMeter(PaoIdentifier paoIdentifier, String meterNumber, String carrierAddress, String rfnSerialNumber, String discCollarAddress)
    {
        Meter meter = new Meter();
        meter.setObjectID(meterNumber);
//      MessageElement element = new MessageElement(QName.valueOf("AMRMeterType"), paoType);
//        MessageElement element2 = new MessageElement(QName.valueOf("AMRRdgGrp"), collectionGroup);
//        Extensions ext = new Extensions();
//        ext.set_any(new MessageElement[]{element2});
//        meter.setExtensions(ext);
        meter.setMeterNo(meterNumber);
//        meter.setSerialNumber( );    //Meter serial number. This is the original number assigned to the meter by the manufacturer. This is NOT rfnSerialNumber.
        meter.setMeterType(paoIdentifier.getPaoType().getPaoTypeName());  //Meter type/model.
//        meter.setManufacturer();    //Meter manufacturer.
//        meter.setSealNumberList(null);  //Seal Number List
//        meter.setAMRDeviceType(paoType);	//This is the Yukon Template Field, Yukon doesn't store what this meter used as it's template.
        meter.setAMRVendor(MultispeakDefines.AMR_VENDOR);
        if (carrierAddress != null) {
            meter.setNameplate(getNameplate(meterNumber, carrierAddress));
        } else if (rfnSerialNumber != null) {
            meter.setNameplate(getNameplate(meterNumber, rfnSerialNumber));
        }
//        meter.setSealNumberList(null);  //List of seals applied to this meter.
//        meter.setUtilityInfo(null);     //This information relates the meter to the account with which it is associated
        
        //Add Module for Disconnect information
        if( discCollarAddress != null) {
	        Module discModule = new Module();
	        discModule.setObjectID(discCollarAddress);
	        discModule.setModuleType("Disconnect Collar");
	        meter.setModuleList(new Module[]{discModule}); 
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
   
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}