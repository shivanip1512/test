/*
 * Created on Dec 19, 2005
 */
package com.cannontech.stars.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.google.common.collect.Lists;

/**
 * SettlementConfigFuncs - deals with lite settlementConfig objects
 * @author snebben
 */
public final class SettlementConfigFuncs {

    private static final Logger log = YukonLogManager.getLogger(SettlementConfigFuncs.class);
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Loads all settlement configs. 
	 * If no "non-default" configs exist (those with configId >= 0, then loadDefaultIfEmpty is checked.
	 * When loadDefaultIfEmpty is true, if no non-default configs are loaded, then the default configs (confiId < 0) are loaded and returned.
	 * When false, only non-defaults are ever attempted to be loaded.
	 * @param loadDefaultIfEmpty
	 * @return
	 */
	public static List<LiteSettlementConfig> getAllLiteConfigsBySettlementType(boolean loadDefaultIfEmpty)
    {
	    List<LiteSettlementConfig> settlementConfigs = getLiteSettlementConfigs(false);
	    if (settlementConfigs.isEmpty()) {
	        settlementConfigs = getLiteSettlementConfigs(true);
	    }
	    return settlementConfigs;
    }

	/**
	 * Returns a List of LiteSettlementConfig objects (HECO only).
	 * When queryForDefaults is true, only "default" configs will be loaded (configId < 0).
	 * When false, only non-default configs are loaded (configId >= 0)
	 * Valid types found in YukonListEntryTypes.
	 * @param yukonDefID
	 * @return
	 */
	private static List<LiteSettlementConfig> getLiteSettlementConfigs(boolean queryForDefaults)
	{
        final List<LiteSettlementConfig> settlementConfigs = new ArrayList<LiteSettlementConfig>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ConfigId, FieldName, FieldValue, Description, RefEntryId FROM SettlementConfig");
        if (!queryForDefaults) {
            sql.append("WHERE ConfigId >= 0");  //remove default/negative configs
        } else {
            sql.append("WHERE ConfigId < 0");  //remove default/negative configs
        }
        
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.query(sql.getSql(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                while (rset.next()) {
                    int configId = rset.getInt("ConfigId");
                    String fieldName = rset.getString("FieldName");
                    String fieldValue = rset.getString("FieldValue");
                    String description = rset.getString("Description");
                    int refEntryId = rset.getInt("RefEntryId");
                    LiteSettlementConfig liteSettlementConfig = new LiteSettlementConfig(configId, fieldName, fieldValue, description, refEntryId);
                    settlementConfigs.add(liteSettlementConfig);
                }
                return null;
            }
        });
        return settlementConfigs;
	}

	/**
	 * Returns all YukonListEntry values for listeStarsEC's "Rate Schedule" list.
	 * @param liteStarsEC
	 * @return
	 */
	public static List<YukonListEntry> getAllAvailRateSchedules(LiteStarsEnergyCompany liteStarsEC)
	{
		YukonSelectionList rateList = liteStarsEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE);
		return rateList.getYukonListEntries();
	}

	/**
	 * Returns List of LiteSettlementConfig values.
	 * If no settlementConfig.refEntryId matches rateEntryId, then the expected default config is returned.
	 * NOTE: This should probably just return 1 item as we only expect there to ever be one (HECO, specifically).
	 *  But to minimize change, we're leaving as a list.
	 * @param rateEntryID YukonListEntryTypes (a rate schedule list's EntryID)
	 * @return
	 */
	public static List<LiteSettlementConfig> getRateScheduleConfigs(int rateEntryID)
	{
		List<LiteSettlementConfig> rateConfigs = Lists.newArrayList();
		
		List<LiteSettlementConfig> liteSettlementConfigs = getAllLiteConfigsBySettlementType(false);
		for (LiteSettlementConfig liteSettlementConfig : liteSettlementConfigs) {
		    // Find and add to list if settlement config refEntryId exists with that rateEntryId
			if( rateEntryID == liteSettlementConfig.getRefEntryID()) {
				rateConfigs.add(liteSettlementConfig);
			}
		}
		
		// If no settlmentConfig.refEntryId found that matched rateEntryId, then load the expected default
		if( rateConfigs.isEmpty()) {
			rateConfigs.add(new LiteSettlementConfig(SettlementConfig.HECO_RATE_DEMAND_CHARGE, SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING, "0", "Rate Schedule billing demand charge", 0));
		}
		return rateConfigs;
	}
	
	/**
	 * Retrieves the DBPers SettlementConfig based on LiteSettlementConfig lsc.
	 * Updates the DBPers SettlementConfig FieldValue with value.
	 * @param configID
	 * @param value
	 */
	public static void updateSettlementConfigTrx(LiteSettlementConfig lsc, String value)
	{
	    DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
	    
		try
		{
			SettlementConfig sConf = (SettlementConfig)LiteFactory.convertLiteToDBPers(lsc);
			//Need to retrieve all the fields of the Fatty since the Lite doesn't fully load it. 
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, sConf);
			sConf = (SettlementConfig)t.execute();
				
			sConf.setFieldValue(value);
			dbPersistentDao.performDBChange(sConf,TransactionType.UPDATE);

		}
		catch (TransactionException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * If exactly one SettlementConfig is not found (with configId >= 0), a default value of "0" is returned.
	 * @param fieldName
	 * @return fieldValue string
	 */
	public static String getLiteSettlementConfig(String fieldName)
	{
	    String fieldValue = "0";   //default to 0 
	    
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT FieldValue FROM SettlementConfig");
	    sql.append("WHERE ConfigId >= 0");  //remove default/negative configs
	    sql.append("AND FieldName").eq(fieldName);
	    JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
	    try {
	        fieldValue = yukonTemplate.queryForObject(sql.getSql(), sql.getArguments(), String.class);
	    } catch (IncorrectResultSizeDataAccessException e) {
	        log.error("SettlementConfig did not return exactly one row for FieldName " + fieldName + ". Check database for distinct FieldName (with configId >= 0).");
	    }
        return fieldValue;
	}

	/**
     * If exactly one SettlementConfig is not found (with configId >= 0), an empty list is returned.
     * @param fieldName
     * @return fieldValue string
     */
    public static List<Pair<Integer, String>> getLiteSettlementConfigs(String fieldName)
    {
        final List<Pair<Integer, String>> refEntryIdAndFieldValue = new ArrayList<Pair<Integer,String>>();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RefEntryId, FieldValue FROM SettlementConfig");  //RefEntryId is the rateSchedule entryId
        sql.append("WHERE ConfigId >= 0");  //remove default/negative configs
        sql.append("AND FieldName").eq(fieldName);
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        yukonTemplate.query(sql.getSql(), sql.getArguments(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                while (rset.next()) {
                    int refEntryId = rset.getInt("RefEntryId");
                    String fieldValue = rset.getString("FieldValue");
                    Pair<Integer, String> pair = new Pair<Integer, String>(refEntryId, fieldValue);
                    refEntryIdAndFieldValue.add(pair);
                }
                return null;
            }
        });
        return refEntryIdAndFieldValue;
    }
}