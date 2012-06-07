package com.cannontech.stars.dr.general.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.general.dao.OperatorAccountSearchDao;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OperatorAccountSeachDaoImpl implements OperatorAccountSearchDao {
	
	private YukonJdbcOperations yukonJdbcOperations;
	private ContactNotificationDao contactNotificationDao;
	private StarsDatabaseCache starsDatabaseCache;
	
	private AccountSearchResultRowMapper accountSearchResultRowMapper;
	
	@PostConstruct
	public void initialize() {
		accountSearchResultRowMapper = new AccountSearchResultRowMapper();
	}
	
	// ACCOUNT NUMBER
	@Override
	public List<Integer> getAccountIdsByAccountNumber(String accountNumber, Set<Integer> energyCompanyIds) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("WHERE ca.AccountNumber").startsWith(accountNumber);
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY ca.AccountNumber");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// PHONE NUMBER
	@Override
	public List<Integer> getAccountIdsByPhoneNumber(String phoneNumber, Set<Integer> energyCompanyIds) {
		
		Set<ContactNotificationType> phoneNumberNotificationCategories = Sets.newHashSetWithExpectedSize(2);
		phoneNumberNotificationCategories.add(ContactNotificationType.HOME_PHONE);
		phoneNumberNotificationCategories.add(ContactNotificationType.WORK_PHONE);
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN Customer cust ON (ca.CustomerId = cust.CustomerId)");
		sql.append("JOIN ContactNotification cn ON (cust.PrimaryContactId = cn.ContactId)");
		sql.append("WHERE cn.Notification").contains(phoneNumber);
		sql.append("AND cn.NotificationCategoryId").in(phoneNumberNotificationCategories);
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY ca.AccountNumber");
		
		List<Integer> accountIds = yukonJdbcOperations.query(sql, new IntegerRowMapper());
		
		if (accountIds.size() <= 0 && phoneNumber.length() == 10) {
			
			sql = new SqlStatementBuilder();
			sql.append("SELECT ca.AccountId");
			sql.append("FROM CustomerAccount ca");
			sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
			sql.append("JOIN Customer cust ON (ca.CustomerId = cust.CustomerId)");
			sql.append("JOIN ContactNotification cn ON (cust.PrimaryContactId = cn.ContactId)");
			sql.append("WHERE cn.Notification").endsWith(phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6));
			sql.append("AND cn.NotificationCategoryId").in(phoneNumberNotificationCategories);
			sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
			sql.append("ORDER BY ca.AccountNumber");
			
			accountIds = yukonJdbcOperations.query(sql, new IntegerRowMapper());
		}
		
		return accountIds;
	}
	
	// LAST NAME
	@Override
	public List<Integer> getAccountIdsByLastName(String lastName, Set<Integer> energyCompanyIds) {
		
		String firstName = null;
		lastName = lastName.trim();
		int commaIndex = lastName.indexOf(",");
	    if(commaIndex > 0) {
	    	firstName = lastName.substring(commaIndex + 1).trim();
	    	lastName = lastName.substring(0, commaIndex).trim();
	    }
		
	    SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN Customer cust ON (ca.CustomerId = cust.CustomerId)");
		sql.append("JOIN Contact cont ON (cust.PrimaryContactId = cont.ContactId)");
		sql.append("WHERE UPPER(cont.ContLastName)").startsWith(lastName.toUpperCase());
		if (firstName != null) {
			sql.append("AND UPPER(cont.ContFirstName)").startsWith(firstName.toUpperCase());
		}
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY cont.ContLastName, cont.ContFirstName");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// SERIAL NUMBER
	@Override
	public List<Integer> getAccountIdsBySerialNumber(String serialNumber, Set<Integer> energyCompanyIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ib.AccountId");
		sql.append("FROM InventoryBase ib");
		sql.append("JOIN LMHardwareBase lhb ON (ib.InventoryId = lhb.InventoryId)");
		sql.append("JOIN ECToInventoryMapping etim ON (lhb.InventoryId = etim.inventoryId)");
		sql.append("WHERE UPPER(lhb.ManufacturerSerialNumber)").eq(serialNumber.toUpperCase());
		sql.append("AND etim.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY lhb.ManufacturerSerialNumber");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// MAP NUMBER
	@Override
	public List<Integer> getAccountIdsByMapNumber(String mapNumber, Set<Integer> energyCompanyIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN AccountSite site ON (ca.AccountSiteId = site.AccountSiteId)");
		sql.append("WHERE UPPER(site.SiteNumber)").startsWith(mapNumber.toUpperCase());
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY site.SiteNumber");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// ADDRESS
	@Override
	public List<Integer> getAccountIdsByAddress(String locationAddress1, Set<Integer> energyCompanyIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN AccountSite site ON (ca.AccountSiteId = site.AccountSiteId)");
		sql.append("JOIN Address addr ON (site.StreetAddressId = addr.AddressId)");
		sql.append("WHERE UPPER(addr.LocationAddress1)").startsWith(locationAddress1.toUpperCase());
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY addr.LocationAddress1, addr.LocationAddress2, addr.CityName, addr.StateCode");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// ALT TRACKING NUMBER
	@Override
	public List<Integer> getAccountIdsByAltTrackingNumber(String altTrackingNumber, Set<Integer> energyCompanyIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN Customer cust ON (ca.CustomerId = cust.CustomerId)");
		sql.append("WHERE UPPER(cust.AltTrackNum)").startsWith(altTrackingNumber.toUpperCase());
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY cust.AltTrackNum");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
	
	// COMPANY NAME
	@Override
	public List<Integer> getAccountIdsByCompanyName(String companyName, Set<Integer> energyCompanyIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ca.AccountId");
		sql.append("FROM CustomerAccount ca");
		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
		sql.append("JOIN CICustomerBase cicb ON (ca.CustomerId = cicb.CustomerId)");
		sql.append("WHERE UPPER(cicb.CompanyName)").startsWith(companyName.toUpperCase());
		sql.append("AND ectam.EnergyCompanyId").in(energyCompanyIds);
		sql.append("ORDER BY cicb.CompanyName");
		
		return yukonJdbcOperations.query(sql, new IntegerRowMapper());
	}
     
	@Override
	public AccountSearchResult getAccountSearchResultForAccountId(int accountId) {
		
		SqlFragmentGenerator<Integer> sqlGenerator = getAccountSearchResultSqlFragmentGenerator();
		SqlFragmentSource sql = sqlGenerator.generate(Collections.singletonList(accountId));
		
		return yukonJdbcOperations.queryForObject(sql, accountSearchResultRowMapper);
	}
	
	@Override
	public List<AccountSearchResult> getAccountSearchResultsForAccountIds(List<Integer> accountIds) {
		
		ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcOperations);
		
		SqlFragmentGenerator<Integer> sqlGenerator = getAccountSearchResultSqlFragmentGenerator();
        
        Function<Integer, Integer> mapper = Functions.identity();

        ParameterizedRowMapper<Entry<Integer, AccountSearchResult>> rowMapper = new ParameterizedRowMapper<Entry<Integer, AccountSearchResult>>() {
            public Entry<Integer, AccountSearchResult> mapRow(ResultSet rs, int rowNum) throws SQLException {
            	AccountSearchResult accountSearchResult = accountSearchResultRowMapper.mapRow(rs, rowNum);
                return Maps.immutableEntry(accountSearchResult.getAccountId(), accountSearchResult);
            }
        };

        Map<Integer, AccountSearchResult> map = template.mappedQuery(sqlGenerator, accountIds, rowMapper, mapper);
        
        return Lists.newArrayList(map.values());
	}
	
	private SqlFragmentGenerator<Integer> getAccountSearchResultSqlFragmentGenerator() {
		
		SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
			
            public SqlFragmentSource generate(List<Integer> subList) {
            	
            	SqlStatementBuilder sql = new SqlStatementBuilder();
        		sql.append("SELECT");
        		sql.append("ca.AccountId,");
        		sql.append("ectam.EnergyCompanyId,");
        		sql.append("ca.AccountNumber,");
        		sql.append("cont.ContFirstName,");
        		sql.append("cont.ContLastName,");
        		sql.append("cicb.CompanyName,");
        		sql.append("cust.PrimaryContactId,");
        		sql.append("cust.CustomerTypeId,");
        		sql.append("cust.AltTrackNum,");
        		sql.append("addr.LocationAddress1,");
        		sql.append("addr.LocationAddress2,");
        		sql.append("addr.CityName,");
        		sql.append("addr.StateCode,");
        		sql.append("addr.ZipCode,");
        		sql.append("addr.County");
        		sql.append("FROM CustomerAccount ca");
        		sql.append("JOIN ECToAccountMapping ectam ON (ca.AccountId = ectam.AccountId)");
        		sql.append("JOIN Customer cust ON (ca.CustomerId = cust.CustomerId)");
        		sql.append("JOIN Contact cont ON (cust.PrimaryContactId = cont.ContactId)");
        		sql.append("JOIN AccountSite site ON (ca.AccountSiteId = site.AccountSiteId)");
        		sql.append("LEFT JOIN CICustomerBase cicb ON (ca.CustomerId = cicb.CustomerId)");
        		sql.append("LEFT JOIN Address addr ON (site.StreetAddressId = addr.AddressId)");
        		sql.append("WHERE ca.AccountId").in(subList);
                return sql;
            }
		};
            
       return sqlGenerator;
	}
	
	private class AccountSearchResultRowMapper implements ParameterizedRowMapper<AccountSearchResult> {
		
		@Override
		public AccountSearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {

			int accountId = rs.getInt("AccountId");
        	int energyCompanyId = rs.getInt("EnergyCompanyId");
			String accountNumber = SqlUtils.convertDbValueToString(rs, "AccountNumber");
			String firstName = SqlUtils.convertDbValueToString(rs, "ContFirstName");
			String lastName = SqlUtils.convertDbValueToString(rs, "ContLastName");
			int customerTypeId = rs.getInt("CustomerTypeId");
			String altTrackingNumber = SqlUtils.convertDbValueToString(rs, "AltTrackNum");
			String companyName = SqlUtils.convertDbValueToString(rs, "CompanyName");
			int primaryContactId = rs.getInt("PrimaryContactId");
			String locationAddress1 = SqlUtils.convertDbValueToString(rs, "LocationAddress1");
			String locationAddress2 = SqlUtils.convertDbValueToString(rs, "LocationAddress2");
			String cityName = SqlUtils.convertDbValueToString(rs, "CityName");
			String stateCode = SqlUtils.convertDbValueToString(rs, "StateCode");
			String zipCode = SqlUtils.convertDbValueToString(rs, "ZipCode");
			String county = SqlUtils.convertDbValueToString(rs, "County");
			
			if (customerTypeId != CustomerTypes.CUSTOMER_CI) {
	    		companyName = null;
	    	}
	    	
	    	Address address = new Address(locationAddress1, locationAddress2, cityName, stateCode, zipCode, county);
	    	
	    	// phone
	    	LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContactId, ContactNotificationType.HOME_PHONE);
	        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContactId, ContactNotificationType.WORK_PHONE);
			
	        String energyCompanyName = starsDatabaseCache.getEnergyCompany(energyCompanyId).getName();
	        
	        return new AccountSearchResult(accountId, energyCompanyId, accountNumber, altTrackingNumber, firstName, 
	                                       lastName, companyName, homePhoneNotif, workPhoneNotif, address, energyCompanyName);
		}
	}
     

	@Autowired
	public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
		this.yukonJdbcOperations = yukonJdbcOperations;
	}
	
	@Autowired
	public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
		this.contactNotificationDao = contactNotificationDao;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
	
}