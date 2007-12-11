package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMCT400Series;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.Nameplate;
import com.cannontech.multispeak.service.UtilityInfo;

public final class MspMeterDaoImpl implements MspMeterDao
{
    private JdbcOperations jdbcOps;

    private static final RowMapper mspMeterRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspMeter(rs);
        };
    };
    
    private static final RowMapper mspCDMeterRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspCDMeter(rs);
        };
    };

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    public List<Meter> getAMRSupportedMeters(String lastReceived, String key, int maxRecords) {
        try {
            String uniqueKey = "METERNUMBER";
            if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                uniqueKey = "PAONAME"; 

            if( lastReceived == null)
            	lastReceived = "";

            String sql = "SELECT " + uniqueKey + ", PAOBJECTID, ADDRESS, TYPE " +
                         " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " + 
                         YukonPAObject.TABLE_NAME + " pao, " +
                         DeviceCarrierSettings.TABLE_NAME + " dcs " +
                         " WHERE DMG.DEVICEID = PAO.PAOBJECTID" + 
                         " AND PAO.PAOBJECTID = DCS.DEVICEID " +
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
            List<Meter> mspMeters = new ArrayList<Meter>();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, maxRecords));
            return mspMeters;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No results found >= objectID " + lastReceived + ".");
        }
    }

    public List<Meter> getCDSupportedMeters(String lastReceived, String key, int maxRecords) {
        try {
            String uniqueKey = "METERNUMBER";
            if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                uniqueKey = "PAONAME"; 
            
            if( lastReceived == null)
            	lastReceived = "";

            String sql = "SELECT " + uniqueKey + ", PAOBJECTID, ADDRESS, TYPE, DISCONNECTADDRESS " +
            			 " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " +
            			 DeviceCarrierSettings.TABLE_NAME + " dcs, " +
            			 YukonPAObject.TABLE_NAME + " pao left outer join " + DeviceMCT400Series.TABLE_NAME + " mct on pao.paobjectid = mct.deviceid " +
            			 " WHERE DMG.DEVICEID = PAO.PAOBJECTID" +
            			 " AND PAO.PAOBJECTID = DCS.DEVICEID " +
            			 " AND ( PAO.TYPE in ('" + DeviceTypes.STRING_MCT_213[0] + "', " +
                       			             "'" + DeviceTypes.STRING_MCT_310ID[0] + "', " +
                       			             "'" + DeviceTypes.STRING_MCT_310IDL[0] + "') " +
                               " OR ( DISCONNECTADDRESS IS NOT NULL) ) " +
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
          
            List<Meter> mspMeters = new ArrayList<Meter>();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspCDMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, maxRecords));
            return mspMeters;
        } catch (IncorrectResultSizeDataAccessException e) {
        	throw new NotFoundException("No results found >= objectID " + lastReceived + ".");
        }
    }

    /**
     * Returns true is objectID (meter) is a disconnect meter. 
     * @param key
     * @return
     */
    public boolean isCDSupportedMeter(String objectID, String key) {
      try {
          String uniqueKey = "METERNUMBER";
          if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
              uniqueKey = "PAONAME"; 

          String sql = "SELECT " + uniqueKey + ", PAOBJECTID, ADDRESS, TYPE, DISCONNECTADDRESS " +
                       " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " +
                       DeviceCarrierSettings.TABLE_NAME + " dcs, " +
                       YukonPAObject.TABLE_NAME + " pao left outer join " + DeviceMCT400Series.TABLE_NAME + " mct on pao.paobjectid = mct.deviceid " +
                       " WHERE DMG.DEVICEID = PAO.PAOBJECTID" + 
                       " AND PAO.PAOBJECTID = DCS.DEVICEID " +
                       " AND ( PAO.TYPE in ('" + DeviceTypes.STRING_MCT_213[0] + "', " +
                       					   "'" + DeviceTypes.STRING_MCT_310ID[0] + "', " +
                       					   "'" + DeviceTypes.STRING_MCT_310IDL[0] + "') " +
                       		 " OR ( PAO.TYPE in ('" + DeviceTypes.STRING_MCT_410CL[0] + "', " +
                       		 					"'" + DeviceTypes.STRING_MCT_410IL[0] + "') " +
                       		 					" AND DISCONNECTADDRESS IS NOT NULL) ) " +
                       " AND " + uniqueKey + " = ?";

          List<Meter> cdMeters = jdbcOps.query(sql, new Object[] { objectID}, mspCDMeterRowMapper);
          return !cdMeters.isEmpty();
      } catch (IncorrectResultSizeDataAccessException e) {
    	  //No results simply mean that the meterNumber is not a CD supported meter 
    	  if (e.getActualSize() > 0)
    		  return true;
    	  return false;
      }
    }
    
    private static Meter createMspMeter( ResultSet rset) throws SQLException {

        String objectID = rset.getString(1).trim();
        int paobjectID = rset.getInt(2);
        String address = rset.getString(3).trim();
        String paoType = rset.getString(4).trim();

        Meter mspMeter = createMeter(objectID, paoType, address);
        return mspMeter;
    }
    
    private static Meter createMspCDMeter( ResultSet rset) throws SQLException {

        String objectID = rset.getString(1).trim();
        int paobjectID = rset.getInt(2);
        String address = rset.getString(3).trim();
        String paoType = rset.getString(4).trim();
        String discAddress = rset.getString(5);
        
        if( (paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410CL[0]) ||
            paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410IL[0]) ) &&
            discAddress == null)
            return null;
        
        Meter mspMeter = createMeter(objectID, paoType, address);
        return mspMeter;
    }
    
    /**
     * Creates a new (MSP) Meter object.
     * @param objectID The Multispeak objectID.
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Meter createMeter(String objectID, String paoType, String address)
    {
        Meter meter = new Meter();
        meter.setObjectID(objectID);
//      MessageElement element = new MessageElement(QName.valueOf("AMRMeterType"), paoType);
//        MessageElement element2 = new MessageElement(QName.valueOf("AMRRdgGrp"), collectionGroup);
//        Extensions ext = new Extensions();
//        ext.set_any(new MessageElement[]{element2});
//        meter.setExtensions(ext);
        meter.setMeterNo(objectID);
//        meter.setSerialNumber( );    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        meter.setMeterType(paoType);  //Meter type/model.
//        meter.setManufacturer();    //Meter manufacturer.
//        meter.setSealNumberList(null);  //Seal Number List
//        meter.setAMRDeviceType(paoType);	//This is the Yukon Template Field, Yukon doesn't store what this meter used as it's template.
        meter.setAMRVendor(MultispeakDefines.AMR_VENDOR);
        meter.setNameplate(getNameplate(objectID, address));
//        meter.setSealNumberList(null);  //List of seals applied to this meter.
//        meter.setUtilityInfo(null);     //This information relates the meter to the account with which it is associated
        
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
     * @param objectID The multispeak objectID
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Nameplate getNameplate(String objectID, String address)
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
     * @param objectID The Multispeak objectID
     * @return
     */
    public static UtilityInfo getUtilityInfo(String objectID)
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
}