package com.cannontech.multispeak.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;

public final class MultispeakDaoImpl implements MultispeakDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;

    private static final Map<String, MultispeakVendor> vendorCache =
        Collections.synchronizedMap(new HashMap<String, MultispeakVendor>());
    private static final String MSPVENDOR_TABLENAME = "MSPVendor";
    private static final String MSPINTERFACE_TABLENAME = "MSPInterface";

    private final YukonRowMapper<MultispeakVendor> mspVendorRowMapper = new YukonRowMapper<MultispeakVendor>() {
        @Override
        public MultispeakVendor mapRow(YukonResultSet rs) throws SQLException {
            return createMspVendor(rs);
        }
    };

    private final YukonRowMapper<MultispeakInterface> mspInterfaceRowMapper =
        new YukonRowMapper<MultispeakInterface>() {
            @Override
            public MultispeakInterface mapRow(YukonResultSet rs) throws SQLException {
                return createMspInterface(rs);
            };
        };

    @Override
    public synchronized MultispeakVendor getMultispeakVendorFromCache(String vendorName, String appName) {
        String key = vendorName + appName;
        MultispeakVendor vendor = vendorCache.get(key);
        if (vendor == null) {
            vendor = getMultispeakVendor(vendorName, appName);
            vendorCache.put(key, vendor);
        }
        return vendor;
    }

    public synchronized void clearMultispeakVendorCache() {
        vendorCache.clear();
    }

    /**
     * Returns the MultispeakVendor object having vendorName.
     * If no match on vendorName is found, an IncorrectResultSizeDataAccessException is thrown
     * If multiple matches on the vendorName, the appName is used to find a better match.
     * When no match on the appname, the (random) first found MultispeakVendor object is returned.
     * Throws NotFoundException if vendorName (and/or appName) are not found.
     */
    @Override
    public MultispeakVendor getMultispeakVendor(String vendorName, String appName) throws NotFoundException {

        String upperVendorName = vendorName.toUpperCase();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD,");
        sql.append("APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,");
        sql.append("MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT, ValidateCertificate");
        sql.append("FROM " + MSPVENDOR_TABLENAME);
        sql.append("WHERE UPPER(COMPANYNAME)").eq(upperVendorName);
        sql.append("ORDER BY COMPANYNAME, APPNAME");

        List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper);

        if (mspVendors == null || mspVendors.isEmpty()) {
            throw new NotFoundException("Company and/or Appname are not defined. Company: " + vendorName + " AppName: " + appName);
        } else if (mspVendors.size() > 1) { // match on app name if possible
            // Find a matching AppName
            for (int i = 0; i < mspVendors.size(); i++) {
                MultispeakVendor tempVendor = mspVendors.get(i);
                if (tempVendor.getAppName().equalsIgnoreCase("(none)")
                    || StringUtils.startsWithIgnoreCase(tempVendor.getAppName(), appName))
                    return tempVendor;
            }
            // throw new IncorrectResultSizeDataAccessException(1, mspInterfaces.size());
        }
        return mspVendors.get(0); // default to the first one in the list.
    }

    /**
     * Returns the MultispeakVendor object having vendorIF.
     */
    @Override
    public MultispeakVendor getMultispeakVendor(int vendorID) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD,");
            sql.append("APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,");
            sql.append("MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT, ValidateCertificate");
            sql.append("FROM " + MSPVENDOR_TABLENAME);
            sql.append("WHERE VENDORID").eq(vendorID);

            MultispeakVendor mspVendor = jdbcTemplate.queryForObject(sql, mspVendorRowMapper);
            return mspVendor;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Vendor with VendorID " + vendorID + " cannot be found.");
        }
    }

    @Override
    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT VENDORID, INTERFACE, ENDPOINT, VERSION");
            sql.append("FROM " + MSPINTERFACE_TABLENAME);
            sql.append("WHERE VENDORID").eq(vendorID);

            List<MultispeakInterface> mspInterfaces = jdbcTemplate.query(sql, mspInterfaceRowMapper);
            return mspInterfaces;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Interfaces with vendorID " + vendorID + " cannot be found.");
        }
    }

    @Override
    public List<MultispeakVendor> getMultispeakVendors(boolean excludeCannon) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD,");
            sql.append("APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,");
            sql.append("MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT, ValidateCertificate");
            sql.append("FROM " + MSPVENDOR_TABLENAME);
            if (excludeCannon) {
                sql.append("WHERE VENDORID").neq(MultispeakVendor.CANNON_MSP_VENDORID);
            }
            List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper);

            return mspVendors;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("MSP Vendors cannot be found.");
        }
    }

    @Override
    public List<MultispeakVendor> getMultispeakCISVendors() {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DISTINCT V.VENDORID, COMPANYNAME, USERNAME, PASSWORD,");
            sql.append("APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,");
            sql.append("MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT, ValidateCertificate");
            sql.append("FROM MSPVENDOR V JOIN MSPINTERFACE I on V.VENDORID = I.VENDORID");
            sql.append("WHERE INTERFACE").eq(MultispeakDefines.CB_Server_STR);

            List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper);

            return mspVendors;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("MSP Vendors cannot be found.");
        }
    }

    @Override
    public synchronized int deleteMultispeakInterface(int vendorID) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM " + MSPINTERFACE_TABLENAME);
            sql.append("WHERE VendorId").eq(vendorID);

            int numDeleted = jdbcTemplate.update(sql);
            clearMultispeakVendorCache();
            return numDeleted;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Multispeak Interfaces were found for vendorID " + vendorID
                + " for deletion.");
        }
    }

    @Override
    public synchronized int addMultispeakInterfaces(MultispeakInterface mspInterface) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto(MSPINTERFACE_TABLENAME);
            sink.addValue("VendorID", mspInterface.getVendorID());
            sink.addValue("Interface", mspInterface.getMspInterface().trim());
            sink.addValue("Endpoint", mspInterface.getMspEndpoint().trim());
            sink.addValue("Version", mspInterface.getVersion());
            
            int numAdded = jdbcTemplate.update(sql);
            clearMultispeakVendorCache();
            return numAdded;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No Multispeak Interface was inserted.");
        }
    }

    @Override
    public synchronized void updateMultispeakVendor(final MultispeakVendor mspVendor) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    SqlParameterSink sink = sql.update(MSPVENDOR_TABLENAME);
                    sink.addValue("VendorID", mspVendor.getVendorID());
                    sink.addValue("CompanyName", mspVendor.getCompanyName().trim());
                    sink.addValueSafe("UserName", mspVendor.getUserName().trim());
                    sink.addValueSafe("Password", mspVendor.getPassword().trim());
                    sink.addValueSafe("AppName", mspVendor.getAppName().trim());
                    sink.addValueSafe("OutUserName", mspVendor.getOutUserName().trim());
                    sink.addValueSafe("OutPassword", mspVendor.getOutPassword().trim());
                    sink.addValue("MaxReturnRecords", mspVendor.getMaxReturnRecords());
                    sink.addValue("RequestMessageTimeout", mspVendor.getRequestMessageTimeout());
                    sink.addValue("MaxInitiateRequestObjects", mspVendor.getMaxInitiateRequestObjects());
                    sink.addValue("TemplateNameDefault", mspVendor.getTemplateNameDefault().trim());
                    sink.addValue("ValidateCertificate", mspVendor.getValidateCertificate());
                    sql.append("WHERE VendorId").eq(mspVendor.getVendorID());

                    jdbcTemplate.update(sql);

                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not updated for vendorID " + mspVendor.getVendorID() + ".");
                }
                if (mspVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
                    deleteMultispeakInterface(mspVendor.getVendorID().intValue());
                    for (MultispeakInterface mspInterface : mspVendor.getMspInterfaces()) {
                        addMultispeakInterfaces(mspInterface);
                    }
                } else {
                    deleteMultispeakInterface(mspVendor.getVendorID().intValue());
                    for (MultispeakInterface mspInterface : mspVendor.getMspInterfaces()) {
                        addMultispeakInterfaces(mspInterface);
                    }
                }
                clearMultispeakVendorCache();
            };
        });
        if (mspVendor.getVendorID() != MultispeakVendor.CANNON_MSP_VENDORID) {
            dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MULTISPEAK, mspVendor.getVendorID());
        }
    }

    @Override
    public synchronized void addMultispeakVendor(final MultispeakVendor mspVendor) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    mspVendor.setVendorID(getNextVendorId());

                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    SqlParameterSink sink = sql.insertInto(MSPVENDOR_TABLENAME);
                    sink.addValue("VendorID", mspVendor.getVendorID());
                    sink.addValue("CompanyName", mspVendor.getCompanyName().trim());
                    sink.addValueSafe("UserName", mspVendor.getUserName().trim());
                    sink.addValueSafe("Password", mspVendor.getPassword().trim());
                    sink.addValueSafe("AppName", mspVendor.getAppName().trim());
                    sink.addValueSafe("OutUserName", mspVendor.getOutUserName().trim());
                    sink.addValueSafe("OutPassword", mspVendor.getOutPassword().trim());
                    sink.addValue("MaxReturnRecords", mspVendor.getMaxReturnRecords());
                    sink.addValue("RequestMessageTimeout", mspVendor.getRequestMessageTimeout());
                    sink.addValue("MaxInitiateRequestObjects", mspVendor.getMaxInitiateRequestObjects());
                    sink.addValue("TemplateNameDefault", mspVendor.getTemplateNameDefault().trim());
                    sink.addValue("ValidateCertificate", mspVendor.getValidateCertificate());

                    jdbcTemplate.update(sql);

                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new DataIntegrityViolationException("Multispeak Vendor not inserted for Company Name "
                        + mspVendor.getCompanyName() + ".");
                }
                for (MultispeakInterface mspInterface : mspVendor.getMspInterfaces()) {
                    mspInterface.setVendorID(mspVendor.getVendorID());
                    addMultispeakInterfaces(mspInterface);
                }

                clearMultispeakVendorCache();
            };
        });
        if (mspVendor.getVendorID() != MultispeakVendor.CANNON_MSP_VENDORID) {
            dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.MULTISPEAK, mspVendor.getVendorID());
        }
    }

    @Override
    public synchronized void deleteMultispeakVendor(final int vendorID) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                deleteMultispeakInterface(vendorID);
                try {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("DELETE FROM " + MSPVENDOR_TABLENAME);
                    sql.append("WHERE VendorId").eq(vendorID);

                    jdbcTemplate.update(sql);
                    clearMultispeakVendorCache();
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not deleted for VendorID " + vendorID + ".");
                }
            };
        });
        if (vendorID != MultispeakVendor.CANNON_MSP_VENDORID) {
            dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.MULTISPEAK, vendorID);
        }
    }

    @Override
    public int getNextVendorId() {
        return nextValueHelper.getNextValue("MSPVendor");
    }

    private MultispeakVendor createMspVendor(YukonResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt("VendorID"));
        String companyName = rset.getString("CompanyName");
        String userName = rset.getStringSafe("UserName");
        String password = rset.getStringSafe("Password");
        String appName = rset.getStringSafe("AppName");
        String outUserName = rset.getStringSafe("OutUserName");
        String outPassword = rset.getStringSafe("OutPassword");
        int maxReturnRecords = rset.getInt("MaxReturnRecords");
        long requestMessageTimeout = rset.getLong("RequestMessageTimeout");
        long maxInitiateRequestObjects = rset.getLong("MaxInitiateRequestObjects");
        String templateNameDefault = rset.getString("TemplateNameDefault").trim();
        Boolean validateCertificate = rset.getBoolean("ValidateCertificate");

        MultispeakVendor mspVendor = new MultispeakVendor(vendorID, companyName, appName, userName, password,
            outUserName, outPassword, maxReturnRecords, requestMessageTimeout, maxInitiateRequestObjects,
            templateNameDefault, validateCertificate);

        List<MultispeakInterface> multispeakInterfaces = getMultispeakInterfaces(vendorID);
        mspVendor.setMspInterfaces(multispeakInterfaces);

        return mspVendor;
    }

    private static MultispeakInterface createMspInterface(YukonResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt("VendorID"));
        String interfaceStr = rset.getString("Interface");
        String endpoint = rset.getString("Endpoint");
        MultiSpeakVersion version = rset.getEnum("Version", MultiSpeakVersion.class);
        MultispeakInterface mspInterface = new MultispeakInterface(vendorID, interfaceStr, endpoint, version);
        return mspInterface;
    }

    @Override
    public boolean isUniqueName(String companyName, String appName, Integer vendorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM MSPVendor");
        sql.append("WHERE CompanyName").eq(companyName);
        sql.append("AND AppName").eq(appName);
        if (vendorId != null) {
            sql.append("AND VendorID").neq(vendorId);
        }
        int duplicateNames = jdbcTemplate.queryForInt(sql);
        return duplicateNames != 0;
    }
}