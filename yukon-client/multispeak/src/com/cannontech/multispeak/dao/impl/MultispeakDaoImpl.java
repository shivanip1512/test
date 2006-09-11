package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.yukon.IDatabaseCache;

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
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;

    public MultispeakVendor getMultispeakVendor(String vendorName) {
        try {
            String upperVendorName = vendorName.toUpperCase();
            String sql = "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, UNIQUEKEY, TIMEOUT, URL" +
                         " FROM " + MSPVENDOR_TABLENAME + 
                         " WHERE UPPER(COMPANYNAME) = ? ";
            
            MultispeakVendor mspVendor = (MultispeakVendor) jdbcOps.queryForObject(sql,
                                                                                   new Object[] { upperVendorName },
                                                                                   mspVendorRowMapper);
            return mspVendor;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Vendor with Company name " + vendorName + " cannot be found.");
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
            String sql = "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, UNIQUEKEY, TIMEOUT, URL " +
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
            
            int numDeleted = jdbcOps.update(sql,new Object[]{vendorID});
            
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
                                 " URL = ? " +
                                 " WHERE VENDORID = ? ";
                    
                    Object [] args = new Object[] {
                        mspVendor.getVendorID(),
                        mspVendor.getCompanyName(), 
                        mspVendor.getUserName(),
                        mspVendor.getPassword(),
                        mspVendor.getUniqueKey(),
                        mspVendor.getTimeout(),
                        mspVendor.getUrl(),
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
                                 " VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
                    Object [] args = new Object[] {
                        mspVendor.getVendorID(),
                        mspVendor.getCompanyName(), 
                        mspVendor.getUserName(),
                        mspVendor.getPassword(),
                        mspVendor.getUniqueKey(),
                        mspVendor.getTimeout(),
                        mspVendor.getUrl()                        
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
                    
                    jdbcOps.update(sql,new Object[]{vendorID});
                    
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

        int vendorID = rset.getInt(1);
        String companyName = rset.getString(2).trim();
        String userName = rset.getString(3).trim();
        String password = rset.getString(4).trim();
        String uniqueKey = rset.getString(5).trim();
        int timeout = rset.getInt(6);
        String url = rset.getString(7).trim();

        MultispeakVendor mspVendor = new MultispeakVendor(vendorID,
                                                      companyName, 
                                                      userName, 
                                                      password,
                                                      uniqueKey, 
                                                      timeout,
                                                      url);
        return mspVendor;
    }

    private static MultispeakInterface createMspInterface( ResultSet rset) throws SQLException {

        int vendorID = rset.getInt(1);
        String interfaceStr = rset.getString(2).trim();
        String endpoint = rset.getString(3).trim();
        
        MultispeakInterface mspInterface= new MultispeakInterface( vendorID, interfaceStr, endpoint);
        return mspInterface;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
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

            String sql = "SELECT " + uniqueKey + ", COLLECTIONGROUP, PAOBJECTID, ADDRESS " +
                         " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " + 
                         YukonPAObject.TABLE_NAME + " pao, " +
                         DeviceCarrierSettings.TABLE_NAME + " dcs " +
                         " WHERE DMG.DEVICEID = PAO.PAOBJECTID" + 
                         " AND PAO.PAOBJECTID = DCS.DEVICEID " +
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
            List mspMeters = new ArrayList<Meter>();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, MultispeakDefines.MAX_RETURN_RECORDS));
            return mspMeters;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No results found >= objectID " + lastReceived + ".");
        }
    }

    public List getCDSupportedMeters(String lastReceived, String key) {
        try {
            String uniqueKey = "METERNUMBER";
            if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
                uniqueKey = "PAONAME"; 

            String sql = "SELECT " + uniqueKey + ", COLLECTIONGROUP, PAOBJECTID, ADDRESS, TYPE, DISCONNECTADDRESS " +
                         " FROM " + DeviceMeterGroup.TABLE_NAME + " dmg, " +
                         DeviceCarrierSettings.TABLE_NAME + " dcs, " +
                         YukonPAObject.TABLE_NAME + " pao left outer join " + DeviceMCT400Series.TABLE_NAME + " mct on pao.paobjectid = mct.deviceid " +
                         " WHERE DMG.DEVICEID = PAO.PAOBJECTID" + 
                         " AND PAO.PAOBJECTID = DCS.DEVICEID " +
                         " AND PAO.TYPE in ('" + DeviceTypes.STRING_MCT_213[0] + "', " + 
                                           "'" + DeviceTypes.STRING_MCT_310ID[0] + "', " +
                                           "'" + DeviceTypes.STRING_MCT_310IDL[0] + "', " +
                                           "'" + DeviceTypes.STRING_MCT_410CL[0] + "', " +
                                           "'" + DeviceTypes.STRING_MCT_410IL[0] + "') " +
                         " AND " + uniqueKey + " > ? ORDER BY " + uniqueKey; 
            List mspMeters = new ArrayList();
            ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, mspCDMeterRowMapper);
            jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, MultispeakDefines.MAX_RETURN_RECORDS));
            return mspMeters;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No results found >= objectID " + lastReceived + ".");
        }
    }
    
    private static Meter createMspMeter( ResultSet rset) throws SQLException {

        String objectID = rset.getString(1).trim();
        String collectionGroup = rset.getString(2).trim();
        int paobjectID = rset.getInt(3);
        String address = rset.getString(4).trim();

        Meter mspMeter = MultispeakFuncs.createMeter(objectID, address);
        return mspMeter;
    }
    
    private static Meter createMspCDMeter( ResultSet rset) throws SQLException {

        String objectID = rset.getString(1).trim();
        String collectionGroup = rset.getString(2).trim();
        int paobjectID = rset.getInt(3);
        String address = rset.getString(4).trim();
        String paoType = rset.getString(5).trim();
        String discAddress = rset.getString(6);
        
        if( (paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410CL[0]) ||
            paoType.equalsIgnoreCase(DeviceTypes.STRING_MCT_410IL[0]) ) &&
            discAddress == null)
            return null;
        
        Meter mspMeter = MultispeakFuncs.createMeter(objectID, address);
        return mspMeter;
    }    
}