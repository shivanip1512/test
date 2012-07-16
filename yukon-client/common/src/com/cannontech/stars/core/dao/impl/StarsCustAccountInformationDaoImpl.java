package com.cannontech.stars.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteAccountSite;
import com.cannontech.stars.database.data.lite.LiteCustomerAccount;
import com.cannontech.stars.database.data.lite.LiteSiteInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Maps;

public class StarsCustAccountInformationDaoImpl implements StarsCustAccountInformationDao {
    
    private final Logger log = YukonLogManager.getLogger(getClass());
    
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private static final YukonRowMapper<LiteAccountInfo> mapper = new YukonRowMapper<LiteAccountInfo>() {
        @Override
        public LiteAccountInfo mapRow(YukonResultSet rs) throws SQLException {
            int accountId = rs.getInt("AccountId");
            
            int energyCompanyId = rs.getInt("EnergyCompanyId");
            
            LiteAccountInfo liteAcctInfo = new LiteAccountInfo(accountId, energyCompanyId);
            
            LiteCustomerAccount customerAccount = new LiteCustomerAccount();
            customerAccount.setAccountID(accountId);
            customerAccount.setAccountSiteID(rs.getInt("AccountSiteId"));
            customerAccount.setAccountNumber(rs.getString("AccountNumber"));
            customerAccount.setCustomerID(rs.getInt("CustomerId"));
            customerAccount.setBillingAddressID(rs.getInt("BillingAddressId"));
            customerAccount.setAccountNotes(rs.getString("AccountNotes"));
            liteAcctInfo.setCustomerAccount(customerAccount);

            LiteAccountSite accountSite = new LiteAccountSite();
            accountSite.setAccountSiteID(customerAccount.getAccountSiteID());
            accountSite.setSiteInformationID(rs.getInt("SiteInformationId"));
            accountSite.setSiteNumber(rs.getString("SiteNumber"));
            accountSite.setStreetAddressID(rs.getInt("StreetAddressId"));
            accountSite.setPropertyNotes(rs.getString("PropertyNotes"));
            accountSite.setCustAtHome(rs.getString("CustAtHome"));
            accountSite.setCustStatus(rs.getString("CustomerStatus"));
            liteAcctInfo.setAccountSite(accountSite);

            LiteSiteInformation siteInformation = new LiteSiteInformation();
            siteInformation.setSiteID(accountSite.getSiteInformationID());
            siteInformation.setFeeder(rs.getString("Feeder"));
            siteInformation.setPole(rs.getString("Pole"));
            siteInformation.setTransformerSize(rs.getString("TransformerSize"));
            siteInformation.setServiceVoltage(rs.getString("ServiceVoltage"));
            siteInformation.setSubstationID(rs.getInt("SubstationId"));
            liteAcctInfo.setSiteInformation(siteInformation);
         
            return liteAcctInfo;
        }
    };
    
    @Override
    public LiteAccountInfo getByAccountId(int accountId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, ");
        sql.append("  acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, ");
        sql.append("  si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, ectam.EnergyCompanyId ");
        sql.append("FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam");
        sql.append("WHERE ac.accountid").eq(accountId);
        sql.append("  AND ac.AccountSiteID = acs.AccountSiteID "); 
        sql.append("  AND acs.SiteInformationID = si.SiteID");
        sql.append("  AND ectam.AccountID = ac.AccountID");

        try {
            LiteAccountInfo liteAcctInfo = yukonJdbcTemplate.queryForObject(sql, mapper);
            return liteAcctInfo;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Customer Account Info not found for Account Id: "+ accountId, e);
            return null;
        }
    }
    
    @Override
    public LiteAccountInfo getById(int accountId, int energyCompanyId) {
        if (accountId == 0) return CustomerAccountPlaceHolder.getPlaceHolderAccount();

        // Get all the accessible energy companies
        Set<Integer> childEnergycompanyIds = ecMappingDao.getChildEnergyCompanyIds(energyCompanyId);

        LiteAccountInfo info = getByAccountId(accountId);
        if (!childEnergycompanyIds.contains(info.getEnergyCompanyId())) {
            // do explicit security log statement
            return null;
        }
        
        return info;
    }

    @Override
    public Map<Integer, LiteAccountInfo> getByIds(Set<Integer> accountIds, final int energyCompanyId) {
        final ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        final List<LiteAccountInfo> resultList = template.query(new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, "); 
                    sql.append(" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, ");
                    sql.append(" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, ectam.EnergyCompanyId");
                    sql.append(" FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam");
                    sql.append(" WHERE ac.accountid IN (");
                    sql.append(subList);
                    sql.append(")");
                    sql.append(" AND ac.AccountSiteID = acs.AccountSiteID "); 
                    sql.append(" AND acs.SiteInformationID = si.SiteID");
                    sql.append(" AND ectam.AccountID = ac.AccountID");
                    sql.append(" AND ectam.EnergyCompanyID IN (");
                    sql.append(getEnergyCompanyIdList(energyCompanyId));
                    sql.append(")");
                    return sql;
                }
            }, accountIds, mapper);

        final Map<Integer, LiteAccountInfo> resultMap = Maps.newHashMapWithExpectedSize(resultList.size());

        for (final LiteAccountInfo account : resultList) {
            resultMap.put(account.getAccountID(), account);
        }

        return resultMap;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteAccountInfo> getAll(int energyCompanyId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, ");
        sql.append(" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, ");
        sql.append(" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, ectam.EnergyCompanyId");
        sql.append("FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam ");
        sql.append("WHERE  ac.AccountSiteID = acs.AccountSiteID "); 
        sql.append("AND acs.SiteInformationID = si.SiteID");
        sql.append("AND ectam.AccountID = ac.AccountID");
        sql.append("AND ectam.EnergyCompanyID").eq(energyCompanyId);

        List<LiteAccountInfo> list = yukonJdbcTemplate.query(sql, mapper);
        return list;
    }
    
    private static class CustomerAccountPlaceHolder {
        private static final LiteAccountInfo placeHolderAccount;
        
        static {
            placeHolderAccount = new LiteAccountInfo(0,0);
            placeHolderAccount.setAccountSite(new LiteAccountSite(0));
            
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            synchronized (cache) {
                LiteCustomer customer =  (cache.getACustomerByCustomerID(-1));
                placeHolderAccount.setCustomer(customer);
            }    
        }
        
        public static LiteAccountInfo getPlaceHolderAccount() {
            return placeHolderAccount;
        }
    }
    
    private List<Integer> getEnergyCompanyIdList(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteStarsEnergyCompany> energyCompanyList = ECUtils.getAllDescendants(energyCompany);
        List<Integer> energyCompanyIdList = ECUtils.toIdList(energyCompanyList);
        return energyCompanyIdList;
    }
    
}