package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteAccountSite;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.yukon.IDatabaseCache;

public class StarsCustAccountInformationDaoImpl implements StarsCustAccountInformationDao {
    private final Logger log = YukonLogManager.getLogger(getClass());
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    @Transactional(readOnly = true)
    public LiteStarsCustAccountInformation getById(final int accountId, int energyCompanyId) {
        if (accountId == 0) return CustomerAccountPlaceHolder.getPlaceHolderAccount();
        
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, "); //1-6 
        sqlBuilder.append(" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, "); //7-12
        sqlBuilder.append(" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID "); //13-17
        sqlBuilder.append("FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam");
        sqlBuilder.append("WHERE ac.accountid = ? ");
        sqlBuilder.append("AND ac.AccountSiteID = acs.AccountSiteID "); 
        sqlBuilder.append("AND acs.SiteInformationID = si.SiteID");
        sqlBuilder.append("AND ectam.AccountID = ac.AccountID");
        sqlBuilder.append("AND ectam.EnergyCompanyID IN (");
        sqlBuilder.append(getEnergyCompanyIdList(energyCompanyId));
        sqlBuilder.append(")");
        final String sql = sqlBuilder.toString();

        try {
            LiteStarsCustAccountInformation liteAcctInfo = simpleJdbcTemplate.queryForObject(sql,
                                                                                             createRowMapper(energyCompanyId),
                                                                                             accountId);
            return liteAcctInfo;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, LiteStarsCustAccountInformation> getByIds(Set<Integer> accountIds, final int energyCompanyId) {
        final ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);

        final List<LiteStarsCustAccountInformation> resultList = 
            template.query(new SqlGenerator<Integer>() {
                @Override
                public String generate(List<Integer> subList) {
                    SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                    sqlBuilder.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, "); //1-6 
                    sqlBuilder.append(" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, "); //7-12
                    sqlBuilder.append(" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID "); //13-17
                    sqlBuilder.append(" FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam");
                    sqlBuilder.append(" WHERE ac.accountid IN (");
                    sqlBuilder.append(subList);
                    sqlBuilder.append(")");
                    sqlBuilder.append(" AND ac.AccountSiteID = acs.AccountSiteID "); 
                    sqlBuilder.append(" AND acs.SiteInformationID = si.SiteID");
                    sqlBuilder.append(" AND ectam.AccountID = ac.AccountID");
                    sqlBuilder.append(" AND ectam.EnergyCompanyID IN (");
                    sqlBuilder.append(getEnergyCompanyIdList(energyCompanyId));
                    sqlBuilder.append(")");
                    String sql = sqlBuilder.toString();
                    return sql;
                }
            }, accountIds, createRowMapper(energyCompanyId), energyCompanyId);

        final Map<Integer, LiteStarsCustAccountInformation> resultMap = 
            new HashMap<Integer, LiteStarsCustAccountInformation>(resultList.size());

        for (final LiteStarsCustAccountInformation account : resultList) {
            resultMap.put(account.getAccountID(), account);
        }

        return resultMap;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteStarsCustAccountInformation> getAll(int energyCompanyId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, "); //1-6 
        sqlBuilder.append(" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, "); //7-12
        sqlBuilder.append(" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID "); //13-17
        sqlBuilder.append("FROM CustomerAccount ac, AccountSite acs, SiteInformation si, ECToAccountMapping ectam ");
        sqlBuilder.append("WHERE  ac.AccountSiteID = acs.AccountSiteID "); 
        sqlBuilder.append("AND acs.SiteInformationID = si.SiteID");
        sqlBuilder.append("AND ectam.AccountID = ac.AccountID");
        sqlBuilder.append("AND ectam.EnergyCompanyID = ?");
        final String sql = sqlBuilder.toString();

        List<LiteStarsCustAccountInformation> list = simpleJdbcTemplate.query(sql, 
                                                                              createRowMapper(energyCompanyId),
                                                                              energyCompanyId);
        return list;
    }
    
    
    private ParameterizedRowMapper<LiteStarsCustAccountInformation> createRowMapper(final int energyCompanyId) {
        
        return new ParameterizedRowMapper<LiteStarsCustAccountInformation>() {
            @Override
            public LiteStarsCustAccountInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
                int accountId = rs.getInt(1);
                
                final LiteStarsCustAccountInformation liteAcctInfo = 
                    new LiteStarsCustAccountInformation(accountId, energyCompanyId);
                
                LiteCustomerAccount customerAccount = new LiteCustomerAccount();
                customerAccount.setAccountID(accountId);
                customerAccount.setAccountSiteID( rs.getInt(2));
                customerAccount.setAccountNumber( rs.getString(3));
                customerAccount.setCustomerID( rs.getInt(4) );
                customerAccount.setBillingAddressID( rs.getInt(5));
                customerAccount.setAccountNotes( rs.getString(6) );
                liteAcctInfo.setCustomerAccount(customerAccount);

                LiteAccountSite accountSite = new LiteAccountSite();
                accountSite.setAccountSiteID( customerAccount.getAccountSiteID());
                accountSite.setSiteInformationID( rs.getInt(7) );
                accountSite.setSiteNumber( rs.getString(8) );
                accountSite.setStreetAddressID( rs.getInt(9) );
                accountSite.setPropertyNotes( rs.getString(10));
                accountSite.setCustAtHome( rs.getString(11));
                accountSite.setCustStatus( rs.getString(12) );
                liteAcctInfo.setAccountSite(accountSite);

                LiteSiteInformation siteInformation = new LiteSiteInformation();
                siteInformation.setSiteID( accountSite.getAccountSiteID() );
                siteInformation.setFeeder( rs.getString(13) );
                siteInformation.setPole( rs.getString(14));
                siteInformation.setTransformerSize( rs.getString(15) );
                siteInformation.setServiceVoltage( rs.getString(16) );
                siteInformation.setSubstationID( rs.getInt(17) );
                liteAcctInfo.setSiteInformation(siteInformation);
             
                return liteAcctInfo;
            }
        };
    }
    
    private static class CustomerAccountPlaceHolder {
        private static final LiteStarsCustAccountInformation placeHolderAccount;
        
        static {
            placeHolderAccount = new LiteStarsCustAccountInformation(0,0);
            placeHolderAccount.setAccountSite(new LiteAccountSite(0));
            
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            synchronized (cache) {
                LiteCustomer customer =  ( cache.getACustomerByCustomerID(-1) );
                placeHolderAccount.setCustomer(customer);
            }    
        }
        
        public static LiteStarsCustAccountInformation getPlaceHolderAccount() {
            return placeHolderAccount;
        }
    }
    
    private List<Integer> getEnergyCompanyIdList(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteStarsEnergyCompany> energyCompanyList = ECUtils.getAllDescendants(energyCompany);
        List<Integer> energyCompanyIdList = toIdList(energyCompanyList);
        return energyCompanyIdList;
    }
    
    private List<Integer> toIdList(List<LiteStarsEnergyCompany> energyCompanyList) {
        List<Integer> idList = new ArrayList<Integer>(energyCompanyList.size());
        for (final LiteStarsEnergyCompany energyCompany : energyCompanyList) {
            idList.add(energyCompany.getEnergyCompanyID());
        }
        return idList;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}
