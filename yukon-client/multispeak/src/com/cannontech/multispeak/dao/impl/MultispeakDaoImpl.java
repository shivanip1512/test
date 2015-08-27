package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;

public final class MultispeakDaoImpl implements MultispeakDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private static final Map<String, MultispeakVendor> vendorCache =
        Collections.synchronizedMap(new HashMap<String, MultispeakVendor>());
    private static final String MSPVENDOR_TABLENAME = "MSPVendor";
    private static final String MSPINTERFACE_TABLENAME = "MSPInterface";

    private final ParameterizedRowMapper<MultispeakVendor> mspVendorRowMapper =
        new ParameterizedRowMapper<MultispeakVendor>() {
            @Override
            public MultispeakVendor mapRow(ResultSet rs, int rowNum) throws SQLException {
                return createMspVendor(rs);
            }
        };

    private final ParameterizedRowMapper<MultispeakInterface> mspInterfaceRowMapper =
        new ParameterizedRowMapper<MultispeakInterface>() {
            @Override
            public MultispeakInterface mapRow(ResultSet rs, int rowNum) throws SQLException {
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
        String sql =
            "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, URL, "
                + " APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT, "
                + " MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT " + " FROM " + MSPVENDOR_TABLENAME
                + " WHERE UPPER(COMPANYNAME) = ? " + " ORDER BY COMPANYNAME, APPNAME ";

        List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper, upperVendorName);

        if (mspVendors == null || mspVendors.isEmpty()) {
            throw new NotFoundException("Company and/or Appname are not defined.");

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
            String sql =
                "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, URL, "
                    + " APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,"
                    + " MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT " + " FROM " + MSPVENDOR_TABLENAME
                    + " WHERE VENDORID = ? ";

            MultispeakVendor mspVendor = jdbcTemplate.queryForObject(sql, mspVendorRowMapper, vendorID);
            return mspVendor;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Vendor with VendorID " + vendorID + " cannot be found.");
        }
    }

    @Override
    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID) {
        try {
            String sql =
                "SELECT VENDORID, INTERFACE, ENDPOINT " + " FROM " + MSPINTERFACE_TABLENAME + " WHERE VENDORID = ? ";

            List<MultispeakInterface> mspInterfaces = jdbcTemplate.query(sql, mspInterfaceRowMapper, vendorID);
            return mspInterfaces;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A MSP Interfaces with vendorID " + vendorID + " cannot be found.");
        }
    }

    @Override
    public List<MultispeakVendor> getMultispeakVendors() {
        try {
            String sql =
                "SELECT VENDORID, COMPANYNAME, USERNAME, PASSWORD, URL, "
                    + " APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,"
                    + " MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT " + " FROM " + MSPVENDOR_TABLENAME;

            List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper);

            return mspVendors;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("MSP Vendors cannot be found.");
        }
    }

    @Override
    public List<MultispeakVendor> getMultispeakCISVendors() {
        try {
            String sql =
                "SELECT V.VENDORID, COMPANYNAME, USERNAME, PASSWORD, URL, "
                    + " APPNAME, OUTUSERNAME, OUTPASSWORD, MAXRETURNRECORDS, REQUESTMESSAGETIMEOUT,"
                    + " MAXINITIATEREQUESTOBJECTS, TEMPLATENAMEDEFAULT "
                    + " FROM MSPVENDOR V JOIN MSPINTERFACE I on V.VENDORID = I.VENDORID "
                    + " WHERE INTERFACE IN ('CB_Server', 'CB_MR')";

            List<MultispeakVendor> mspVendors = jdbcTemplate.query(sql, mspVendorRowMapper);

            return mspVendors;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("MSP Vendors cannot be found.");
        }
    }

    @Override
    public synchronized int deleteMultispeakInterface(int vendorID) {
        try {
            String sql = "DELETE FROM " + MSPINTERFACE_TABLENAME + " WHERE VENDORID = ? ";

            int numDeleted = jdbcTemplate.update(sql, new Object[] { new Integer(vendorID) });
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
            String sql = "INSERT INTO " + MSPINTERFACE_TABLENAME + " VALUES (?, ?, ?)";

            int numAdded =
                jdbcTemplate.update(sql, new Object[] { mspInterface.getVendorID(), mspInterface.getMspInterface(),
                    mspInterface.getMspEndpoint() });
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
                    String sql =
                        "UPDATE " + MSPVENDOR_TABLENAME + " SET VENDORID = ?, " + " COMPANYNAME = ?, "
                            + " USERNAME = ?, " + " PASSWORD = ?, " + " URL = ?, " + " APPNAME = ?, "
                            + " OUTUSERNAME = ?, " + " OUTPASSWORD = ?, " + " MAXRETURNRECORDS = ?, "
                            + " REQUESTMESSAGETIMEOUT = ?, " + " MAXINITIATEREQUESTOBJECTS = ?, "
                            + " TEMPLATENAMEDEFAULT = ? " + " WHERE VENDORID = ? ";

                    Object[] args =
                        new Object[] { mspVendor.getVendorID(), mspVendor.getCompanyName(),
                            SqlUtils.convertStringToDbValue(mspVendor.getUserName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getPassword()),
                            SqlUtils.convertStringToDbValue(mspVendor.getUrl()),
                            SqlUtils.convertStringToDbValue(mspVendor.getAppName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getOutUserName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getOutPassword()),
                            mspVendor.getMaxReturnRecords(), mspVendor.getRequestMessageTimeout(),
                            mspVendor.getMaxInitiateRequestObjects(), mspVendor.getTemplateNameDefault(),
                            mspVendor.getVendorID() // Where Clause
                        };
                    jdbcTemplate.update(sql, args);

                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not updated for vendorID " + mspVendor.getVendorID()
                        + ".");
                }

                deleteMultispeakInterface(mspVendor.getVendorID().intValue());
                for (int i = 0; i < mspVendor.getMspInterfaces().size(); i++) {
                    addMultispeakInterfaces(mspVendor.getMspInterfaces().get(i));
                }

                clearMultispeakVendorCache();
            };
        });

    }

    @Override
    public synchronized void addMultispeakVendor(final MultispeakVendor mspVendor) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    mspVendor.setVendorID(getNextVendorId());
                    String sql =
                        "INSERT INTO "
                            + MSPVENDOR_TABLENAME
                            + " (VendorID, CompanyName, UserName, Password, URL, AppName, OutUserName, OutPassword, "
                            + " MaxReturnRecords, RequestMessageTimeout, MaxInitiateRequestObjects, TemplateNameDefault) "
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    Object[] args =
                        new Object[] { mspVendor.getVendorID(), mspVendor.getCompanyName(),
                            SqlUtils.convertStringToDbValue(mspVendor.getUserName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getPassword()),
                            SqlUtils.convertStringToDbValue(mspVendor.getUrl()),
                            SqlUtils.convertStringToDbValue(mspVendor.getAppName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getOutUserName()),
                            SqlUtils.convertStringToDbValue(mspVendor.getOutPassword()),
                            mspVendor.getMaxReturnRecords(), mspVendor.getRequestMessageTimeout(),
                            mspVendor.getMaxInitiateRequestObjects(), mspVendor.getTemplateNameDefault() };
                    jdbcTemplate.update(sql, args);

                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not inserted for Company Name "
                        + mspVendor.getCompanyName() + ".");
                } catch (DataIntegrityViolationException e) {
                    throw new DuplicateException(
                        "Cannot create Multispeak Vendor Integration with the same CompanyName/AppName " +
                        "combination as an existing Vendor.", e);
                }

                for (int i = 0; i < mspVendor.getMspInterfaces().size(); i++) {
                    MultispeakInterface mspInterface = mspVendor.getMspInterfaces().get(i);
                    mspInterface.setVendorID(mspVendor.getVendorID());
                    addMultispeakInterfaces(mspInterface);
                }

                clearMultispeakVendorCache();
            };
        });
    }

    @Override
    public synchronized void deleteMultispeakVendor(final int vendorID) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                deleteMultispeakInterface(vendorID);
                try {
                    String sql = "DELETE FROM " + MSPVENDOR_TABLENAME + " WHERE VENDORID = ? ";

                    jdbcTemplate.update(sql, new Object[] { new Integer(vendorID) });
                    clearMultispeakVendorCache();
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Multispeak Vendor not deleted for VendorID " + vendorID + ".");
                }
            };
        });
    }

    @Override
    public int getNextVendorId() {
        return nextValueHelper.getNextValue("MSPVendor");
    }

    private MultispeakVendor createMspVendor(ResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt(1));
        String companyName = rset.getString(2).trim();
        String userName = rset.getString(3).trim();
        String password = rset.getString(4).trim();
        String url = rset.getString(5).trim();
        String appName = rset.getString(6).trim();
        String outUserName = rset.getString(7).trim();
        String outPassword = rset.getString(8).trim();
        int maxReturnRecords = rset.getInt(9);
        long requestMessageTimeout = rset.getLong(10);
        long maxInitiateRequestObjects = rset.getLong(11);
        String templateNameDefault = rset.getString(12).trim();

        MultispeakVendor mspVendor =
            new MultispeakVendor(vendorID, companyName, appName, userName, password, outUserName, outPassword,
                maxReturnRecords, requestMessageTimeout, maxInitiateRequestObjects, templateNameDefault, url);

        List<MultispeakInterface> multispeakInterfaces = getMultispeakInterfaces(vendorID);
        mspVendor.setMspInterfaces(multispeakInterfaces);

        return mspVendor;
    }

    private static MultispeakInterface createMspInterface(ResultSet rset) throws SQLException {

        Integer vendorID = new Integer(rset.getInt(1));
        String interfaceStr = rset.getString(2).trim();
        String endpoint = rset.getString(3).trim();

        MultispeakInterface mspInterface = new MultispeakInterface(vendorID, interfaceStr, endpoint);
        return mspInterface;
    }
}