package com.cannontech.yukon.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.AbstractRowCallbackHandler;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;

public class CICustomerLoader implements Runnable 
{
    //if total contact number is over this, better not load this way
    private final int MAX_CUSTOMER_LOAD = 50000;
    
    //	Map<Integer(custID), LiteCustomer>
    private Map<Integer, LiteCICustomer> allCICustomersMap = new HashMap<Integer, LiteCICustomer>();
    private List<LiteCICustomer> allCICustomers = null;
    
    @SuppressWarnings("unchecked")
    public CICustomerLoader(List<LiteCICustomer> custArray, String alias) {
        super();
        this.allCICustomers = custArray;
    }
    
    public void run() {
        Date timerStart = null;
        Date timerStop = null;
        
        timerStart = new Date();
        
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        
        // First load up all of the customers. If we had a way to only load MAX_CUSTOMER_LOAD, we
        // would. As a work around, we'll sort by CustomerID, determine the last customer loaded,
        // then use its ID in subsequent where clauses to limit what data is returned.
        String sqlString = 
            "select CustomerID, PrimaryContactID, TimeZone, CustomerTypeID, CustomerNumber, RateScheduleID, " +
            "AltTrackNum, TemperatureUnit " +
            "from " + Customer.TABLE_NAME + " " +
            "where CustomerTypeID = " + CustomerTypes.CUSTOMER_CI + " " +
            "order by CustomerID";
        
        // The following is reusable if slightly refactored.
        final RowMapper customerMapper = new RowMapper() {
            
            public Object mapRow(ResultSet rset, int rowNum) throws SQLException {
                int cstID = rset.getInt("CustomerID");
                int contactID = rset.getInt("PrimaryContactID");
                String timeZone = rset.getString("TimeZone").trim();
                int custTypeID = rset.getInt("CustomerTypeID");
                String custNumber = rset.getString("CustomerNumber").trim();
                int custRateScheduleID = rset.getInt("RateScheduleID");
                String custAltTrackNum = rset.getString("AltTrackNum").trim();
                String temperatureUnit = rset.getString("TemperatureUnit").trim();
                
                LiteCICustomer lc = new LiteCICustomer(cstID);
                lc.setPrimaryContactID(contactID);
                lc.setTimeZone(timeZone);
                lc.setCustomerTypeID(custTypeID);
                lc.setCustomerNumber(custNumber);
                lc.setRateScheduleID(custRateScheduleID);
                lc.setAltTrackingNumber(custAltTrackNum);
                lc.setTemperatureUnit(temperatureUnit);
                lc.setAdditionalContacts(new Vector<LiteContact>(0));
                
                return lc;
            }
            
        };
        
        template.query(sqlString, new MaxRowCalbackHandlerRse(new AbstractRowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                LiteCICustomer customer = (LiteCICustomer) customerMapper.mapRow(rs, rowNum);
                allCICustomers.add(customer);
                allCICustomersMap.put(customer.getCustomerID(), customer);
            }
        }, MAX_CUSTOMER_LOAD));
        
        // Because the customers were ordered by their id, we can look at the last
        // one to determine the max customerId that was loaded. This will then be used
        // to limit how much is loaded in the following queries.
        int maxCustomerID = 0;
        if(!allCICustomers.isEmpty()){
        	LiteCustomer lastCustomer = allCICustomers.get(allCICustomers.size() - 1);
        	maxCustomerID = lastCustomer.getCustomerID();
        }
        
        // Select the additional contact for each of the customers that we have loaded so far.
        sqlString = 
            "SELECT ca.CustomerID, ca.ContactID " + 
            "FROM CustomerAdditionalContact ca, " + 
            Customer.TABLE_NAME + " c " + 
            "WHERE ca.CustomerID=c.CustomerID " +
            "and c.CustomerTypeID = " + CustomerTypes.CUSTOMER_CI + " " +
            "and ca.CustomerID <= " + maxCustomerID + " " +
            "ORDER BY ca.CustomerID, ca.Ordering";
        
        final Map<LiteCustomer, Vector<LiteContact>> temporaryAdditionalContacts = new TreeMap<LiteCustomer, Vector<LiteContact>>();
        
        template.query(sqlString, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                int custumerId = rs.getInt("CustomerID");
                LiteCustomer liteCustomer = allCICustomersMap.get(custumerId);
                // for the following to be efficient, the contacts should have been cached already
                LiteContact contact = DaoFactory.getContactDao().getContact(rs.getInt("ContactID"));
                Vector<LiteContact> contactList = temporaryAdditionalContacts.get(liteCustomer);
                if (contactList == null) {
                    contactList = new Vector<LiteContact>();
                    temporaryAdditionalContacts.put(liteCustomer, contactList);
                }
                contactList.add(contact);
            }
        });
        
        // It is important that we never call getAdditionalContacts on the lite customer. This
        // would cause the lite object to load all of the additional contact (lazy style) first--
        // which would defeat the purpose of what we're trying to do here.
        for (Map.Entry<LiteCustomer, Vector<LiteContact>> entry : temporaryAdditionalContacts.entrySet()) {
            LiteCustomer customer = entry.getKey();
            Vector<LiteContact> additionalContacts = entry.getValue();
            customer.setAdditionalContacts(additionalContacts);
        }
        

        // Now we do the same for accountIds as we just did with additional contacts.
        sqlString =	"SELECT acct.AccountID, map.EnergyCompanyID, acct.CustomerID " +
            "FROM CustomerAccount acct, ECToAccountMapping map, Customer c " +
            "WHERE acct.AccountID = map.AccountID " +
            "and acct.CustomerID = c.CustomerID " +
            "and c.CustomerTypeID = " + CustomerTypes.CUSTOMER_CI + " " +                
            "and acct.CustomerID <= " + maxCustomerID + " " +
            "order by acct.customerID";
        
        final Map<LiteCustomer, Vector<Integer>> temporaryAccountIds = new TreeMap<LiteCustomer, Vector<Integer>>();
        
        template.query(sqlString, new RowCallbackHandler() {
            public void processRow(ResultSet rset) throws SQLException {
                Integer accountId = rset.getInt("AccountID");
                int energyCompanyId = rset.getInt("EnergyCompanyID");
                Integer customerId = rset.getInt("CustomerID");
                
                LiteCustomer customer = allCICustomersMap.get(customerId);
                customer.setEnergyCompanyID(energyCompanyId);
                
                Vector<Integer> accountList = temporaryAccountIds.get(customer);
                if (accountList == null) {
                    accountList = new Vector<Integer>();
                    temporaryAccountIds.put(customer, accountList);
                }
                accountList.add(accountId);
            }
        });
        
        for (Map.Entry<LiteCustomer, Vector<Integer>> entry : temporaryAccountIds.entrySet()) {
            entry.getKey().setAccountIDs(entry.getValue());
        }
        
        // Finally, fill in the extra CICustomer data for those customers that are C&I.
        sqlString = 
            "select CustomerID, MainAddressID, CompanyName, " +
            "CustomerDemandLevel, CurtailAmount, CICustType " +
            "from " + CICustomerBase.TABLE_NAME + " " +
            "where CustomerID <= " + maxCustomerID;
        
        template.query(sqlString, new RowCallbackHandler() {
            public void processRow(ResultSet rset) throws SQLException {
                int customerId = rset.getInt("CustomerID");
                int addressID = rset.getInt("MainAddressID");
                String name = rset.getString("CompanyName").trim();
                double demandLevel = rset.getDouble("CustomerDemandLevel");
                double curtAmount = rset.getDouble("CurtailAmount");
                int customerTypeId = rset.getInt("CICustType");
                
                LiteCustomer liteCustomer = allCICustomersMap.get(customerId);
                if (liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
                    LiteCICustomer liteCiCustomer = (LiteCICustomer) liteCustomer;
                    
                    liteCiCustomer.setMainAddressID(addressID);
                    liteCiCustomer.setCompanyName(name);
                    liteCiCustomer.setDemandLevel(demandLevel);
                    liteCiCustomer.setCurtailAmount(curtAmount);
                    liteCiCustomer.setCICustType(customerTypeId);
                } else {
                    CTILogger.warn("Customer " + liteCustomer.getCustomerID() + " isn't a CICustomer as expected.");
                }
            }
        });
        
        timerStop = new Date();
        CTILogger.info((timerStop.getTime() - timerStart.getTime())*.001 + 
                       " Secs for CICustomerLoader with Contacts (" + allCICustomers.size() + 
                       " loaded, MAX_CUSTOMER_LOAD="+ MAX_CUSTOMER_LOAD + ")" );
    }
    
}
