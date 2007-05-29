package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMCT400Series;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.Extensions;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.Nameplate;
import com.cannontech.multispeak.service.UtilityInfo;

public final class MultispeakDaoImpl implements MultispeakDao
{
    private static final String MSPVENDOR_TABLENAME = "MSPVendor";
    private static final String MSPINTERFACE_TABLENAME = "MSPInterface";

    private static final RowMapper mspVendorRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspVendor(rs);
        };
    };

    private static final RowMapper mspInterfaceRowMapper = new RowMapper() {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspInterface(rs);
        };
    };
    
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
    
    private JdbcOperations jdbcOps;
    private TransactionTemplate transactionTemplate;
    private NextValueHelper nextValueHelper;

    /**
     * Returns the MultispeakVendor object having vendorName.
     * If no match on vendorName is found, an IncorrectResultSizeDataAccessException is thrown
     * If multiple matches on the vendorName, the appName is used to find a better match.
     * When no match on the appname, the (random) first found MultispeakVendor object is returned.
     */
    public MultispeakVendor getMultispeakVendor(String vendorName, String appName) {

        String upperVendorName = vendorName.toUpperCase();
        String sql = "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, UNIQUEKEY, TIMEOUT, URL, " +
                    " APPNAME, OUTUSERNAME, OUTPASSWORD " +
                     " FROM " + MSPVENDOR_TABLENAME + 
                     " WHERE UPPER(COMPANYNAME) = ? " +
                     " ORDER BY COMPANYNAME, APPNAME ";
                
        List mspInterfaces = jdbcOps.query(sql,
                                           new Object[] { upperVendorName},
                                           mspVendorRowMapper);
        
        if( mspInterfaces == null || mspInterfaces.isEmpty()) {
            throw new IncorrectResultSizeDataAccessException(1, 0);
            
        } else if( mspInterfaces.size() > 1) {  //match on app name if possible
            //Find a matching AppName
            for (int i = 0; i < mspInterfaces.size(); i++){
                MultispeakVendor tempVendor = ((MultispeakVendor)mspInterfaces.get(i));
                if (tempVendor.getAppName().equalsIgnoreCase("(none)") ||
                        tempVendor.getAppName().toUpperCase().startsWith(appName.toUpperCase()))
                    return tempVendor; 
            }
//            throw new IncorrectResultSizeDataAccessException(1, mspInterfaces.size());
        }
        return (MultispeakVendor)mspInterfaces.get(0);    //default to the first one in the list.
    }
    
    /**
     * Returns the MultispeakVendor object having vendorIF.
     */
    public MultispeakVendor getMultispeakVendor(int vendorID) {
        try {
                        String sql = "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, UNIQUEKEY, TIMEOUT, URL, " +
                                     " APPNAME, OUTUSERNAME, OUTPASSWORD " +
                                     " FROM " + MSPVENDOR_TABLENAME +
                                     " WHERE VENDORID = ? ";
            
            MultispeakVendor mspVendor = (MultispeakVendor) jdbcOps.queryForObject(sql,
                                                                                   new Object[] { new Integer(vendorID)},
                                                                                   mspVendorRowMapper);
            return mspVendor;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Vendor with VendorID " + vendorID + " cannot be found.");
        }
    }
    
    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID) {
        try {
            String sql = "SELECT VENDORID, INTERFACE, ENDPOINT " +
                         " FROM " + MSPINTERFACE_TABLENAME + 
                         " WHERE VENDORID = ? ";
            
            List<MultispeakInterface> mspInterfaces = jdbcOps.query(sql,
                                                                    new Object[] { vendorID },
                                                                    mspInterfaceRowMapper);
            return mspInterfaces;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Interfaces with vendorID " + vendorID + " cannot be found.");
        }
    }
    
    public List<MultispeakVendor> getMultispeakVendors()
    {
        try {
            String sql = "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, UNIQUEKEY, TIMEOUT, URL, " +
                         " APPNAME, OUTUSERNAME, OUTPASSWORD " +
                         " FROM " + MSPVENDOR_TABLENAME;
            
            List<MultispeakVendor> mspVendors = jdbcOps.query(sql,mspVendorRowMapper);
            
            return mspVendors;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("MSP Vendors cannot be found.");
        }
    }
    
    public int deleteMultispeakInterface(int vendorID)
    {
        try {
            String sql = "DELETE FROM " + MSPINTERFACE_TABLENAME + 
                         " WHERE VENDORID = ? ";
            
            int numDeleted = jdbcOps.update(sql,new Object[]{new Integer(vendorID)});
            
            return numDeleted;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Multispeak Interfaces were found for vendorID "+ vendorID +" for deletion.");
        }        
    }
    
    public int addMultispeakInterfaces(MultispeakInterface mspInterface)
    {
        try {
            String sql = "INSERT INTO " + MSPINTERFACE_TABLENAME + 
                         " VALUES (?, ?, ?)";
            
            int numAdded = jdbcOps.update(sql,new Object[]{mspInterface.getVendorID(), 
                                                           mspInterface.getMspInterface(),
                                                           mspInterface.getMspEndpoint()});
            
            return numAdded;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Multispeak Interface was inserted.");
        }        
    }
    
    public void updateMultispeakVendor(final MultispeakVendor mspVendor)
    {
        transactionTemplate.execute(new TransactionCallbackWithoutResult(){
            protected void doInTransactionWithoutResult(TransactionStatus status){
                try {
                    String sql = "UPDATE " + MSPVENDOR_TABLENAME + 
                                 " SET VENDORID = ?, " +
                                 " COMPANYNAME = ?, " +
                                 " USERNAME = ?, " +
                                 " PASSWORD = ?, " +
                                 " UNIQUEKEY = ?, " +
                                 " TIMEOUT = ?, " +
                                 " URL = ?, " +
                                 " APPNAME = ?, " +
                                 " OUTUSERNAME = ?, " +
                                 " OUTPASSWORD = ? " +
                                 " WHERE VENDORID = ? ";
                    
                    Object [] args = new Object[] {
                        mspVendor.getVendorID(),
                        mspVendor.getCompanyName(), 
                        mspVendor.getUserName(),
                        mspVendor.getPassword(),
                        mspVendor.getUniqueKey(),
                        new Integer(mspVendor.getTimeout()),
                        mspVendor.getUrl(),
                        mspVendor.getAppName(),
                        mspVendor.getOutUserName(),
                        mspVendor.getOutPassword(),                        
                        mspVendor.getVendorID() //Where Clause
                    };
                    jdbcOps.update(sql, args);
                    
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not updated for vendorID "+ mspVendor.getVendorID() +".");
                }   
                
                deleteMultispeakInterface(mspVendor.getVendorID().intValue());
                for (int i = 0; i < mspVendor.getMspInterfaces().size(); i++)
                    addMultispeakInterfaces(mspVendor.getMspInterfaces().get(i));
            };
        });
        
    }
    public void addMultispeakVendor(final MultispeakVendor mspVendor)
    {
        transactionTemplate.execute(new TransactionCallbackWithoutResult(){
            protected void doInTransactionWithoutResult(TransactionStatus status){
                try {
                    mspVendor.setVendorID(getNextVendorId());
                    String sql = "INSERT INTO " + MSPVENDOR_TABLENAME + 
                                 " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    Object [] args = new Object[] {
                        mspVendor.getVendorID(),
                        mspVendor.getCompanyName(), 
                        mspVendor.getUserName(),
                        mspVendor.getPassword(),
                        mspVendor.getUniqueKey(),
                        new Integer(mspVendor.getTimeout()),
                        mspVendor.getUrl(),
                        mspVendor.getAppName(),
                        mspVendor.getOutUserName(),
                        mspVendor.getOutPassword()
                    };
                    jdbcOps.update(sql, args);
                    
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not inserted for Company Name "+ mspVendor.getCompanyName() +".");
                }                
                for (int i = 0; i < mspVendor.getMspInterfaces().size(); i++){
                    MultispeakInterface mspInterface = mspVendor.getMspInterfaces().get(i);
                    mspInterface.setVendorID(mspVendor.getVendorID());
                    addMultispeakInterfaces(mspInterface);
                }
            };
        });        
    }
    
    public void deleteMultispeakVendor(final int vendorID)
    {
        transactionTemplate.execute(new TransactionCallbackWithoutResult(){
            protected void doInTransactionWithoutResult(TransactionStatus status){
                deleteMultispeakInterface(vendorID);
                try {
                    String sql = "DELETE FROM " + MSPVENDOR_TABLENAME + 
                    " WHERE VENDORID = ? ";
                    
                    jdbcOps.update(sql,new Object[]{new Integer(vendorID)});
                    
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not deleted for VendorID "+ vendorID +".");
                }                
            };
        });        
    }    
    public int getNextVendorId() {
        return nextValueHelper.getNextValue("MSPVendor");
    }
    
    private static MultispeakVendor createMspVendor( ResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt(1));
        String companyName = rset.getString(2).trim();
        String userName = rset.getString(3).trim();
        String password = rset.getString(4).trim();
        String uniqueKey = rset.getString(5).trim();
        int timeout = rset.getInt(6);
        String url = rset.getString(7).trim();
        String appName = rset.getString(8).trim();
        String outUserName = rset.getString(9).trim();
        String outPassword = rset.getString(10).trim();
        
        MultispeakVendor mspVendor = new MultispeakVendor(vendorID, companyName, appName, 
                                                          userName, password, outUserName, outPassword,
                                                          uniqueKey,  timeout, url);
        return mspVendor;
    }

    private static MultispeakInterface createMspInterface( ResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt(1));
        String interfaceStr = rset.getString(2).trim();
        String endpoint = rset.getString(3).trim();
        
        MultispeakInterface mspInterface= new MultispeakInterface( vendorID, interfaceStr, endpoint);
        return mspInterface;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    /**
     * @param transactionTemplate The transactionTemplate to set.
     */
    public void setTransactionTemplate(TransactionTemplate transactionTemplate)
    {
        this.transactionTemplate = transactionTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public List<Meter> getAMRSupportedMeters(String lastReceived, String key) {
        try {
            String uniqueKey = "METERNUMBER";
            if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                uniqueKey = "PAONAME"; 

            if( lastReceived == null)
            	lastReceived = "";

            String sql = "SELECT " + uniqueKey + ", COLLECTIONGROUP, BILLINGGROUP, PAOBJECTID, ADDRESS, TYPE " +
                         " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " + 
                         YukonPAObject.TABLE_NAME + " pao, " +
                         DeviceCarrierSettings.TABLE_NAME + " dcs " +
                         " WHERE DMG.DEVICEID = PAO.PAOBJECTID" + 
                         " AND PAO.PAOBJECTID = DCS.DEVICEID " +
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
            List<Meter> mspMeters = new ArrayList<Meter>();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, MultispeakDefines.MAX_RETURN_RECORDS));
            return mspMeters;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No results found >= objectID " + lastReceived + ".");
        }
    }

    public List<Meter> getCDSupportedMeters(String lastReceived, String key) {
        try {
            String uniqueKey = "METERNUMBER";
            if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                uniqueKey = "PAONAME"; 
            
            if( lastReceived == null)
            	lastReceived = "";

            String sql = "SELECT " + uniqueKey + ", COLLECTIONGROUP, BILLINGGROUP, PAOBJECTID, ADDRESS, TYPE, DISCONNECTADDRESS " +
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
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
          
            List<Meter> mspMeters = new ArrayList<Meter>();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspCDMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, MultispeakDefines.MAX_RETURN_RECORDS));
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

          String sql = "SELECT " + uniqueKey + ", COLLECTIONGROUP, BILLINGGROUP, PAOBJECTID, ADDRESS, TYPE, DISCONNECTADDRESS " +
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
        String collectionGroup = rset.getString(2).trim();
        String billingGroup = rset.getString(3).trim();
        int paobjectID = rset.getInt(4);
        String address = rset.getString(5).trim();
        String paoType = rset.getString(6).trim();

        Meter mspMeter = createMeter(objectID, paoType, collectionGroup, billingGroup, address);
        return mspMeter;
    }
    
    private static Meter createMspCDMeter( ResultSet rset) throws SQLException {

        String objectID = rset.getString(1).trim();
        String collectionGroup = rset.getString(2).trim();
        String billingGroup = rset.getString(3).trim();
        int paobjectID = rset.getInt(4);
        String address = rset.getString(5).trim();
        String paoType = rset.getString(6).trim();
        String discAddress = rset.getString(7);
        
        if( (paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410CL[0]) ||
            paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410IL[0]) ) &&
            discAddress == null)
            return null;
        
        Meter mspMeter = createMeter(objectID, paoType, collectionGroup, billingGroup, address);
        return mspMeter;
    }
    
    /**
     * Creates a new (MSP) Meter object.
     * @param objectID The Multispeak objectID.
     * @param address The meter's transponderID (Physical Address)
     * @return
     */
    public static Meter createMeter(String objectID, String paoType, String collectionGroup, String billingGroup, String address)
    {
        Meter meter = new Meter();
        meter.setObjectID(objectID);
//      MessageElement element = new MessageElement(QName.valueOf("AMRMeterType"), paoType);
        MessageElement element2 = new MessageElement(QName.valueOf("AMRRdgGrp"), collectionGroup);
        Extensions ext = new Extensions();
        ext.set_any(new MessageElement[]{element2});
        meter.setExtensions(ext);
        meter.setMeterNo(objectID);
//        meter.setSerialNumber( );    //Meter serial number. This is the original number assigned to the meter by the manufacturer.
        meter.setMeterType(paoType);  //Meter type/model.
//        meter.setManufacturer();    //Meter manufacturer.
//        meter.setSealNumberList(null);  //Seal Number List
        meter.setAMRDeviceType(paoType);
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